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

import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.rangun.sec.SECPlugin;
import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class WasteBinListener implements Listener {

	private final SECPlugin plugin;

	public WasteBinListener(final SECPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryPickupItemEvent(final InventoryPickupItemEvent event) {

		final Block block = getBlockFromInventory(event.getInventory());

		if (event.getInventory().getHolder() instanceof Hopper
				&& Utils.isWasteBin(block, plugin.getDescription().getName())) {

			event.setCancelled(true);

			if (transferToWasteBin(block, event.getItem().getItemStack()).isEmpty()) {
				event.getItem().remove();
			}
		}
	}

	@EventHandler
	public void onInventoryMoveItemEvent(final InventoryMoveItemEvent event) {

		final Block block = getBlockFromInventory(event.getDestination());

		if (block != null && event.getDestination().getHolder() instanceof Hopper
				&& Utils.isWasteBin(block, plugin.getDescription().getName())) {

			event.setCancelled(true);

			(new BukkitRunnable() {

				@Override
				public void run() {

					ItemStack item = null;

					for (final ItemStack itemX : event.getSource().getContents()) {

						if (itemX != null) {
							item = itemX;
							break;
						}
					}

					if (item != null && item.getAmount() > 0) {

						final ItemStack copyItem = item.clone();
						copyItem.setAmount(1);

						if (transferToWasteBin(block, copyItem).isEmpty()) {
							item.setAmount(item.getAmount() - 1);
						}
					}
				}

			}).runTaskLater(plugin, 1L);
		}
	}

	private Block getBlockFromInventory(final Inventory inv) {
		return inv.getHolder() instanceof BlockInventoryHolder && inv.getLocation() != null
				? ((BlockInventoryHolder) inv.getHolder()).getBlock().getWorld().getBlockAt(inv.getLocation())
				: null;
	}

	private Map<Integer, ItemStack> transferToWasteBin(final Block block, final ItemStack itemStack) {
		return plugin.getWasteBin((Hopper) block.getState()).addItem(itemStack);
	}

	/*--
	private boolean isFull(final Block block) {
		return Arrays.stream(plugin.getWasteBin((Hopper) block.getState()).getStorageContents())
				.anyMatch(itemStack -> itemStack == null || itemStack.getAmount() < itemStack.getMaxStackSize());
	} */
}
