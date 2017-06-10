package alec_wam.CrystalMod.api.enhancements;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IEnhancement {

	public ResourceLocation getID();
	
	public ItemStack getDisplayItem();
	
	public boolean canApply(ItemStack stack, EntityPlayer player);
	
	public boolean isApplied(ItemStack stack);
	
	public ItemStack apply(ItemStack stack, EntityPlayer player);
	
	public ItemStack remove(ItemStack stack, EntityPlayer player);
	
	public String getNBTID();
	
	public NonNullList<ItemStack> getRequiredItems();
	
	public boolean removeItemsFromPlayer(EntityPlayer player);
	public boolean returnItemsToPlayer(EntityPlayer player);
	
	@SideOnly(Side.CLIENT)
	public void addToolTip(ItemStack stack, List<String> list);
	
	public default boolean requiresKnowledge(){
		return true;
	}
	
}
