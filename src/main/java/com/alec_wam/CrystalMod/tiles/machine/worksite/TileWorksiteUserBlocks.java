package com.alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public abstract class TileWorksiteUserBlocks extends TileWorksiteBlockBased {

	private byte[] targetMap = new byte[16 * 16];

	public TileWorksiteUserBlocks() {

	}

	@Override
	public boolean userAdjustableBlocks() {
		return true;
	}

	protected boolean isTarget(BlockPos p) {
		int x = p.getX() - bbMin.getX();
		int z = p.getZ() - bbMin.getZ();
		return targetMap[z * 16 + x] == 1;
	}

	protected boolean isTarget(int x1, int y1) {
		int x = x1 - bbMin.getX();
		int z = y1 - bbMin.getZ();
		return targetMap[z * 16 + x] == 1;
	}

	@Override
	protected void validateCollection(Collection<BlockPos> blocks) {
		Iterator<BlockPos> it = blocks.iterator();
		BlockPos pos;
		while (it.hasNext() && (pos = it.next()) != null) {
			if (!isInBounds(pos)) {
				it.remove();
			} else if (!isTarget(pos)) {
				it.remove();
			}
		}
	}

	public void onTargetsAdjusted() {
		
	}

	@Override
	protected void onBoundsSet() {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				targetMap[z * 16 + x] = (byte) 1;
			}
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound tag) {
		super.writeCustomNBT(tag);
		tag.setByteArray("targetMap", targetMap);
	}

	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		super.readCustomNBT(tag);
		if (tag.hasKey("targetMap")
				&& tag.getTag("targetMap") instanceof NBTTagByteArray) {
			targetMap = tag.getByteArray("targetMap");
		}
	}

	public byte[] getTargetMap() {
		return targetMap;
	}

	public void setTargetBlocks(byte[] targets) {
		targetMap = targets;
	}

}
