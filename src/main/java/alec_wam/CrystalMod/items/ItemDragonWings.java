package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.client.model.dynamic.ItemRenderDragonWings;
import alec_wam.CrystalMod.proxy.ClientProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDragonWings extends Item implements ICustomModel {

	public static final String UPGRADE_NBT = "CrystalMod.DragonWings";
	
	public ItemDragonWings(){
		super();
		setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.MISC);
		ModItems.registerItem(this, "dragonwings");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new ItemRenderDragonWings());
	}
	
}
