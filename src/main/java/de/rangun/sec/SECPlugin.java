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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.rangun.sec.commands.SECCommand;
import de.rangun.sec.gui.ConfigGUI;
import de.rangun.sec.gui.ConfigGUICallback;
import de.rangun.sec.listener.BlockBreakExplodeListener;
import de.rangun.sec.listener.BlockPlaceListener;
import de.rangun.sec.listener.JoinListener;
import de.rangun.sec.listener.PlayerDeathListener;
import de.rangun.sec.listener.PlayerInteractListener;
import de.rangun.sec.listener.VehicleExitListener;
import de.rangun.sec.listener.WasteBinListener;
import de.rangun.sec.utils.Utils;
import de.rangun.spiget.PluginClient;

public final class SECPlugin extends JavaPlugin implements ConfigGUICallback { // NOPMD by heiko on 25.06.22, 09:13

	private ConfigGUI configGui;

	private FileConfiguration config;

	private final static Map<String, SECWasteBin> WASTEBINS = new ConcurrentHashMap<>();

	private static final class SECWasteBin implements InventoryHolder {

		private final Inventory inv;
		private final String name;
		private final Set<Hopper> wasteBinHoppers = new HashSet<>();

		public SECWasteBin(final Plugin plugin, final String name) {

			this.name = !ChatColor.stripColor(name).isEmpty() ? ChatColor.translateAlternateColorCodes('&', name) // NOPMD
																													// by
																													// heiko
																													// on
																													// 14.06.22,
																													// 04:22
					: "" + ChatColor.RESET + ChatColor.DARK_RED + ChatColor.BOLD + plugin.getDescription().getName() // NOPMD
																														// by
																														// heiko
																														// on
																														// 14.06.22,
																														// 04:22
							+ " by " + String.join(", ", plugin.getDescription().getAuthors());

			this.inv = Bukkit.createInventory(this, 54, this.name);

			final long ticksToClean = 36000L; // NOPMD by heiko on 13.06.22, 15:40

			(new BukkitRunnable() {

				@Override
				public void run() {

					if (inv.isEmpty()) {
						return;
					}

					for (final Hopper hopper : wasteBinHoppers) {
						hopper.getWorld().spawnParticle(Particle.SPELL, hopper.getLocation().add(0.5d, 1.0d, 0.5d), 1);
					}

					inv.clear();
				}

			}).runTaskTimer(plugin, ticksToClean, ticksToClean);
		}

		@Override
		public Inventory getInventory() {
			return inv;
		}

		private void addWasteBinHopper(final Hopper hopper) {
			wasteBinHoppers.add(hopper);
		}

		private void removeWasteBinHopper(final Hopper hopper) { // NOPMD by heiko on 13.06.22, 15:40
			wasteBinHoppers.remove(hopper);
		}
	}

	private final PluginClient spigetClient = new PluginClient(102446, getDescription().getVersion(), // NOPMD by heiko
																										// on 05.06.22,
																										// 13:56
			getDescription().getName(), getLogger());

	@Override
	public void onEnable() {

		saveResource("config.yml", false);

		config = getConfig();
		configGui = new ConfigGUI(this);

		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new VehicleExitListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockBreakExplodeListener(this), this);
		getServer().getPluginManager().registerEvents(new JoinListener(spigetClient), this);
		getServer().getPluginManager().registerEvents(new WasteBinListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
		getServer().getPluginManager().registerEvents(getConfigGUI(), this);

		final SECCommand sec = new SECCommand(this);

		getCommand("sec").setExecutor(sec);
		getCommand("sec").setTabCompleter(sec);

		final int pluginId = 15388; // NOPMD by heiko on 05.06.22, 13:56
		new Metrics(this, pluginId);

		new BukkitRunnable() {

			@Override
			public void run() {
				spigetClient.checkVersion();
			}

		}.runTaskAsynchronously(this);
	}

	public void removeWasteBinHopper(final Hopper hopper) {

		final SECWasteBin wasteBin = WASTEBINS.get(normalizedWasteBinName(hopper.getCustomName()));

		if (wasteBin != null) {
			wasteBin.removeWasteBinHopper(hopper);
		}
	}

	public Inventory getWasteBin(final Hopper hopper) {

		if (!Utils.isWasteBin(hopper.getBlock(), getDescription().getName())) {
			throw new IllegalStateException("Hopper is not a waste bin hopper");
		}

		final String binName = normalizedWasteBinName(hopper.getCustomName());

		WASTEBINS.putIfAbsent(binName, new SECWasteBin(this, "" + ChatColor.GOLD + ChatColor.BOLD // NOPMD
																									// by
																									// heiko
																									// on
																									// 13.06.22,
																									// 15:41
				+ hopper.getCustomName().substring(getDescription().getName().length() + 3)));

		final SECWasteBin bin = WASTEBINS.get(binName);

		bin.addWasteBinHopper(hopper);

		return bin.getInventory();
	}

	private String normalizedWasteBinName(final String name) {
		// return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
		// name));
		return name;
	}

	public ConfigGUI getConfigGUI() {
		return configGui;
	}

	@Override
	public void setChairsEnabled(final Boolean enabled) {
		config.set("chairs_enabled", enabled);
	}

	@Override
	public void setWasteBinEnabled(final Boolean enabled) {
		config.set("wastebins_enabled", enabled);
	}

	@Override
	public boolean isChairsEnabled() {
		return config.getBoolean("chairs_enabled", true);
	}

	@Override
	public boolean isWasteBinsEnabled() {
		return config.getBoolean("wastebins_enabled", false);
	}
}
