package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;

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
		setWorkBoundsMin(new BlockPos(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ())));
		setWorkBoundsMax(new BlockPos(Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ())));
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
	
	@Override
	public void readCustomNBT(NBTTagCompound tag)
	{
		super.readCustomNBT(tag);
		if(tag.hasKey("bbMin"))
		{
			NBTTagCompound min = tag.getCompoundTag("bbMin");
			int x = min.hasKey("x") ? min.getInteger("x") : getPos().getX();
			int y = min.hasKey("y") ? min.getInteger("y") : getPos().getY();
			int z = min.hasKey("z") ? min.getInteger("z") : getPos().getZ();
			bbMin = new BlockPos(x, y, z);
		} else {
			bbMin = getPos();
		}
		if(tag.hasKey("bbMax"))
		{
			NBTTagCompound max = tag.getCompoundTag("bbMax");
			int x = max.hasKey("x") ? max.getInteger("x") : getPos().getX()+1;
			int y = max.hasKey("y") ? max.getInteger("y") : getPos().getY();
			int z = max.hasKey("z") ? max.getInteger("z") : getPos().getZ()+1;
			bbMax = new BlockPos(x, y, z);
		} else {
			bbMax = getPos().add(1, 0, 1);
		}
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound tag)
	{
		super.writeCustomNBT(tag);
		if(bbMin!=null)
		{
			NBTTagCompound innerTag = new NBTTagCompound();
			innerTag.setInteger("x", bbMin.getX());
			innerTag.setInteger("y", bbMin.getY());
			innerTag.setInteger("z", bbMin.getZ());
			tag.setTag("bbMin", innerTag);
		}
		if(bbMax!=null)
		{
			NBTTagCompound innerTag = new NBTTagCompound();
			innerTag.setInteger("x", bbMax.getX());
			innerTag.setInteger("y", bbMax.getY());
			innerTag.setInteger("z", bbMax.getZ());
			tag.setTag("bbMax", innerTag);
		}
	}


}
