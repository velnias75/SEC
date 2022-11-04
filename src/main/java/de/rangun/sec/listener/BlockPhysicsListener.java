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

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.Plugin;

import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class BlockPhysicsListener extends AbstractListener {

	public BlockPhysicsListener(final Plugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockPhysicsEvent(final BlockPhysicsEvent event) {

		if (event.isCancelled()) {
			return;
		}

		final Block testBlock = event.getBlock();
		final World world = testBlock.getWorld();
		final Block chairCandidate = world.getBlockAt(testBlock.getLocation().add(0d, 1d, 0d));

		if (!testBlock.isBlockFacePowered(BlockFace.UP)) {

			if (Utils.isValidForChair(chairCandidate, (block) -> { // NOPMD by heiko on 04.11.22, 11:25

				return true;

			})) {

				Utils.doForNearbyZordanPigs(world, chairCandidate.getLocation(), pig, (p) -> {

					for (final Entity ent : p.getPassengers()) {
						ent.teleport(ent.getLocation().add(0.0f, 1.5f, 0.0f));
					}

					p.remove();

					return true;

				});
			}
		}
	}
}
