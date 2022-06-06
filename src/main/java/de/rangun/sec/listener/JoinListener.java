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

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.rangun.spiget.MessageRetriever;

/**
 * @author heiko
 *
 */
public final class JoinListener implements Listener {

	private final String pluginName;
	private final MessageRetriever msgs;

	public JoinListener(final String pluginName, MessageRetriever msgs) {

		super();

		this.pluginName = pluginName;
		this.msgs = msgs;
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {

		if (event.getPlayer().isOp()) {

			for (final String jm : msgs.getJoinMessages()) {
				event.getPlayer().sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + "[" // NOPMD by heiko on
																								// 05.06.22, 13:57
						+ pluginName + ": " + jm + "]");
			}
		}
	}
}
