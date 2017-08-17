package alec_wam.CrystalMod.util;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.container.ContainerMessageBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public class BlockUtil {
	
	public static BlockPos getMin(BlockPos pos1, BlockPos pos2)
	{
		BlockPos pos = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
		return pos;
	}
	
	public static BlockPos getMax(BlockPos pos1, BlockPos pos2)
  	{
		BlockPos pos = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
		return pos;
  	}
	
	public static BlockPos moveRight(BlockPos pos, EnumFacing facing, int amt)
    {
		return offsetForHorizontalDirection(pos, facing.rotateY(), amt);
    }

	public static BlockPos moveBack(BlockPos pos, EnumFacing facing, int amt)
    {
	    return offsetForHorizontalDirection(pos, facing.getOpposite(), amt);
    }

	public static BlockPos moveLeft(BlockPos pos, EnumFacing facing, int amt)
    {
		return offsetForHorizontalDirection(pos, facing.rotateYCCW(), amt);
    }

	public static BlockPos moveForward(BlockPos pos, EnumFacing facing, int amt)
    {
		return offsetForHorizontalDirection(pos, facing, amt);
    }

	public static BlockPos offsetForHorizontalDirection(BlockPos pos, EnumFacing facing, int amt)
    {
		return pos.add(facing.getFrontOffsetX() * amt, facing.getFrontOffsetY() * amt, facing.getFrontOffsetZ() * amt);
    }

	public static void markBlockForUpdate(World world, BlockPos pos){
		markBlockForUpdate(world, pos, false);
	}
	
	public static void markBlockForUpdate(World world, BlockPos pos, boolean notifyObv){
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.notifyNeighborsOfStateChange(pos, state.getBlock(), notifyObv);
	}

	public static void openWorksiteGui(EntityPlayer player, int id, int x, int y, int z) {
		if(player.getEntityWorld().isRemote)
	    {
			PacketGuiMessage pkt = new PacketGuiMessage("Gui");
			pkt.setOpenGui(id, x, y, z);
			CrystalModNetwork.sendToServer(pkt);
	    }
		else
	    {
		    player.openGui(CrystalMod.instance, id, player.getEntityWorld(), x, y, z);
		    if(player.openContainer instanceof ContainerMessageBase)
		    {
		    	((ContainerMessageBase)player.openContainer).sendInitData();
		    }
	    }
	}

	public static NBTTagCompound saveBlockPos(BlockPos pos) {
		BlockPos realPos = pos == null ? BlockPos.ORIGIN : pos;
		NBTTagCompound compoundTag = new NBTTagCompound();
		compoundTag.setInteger("x", realPos.getX());
		compoundTag.setInteger("y", realPos.getY());
		compoundTag.setInteger("z", realPos.getZ());
		return compoundTag;
	}
	
	public static BlockPos loadBlockPos(NBTTagCompound compoundTag) {
		int x = compoundTag.getInteger("x");
		int y = compoundTag.getInteger("y");
		int z = compoundTag.getInteger("z");
		return new BlockPos(x, y, z);
	}

	//https://github.com/SlimeKnights/TinkersConstruct/blob/master/src/main/java/slimeknights/tconstruct/library/utils/ToolHelper.java
	public static void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
		if(world.isAirBlock(pos)) {
			return;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if(!ToolUtil.isToolEffective(stack, state)) {
			return;
		}

		IBlockState refState = world.getBlockState(refPos);
		float refStrength = ForgeHooks.blockStrength(refState, player, world, refPos);
		float strength = ForgeHooks.blockStrength(state, player, world, pos);

		if(!ForgeHooks.canHarvestBlock(block, player, world, pos) || refStrength / strength > 10f) {
			return;
		}

		if(player.capabilities.isCreativeMode) {
			block.onBlockHarvested(world, pos, state, player);
			if(block.removedByPlayer(state, world, pos, player, false)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}

			if(!world.isRemote) {
				CrystalModNetwork.sendMCPacket(player, new SPacketBlockChange(world, pos));
			}
			return;
		}

		stack.onBlockDestroyed(world, state, pos, player);

		if(!world.isRemote) {
			int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
			if(xp == -1) {
				return;
			}


			TileEntity tileEntity = world.getTileEntity(pos);
			if(block.removedByPlayer(state, world, pos, player, true))
			{
				block.onBlockDestroyedByPlayer(world, pos, state);
				block.harvestBlock(world, player, pos, state, tileEntity, stack);
				block.dropXpOnBlockBreak(world, pos, xp);
			}

			CrystalModNetwork.sendMCPacket(player, new SPacketBlockChange(world, pos));
		}
		else {
			Minecraft.getMinecraft();
			world.playBroadcastSound(2001, pos, Block.getStateId(state));
			if(block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}
			stack.onBlockDestroyed(world, state, pos, player);

			if(ItemStackTools.getStackSize(stack) == 0 && stack == player.getHeldItemMainhand()) {
				ForgeEventFactory.onPlayerDestroyItem(player, stack, EnumHand.MAIN_HAND);
				player.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
			}

			Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft
					.getMinecraft().objectMouseOver.sideHit));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> T searchBoxForTile(final World world, final AxisAlignedBB area, final Class<T> tileClazz) {
		final int x0 = (int)Math.floor(area.minX) >> 4;
        final int x2 = (int)Math.ceil(area.maxX) >> 4;
        final int z0 = (int)Math.floor(area.minZ) >> 4;
        final int z2 = (int)Math.ceil(area.maxZ) >> 4;
        for (int x3 = x0; x3 <= x2; ++x3) {
            for (int z3 = z0; z3 <= z2; ++z3) {
                final Chunk chunk = world.getChunkFromChunkCoords(x3, z3);
                for (final Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
                    final BlockPos pos = entry.getKey();
                    if (tileClazz == entry.getValue().getClass() && area.isVecInside(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5))) {
                    	return (T)entry.getValue();
                    }
                }
            }
        }
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> List<T> searchBoxForTiles(final World world, final AxisAlignedBB area, final Class<T> tileClazz, List<T> list) {
        final int x0 = (int)Math.floor(area.minX) >> 4;
        final int x2 = (int)Math.ceil(area.maxX) >> 4;
        final int z0 = (int)Math.floor(area.minZ) >> 4;
        final int z2 = (int)Math.ceil(area.maxZ) >> 4;
        if (list == null) {
            list = Lists.<T>newArrayList();
        }
        for (int x3 = x0; x3 <= x2; ++x3) {
            for (int z3 = z0; z3 <= z2; ++z3) {
                final Chunk chunk = world.getChunkFromChunkCoords(x3, z3);
                for (final Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
                    final BlockPos pos = entry.getKey();
                    if (tileClazz == entry.getValue().getClass() && area.isVecInside(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5))) {
                        list.add((T)entry.getValue());
                    }
                }
            }
        }
        return list;
    }

	public static ImmutableList<BlockPos> getBlocksInBB(BlockPos start, int width, int height, int depth) {
		ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
		int x = -width/2, y = -height/2, z = -depth/2;
		for(int xp = x; xp <= width/2; xp++) {
			for(int yp = y; yp <= height/2; yp++) {
				for(int zp = z; zp <= depth/2; zp++) {
					BlockPos pos = start.add(xp, yp, zp);
					if(pos.getX() == start.getX() && pos.getY() == start.getY() && pos.getZ() == start.getZ()) {
						continue;
					}
					builder.add(pos);
				}
			}
		}

		return builder.build();
	}
	
	public static void createOrb(World world, BlockPos pos, IBlockState state, int radius, boolean hollow, boolean onlyReplace){
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					BlockPos pos2 = new BlockPos(x, y, z).add(pos.getX(), pos.getY(), pos.getZ());
					int squareDistance = (x)*(x) + (y) * (y) + (z) * (z);
					if((hollow ? squareDistance == radius : squareDistance <= radius)) {
						if(onlyReplace ? !world.isAirBlock(pos2) : true){
							world.setBlockState(pos2, state, 3);
						}
					}
				}
			}
		}
	}
	
	public static List<BlockPos> createCircle(World world, BlockPos center, int size){
		List<BlockPos> list = Lists.newArrayList();
		double circleSize = size-0.5;
		for(int x = -size; x <= size; x++){
			for(int z = -size; z <= size; z++){
				BlockPos pos2 = center.add(x, 0, z);
				double dis = pos2.getDistance(center.getX(), center.getY(), center.getZ());
				if(dis >= circleSize && dis < circleSize+1){
					list.add(pos2);
				}
			}
		}
		return list;
	}
	
	public static List<BlockPos> createOrb(World world, BlockPos center, int size){
		List<BlockPos> list = Lists.newArrayList();
		double circleSize = size-0.5;
		for(int x = -size; x <= size; x++){
			for(int y = -size; y <= size; y++){
				for(int z = -size; z <= size; z++){
					BlockPos pos2 = center.add(x, y, z);
					double dis = pos2.getDistance(center.getX(), center.getY(), center.getZ());
					if(dis >= circleSize && dis < circleSize+1){
						list.add(pos2);
					}
				}
			}
		}
		return list;
	}
	
	public static List<BlockPos> createSpecialOrb(World world, BlockPos center, int size, @Nonnull BlockFilter filter){
		List<BlockPos> list = Lists.newArrayList();
		double circleSize = size-0.5;
		for(int x = -size; x <= size; x++){
			for(int y = -size; y <= size; y++){
				for(int z = -size; z <= size; z++){
					BlockPos pos2 = center.add(x, y, z);
					IBlockState state = world.getBlockState(pos2);
					double dis = pos2.getDistance(center.getX(), center.getY(), center.getZ());
					if(dis >= circleSize && dis < circleSize+1 && filter.isValid(world, pos2, state)){
						list.add(pos2);
					}
				}
			}
		}
		return list;
	}
	
	public static abstract class BlockFilter {
		
		public abstract boolean isValid(World world, BlockPos pos, IBlockState state);
		
	}
	
	public static void writeBlockPosToNBT(NBTTagCompound compound, String tagName, BlockPos pos)
	{
		NBTTagCompound posPounds = new NBTTagCompound();
		
		posPounds.setInteger("posX", pos.getX());
		posPounds.setInteger("posY", pos.getY());
		posPounds.setInteger("posZ", pos.getZ());
		
		compound.setTag(tagName, posPounds);
	}
	
	public static BlockPos readBlockPosFromNBT(NBTTagCompound compound, String tagName)
	{
		NBTTagCompound posPounds = compound.getCompoundTag(tagName);
		
		return new BlockPos(posPounds.getInteger("posX"),posPounds.getInteger("posY"),posPounds.getInteger("posZ"));
	}

	public static String getNameForBlock(Block block) {
		Object obj = Block.REGISTRY.getNameForObject(block);
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}
	
}
