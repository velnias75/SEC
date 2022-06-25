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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import de.rangun.sec.SECPlugin;
import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class BlockPlaceListener implements Listener { // NOPMD by heiko on 13.06.22, 15:35

	private final SECPlugin plugin;

	public BlockPlaceListener(final SECPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlaceEvent(final BlockPlaceEvent event) {

		if (!plugin.isChairsEnabled()) {
			return;
		}

		final Block block = event.getBlockAgainst();
		final ItemStack handItem = event.getItemInHand();

		if (event.canBuild() && !event.getPlayer().isSneaking() && !Material.AIR.equals(handItem.getType())
				&& Utils.isValidForChair(block)) {

			event.setCancelled(true);
		}
	}
}
