package alec_wam.CrystalMod.tiles.machine.inventory.charger;



import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.crate.BlockCrate.CrateType;
import alec_wam.CrystalMod.tiles.crate.BlockCrate.CustomBlockStateMapper;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryCharger extends BlockContainer implements ICustomModel {
	
	public BlockInventoryCharger() {
		super(Material.IRON);
		this.setHardness(1f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		for(ChargerBlockType type : ChargerBlockType.values()){
			ResourceLocation baseLocation = new ResourceLocation(getRegistryName().getResourcePath() + "_" + type.getName());
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
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
        if (te !=null && te instanceof TileEntityInventoryCharger) {
        	TileEntityInventoryCharger interf = (TileEntityInventoryCharger)te;
        	face = EnumFacing.getFront(interf.facing);
        }
        return state.withProperty(BlockStateFacing.facingProperty, face);
    }
	  
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(ChargerBlockType type : ChargerBlockType.values()) {
			list.add(new ItemStack(this, 1, type.getMeta()));
		}
	}

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateInventoryCharger(this);
    }
    
    public int getMetaFromState(IBlockState state){
    	return ((ChargerBlockType) state.getValue(BlockStateInventoryCharger.typeProperty)).getMeta();
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta){
    	return getDefaultState().withProperty(BlockStateInventoryCharger.typeProperty, ChargerBlockType.values()[meta]);
    }
    
    @Override
    public int damageDropped(IBlockState state) {
      return getMetaFromState(state);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(meta == 1){
			return new TileEntityInventoryChargerRF();
		}
		return new TileEntityInventoryChargerCU();
	}
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing face = getFacingFromEntity(pos, placer);
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityInventoryCharger){
        	((TileEntityInventoryCharger)tile).facing = face.getIndex();
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
        if(te !=null && te instanceof TileEntityInventoryCharger){
        	TileEntityInventoryCharger bat = (TileEntityInventoryCharger)te;
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
    

    
    public static enum ChargerBlockType implements IStringSerializable, IEnumMeta {
		CU("cu"),
		RF("rf");

		private final String unlocalizedName;
		public final int meta;

		ChargerBlockType(String name) {
	      meta = ordinal();
	      unlocalizedName = name;
	    }

	    @Override
	    public String getName() {
	      return unlocalizedName;
	    }

	    @Override
	    public int getMeta() {
	      return meta;
	    }
    	
    }
    


    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam)
    {
    	super.eventReceived(state, worldIn, pos, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }
    
    public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			ChargerBlockType type = state.getValue(BlockStateInventoryCharger.typeProperty);
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
