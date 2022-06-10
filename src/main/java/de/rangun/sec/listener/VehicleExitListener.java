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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

		if (EntityType.PIG.equals(event.getVehicle().getType())
				&& event.getVehicle().getPersistentDataContainer().has(pig, PersistentDataType.BYTE)) {

			for (final Entity p : event.getVehicle().getPassengers()) {

				final Location safeLoc = p.getWorld().getHighestBlockAt(p.getLocation().add(0.0d, 1.5d, 0.0d))
						.getLocation().add(0.5d, 1.0d, 0.5d);

				p.teleport(safeLoc);
			}

			event.getVehicle().remove();
			event.setCancelled(true);
		}
	}
}
