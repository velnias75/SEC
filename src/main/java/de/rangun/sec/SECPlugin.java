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

package de.rangun.sec;

import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import de.rangun.sec.listener.BlockPlaceListener;
import de.rangun.sec.listener.JoinListener;
import de.rangun.sec.listener.PlayerInteractListener;
import de.rangun.sec.listener.VehicleExitListener;
import de.rangun.spiget.PluginClient;

public final class SECPlugin extends JavaPlugin { // NOPMD by heiko on 05.06.22, 07:22

	private final PluginClient spigetClient = new PluginClient(102446, getDescription().getVersion(), // NOPMD by heiko
																										// on 05.06.22,
																										// 13:56
			getDescription().getName(), getLogger());

	@Override
	public void onEnable() {

		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new VehicleExitListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
		getServer().getPluginManager().registerEvents(new JoinListener(this), this);

		final int pluginId = 15388; // NOPMD by heiko on 05.06.22, 13:56
		new Metrics(this, pluginId);

		spigetClient.checkVersion();
	}

	public List<String> getJoinMessages() {
		return spigetClient.getJoinMessages();
	}
}
