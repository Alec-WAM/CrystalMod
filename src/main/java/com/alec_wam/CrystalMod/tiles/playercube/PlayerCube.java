package com.alec_wam.CrystalMod.tiles.playercube;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.util.NBTUtil;
import com.alec_wam.CrystalMod.util.UUIDUtils;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

public class PlayerCube  implements IInventoryChangedListener
{
	GameProfile owner;

	CubeManager manager;

	InventoryBasic cubeInventory = new InventoryBasic("PlayerCube", false, 2 + 9);

	BlockPos spawnBlock;
	
	BlockPos minBlock;
	
	boolean chunkLoaded;
	
	public String name;
	
	public List<TileEntityPlayerCubePortal> watchers = Lists.newArrayList();

	public PlayerCube(CubeManager manager)
	{
		this.manager = manager;
	}

	public PlayerCube(CubeManager manager, GameProfile owner, String ID, BlockPos corner)
	{
		this(manager);

		this.owner = owner;

		spawnBlock = corner.add(8, 0, 8);
		
		minBlock = corner;
		
		name = ID;
	}

	public InventoryBasic getInventory()
	{
		return cubeInventory;
	}

	public void writeToNBT(NBTTagCompound compound)
	{
		compound.setString("CubeName", name);
		if(this.owner !=null){
			compound.setString("OwnerName", owner.getName());
			compound.setString("OwnerUUID", UUIDUtils.fromUUID(owner.getId()));
		}
		compound.setBoolean("chunkLoaded", chunkLoaded);
		NBTUtil.writeBlockPosToNBT(compound, "spawnBlock", spawnBlock);
		NBTUtil.writeBlockPosToNBT(compound, "minBlock", minBlock);
	}

	public void readFromNBT(NBTTagCompound compound)
	{
		this.name = compound.getString("CubeName");
		if(compound.hasKey("OwnerName") && compound.hasKey("OwnerUUID")){
			owner = new GameProfile(UUIDUtils.fromString(compound.getString("OwnerUUID")), compound.getString("OwnerName"));
		}
		this.chunkLoaded = compound.getBoolean("chunkLoaded");
		this.spawnBlock = NBTUtil.readBlockPosFromNBT(compound, "spawnBlock");
		this.minBlock = NBTUtil.readBlockPosFromNBT(compound, "minBlock");
	}

	public GameProfile getOwner()
	{
		return owner;
	}

	public void generate(World worldObj)
	{
		BlockPos pos1 = minBlock;
		BlockPos pos2 = minBlock.add(15, 15, 15);

		generateCube(worldObj, pos1, pos2, ModBlocks.cubeBlock.getDefaultState(), 3);
		generateCube(worldObj, pos1.add(7, 0, 7), pos1.add(8, 0, 8), ModBlocks.cubeCore.getDefaultState(), 3);
	}

	public BlockPos getSpawnBlock()
	{
		return spawnBlock;
	}

	private static void generateCube(World worldObj, BlockPos pos1, BlockPos pos2, IBlockState state, int flag)
	{
		int minX = Math.min(pos1.getX(), pos2.getX());
		int minY = Math.min(pos1.getY(), pos2.getY());
		int minZ = Math.min(pos1.getZ(), pos2.getZ());

		int maxX = Math.max(pos1.getX(), pos2.getX());
		int maxY = Math.max(pos1.getY(), pos2.getY());
		int maxZ = Math.max(pos1.getZ(), pos2.getZ());

		for (int x = minX; x <= maxX; x++)
		{
			for (int y = minY; y <= maxY; y++)
			{
				for (int z = minZ; z <= maxZ; z++)
				{
					if (x == minX || y == minY || z == minZ || x == maxX || y == maxY || z == maxZ)
					{
						worldObj.setBlockState(new BlockPos(x, y, z), state, flag);
					}
				}
			}
		}
	}

	@Override
	public void onInventoryChanged(InventoryBasic inventory)
	{
		this.manager.markDirty();
	}

	public boolean isChunkLoaded() {
		return chunkLoaded;
	}
	
	public List<ChunkPos> getChunkCoords() {
		List<ChunkPos> list = Lists.newArrayList();
		final int x = this.spawnBlock.getX() >> 4;
        final int z = this.spawnBlock.getZ() >> 4;
		list.add(new ChunkPos(x, z));
		return list;
	}
}
