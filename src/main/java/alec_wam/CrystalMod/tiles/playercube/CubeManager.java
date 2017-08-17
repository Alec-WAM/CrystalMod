package alec_wam.CrystalMod.tiles.playercube;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.world.ModDimensions;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CubeManager extends WorldSavedData {

	public static final String ID = "PlayerCubeManager";
	
	private World worldObj;

	public HashMap<GameProfile, List<PlayerCube>> cubes;
	public HashMap<GameProfile, BlockPos> positions;
	
	public CubeManager(String name) {
		super(name);
		
		cubes = new HashMap<GameProfile, List<PlayerCube>>();
		positions = new HashMap<GameProfile, BlockPos>();
		this.worldObj = DimensionManager.getWorld(ModDimensions.CUBE_ID);
	}
	
	public CubeManager()
	{
		this(ID);

		cubes = new HashMap<GameProfile, List<PlayerCube>>();
		positions = new HashMap<GameProfile, BlockPos>();
		this.worldObj = DimensionManager.getWorld(ModDimensions.CUBE_ID);
	}
	
	public PlayerCube getPlayerCubeFromPos(World worldObj, BlockPos pos)
	{
		if (worldObj.provider.getDimension() != ModDimensions.CUBE_ID)
		{
			return null;
		}
		else
		{
			for (List<PlayerCube> cubeLists : cubes.values())
			{
				for(PlayerCube cube : cubeLists){
					if(pos.getX() > cube.minBlock.getX() && pos.getX() < cube.minBlock.getX()+15){
						if(pos.getZ() > cube.minBlock.getZ() && pos.getZ() < cube.minBlock.getZ()+15){
							if(pos.getY() > 0 && pos.getY() < 15){
								return cube;
							}
						}
					}
				}
			}

			return null;
		}
	}

	public PlayerCube getCube(GameProfile uuid, String ID){
		for(PlayerCube cube : getCubes(uuid)){
			if(cube.name.equals(ID)){
				return cube;
			}
		}
		return null;
	}
	
	public List<PlayerCube> getCubes(GameProfile uuid){
		if (cubes.containsKey(uuid))
		{
			if(cubes.get(uuid) == null){
				List<PlayerCube> cubes2 = Lists.newArrayList();
				cubes.put(uuid, cubes2);
				this.markDirty();
			}
			return cubes.get(uuid);
		}
		else
		{
			List<PlayerCube> cubes2 = Lists.newArrayList();
			cubes.put(uuid, cubes2);
			this.markDirty();
			return cubes.get(uuid);
		}
	}
	
	public String teleportPlayerToPlayerCube(EntityPlayerMP player, GameProfile uuid, String ID)
	{
		// Save Old Position / Dimension
		NBTTagCompound compound = PlayerUtil.getPersistantNBT(player);
		compound.setDouble("playerCubePosX", player.posX);
		compound.setDouble("playerCubePosY", player.posY);
		compound.setDouble("playerCubePosZ", player.posZ);
		compound.setInteger("playerCubeDimension", player.dimension);

		PlayerCube playerCube = getCube(uuid, ID);
		
		if(playerCube == null){
			PlayerCube gen = generatePlayerCube(uuid, ID);
			if(gen == null) return "TooManyCubes";
			playerCube = gen;
		}
		
		BlockPos spawn = playerCube.getSpawnBlock();

		if (player.dimension != ModDimensions.CUBE_ID)
		{
			PlayerUtil.teleportPlayerToDimension(player, ModDimensions.CUBE_ID);
		}
		player.connection.setPlayerLocation(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, player.rotationYaw, player.rotationPitch);
		return "Passed";
	}

	private PlayerCube generatePlayerCube(GameProfile uuid, String ID)
	{
		if(Config.playerCubePlayerLimit == 0 || getCubes(uuid).size() >= Config.playerCubePlayerLimit){
			return null;
		}
		PlayerCube cube = new PlayerCube(this, uuid, ID, getCubePos(uuid));

		cube.generate(worldObj);
		getCubes(uuid).add(cube);
		
		increaseNextPosition(uuid);
		this.markDirty();
		return cube;
	}

	private BlockPos getCubePos(GameProfile uuid)
	{
		if(positions.containsKey(uuid)){
			return positions.get(uuid);
		}
		if(positions.isEmpty()){
			positions.put(uuid, new BlockPos(0, 0, 0));
		}else{
			positions.put(uuid, new BlockPos(0, 0, positions.size()*16));
		}
		return positions.get(uuid);
	}
	
	private void increaseNextPosition(GameProfile uuid)
	{
		if(positions.containsKey(uuid)){
			positions.put(uuid, positions.get(uuid).add(16, 0, 0));
			this.markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		cubes.clear();
		positions.clear();
		NBTTagList cubeTags = nbt.getTagList("cubes", (byte) 10);
		System.out.println("cubeTags = "+cubeTags.tagCount());
		for (int i = 0; i < cubeTags.tagCount(); i++)
		{
			NBTTagCompound cubeCompound = cubeTags.getCompoundTagAt(i);
			String name = cubeCompound.getString("OwnerName");
			UUID uuid = UUIDUtils.fromString(cubeCompound.getString("OwnerUUID"));
			GameProfile profile = new GameProfile(uuid, name);
			List<PlayerCube> cubeList = Lists.newArrayList();
			NBTTagList cubesData = cubeCompound.getTagList("cubeData", (byte) 10);
			for (int i2 = 0; i2 < cubesData.tagCount(); i2++)
			{
				NBTTagCompound cubeNBT = cubesData.getCompoundTagAt(i2);
				PlayerCube cube = new PlayerCube(this);
				cube.readFromNBT(cubeNBT);
				cubeList.add(cube);
			}
			cubes.put(profile, cubeList);
		}

		NBTTagList postionTags = nbt.getTagList("positions", (byte) 10);

		for (int i = 0; i < postionTags.tagCount(); i++)
		{
			NBTTagCompound cubeCompound = postionTags.getCompoundTagAt(i);
			String name = cubeCompound.getString("OwnerName");
			UUID uuid = UUIDUtils.fromString(cubeCompound.getString("OwnerUUID"));
			GameProfile profile = new GameProfile(uuid, name);
			BlockPos pos = BlockUtil.readBlockPosFromNBT(cubeCompound, "pos");
			positions.put(profile, pos);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList cubeTags = new NBTTagList();

		for (Entry<GameProfile, List<PlayerCube>> cube : cubes.entrySet())
		{
			if(cube.getKey() == null || cube.getValue() == null)continue;
			NBTTagCompound cubeCompound = new NBTTagCompound();
			cubeCompound.setString("OwnerName", cube.getKey().getName());
			final UUID id = cube.getKey().getId();
			cubeCompound.setString("OwnerUUID", UUIDUtils.fromUUID(id));
			NBTTagList cubesData = new NBTTagList();
			
			for(PlayerCube cub : cube.getValue()){
				NBTTagCompound cubeNBT = new NBTTagCompound();
				cub.writeToNBT(cubeNBT);
				cubesData.appendTag(cubeNBT);
			}
			cubeCompound.setTag("cubeData", cubesData);
			cubeTags.appendTag(cubeCompound);
		}

		nbt.setTag("cubes", cubeTags);

		NBTTagList cubePositions = new NBTTagList();
		
		for (Entry<GameProfile, BlockPos> cube : this.positions.entrySet())
		{
			NBTTagCompound cubeCompound = new NBTTagCompound();
			cubeCompound.setString("OwnerName", cube.getKey().getName());
			cubeCompound.setString("OwnerUUID", UUIDUtils.fromUUID(cube.getKey().getId()));
			BlockUtil.writeBlockPosToNBT(cubeCompound, "pos", cube.getValue());
			cubePositions.appendTag(cubeCompound);
		}
		
		nbt.setTag("positions", cubePositions);
		return nbt;
	}

	public static CubeManager getInstance()
	{
		WorldServer world = DimensionManager.getWorld(ModDimensions.CUBE_ID);
		if (world != null)
		{
			WorldSavedData handler = world.getPerWorldStorage().getOrLoadData(CubeManager.class, ID);
			if (handler == null)
			{
				handler = new CubeManager();
				world.getPerWorldStorage().setData(ID, handler);
			}

			return (CubeManager) handler;
		}

		return null;
	}

	public static void reset()
	{
		WorldServer world = DimensionManager.getWorld(ModDimensions.CUBE_ID);
		if (world != null)
		{
			world.getMapStorage().setData(ID, new CubeManager());
		}
	}

	public void teleportPlayerBack(EntityPlayerMP player)
	{
		// Save Read Position / Dimension
		NBTTagCompound compound = PlayerUtil.getPersistantNBT(player);
		if (compound.hasKey("playerCubePosX"))
		{
			double spectrePosX = compound.getDouble("playerCubePosX");
			double spectrePosY = compound.getDouble("playerCubePosY");
			double spectrePosZ = compound.getDouble("playerCubePosZ");
			int spectreDimension = compound.getInteger("playerCubeDimension");

			if (player.dimension != spectreDimension)
			{
				PlayerUtil.teleportPlayerToDimension(player, spectreDimension);
			}
			player.connection.setPlayerLocation(spectrePosX, spectrePosY, spectrePosZ, player.rotationYaw, player.rotationPitch);
		}
		else
		{
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().recreatePlayerEntity(player, player.dimension, true);
		}
	}

	/*public void checkPosition(EntityPlayerMP player)
	{
		PlayerCube cube = getPlayerCubeFromPos(player.worldObj, player.getPosition());

		if (!player.capabilities.isCreativeMode && (cube == null || !cube.getOwner().equals(player.getGameProfile().getId())))
		{
			PlayerCube playerCube = cubes.get(player.getGameProfile().getId());

			if (playerCube != null)
			{
				BlockPos spawn = playerCube.getSpawnBlock();
				player.playerNetServerHandler.setPlayerLocation(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, player.rotationYaw, player.rotationPitch);
			}
			else
			{
				teleportPlayerBack(player);
			}
		}
	}*/

	public World getWorld()
	{
		return worldObj;
	}

}
