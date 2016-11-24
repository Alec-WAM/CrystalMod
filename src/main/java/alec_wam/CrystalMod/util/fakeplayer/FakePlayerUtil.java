package alec_wam.CrystalMod.util.fakeplayer;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.world.DropCapture;
import alec_wam.CrystalMod.world.DropCapture.CaptureContext;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class FakePlayerUtil {

	private static final WeakHashMap<World, EntityPlayer> FAKE_PLAYERS = new WeakHashMap<World, EntityPlayer>();
	
	private static WeakReference<FakePlayer> CRYSTALMOD_PLAYER = null;
	private static final GameProfile CRYSTALMOD = new GameProfile(UUID.nameUUIDFromBytes("[CrystalMod]".getBytes()), "[CrystalMod]");
	
	private static FakePlayer getCrystalMod(WorldServer world)
    {
        FakePlayer ret = CRYSTALMOD_PLAYER != null ? CRYSTALMOD_PLAYER.get() : null;
        if (ret == null)
        {
            ret = FakePlayerFactory.get((WorldServer) world, CRYSTALMOD);
            CRYSTALMOD_PLAYER = new WeakReference<FakePlayer>(ret);
        }
        return ret;
    }
	
	public static EntityPlayer getPlayer( final WorldServer w )
	{
		if( w == null )
		{
			throw new InvalidParameterException( "World is null." );
		}

		final EntityPlayer wrp = FAKE_PLAYERS.get( w );
		if( wrp != null )
		{
			return wrp;
		}

		final EntityPlayer p = getCrystalMod(w);
		FAKE_PLAYERS.put( w, p );
		return p;
	}
	
	private static boolean removeBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, boolean canHarvest) {
		final Block block = state.getBlock();
		block.onBlockHarvested(world, pos, state, player);
		final boolean result = block.removedByPlayer(state, world, pos, player, canHarvest);
		if (result) block.onBlockDestroyedByPlayer(world, pos, state);
		return result;
	}
	
	public static List<EntityItem> breakBlock(World world, BlockPos blockPos){
		return breakBlock(world, blockPos, new ItemStack(Items.DIAMOND_PICKAXE, 1, 0));
	}
	
	public static List<EntityItem> breakBlock(World world, BlockPos blockPos, ItemStack stackToUse){
		if(!(world instanceof WorldServer))return Lists.newArrayList();
		WorldServer worldObj = (WorldServer) world;
		EntityPlayer fakePlayer = getPlayer(worldObj);
		fakePlayer.inventory.currentItem = 0;
		fakePlayer.inventory.setInventorySlotContents(0, stackToUse);

		if (!worldObj.isBlockModifiable(fakePlayer, blockPos)) return Lists.newArrayList();

		// this mirrors ItemInWorldManager.tryHarvestBlock
		final IBlockState state = worldObj.getBlockState(blockPos);

		final CaptureContext dropsCapturer = DropCapture.instance.start(blockPos);

		final List<EntityItem> drops;
		try {
			BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(worldObj, blockPos, state, fakePlayer);
			if (MinecraftForge.EVENT_BUS.post(event)) return Lists.newArrayList();

			final TileEntity te = worldObj.getTileEntity(blockPos); // OHHHHH YEEEEAAAH

			boolean canHarvest = state.getBlock().canHarvestBlock(worldObj, blockPos, fakePlayer);
			boolean isRemoved = removeBlock(world, fakePlayer, blockPos, state, canHarvest);
			if (isRemoved && canHarvest) {
				state.getBlock().harvestBlock(worldObj, fakePlayer, blockPos, state, te, fakePlayer.getActiveItemStack());
				worldObj.playEvent(fakePlayer, 2001, blockPos, Block.getStateId(state));
			}

		} finally {
			drops = dropsCapturer.stop();
		}

		return drops;
	}
	
	public static void destroyBlockPartially(World world, int entID, BlockPos pos, int par5)
	{
	    world.sendBlockBreakProgress(entID, pos, par5);
	}
	
	public static boolean rightClickBlock(World world, BlockPos blockPos, EnumFacing face, ItemStack stackToUse){
		if(!(world instanceof WorldServer))return false;
		WorldServer worldObj = (WorldServer) world;
		EntityPlayer fakePlayer = getPlayer(worldObj);
		if(!worldObj.canMineBlockBody(fakePlayer, blockPos))return false;
		final Vec3d oldPos = fakePlayer.getPositionVector();
		fakePlayer.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		
		fakePlayer.inventory.currentItem = 0;
		fakePlayer.setActiveHand(EnumHand.MAIN_HAND);
		fakePlayer.inventory.setInventorySlotContents(0, stackToUse);
		
		if(!ItemStackTools.isNullStack(fakePlayer.getHeldItem(EnumHand.MAIN_HAND))){
			EnumActionResult result = fakePlayer.getHeldItem(EnumHand.MAIN_HAND).onItemUse(fakePlayer, world, blockPos, EnumHand.MAIN_HAND, face, 0f, 0f, 0f);
			fakePlayer.setPosition(oldPos.xCoord, oldPos.yCoord, oldPos.zCoord);
			return result == EnumActionResult.SUCCESS ? true : false;
		}
		
		IBlockState state = worldObj.getBlockState(blockPos);
		if(state !=null && state.getBlock() !=null){
			boolean result = state.getBlock().onBlockActivated(worldObj, blockPos, state, fakePlayer, EnumHand.MAIN_HAND, fakePlayer.getHeldItem(EnumHand.MAIN_HAND), face, 0f, 0f, 0f);
			fakePlayer.setPosition(oldPos.xCoord, oldPos.yCoord, oldPos.zCoord);
			return result;
		}
		
		fakePlayer.setPosition(oldPos.xCoord, oldPos.yCoord, oldPos.zCoord);
		return false;
	}
}
