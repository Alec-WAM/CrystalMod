package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;

public abstract class TileWorksiteBounded extends TileWorksiteBase {

	public BlockPos bbMin;
	public BlockPos bbMax;
	
	@Override
	public EnumSet<WorksiteUpgrade> getValidUpgrades()
	{
		return EnumSet.of(
				WorksiteUpgrade.ENCHANTED_TOOLS_1,
				WorksiteUpgrade.ENCHANTED_TOOLS_2,
				WorksiteUpgrade.ENCHANTED_TOOLS_3,
				WorksiteUpgrade.SIZE_MEDIUM,
				WorksiteUpgrade.SIZE_LARGE,
				WorksiteUpgrade.TOOL_QUALITY_1,
				WorksiteUpgrade.TOOL_QUALITY_2,
				WorksiteUpgrade.TOOL_QUALITY_3,
				WorksiteUpgrade.BASIC_CHUNK_LOADER
				);
	}
	
	@Override
	public final boolean hasWorkBounds()
	{
		return true;
	}
	
	@Override
	public final BlockPos getWorkBoundsMin()
	{
		return bbMin;
	}

	@Override
	public final BlockPos getWorkBoundsMax()
	{
		return bbMax;
	}
	
	@Override
	public final void setBounds(BlockPos min, BlockPos max)
	{  
		setWorkBoundsMin(BlockUtil.getMin(min, max));
		setWorkBoundsMax(BlockUtil.getMax(min, max));
		onBoundsSet();
	}
	
	@Override
	public int getBoundsMaxWidth()
	{
		return getUpgrades().contains(WorksiteUpgrade.SIZE_MEDIUM) ? 9 : getUpgrades().contains(WorksiteUpgrade.SIZE_LARGE) ? 16 : 5;
	}
	
	@Override
	public int getBoundsMaxHeight(){return 1;}
	
	/**
	 * Used by user-set-blocks tile to set all default harvest-checks to true when bounds are FIRST set 
	 */
	protected void onBoundsSet()
	{
	  
	}
	
	public void onBoundsAdjusted()
	{
	  
	}

	public boolean isInBounds(BlockPos pos)
	{
		return pos.getX() >= bbMin.getX() && pos.getX() <= bbMax.getX() && pos.getZ() >= bbMin.getZ() && pos.getZ() <= bbMax.getZ();
	}
	
	protected void validateCollection(Collection<BlockPos> blocks)
	{
		Iterator<BlockPos> it = blocks.iterator();
		BlockPos pos;
		while(it.hasNext() && (pos=it.next())!=null)
		{
			if(!isInBounds(pos)){it.remove();}
		}
	}
	
	public final void setWorkBoundsMin(BlockPos min)
	{
		bbMin = min;
	}

	public final void setWorkBoundsMax(BlockPos max)
	{
		bbMax = max;
	}
	
	public void update(){
		if(this.bbMax == null){
			this.bbMax = pos.add(1, 0, 1);
		}
		if(this.bbMin == null){
			this.bbMin = pos;
		}
		super.update();
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag)
	{
		super.readCustomNBT(tag);
		if(tag.hasKey("bbMin"))
		{
			NBTTagCompound min = tag.getCompoundTag("bbMin");
			bbMin = BlockUtil.loadBlockPos(min);
		}
		if(bbMin == null){
			bbMin = getPos();
		}
		if(tag.hasKey("bbMax"))
		{
			NBTTagCompound max = tag.getCompoundTag("bbMax");
			bbMax = BlockUtil.loadBlockPos(max);
		} 
		if(bbMax == null){
			bbMax = getPos().add(1, 0, 1);
		}
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound tag)
	{
		super.writeCustomNBT(tag);
		tag.setTag("bbMin", BlockUtil.saveBlockPos(bbMin));
		tag.setTag("bbMax", BlockUtil.saveBlockPos(bbMax));
	}


}
