package alec_wam.CrystalMod.items.tools;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.api.enhancements.KnowledgeManager;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnhancementKnowledge extends Item {

	public ItemEnhancementKnowledge(){
		super();
		setCreativeTab(CreativeTabs.TOOLS);
		ModItems.registerItem(this, "enhancementknowledge");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		ItemStack stack = playerIn.getHeldItem(handIn);
		
		IEnhancement enhancement = getEnhancement(stack);
		if(enhancement !=null){
			if(!worldIn.isRemote){
				UUID uuid = EntityPlayer.getUUID(playerIn.getGameProfile());
				if(!KnowledgeManager.hasKnowledge(uuid, enhancement)){
					KnowledgeManager.setHasKnowledge(uuid, enhancement, true);
					String id = enhancement.getID().getResourceDomain()+"."+enhancement.getID().getResourcePath();
					ChatUtil.sendChat(playerIn, Lang.localizeFormat("enhancement.unlocked", new Object[]{Lang.translateToLocal("enhancement."+id+".name")}));
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, ItemUtil.consumeItem(stack));
				}
			}
		}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        //TODO Add Loot generation
		for(IEnhancement enhancement : EnhancementManager.getEnhancements()){
        	if(enhancement.requiresKnowledge())subItems.add(createItem(enhancement));
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		IEnhancement enhancement = getEnhancement(stack);
		if(enhancement !=null){
			String id = enhancement.getID().getResourceDomain()+"."+enhancement.getID().getResourcePath();
			tooltip.add(Lang.translateToLocal("enhancement."+id+".name"));
		}
    }
	
	public static ItemStack createItem(IEnhancement enhancement){
		ItemStack stack = new ItemStack(ModItems.enhancementKnowledge);
		ItemNBTHelper.setString(stack, "Type", enhancement.getID().toString());
		return stack;
	}
	
	public static IEnhancement getEnhancement(ItemStack stack){
		if(!ItemNBTHelper.verifyExistance(stack, "Type"))return null;
		ResourceLocation res = new ResourceLocation(ItemNBTHelper.getString(stack, "Type", ""));
		return EnhancementManager.getEnhancement(res);
	}
	
	public static ItemStack createRandomBook(Random rand){
		ItemStack stack = new ItemStack(ModItems.enhancementKnowledge);
		IEnhancement enhancement = null;
		List<IEnhancement> list = Lists.newArrayList();
		for(IEnhancement e : EnhancementManager.getEnhancements()){
			if(e.requiresKnowledge())list.add(e);
		}
		enhancement = list.get(MathHelper.getInt(rand, 0, list.size()-1));
		if(enhancement == null) return ItemStackTools.getEmptyStack();
		
		ItemNBTHelper.setString(stack, "Type", enhancement.getID().toString());
		return stack;
	}
}
