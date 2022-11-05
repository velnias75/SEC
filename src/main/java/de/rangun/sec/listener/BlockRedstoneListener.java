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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.Plugin;

import de.rangun.sec.utils.Utils;

/**
 * @author heiko
 *
 */
public final class BlockRedstoneListener extends AbstractListener {

	public BlockRedstoneListener(final Plugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockRedstoneEvent(final BlockRedstoneEvent event) {

		final Block block = event.getBlock();

		for (final Block b : surroundingBlocks(block)) {

			final World world = b.getWorld();
			final Block chairCandidate = world.getBlockAt(b.getLocation().add(0d, 1d, 0d));

			if (Utils.isValidForChair(chairCandidate, (p) -> {
				return true;
			}) && b.isBlockFaceIndirectlyPowered(BlockFace.UP)) {

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

	private List<Block> surroundingBlocks(final Block block) {

		final ArrayList<Block> blocks = new ArrayList<Block>(BlockFace.values().length);

		for (final BlockFace face : BlockFace.values()) {

			if (face == BlockFace.UP) {

				final Block above = block.getRelative(BlockFace.UP);
				final Block above2 = above.getRelative(BlockFace.UP);

				blocks.add(above);
				blocks.add(above2);
			}

			blocks.add(block.getRelative(face));
		}

		return blocks;
	}
}
