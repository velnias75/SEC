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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class PlayerDeathListener extends AbstractListener {

	public PlayerDeathListener(final Plugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDeathEvent(final PlayerDeathEvent event) {

		final Player player = event.getEntity();

		Utils.doForNearbyZordanPigs(player.getWorld(), player.getLocation(), pig, (p) -> {

			p.remove();
			return true;

		});
	}
}
