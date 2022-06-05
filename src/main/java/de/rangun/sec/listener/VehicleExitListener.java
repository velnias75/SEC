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

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * @author heiko
 *
 */
public final class VehicleExitListener extends AbstractListener {

	public VehicleExitListener(final Plugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onVehicleExitEvent(final VehicleExitEvent event) {

		if (event.getVehicle().getPersistentDataContainer().has(pig, PersistentDataType.BYTE)) {

			for (final Entity p : event.getVehicle().getPassengers()) {
				p.teleport(p.getLocation().add(0.0f, 1.5f, 0.0f));
			}

			event.getVehicle().remove();
			event.setCancelled(true);
		}
	}
}
