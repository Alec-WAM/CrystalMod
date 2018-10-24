package alec_wam.CrystalMod.tiles.campfire;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketSpawnParticle;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCampfire extends BlockContainer {
	
	public BlockCampfire() {
		super(Material.ROCK);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@SideOnly(Side.CLIENT)
    @Override
	public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }
    
    public static final AxisAlignedBB CAMPFIRE_BB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.2D, 1.0D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CAMPFIRE_BB;
    }
    
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(player.isSneaking()) {
			return false;
		}
		if(tile !=null && tile instanceof TileEntityCampfire){
			TileEntityCampfire fire = (TileEntityCampfire)tile;
			ItemStack held = player.getHeldItem(hand);
			if(ItemStackTools.isValid(held)){
				if(held.getItem() == Items.STICK && fire.addStick()){
					if(!player.capabilities.isCreativeMode){
						player.setHeldItem(hand, ItemUtil.consumeItem(held));
					}
					return true;
				}
				if(held.getItem() == Items.FLINT_AND_STEEL && fire.lightFire()){
					held.damageItem(1, player);
					return true;
				}
			} else {
				if(!world.isRemote){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCampfire();
	}

}
