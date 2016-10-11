package alec_wam.CrystalMod.util;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksiteBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.notifyBlockOfStateChange(pos, state.getBlock());
	}

	public static void openWorksiteGui(EntityPlayer player, int id, int x, int y, int z) {
		if(player.worldObj.isRemote)
	    {
			PacketGuiMessage pkt = new PacketGuiMessage("Gui");
			pkt.setOpenGui(id, x, y, z);
			CrystalModNetwork.sendToServer(pkt);
	    }
		else
	    {
		    player.openGui(CrystalMod.instance, id, player.worldObj, x, y, z);
		    if(player.openContainer instanceof ContainerWorksiteBase)
		    {
		    	((ContainerWorksiteBase)player.openContainer).sendInitData();
		    }
	    }
	}

	public static NBTTagCompound saveBlockPos(BlockPos pos) {
		NBTTagCompound compoundTag = new NBTTagCompound();
		compoundTag.setInteger("x", pos.getX());
		compoundTag.setInteger("y", pos.getY());
		compoundTag.setInteger("z", pos.getZ());
		return compoundTag;
	}
	
	public static BlockPos loadBlockPos(NBTTagCompound compoundTag) {
		int x = compoundTag.getInteger("x");
		int y = compoundTag.getInteger("y");
		int z = compoundTag.getInteger("z");
		return new BlockPos(x, y, z);
	}
	
}
