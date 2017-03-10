package alec_wam.CrystalMod.tiles.fusion.recipes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.pedistals.IFusionPedistal;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.api.recipe.IFusionRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.StringUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FusionRecipeEnchantment implements IFusionRecipe {

	public FusionRecipeEnchantment(){
	}

	@Override
	public Object getMainInput() {
		return "null";
	}

	@Override
	public List<?> getInputs() {
		return Lists.newArrayList();
	}

	@Override
	public ItemStack getOutput() {
		return ItemStackTools.getEmptyStack();
	}
	
	@Override
	public boolean matches(IFusionPedistal fpedistal, World world, List<IPedistal> pedistals) {
		if(ItemStackTools.isEmpty(fpedistal.getStack())) return false;
		ItemStack tool = fpedistal.getStack();
		boolean oneBook = false;
		for(IPedistal pedistal : pedistals){
			ItemStack stack = pedistal.getStack();
			if(ItemStackTools.isValid(stack)){
				if(stack.getItem() == Items.ENCHANTED_BOOK && !Items.ENCHANTED_BOOK.getEnchantments(stack).hasNoTags()){
					if (!tool.getItem().isBookEnchantable(tool, stack)) {
						return false;
					}
					oneBook = true;
				} else {
					return false;
				}
			}
		}
		
		return oneBook;
	}
	
	@Override
	public String canCraft(IFusionPedistal fpedistal, World world, List<IPedistal> pedistals) {
		if(ItemStackTools.isEmpty(fpedistal.getStack())) return Lang.localize("fusion.message.emptyinput");
		ItemStack tool = fpedistal.getStack();
		Map<Enchantment, Integer> toolEnchantments = EnchantmentHelper.getEnchantments(tool);
		for(IPedistal pedistal : pedistals){
			ItemStack stack = pedistal.getStack();
			if(ItemStackTools.isValid(stack)){
				if(stack.getItem() == Items.ENCHANTED_BOOK && !Items.ENCHANTED_BOOK.getEnchantments(stack).hasNoTags()){
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					List<String> names = Lists.newArrayList();
					for(Enchantment enchantment : enchantments.keySet()){
						int currentLvl = toolEnchantments.containsKey(enchantment) ? ((Integer)toolEnchantments.get(enchantment)).intValue() : 0;
						int bookLvl = ((Integer)enchantments.get(enchantment)).intValue();
						int newLevel = currentLvl == bookLvl ? currentLvl + 1 : currentLvl + bookLvl;
						String name = Lang.translateToLocal(enchantment.getName());
						names.add(name);
						
						boolean canApply = enchantment.canApply(tool);
						if (tool.getItem() == Items.ENCHANTED_BOOK)
						{
							canApply = true;
						}
						if(!canApply){
							return Lang.localizeFormat("fusion.message.cantenchant", name);
						}
						
						for (Enchantment toolEnchantment : toolEnchantments.keySet())
						{
							if (toolEnchantment != enchantment && !(enchantment.func_191560_c(toolEnchantment)))
							{
								return Lang.localizeFormat("fusion.message.cantcombine", name, Lang.translateToLocal(toolEnchantment.getName())); 
							}
						}
						
						if(canApply){
							toolEnchantments.put(enchantment, Integer.valueOf(newLevel));
						}
					}
					if (!tool.getItem().isBookEnchantable(tool, stack)) {
						return Lang.localizeFormat("fusion.message.cantenchant", StringUtils.makeReadable(names));
					}
				} else {
					return Lang.localizeFormat("fusion.message.invalid", stack.getDisplayName());
				}
			}
		}
		for(Entry<Enchantment, Integer> entry : toolEnchantments.entrySet()){
			Enchantment enchant = entry.getKey();
			int lvl = entry.getValue();
			if(lvl > enchant.getMaxLevel()){
				return Lang.translateToLocalFormatted("Level "+Lang.translateToLocal("enchantment.level."+lvl)+" is too large for "+Lang.translateToLocal(enchant.getName()));
			}
		}
		return "true";
	}

	@Override
	public void finishCrafting(IFusionPedistal fpedistal, World world, List<IPedistal> linkedPedistals) {
		if(!matches(fpedistal, world, linkedPedistals))return;
		if(!canCraft(fpedistal, world, linkedPedistals).equalsIgnoreCase("true"))return;
		ItemStack tool = fpedistal.getStack().copy();
		Map<Enchantment, Integer> toolEnchantments = EnchantmentHelper.getEnchantments(tool);
		for(IPedistal pedistal : linkedPedistals){
			ItemStack stack = pedistal.getStack();
			if(ItemStackTools.isValid(stack) && stack.getItem() == Items.ENCHANTED_BOOK && !Items.ENCHANTED_BOOK.getEnchantments(stack).hasNoTags()){
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				for (Enchantment enchantment1 : enchantments.keySet())
                {
                    if (enchantment1 != null)
                    {
                    	int i3 = toolEnchantments.containsKey(enchantment1) ? ((Integer)toolEnchantments.get(enchantment1)).intValue() : 0;
                        int j3 = ((Integer)enchantments.get(enchantment1)).intValue();
                        j3 = i3 == j3 ? j3 + 1 : Math.max(j3, i3);
                        boolean flag1 = enchantment1.canApply(tool);

                        if (tool.getItem() == Items.ENCHANTED_BOOK)
                        {
                            flag1 = true;
                        }

                        for (Enchantment enchantment : toolEnchantments.keySet())
                        {
                            if (enchantment != enchantment1 && !(enchantment1.func_191560_c(enchantment)))
                            {
                                flag1 = false;
                            }
                        }

                        if (flag1)
                        {
                            if (j3 > enchantment1.getMaxLevel())
                            {
                                j3 = enchantment1.getMaxLevel();
                            }

                            toolEnchantments.put(enchantment1, Integer.valueOf(j3));
                        } else {
                        	return;
                        }
                    }
                }
				if (!tool.getItem().isBookEnchantable(tool, stack)) {
					return;
				} else {
					pedistal.setStack(ItemUtil.consumeItem(stack));
				}
			}
		}
		EnchantmentHelper.setEnchantments(toolEnchantments, tool);
		fpedistal.setStack(tool);
	}

	@Override
	public Vec3d getRecipeColor() {
		return new Vec3d(255, 0, 255);
	}
	
}
