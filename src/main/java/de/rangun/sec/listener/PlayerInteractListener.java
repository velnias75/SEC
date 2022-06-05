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
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

/**
 * @author heiko
 *
 */
public final class PlayerInteractListener extends AbstractListener {

	public PlayerInteractListener(final Plugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteractEvent(final PlayerInteractEvent event) {

		if (!event.hasBlock()) {
			return;
		}

		final Block block = event.getClickedBlock();
		final Action action = event.getAction();
		final Player player = event.getPlayer();

		if (Action.RIGHT_CLICK_BLOCK.equals(action) && !player.isSneaking() && isValidForChair(block)) {

			final Location location = player.getLocation();

			location.setYaw(getChairYaw(block));
			player.teleport(location);

			block.getWorld().spawn(block.getLocation().add(0.5d, -0.5d, 0.5d), Pig.class, new Consumer<Pig>() {

				@Override
				public void accept(final Pig vehicle) {

					vehicle.setInvisible(true);
					vehicle.setSilent(true);
					vehicle.setInvulnerable(true);
					vehicle.setGravity(false);
					vehicle.addPassenger(player);
					vehicle.setAware(false);
					vehicle.setAI(false);
					vehicle.setRotation(getChairYaw(block), 0.0f);
					vehicle.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.0000000001d); // NOPMD by heiko on
																									// 05.06.22, 09:39
					vehicle.getPersistentDataContainer().set(pig, PersistentDataType.BYTE, (byte) 1);
				}
			});

			event.setCancelled(true);
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
