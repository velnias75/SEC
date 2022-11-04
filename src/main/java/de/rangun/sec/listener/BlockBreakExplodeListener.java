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

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.rangun.sec.SECPlugin;
import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class BlockBreakExplodeListener extends AbstractListener {

	private final SECPlugin plugin;

	public BlockBreakExplodeListener(final SECPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreakEvent(final BlockBreakEvent event) {
		brokenBlocks(List.of(event.getBlock()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockExplodeEvent(final BlockExplodeEvent event) {
		brokenBlocks(event.blockList());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplodeEvent(final EntityExplodeEvent event) {
		brokenBlocks(event.blockList());
	}

	private void brokenBlocks(final List<Block> blocks) {

		for (final Block block : blocks) {

			if (Utils.isWasteBin(block, plugin.getDescription().getName())) {
				plugin.removeWasteBinHopper((Hopper) block.getState());
			}

			if (Utils.isValidForChair(block)) {

				Utils.doForNearbyZordanPigs(block.getWorld(), block.getLocation(), pig, (p) -> {

					p.remove();
					return true;

				});
			}
		}
	}
}
