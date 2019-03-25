package alec_wam.CrystalMod.tiles.pipes.estorage.security;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class BlockSecurityController extends BlockContainer {

	public static final PropertyBool CONNECTED = PropertyBool.create("connected");
	
	public BlockSecurityController() {
		super(Material.IRON);
		setHardness(2F);
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
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileSecurityController){
			TileSecurityController controller = (TileSecurityController)tile;
			if(!world.isRemote){
				if(UUIDUtils.areEqual(EntityPlayer.getUUID(player.getGameProfile()), controller.owner)){
					player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					GuiHandler.openNetworkGui(world, pos, player, NetworkAbility.VIEW, NetworkAbility.SECURITY);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof IInventory)
        {
            ItemUtil.dropContent(0, (IInventory)tile, world, tile.getPos());
        }
        IItemHandler handler = ItemUtil.getExternalItemHandler(world, pos, EnumFacing.UP);
        if(handler !=null){
        	ItemUtil.dropContent(0, handler, world, pos);
        }
        super.breakBlock(world, pos, blockState);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSecurityController();
	}
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{
            CONNECTED
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

        if (tile instanceof TileSecurityController) {
            return state.withProperty(CONNECTED, ((TileSecurityController) tile).connected);
        }

        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote && placer instanceof EntityPlayer) {
        	TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileSecurityController)((TileSecurityController)tile).owner = EntityPlayer.getUUID(((EntityPlayer)placer).getGameProfile());
        }
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
