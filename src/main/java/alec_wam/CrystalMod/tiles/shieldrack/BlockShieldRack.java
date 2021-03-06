package alec_wam.CrystalMod.tiles.shieldrack;

import java.util.Random;

import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockShieldRack extends EnumBlock<WoodenBlockProperies.WoodType> implements ITileEntityProvider, ICustomModel {
	
	public BlockShieldRack() {
		super(Material.WOOD, WoodenBlockProperies.WOOD, WoodType.class);
		setHardness(1.0F);
		setCreativeTab(CreativeTabs.COMBAT);
		setSoundType(SoundType.WOOD);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		for(WoodType type : WoodType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
		ClientRegistry.bindTileEntitySpecialRenderer(TileShieldRack.class, new RenderTileShieldRack());
	}
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateFacing(this, WoodenBlockProperies.WOOD);
    }
    
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null) {
        	if(te instanceof IFacingTile){
        		int facing = ((IFacingTile)te).getFacing();
        		face = EnumFacing.getHorizontal(facing);
        	}
        }
        return state.withProperty(BlockStateFacing.facingProperty, face);
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state){
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
		for (EnumFacing enumfacing : EnumFacing.HORIZONTALS)
	    {
	        if (canBlockStay(worldIn, pos.offset(enumfacing), enumfacing))
	        {
	            return true;
	        }
	    }
		return false;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.checkAndDropBlock(worldIn, pos, state);
    }

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    protected void checkAndDropBlock(World world, BlockPos pos, IBlockState state)
    {
    	EnumFacing facing = state.getValue(BlockStateFacing.facingProperty);
    	TileEntity te = world.getTileEntity(pos);
        if (te !=null) {
        	if(te instanceof IFacingTile){
        		int face = ((IFacingTile)te).getFacing();
        		facing = EnumFacing.getHorizontal(face);
        	}
        }    	
    	BlockPos otherPos = pos.offset(facing.getOpposite());
        if (!canBlockStay(world, otherPos, facing))
        {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing)
    {
    	//BlockPos otherPos = ;
        return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, facing);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing facing = EnumFacing.NORTH;
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileShieldRack){
			facing = EnumFacing.getHorizontal(((TileShieldRack)tile).getFacing());
		}
	    return getBoundingBox(pos, facing);
	}

	private static final float BLOCK_SIZE = 1f / 16f;
	
	public AxisAlignedBB getBoundingBox(BlockPos pos, EnumFacing facing) {
	    int x = 0, y = 0, z = 0;
	    switch (facing) {
	    case NORTH:
	      return new AxisAlignedBB(x, y, z + (1- BLOCK_SIZE), x + 1, y + 1, z + 1);
	    case SOUTH:
	      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + BLOCK_SIZE);
	    case WEST:
	      return new AxisAlignedBB(x + (1 - BLOCK_SIZE), y, z, x + 1, y + 1, z + 1);
	    case EAST:
	      return new AxisAlignedBB(x, y, z, x + BLOCK_SIZE, y + 1, z + 1);
	    default:
	      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
	    }
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileShieldRack)) return false;
		TileShieldRack rack = (TileShieldRack)tile;
		ItemStack held = player.getHeldItem(hand);
		EnumFacing rot = EnumFacing.getHorizontal(rack.getFacing());
		if(rot.getAxis() == Axis.X){
			if(hitZ > 0.3 && hitZ < 0.6 && (ItemStackTools.isEmpty(held) || held.getItem() == Items.SHIELD)){
				//Shield
				final ItemStack handStack = held;
				final ItemStack shieldStack = rack.getShieldStack();
				if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
				rack.setShieldStack(handStack);
				player.setHeldItem(hand, shieldStack);
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 1.0F, false);
				BlockUtil.markBlockForUpdate(world, pos);
				return true;
			}
			if(rot == EnumFacing.WEST){
				if(hitZ < 0.3 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Left
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getLeftStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setLeftStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
				if(hitZ > 0.6 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Right
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getRightStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setRightStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
			if(rot == EnumFacing.EAST){
				if(hitZ > 0.6 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Left
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getLeftStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setLeftStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
				if(hitZ < 0.3 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Right
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getRightStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setRightStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
		}
		if(rot.getAxis() == Axis.Z){
			if(hitX > 0.3 && hitX < 0.6 && (ItemStackTools.isEmpty(held) || held.getItem() == Items.SHIELD)){
				//Shield
				final ItemStack handStack = held;
				final ItemStack shieldStack = rack.getShieldStack();
				if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
				rack.setShieldStack(handStack);
				player.setHeldItem(hand, shieldStack);
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 1.0F, false);
				BlockUtil.markBlockForUpdate(world, pos);
				return true;
			}
			if(rot == EnumFacing.SOUTH){
				if(hitX < 0.3 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Left
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getLeftStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setLeftStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
				if(hitX > 0.6 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Right
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getRightStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setRightStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
			if(rot == EnumFacing.NORTH){
				if(hitX > 0.6 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Left
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getLeftStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setLeftStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
				if(hitX < 0.3 && (ItemStackTools.isEmpty(held) || ToolUtil.isMeleeWeapon(held))){
					//Right
					final ItemStack handStack = held;
					final ItemStack shieldStack = rack.getRightStack();
					if(ItemStackTools.isEmpty(held) && ItemStackTools.isEmpty(shieldStack))return false;
					rack.setRightStack(handStack);
					player.setHeldItem(hand, shieldStack);
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), getSoundType().getHitSound(), SoundCategory.BLOCKS, 0.5F, 0.8F, false);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
		}
        return false;
    }
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof TileShieldRack){
        	TileShieldRack rack = (TileShieldRack)tile;
        	ItemUtil.spawnItemInWorldWithRandomMotion(worldIn, rack.getLeftStack(), pos);
        	ItemUtil.spawnItemInWorldWithRandomMotion(worldIn, rack.getShieldStack(), pos);
        	ItemUtil.spawnItemInWorldWithRandomMotion(worldIn, rack.getRightStack(), pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileShieldRack();
	}
	
	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			WoodType type = state.getValue(WoodenBlockProperies.WOOD);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(BlockStateFacing.facingProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateFacing.facingProperty));
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath() + "_" + type.getName();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

}
