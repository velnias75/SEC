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

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;

import com.google.common.collect.ImmutableSet;

/**
 * @author heiko
 *
 */
final class ChairCandidateChecker {

	private static final Set<Material> UNSAFEMATERIAL = ImmutableSet.of(Material.GLOWSTONE);

	private ChairCandidateChecker() {
	}

	public static boolean isValidForChair(final Block block) {

		if (!(block.getBlockData() instanceof Stairs)) {
			return false; // NOPMD by heiko on 05.06.22, 06:58
		}

		final Stairs stair = (Stairs) block.getBlockData();

		return Shape.STRAIGHT.equals(stair.getShape()) && Half.BOTTOM.equals(stair.getHalf())
				&& ((block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ()) // NOPMD by heiko on
																								// 05.06.22, 06:58
						.getBlockPower(BlockFace.UP) > 0
						|| isActiveTorch(block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ())))
						&& isSaveBlock(block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ()))
						&& isSaveBlock(block.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ())));
	}

	private static boolean isNotOccludingUnsave(final Block block) {
		return UNSAFEMATERIAL.contains(block.getType());
	}

	private static boolean isSaveBlock(final Block block) {
		return block.isEmpty() || block.isLiquid() || !(block.getType().isOccluding() || isNotOccludingUnsave(block));
	}

	private static boolean isActiveTorch(final Block block) {
		return Material.REDSTONE_TORCH.equals(block.getType()) && ((Lightable) block.getBlockData()).isLit();
	}
}
