package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHDDArray extends BlockContainer {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public BlockHDDArray() {
		super(Material.IRON);
		this.setHardness(2f);
		setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null && te instanceof TileHDDArray) {
        	TileHDDArray array = (TileHDDArray)te;
        	face = EnumFacing.getFront(array.facing);
        }
        return state.withProperty(FACING, face);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }
	
	@Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileHDDArray)) {
        	if(!world.isRemote)GuiHandler.openNetworkGui(world, pos, player, NetworkAbility.VIEW);
        	return true;
        }
    	return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileHDDArray();
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
		TileEntity tileentitychest = world.getTileEntity(pos);
        if (tileentitychest != null && tileentitychest instanceof IInventory)
        {
        	ItemUtil.dropContent(0, (IInventory)tileentitychest, world, tileentitychest.getPos());
        }
        super.breakBlock(world, pos, blockState);
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing face = getFacingFromEntity(pos, placer);
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof TileHDDArray){
        	((TileHDDArray)tile).facing = face.getIndex();
        	BlockUtil.markBlockForUpdate(world, pos);
        }
    }
    
    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entityIn) {
        if (MathHelper.abs((float) entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.posY + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return EnumFacing.UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof TileHDDArray){
        	TileHDDArray bat = (TileHDDArray)te;
        	int next = bat.facing;
        	next++;
        	next%=6;
        	bat.facing = next;
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }
    
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
        return EnumFacing.VALUES;
    }
    
    public static EnumFacing getFacing(int meta) {
        return EnumFacing.values()[meta & 7];
    }
}
