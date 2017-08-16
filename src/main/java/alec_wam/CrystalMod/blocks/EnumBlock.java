package alec_wam.CrystalMod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnumBlock<E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> extends Block implements ICustomModel {
  public final PropertyEnum<E> prop;
  private final E[] values;

  public static PropertyEnum<?> tmp;
  
  public EnumBlock(Material material, PropertyEnum<E> prop, Class<E> clazz) {
    super(preInit(material, prop));
    this.prop = prop;
    values = clazz.getEnumConstants();
  }

  private static Material preInit(Material material, PropertyEnum<?> property) {
	tmp = property;
    return material;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
    for(E type : values) {
      list.add(new ItemStack(this, 1, type.getMeta()));
    }
  }
  
  @Override
  protected BlockStateContainer createBlockState() {
    if(prop == null) {
      return new BlockStateContainer(this, tmp);
    }
    return new BlockStateContainer(this, prop);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(prop, fromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(prop).getMeta();
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  protected E fromMeta(int meta) {
    if(meta < 0 || meta >= values.length) {
      meta = 0;
    }

    return values[meta];
  }
  
  @Override
@SideOnly(Side.CLIENT)
  public void initModel() {
	for(E type : values)
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), prop.getName()+"="+type.getName()));
  }

  public static interface IEnumMeta {
    int getMeta();
  }
}
