/*
 * Copyright 2022 by Heiko Schäfer <heiko@rangun.de>
 *
 * This file is part of SEC.
 *
 * SEC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * SEC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SEC.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.rangun.sec.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Consumer;

import de.rangun.sec.SECPlugin;
import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class PlayerInteractListener extends AbstractListener {

	private final SECPlugin plugin;

	public PlayerInteractListener(final SECPlugin plugin) {

		super(plugin);
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteractEvent(final PlayerInteractEvent event) {

		if (!event.hasBlock()) {
			return; // NOPMD by heiko on 04.11.22, 07:06
		}

		final Block block = event.getClickedBlock();
		final Action action = event.getAction();
		final Player player = event.getPlayer();

		if (plugin.isChairsEnabled() && Action.RIGHT_CLICK_BLOCK.equals(action) && !(player.isSneaking()
				&& !Material.AIR.equals(player.getInventory().getItemInMainHand().getType()))) {

			if (Utils.isValidForChair(block)) {

				final Location zpigAtLocation = Utils.doForNearbyZordanPigs(block.getWorld(), block.getLocation(), pig,
						(p) -> {
							return !p.getPassengers().isEmpty();
						});

				if (zpigAtLocation != null) {

					final Location newLocation = zpigAtLocation.add(-0.5d, 0.5d, -0.5d);

					if (block.getLocation().getBlockX() == newLocation.getX()
							&& block.getLocation().getBlockY() == newLocation.getY()
							&& block.getLocation().getBlockZ() == newLocation.getZ()) {
						return;
					}
				}

				final Location location = player.getLocation();

				location.setYaw(getChairYaw(block));
				player.teleport(location);

				block.getWorld().spawn(Utils.getPigLocation(block), Pig.class, new Consumer<Pig>() {

					@Override
					public void accept(final Pig vehicle) {

						vehicle.setInvisible(true);
						vehicle.setSilent(true);
						vehicle.setInvulnerable(true);
						vehicle.setGravity(false);
						vehicle.addPassenger(player);
						vehicle.setAware(false);
						vehicle.setAI(false);
						vehicle.setLootTable(null);
						vehicle.addScoreboardTag(TAG);
						vehicle.setRotation(getChairYaw(block), 0.0f);
						vehicle.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.0000000001d); // NOPMD by
																										// heiko on
																										// 05.06.22,
																										// 09:39
						vehicle.getPersistentDataContainer().set(pig, PersistentDataType.BYTE, (byte) 1);
					}
				});

				event.setCancelled(true);
			}

			if (plugin.isWasteBinsEnabled() && Utils.isWasteBin(block, plugin.getDescription().getName())) {

				player.openInventory(plugin.getWasteBin((Hopper) block.getState()));
				event.setCancelled(true);
			}
		}
	}

	private float getChairYaw(final Block block) {

		switch (((Stairs) block.getBlockData()).getFacing()) {
		case SOUTH:
			return 180.0f; // NOPMD by heiko on 05.06.22, 09:36
		case NORTH:
			return 0.0f; // NOPMD by heiko on 05.06.22, 09:36
		case WEST:
			return -90.0f; // NOPMD by heiko on 05.06.22, 09:36
		default:
			return 90.0f;
		}
	}
}
