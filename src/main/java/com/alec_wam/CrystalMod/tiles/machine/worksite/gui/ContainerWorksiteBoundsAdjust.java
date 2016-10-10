package com.alec_wam.CrystalMod.tiles.machine.worksite.gui;

import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteUserBlocks;
import com.alec_wam.CrystalMod.util.BlockUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class ContainerWorksiteBoundsAdjust extends ContainerWorksiteBase {

	public BlockPos pos, min, max;
	public TileWorksiteBase worksite;

	public ContainerWorksiteBoundsAdjust(EntityPlayer player, TileWorksiteBase worksite) {
		super(player, worksite.getPos());
		pos = worksite.getPos();
		this.worksite = worksite;
		min = new BlockPos(worksite.getWorkBoundsMin());
		max = new BlockPos(worksite.getWorkBoundsMax());
	}

	@Override
	public void sendInitData() {
		if (worksite instanceof TileWorksiteUserBlocks) {
			TileWorksiteUserBlocks twub = (TileWorksiteUserBlocks) worksite;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setByteArray("checkedMap", twub.getTargetMap());
			sendDataToGui(tag);
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("guiClosed")) {
			if (tag.hasKey("min") && tag.hasKey("max")) {
				BlockPos min = BlockUtil.loadBlockPos(tag
						.getCompoundTag("min"));
				BlockPos max = BlockUtil.loadBlockPos(tag
						.getCompoundTag("max"));
				worksite.setWorkBoundsMin(min);
				worksite.setWorkBoundsMax(max);
				worksite.onBoundsAdjusted();
				worksite.onPostBoundsAdjusted();
			}
			if (tag.hasKey("checkedMap")
					&& worksite instanceof TileWorksiteUserBlocks) {
				TileWorksiteUserBlocks twub = (TileWorksiteUserBlocks) worksite;
				byte[] map = tag.getByteArray("checkedMap");
				twub.setTargetBlocks(map);
				twub.onTargetsAdjusted();
			}
			BlockUtil.markBlockForUpdate(worksite.getWorld(), worksite.getPos());
		}
	}

}
