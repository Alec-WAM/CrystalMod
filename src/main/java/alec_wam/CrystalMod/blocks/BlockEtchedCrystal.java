package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEtchedCrystal extends EnumBlock<BlockEtchedCrystal.EtchedCrystalBlockType> {

	public static final PropertyEnum<EtchedCrystalBlockType> TYPE = PropertyEnum.<EtchedCrystalBlockType>create("type", EtchedCrystalBlockType.class);
	
	public BlockEtchedCrystal() {
		super(Material.ROCK, TYPE, EtchedCrystalBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EtchedCrystalBlockType.BLUE));
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(EtchedCrystalBlockType type : EtchedCrystalBlockType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
    
    public static enum EtchedCrystalBlockType implements IStringSerializable, IEnumMeta {
		BLUE("blue"),
		RED("red"),
		GREEN("green"),
		DARK("dark"),
		PURE("pure");

		private final String unlocalizedName;
		public final int meta;

		EtchedCrystalBlockType(String name) {
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

}
