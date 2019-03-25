package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class BlockCrafter extends BlockContainer {

	public static final PropertyDirection DIRECTION = PropertyDirection.create("direction");
	
	public BlockCrafter() {
		super(Material.IRON);
		setHardness(2.0F);
        this.setResistance(10.0F);
		setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,float hX, float hY, float hZ){
		if(!player.isSneaking()){
			player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCrafter();
	}
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{
            DIRECTION
        });
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
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCrafter) {
            return state.withProperty(DIRECTION, ((TileCrafter) tile).getDirection());
        }

        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);

        if (!world.isRemote && tile instanceof TileCrafter) {
            EnumFacing dir = ((TileCrafter) tile).getDirection();

            int newDir = dir.ordinal() + 1;

            if (newDir > EnumFacing.VALUES.length - 1) {
                newDir = 0;
            }

            ((TileCrafter) tile).setDirection(EnumFacing.getFront(newDir));

            BlockUtil.markBlockForUpdate(world, pos);

            return true;
        }

        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, player, itemStack);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCrafter) {
            EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, player);

            if (player.isSneaking() && hasOppositeFacingOnSneakPlace()) {
                facing = facing.getOpposite();
            }

            ((TileCrafter) tile).setDirection(facing);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCrafter) {
        	if(((TileCrafter) tile).getDroppedItems() != null){
	            IItemHandler handler = ((TileCrafter) tile).getDroppedItems();
	
	            for (int i = 0; i < handler.getSlots(); ++i) {
	                if (handler.getStackInSlot(i) != null) {
	                	ItemUtil.spawnItemInWorldWithRandomMotion(world, handler.getStackInSlot(i), pos);
	                }
	            }
        	}
            if (!world.isRemote) {
            	((TileCrafter) tile).onDisconnected();
            }
        }

        
        
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }

    public boolean hasOppositeFacingOnSneakPlace() {
        return false;
    }

}
