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

package de.rangun.sec.utils;

import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.ImmutableSet;

/**
 * @author heiko
 *
 */
public final class Utils {

	private static final Set<Material> UNSAFEMATERIAL = ImmutableSet.of(Material.GLOWSTONE);

	private Utils() {
	}

	public static Location getPigLocation(final Block block) {
		return block.getLocation().add(0.5d, -0.5d, 0.5d);
	}

	public static boolean isValidForChair(final Block block) {

		return isValidForChair(block, (p) -> {

			final World world = p.getWorld();

			final int blockX = p.getX();
			final int blockY = p.getY();
			final int blockZ = p.getZ();

			return world.getBlockAt(blockX, blockY - 1, blockZ).isBlockFacePowered(BlockFace.UP);
		});
	}

	public static boolean isValidForChair(final Block block, final Predicate<Block> powerCheck) {

		final BlockData blockData = block.getBlockData();

		if (!(blockData instanceof Stairs)) {
			return false; // NOPMD by heiko on 05.06.22, 06:58
		}

		final Stairs stair = (Stairs) blockData;

		final World world = block.getWorld();

		final int blockX = block.getX();
		final int blockY = block.getY();
		final int blockZ = block.getZ();

		return Shape.STRAIGHT.equals(stair.getShape()) && Half.BOTTOM.equals(stair.getHalf())
				&& (powerCheck.test(block) || isActiveTorch(world.getBlockAt(blockX, blockY - 1, blockZ)))
				&& isSaveBlock(world.getBlockAt(blockX, blockY + 1, blockZ))
				&& isSaveBlock(world.getBlockAt(blockX, blockY + 2, blockZ));
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

	public static boolean isWasteBin(final Block block, final String pluginName) {

		return Material.HOPPER.equals(block.getType()) && ((Hopper) block.getState()).getCustomName() != null
				&& ("[" + pluginName + "] ") // NOPMD by heiko on 13.06.22, 15:34
						.equals(((Hopper) block.getState()).getCustomName().substring(0, pluginName.length() + 3));
	}

	public static Location removeNearbyZordanPigs(final World world, final Location location, final NamespacedKey pig) {

		return doForNearbyZordanPigs(world, location, pig, (p) -> {

			p.remove();
			return true;

		});
	}

	public static Location doForNearbyZordanPigs(final World world, final Location location, final NamespacedKey pig,
			final Predicate<Entity> consumer) {

		for (final Entity ent : world.getNearbyEntities(location, 1d, 1d, 1d,
				(entity) -> EntityType.PIG.equals(entity.getType())
						&& entity.getPersistentDataContainer().has(pig, PersistentDataType.BYTE))) {

			if (consumer != null && consumer.test(ent)) {
				return ent.getLocation(); // NOPMD by heiko on 05.11.22, 05:21
			}
		}

		return null;
	}
}
