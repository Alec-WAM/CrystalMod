package alec_wam.CrystalMod.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.container.ContainerMessageBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
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
			PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
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
	
	public static <T extends TileEntity> List<T> searchBoxForTiles(final World world, final AxisAlignedBB area, final Class<T> tileClazz, List<T> list) {
        final int x0 = (int)Math.floor(area.minX) >> 4;
        final int x2 = (int)Math.ceil(area.maxX) >> 4;
        final int z0 = (int)Math.floor(area.minZ) >> 4;
        final int z2 = (int)Math.ceil(area.maxZ) >> 4;
        if (list == null) {
            list = (List<T>)Lists.<T>newArrayList();
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
	
}
