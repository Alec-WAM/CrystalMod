package alec_wam.CrystalMod.items.tools.backpack.upgrade;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBackpackUpgrade extends Item implements ICustomModel {

	public ItemBackpackUpgrade() {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		ModItems.registerItem(this, "backpackupgrade");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(BackpackUpgrade type : BackpackUpgrade.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + BackpackUpgrade.byMetadata(i).getName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < BackpackUpgrade.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	public static boolean canEquip(InventoryBackpackUpgrades inventory){
		return true;
	}
	
	public static enum BackpackUpgrade implements IStringSerializable, IEnumMetaItem {
		HOPPER, ENDER, RESTOCKING, VOID, POCKETS, BOW, DESPAWN, DEATH;

		@Override
		public int getMetadata() {
			return ordinal();
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
		public static BackpackUpgrade byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= values().length)
            {
                metadata = 0;
            }

            return values()[metadata];
        }
		
	}
	
}
