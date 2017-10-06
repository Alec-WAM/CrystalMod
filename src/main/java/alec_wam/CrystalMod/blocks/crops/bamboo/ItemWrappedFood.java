package alec_wam.CrystalMod.blocks.crops.bamboo;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWrappedFood extends ItemFood implements ICustomModel {

	public ItemWrappedFood(){
		super(0, 0.0F, false);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "wrappedfood");
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void initModel(){
		ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return ModelWrappedFood.LOCATION;
            }
        });
        ModelBakery.registerItemVariants(this, ModelWrappedFood.LOCATION);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		List<ItemStack> foods = Lists.newArrayList();
		foods.add(new ItemStack(Items.COOKED_BEEF));
		foods.add(new ItemStack(Items.APPLE));
		for(ItemStack food : foods){
			ItemStack wrapped = new ItemStack(this);
			setFood(wrapped, food);
			list.add(wrapped);
		}
	}
	
	@Override
	public int getHealAmount(ItemStack stack)
    {
		ItemStack food = getFood(stack);
		if(ItemStackTools.isValid(food)){
			if(food.getItem() instanceof ItemFood){
				return ((ItemFood)food.getItem()).getHealAmount(food) + 1;
			}
		}
        return 0;
    }

	@Override
	public float getSaturationModifier(ItemStack stack)
    {
		ItemStack food = getFood(stack);
		if(ItemStackTools.isValid(food)){
			if(food.getItem() instanceof ItemFood){
				return ((ItemFood)food.getItem()).getSaturationModifier(food);
			}
		}
        return 0.0f;
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
		ItemStack food = getFood(stack);
		if(ItemStackTools.isValid(food)){
			list.add(food.getDisplayName());
		}
	}
	
	public static ItemStack getFood(ItemStack wrapped){
		if(ItemStackTools.isValid(wrapped)){
			return ItemUtil.getStackFromString(ItemNBTHelper.getString(wrapped, "Food", ""), true);
		}
		return ItemStackTools.getEmptyStack();
	}
	
	public static void setFood(ItemStack stack, ItemStack food){
		if(ItemStackTools.isValid(stack)){
			if(ItemStackTools.isEmpty(food)){
				ItemNBTHelper.setString(stack, "Food", "");
			}else{
				ItemNBTHelper.setString(stack, "Food", ItemUtil.getStringForItemStack(food, true, false));
			}
		}
	}
	
}
