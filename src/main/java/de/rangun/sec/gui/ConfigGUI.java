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

package de.rangun.sec.gui;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ConfigGUI implements Listener {

	private final ConfigGUICallback callback;
	private final Inventory inv;

	private enum Slot {

		CHAIRS(9), WASTEBIN(10), SAVE_EXIT(17);

		public final int slot;

		Slot(final int slot) {
			this.slot = slot;
		}
	}

	public ConfigGUI(final ConfigGUICallback callback) {

		this.callback = callback;

		// Create a new inventory, with no owner (as this isn't a real inventory), a
		// size of nine, called example
		inv = Bukkit.createInventory(null, 18, "§6§lSEC-Configuration");

		// Put the items into the inventory
		initializeItems();
	}

	// You can call this whenever you want to put the items in
	public void initializeItems() {

		inv.setItem(0, createGuiItem(Material.OAK_STAIRS, "§r§cChairs", "§r§o§aSuper Easy Chairs"));

		inv.setItem(Slot.CHAIRS.slot, getEnableDisableItemStack(callback.isChairsEnabled()));

		inv.setItem(1, createGuiItem(Material.HOPPER, "§r§7Wastebins", "§r§o§aSuper Easy Wastebins"));

		inv.setItem(Slot.WASTEBIN.slot, getEnableDisableItemStack(callback.isWasteBinsEnabled()));

		inv.setItem(Slot.SAVE_EXIT.slot,
				createGuiItem(Material.REDSTONE_TORCH, "§r§4Save and exit config", (String[]) null));
	}

	// Nice little method to create a gui item with a custom name, and description
	private ItemStack createGuiItem(final Material material, final String name, final String... lore) {

		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();

		// Set the name of the item
		meta.setDisplayName(name);

		// Set the lore of the item
		if (lore != null) {
			meta.setLore(Arrays.asList(lore));
		}

		item.setItemMeta(meta);

		return item;
	}

	// You can open the inventory with this
	public void openInventory(final HumanEntity ent) {
		ent.openInventory(inv);
	}

	// Check for clicks on items
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {

		if (!event.getInventory().equals(inv)) {
			return; // NOPMD by heiko on 25.06.22, 09:10
		}

		event.setCancelled(true);

		final ItemStack clickedItem = event.getCurrentItem();

		// verify current item is not null
		if (clickedItem == null || clickedItem.getType().isAir()) {
			return; // NOPMD by heiko on 25.06.22, 09:10
		}

		final Player player = (Player) event.getWhoClicked();

		if (!player.hasPermission("sec.admin")) {

			player.closeInventory();
			player.sendMessage(ChatColor.RED + "You have no permsission to change the configuration");

			return;
		}

		final boolean enabled = Material.RED_STAINED_GLASS_PANE.equals(inv.getItem(event.getRawSlot()).getType());

		if (event.getRawSlot() == Slot.SAVE_EXIT.slot) {

			callback.saveConfig();
			player.closeInventory();

		} else {

			if (event.getRawSlot() == Slot.CHAIRS.slot) {

				callback.setChairsEnabled(enabled);
				inv.setItem(Slot.CHAIRS.slot, getEnableDisableItemStack(enabled));

			} else if (event.getRawSlot() == Slot.WASTEBIN.slot) {

				callback.setWasteBinEnabled(enabled);
				inv.setItem(Slot.WASTEBIN.slot, getEnableDisableItemStack(enabled));
			}
		}
	}

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent event) {

		if (event.getInventory().equals(inv)) {
			event.setCancelled(true);
		}
	}

	private ItemStack getEnableDisableItemStack(final boolean enabled) {
		return enabled ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "§r§o§2enabled", "§r§o§fclick to disable")
				: createGuiItem(Material.RED_STAINED_GLASS_PANE, "§r§o§4disabled", "§r§o§fclick to enable");
	}
}
