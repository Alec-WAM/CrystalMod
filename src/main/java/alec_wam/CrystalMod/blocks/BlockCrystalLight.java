package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.IEnumMeta;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalLight extends EnumBlock<BlockCrystalLight.LightBlockType> {

	public static final PropertyEnum<LightBlockType> TYPE = PropertyEnum.<LightBlockType>create("type", LightBlockType.class);
	
	public BlockCrystalLight() {
		super(Material.IRON, TYPE, LightBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
		this.setLightLevel(0.5F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, LightBlockType.BLUE));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(LightBlockType type : LightBlockType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
	
    public static enum LightBlockType implements IStringSerializable, IEnumMeta {
		BLUE,
		RED,
		GREEN,
		DARK,
		PURE,
		INFINITE,
		SPIRAL_DARK,
		SPIRAL_PURE;

		private final String unlocalizedName;
		public final int meta;

		LightBlockType() {
	      meta = ordinal();
	      unlocalizedName = name().toLowerCase();
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
