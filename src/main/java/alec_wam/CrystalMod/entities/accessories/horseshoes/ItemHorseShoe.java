package alec_wam.CrystalMod.entities.accessories.horseshoes;

import java.util.Map;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemHorseShoe extends Item {

	public ItemHorseShoe(){
		super();
		setCreativeTab(CreativeTabs.COMBAT);
		setMaxStackSize(1);
		ModItems.registerItem(this, "horseshoes");
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void enchantShoes(AnvilUpdateEvent event){
		ItemStack shoes = event.getLeft();
		if(ItemStackTools.isValid(shoes) && shoes.getItem() == this){
			ItemStack rightStack = event.getRight();
			if (!rightStack.isEmpty())
			{
				if(rightStack.getItem() != Items.ENCHANTED_BOOK || Items.ENCHANTED_BOOK.getEnchantments(rightStack).hasNoTags())return;
				int i = 0;
		        int j = 0;
		        
				
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(shoes);
				Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(rightStack);
				boolean flag2 = false;
				boolean flag3 = false;

				for (Enchantment enchantment1 : map1.keySet())
				{
					if (enchantment1 != null)
					{
						int i2 = map.containsKey(enchantment1) ? ((Integer)map.get(enchantment1)).intValue() : 0;
						int j2 = ((Integer)map1.get(enchantment1)).intValue();
						j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
						boolean flag1 = canApplyEnchantment(enchantment1);

						for (Enchantment enchantment : map.keySet())
						{
							if (enchantment != enchantment1 && !enchantment1.func_191560_c(enchantment))
							{
								flag1 = false;
								++i;
							}
						}

						if (!flag1)
						{
							flag3 = true;
						}
						else
						{
							flag2 = true;

							if (j2 > enchantment1.getMaxLevel())
							{
								j2 = enchantment1.getMaxLevel();
							}

							map.put(enchantment1, Integer.valueOf(j2));
							int k3 = 0;

                            switch (enchantment1.getRarity())
                            {
                                case COMMON:
                                    k3 = 1;
                                    break;
                                case UNCOMMON:
                                    k3 = 2;
                                    break;
                                case RARE:
                                    k3 = 4;
                                    break;
                                case VERY_RARE:
                                    k3 = 8;
                            }
							
                            k3 = Math.max(1, k3 / 2);

							i += k3 * j2;
						}
					}
				}

				if (flag3 && !flag2)
				{
					event.setCanceled(true);
					return;
				} 
				event.setCost(j + i);
				ItemStack copy = shoes.copy();
				EnchantmentHelper.setEnchantments(map, copy);
				event.setOutput(copy);
			}
		}
	}
	
	public static boolean canApplyEnchantment(Enchantment enchantment){
		if(enchantment == Enchantments.FEATHER_FALLING || enchantment == Enchantments.DEPTH_STRIDER	|| enchantment == Enchantments.FROST_WALKER){
			return true;
		}
		return false;
	}
	
}
