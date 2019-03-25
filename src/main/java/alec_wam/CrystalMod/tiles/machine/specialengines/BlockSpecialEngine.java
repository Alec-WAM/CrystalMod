package alec_wam.CrystalMod.tiles.machine.specialengines;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSpecialEngine extends EnumBlock<BlockSpecialEngine.SpecialEngineType> implements ITileEntityProvider, ICustomModel {

	public static final PropertyEnum<SpecialEngineType> ENGINE_TYPE = PropertyEnum.<SpecialEngineType>create("engine", SpecialEngineType.class);
	public static final PropertyBool activeProperty = PropertyBool.create("active");
	
	public BlockSpecialEngine() {
		super(Material.IRON, ENGINE_TYPE, SpecialEngineType.class);
		setHardness(5.0F);
		setResistance(15.0F);
		setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		if(prop == null) {
			return new BlockStateContainer(this, tmp, activeProperty);
		}
		return new BlockStateContainer(this, prop, activeProperty);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		boolean active = false;	
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileInfiniteEngine){
			active = ((TileInfiniteEngine)tile).isRunning;
		}
		if(tile !=null && tile instanceof TileFiniteEngine){
			active = ((TileFiniteEngine)tile).isRunning;
		}
        return state.withProperty(activeProperty, active);
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		try{
			SpecialEngineType type = fromMeta(meta);
			if(type.clazz !=null){
				return type.clazz.newInstance();
			}
		}catch(Exception e){
			
		}
		return null;
	}
	
	
	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			SpecialEngineType type = state.getValue(ENGINE_TYPE);
			boolean active = state.getValue(activeProperty);
			return new ModelResourceLocation(state.getBlock().getRegistryName()+ "_" + type.getName(), "active="+active);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
    	for(SpecialEngineType type : SpecialEngineType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "active=false"));
		}
	}
	
	public static enum SpecialEngineType implements IStringSerializable, IEnumMeta {
		INFINITE("infinite", TileInfiniteEngine.class),
		FINITE("finite", TileFiniteEngine.class);
		
		private final String unlocalizedName;
		public final Class<? extends TileEntity> clazz;
		public final int meta;

		SpecialEngineType(String name, Class<? extends TileEntity> clazz) {
	      meta = ordinal();
	      this.clazz = clazz;
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

}
