package alec_wam.CrystalMod.tiles.spawner;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityCustomSpawner extends TileEntityMod {

	public boolean isSetToSpawn = false;
	
	private final CustomSpawnerBaseLogic spawnerBaseLogic = new CustomSpawnerBaseLogic(){
		@Override
		public void blockEvent(int par1)
		{
			getWorld().addBlockEvent(getPos(), ModBlocks.customSpawner, par1, 0);
		}
		@Override
		public World getSpawnerWorld()
		{
			return getWorld();
		}
		@Override
		public BlockPos getSpawnerPos()
		{
			return getPos();
		}
	};
	
	@Override
	public void update(){
		super.update();
		if (isSetToSpawn) {
			spawnerBaseLogic.updateSpawner();
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound tagCompound){
		super.writeCustomNBT(tagCompound);
		spawnerBaseLogic.writeToNBT(tagCompound);
		tagCompound.setBoolean("Running", isSetToSpawn);
	}

	@Override
	public void readCustomNBT(NBTTagCompound tagCompound){
		super.readCustomNBT(tagCompound);
		spawnerBaseLogic.readFromNBT(tagCompound);
		isSetToSpawn = tagCompound.getBoolean("Running");
	}
	
	public CustomSpawnerBaseLogic getBaseLogic()
	{
		return spawnerBaseLogic;
	}
	
}
