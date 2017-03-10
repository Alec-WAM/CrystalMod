package alec_wam.CrystalMod.tiles.pipes.wireless;


import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard.CardType;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.StringUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWirelessPipeWrapper extends BlockContainer implements ICustomModel {

	public static final PropertyBool SENDER = PropertyBool.create("sender");
	public BlockWirelessPipeWrapper() {
		super(Material.IRON);
		this.setHardness(2f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setDefaultState(getDefaultState().withProperty(SENDER, Boolean.valueOf(false)));
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }

	public static final String NBT_CON_X = "CrystalMod.PipeWrapper.ConX";
	public static final String NBT_CON_Y = "CrystalMod.PipeWrapper.ConY";
	public static final String NBT_CON_Z = "CrystalMod.PipeWrapper.ConZ";
	public static final String NBT_CON_D = "CrystalMod.PipeWrapper.ConD";
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), this.getMetaFromState(getDefaultState().withProperty(SENDER, Boolean.valueOf(false))), new ModelResourceLocation(this.getRegistryName(), "sender=false"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), this.getMetaFromState(getDefaultState().withProperty(SENDER, Boolean.valueOf(true))), new ModelResourceLocation(this.getRegistryName(), "sender=true"));
	}
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityPipeWrapper)) {
        	TileEntityPipeWrapper wrapper = (TileEntityPipeWrapper) tile;
        	ItemStack held = player.getHeldItem(hand);
        	
        	if(!ItemStackTools.isNullStack(held) && held.getItem() == ModItems.miscCard && CardType.byMetadata(held.getMetadata()) == CardType.EPORTAL){
        		if(ItemNBTHelper.verifyExistance(held, NBT_CON_X)){
        			if(!world.isRemote){
        				int x = ItemNBTHelper.getInteger(held, NBT_CON_X, pos.getX());
            			int y = ItemNBTHelper.getInteger(held, NBT_CON_Y, pos.getY());
            			int z = ItemNBTHelper.getInteger(held, NBT_CON_Z, pos.getZ());
            			BlockPos newCon = new BlockPos(x, y, z);
            			int dim = ItemNBTHelper.getInteger(held, NBT_CON_D, player.dimension);
            			if(newCon != pos && newCon !=wrapper.connectionPos){
            				wrapper.connectionPos = newCon;
            				wrapper.connectionDim = dim;
            				BlockUtil.markBlockForUpdate(world, pos);
            			}
            			ChatUtil.sendChat(player, "Linked to "+x+" "+y+" "+z+" Dimension:"+StringUtils.getDimensionName(player.getUniqueID(), dim));
        			}
        			return true;
        		}else{
        			ItemNBTHelper.setInteger(held, BlockWirelessPipeWrapper.NBT_CON_X, pos.getX());
        			ItemNBTHelper.setInteger(held, BlockWirelessPipeWrapper.NBT_CON_Y, pos.getY());
        			ItemNBTHelper.setInteger(held, BlockWirelessPipeWrapper.NBT_CON_Z, pos.getZ());
        			int dim = world.provider.getDimension();
        			ItemNBTHelper.setInteger(held, BlockWirelessPipeWrapper.NBT_CON_D, dim);
    				if(!world.isRemote)ChatUtil.sendChat(player, "Card set to "+pos.getX()+" "+pos.getY()+" "+pos.getZ()+" Dimension:"+StringUtils.getDimensionName(player.getUniqueID(), dim));
    				return true;
        		}
        	}
        }
    	return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPipeWrapper();
	}
	
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
	@Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof IPipeWrapper){
			return state.withProperty(SENDER, !((IPipeWrapper)tile).isSender());
		}
		return state.withProperty(SENDER, true);
	}
	
	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {SENDER});
    }

}
