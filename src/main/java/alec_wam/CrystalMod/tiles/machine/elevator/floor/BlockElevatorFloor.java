package alec_wam.CrystalMod.tiles.machine.elevator.floor;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ItemStackTools;

public class BlockElevatorFloor extends BlockContainer {

	public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public BlockElevatorFloor() {
		super(Material.IRON);
		setHardness(2.0f);
		setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(CrystalMod.tabBlocks);
	}

	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityElevatorFloor();
	}
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		ItemStack held = playerIn.getHeldItem(hand);
		TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityElevatorFloor) {
        	TileEntityElevatorFloor floor = (TileEntityElevatorFloor) te;
        	if(!worldIn.isRemote){
        		if(ItemStackTools.isValid(held) && held.getItem() == Items.NAME_TAG && held.hasDisplayName()){
        			floor.setName(held.getDisplayName());
        			floor.markDirty();
        			return true;
        		}
        	}
        	return true;
        }
        return false;
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        world.setBlockState(pos, state.withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite()), 2);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElevatorFloor) {
        	TileEntityElevatorFloor tileEntityElevator = (TileEntityElevatorFloor) te;
            tileEntityElevator.updateFloors();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElevatorFloor) {
        	TileEntityElevatorFloor tileEntityElevator = (TileEntityElevatorFloor) te;
            tileEntityElevator.updateFloors();
        }
        super.breakBlock(world, pos, state);
    }
	
	@Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return super.rotateBlock(world, pos, axis);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING_HORIZ, getFacingHoriz(meta));
    }
	
	public static EnumFacing getFacingHoriz(int meta) {
        return EnumFacing.values()[meta+2];
    }
	
	public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING_HORIZ).getIndex()-2;
    }
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ);
    }

}
