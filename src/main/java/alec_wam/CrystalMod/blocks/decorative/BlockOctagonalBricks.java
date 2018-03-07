package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOctagonalBricks extends EnumBlock<BlockOctagonalBricks.OctagonBrickType> {

	public static final PropertyEnum<OctagonBrickType> TYPE = PropertyEnum.<OctagonBrickType>create("type",
			OctagonBrickType.class);

	public BlockOctagonalBricks() {
		super(Material.ROCK, TYPE, OctagonBrickType.class);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setHardness(1.5f);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, OctagonBrickType.NORMAL));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (OctagonBrickType type : OctagonBrickType.values())
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(),
					new ModelResourceLocation(this.getRegistryName(), TYPE.getName() + "=" + type.getName()));
	}

	public static enum OctagonBrickType implements IStringSerializable, IEnumMeta {
		NORMAL("normal"), TILES("tiles");

		private final String unlocalizedName;
		public final int meta;

		OctagonBrickType(String name) {
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
