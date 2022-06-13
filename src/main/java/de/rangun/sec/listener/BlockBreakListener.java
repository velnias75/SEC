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

import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import de.rangun.sec.SECPlugin;
import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class BlockBreakListener implements Listener {

	private final SECPlugin plugin;

	public BlockBreakListener(final SECPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreakEvent(final BlockBreakEvent event) {

		if (Utils.isWasteBin(event.getBlock(), plugin.getDescription().getName())) {
			plugin.removeWasteBinHopper((Hopper) event.getBlock().getState());
		}
	}
}
