package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

public class ItemPipeFilter extends Item {

	public ItemPipeFilter(Properties properties) {
		super(properties);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		String filterName = ItemNBTHelper.getString(stack, "FilterName", "");
		FilterSettings settings = new FilterSettings(stack);
		if(!filterName.isEmpty()){
			tooltip.add(new StringTextComponent(TextFormatting.GOLD + "" + TextFormatting.ITALIC + filterName));
		}
		if(settings.isBlacklist()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.blacklist"));
		} else {
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.whitelist"));
		}
		if(settings.isDamage()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.damage"));
		}
		if(settings.isNBT()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.nbt"));			
		}
		if(settings.useTag()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.tag"));			
		}
		
		if(ItemNBTHelper.verifyExistance(stack, "FilterItems")){
			tooltip.add(new StringTextComponent(""));
			if(!Screen.hasShiftDown()){
				tooltip.add(new TranslationTextComponent("crystalmod.info.filter.shift"));
			} else {
				tooltip.add(new TranslationTextComponent("crystalmod.info.filter.listheader"));
				buildFilterList(stack, tooltip);
			}
		}
	}
	
	private static void buildFilterList(ItemStack filter, List<ITextComponent> names){
		if(ItemNBTHelper.verifyExistance(filter, "FilterItems")){
			NonNullList<ItemStack> stacks = loadFilterStacks(filter);
			for(ItemStack stack : stacks){
				if(ItemStackTools.isValid(stack)){
					if(stack.getItem() == ModItems.itemFilter){
						//Load that filter
						String filterName = ItemNBTHelper.getString(stack, "FilterName", "");
						if(!filterName.isEmpty()){
							names.add(new StringTextComponent(TextFormatting.GOLD + "" + TextFormatting.ITALIC + filterName));
							continue;
						} else {
							names.add(new TranslationTextComponent("crystalmod.info.filter.otherfilter"));
						}
						continue;
					} 
					names.add(stack.getDisplayName());
				}
			}
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn) {
		ItemStack stack = player.getHeldItem(handIn);
		if(world.isRemote){
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
		} else {
			if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer))
	        {
	            ServerPlayerEntity entityPlayerMP = (ServerPlayerEntity) player;

	            GuiHandler.openCustomGui(GuiHandler.ITEM_NORMAL, entityPlayerMP, new FilterGui(stack, handIn), buf -> buf.writeEnumValue(handIn));
	            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
	        }
		}		
		return new ActionResult<ItemStack>(ActionResultType.PASS, stack);
	}
	
	public class FilterGui implements INamedContainerProvider {

		private ItemStack stack;
		private Hand hand;
		public FilterGui(ItemStack stack, Hand hand){
			this.stack = stack;
			this.hand = hand;
		}
		
		@Override
		public ITextComponent getDisplayName() {
			return stack.getDisplayName();
		}

		@Override
		public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerIn) {
			return new ContainerPipeFilter(i, playerIn, stack, hand);
		}
		
	}
	
	public static boolean passesFilter(ItemStack item, ItemStack filter){
		if(ItemStackTools.isNullStack(item) || item.getItem() == null || (ItemStackTools.isValid(filter) && filter.getItem() !=ModItems.itemFilter))return false;
		if(ItemStackTools.isNullStack(filter))return true;	
		Map<ItemStack, FilterSettings> filteredList = Maps.newHashMap();
		buildFilterList(filter, filteredList);
		if(filteredList.isEmpty()){
			FilterSettings masterSettings = new FilterSettings(filter);
			return masterSettings.isBlacklist() ? true : false;
		}
		
		boolean matched = false;
		for(Entry<ItemStack, FilterSettings> filterData : filteredList.entrySet()){
			ItemStack filterStack = filterData.getKey();
			FilterSettings settings = filterData.getValue();
			if(ItemStackTools.isValid(filterStack)) {				
				if(item.getItem() == filterStack.getItem()){
					matched = true;
					if(settings.isDamage() && item.getDamage() != filterStack.getDamage()){
						matched = false;
					}
					else if(settings.isNBT()) {
						if(filterStack.getTag() == null || item.getTag() == null || !filterStack.getTag().equals(item.getTag())){
							matched = false;
						}
					}  
				}
				
				//Use tag data if the filter has that enabled
				if(settings.useTag() && !matched){
					if(ItemUtil.matchUsingTags(item, filterStack)){
						matched = true;
					}
				}
			}
			if(settings.isBlacklist()) {				
				if(matched)return false;
			}
			else {				
				if(!matched)return false;
			}
		}
		return true;
	}
	
	//TODO Look into capping depth of filters
	private static void buildFilterList(ItemStack filter, Map<ItemStack, FilterSettings> filterList){
		if(ItemNBTHelper.verifyExistance(filter, "FilterItems")){
			FilterSettings settings = new FilterSettings(filter);
			NonNullList<ItemStack> stacks = loadFilterStacks(filter);
			for(ItemStack stack : stacks){
				if(ItemStackTools.isValid(stack)){
					if(stack.getItem() == ModItems.itemFilter){
						//Load that filter
						//Allows multi filter to filter more items
						buildFilterList(stack, filterList);
					} else {
						filterList.put(ItemUtil.copy(stack, 1), settings);
					}
				}
			}
		}
	}

	public static class FilterSettings {
		public static final String NBT_BLACKLIST = "Blacklist";
		public static final String NBT_DAMAGE_MATCH = "DamageMatch";
		public static final String NBT_NBT_MATCH = "NBTMatch";
		public static final String NBT_TAG_MATCH = "TagMatch";
		private boolean blacklist;
		private boolean damage;
		private boolean nbt;
		private boolean tag;
		
		public FilterSettings(ItemStack filter){
			this.blacklist = ItemNBTHelper.getBoolean(filter, NBT_BLACKLIST, true);
			this.damage = ItemNBTHelper.getBoolean(filter, NBT_DAMAGE_MATCH, false);
			this.nbt = ItemNBTHelper.getBoolean(filter, NBT_NBT_MATCH, false);
			this.tag = ItemNBTHelper.getBoolean(filter, NBT_TAG_MATCH, false);
		}
		
		public FilterSettings(boolean blacklist, boolean damage, boolean nbt, boolean tag){
			this.blacklist = blacklist;
			this.damage = damage;
			this.nbt = nbt;
			this.tag = tag;
		}
		
		public boolean isBlacklist() {
			return blacklist;
		}
		
		public void setBlackList(boolean value){
			this.blacklist = value;
		}
		
		public boolean isDamage() {
			return damage;
		}
		
		public void setIsDamage(boolean value){
			this.damage = value;
		}
		
		public boolean isNBT() {
			return nbt;
		}
		
		public void setIsNBT(boolean value){
			this.nbt = value;
		}
		
		public boolean useTag() {
			return tag;
		}
		
		public void setUseTag(boolean value){
			this.tag = value;
		}
		
		public void saveToItem(ItemStack stack){
			ItemNBTHelper.putBoolean(stack, NBT_BLACKLIST, blacklist);
			ItemNBTHelper.putBoolean(stack, NBT_DAMAGE_MATCH, damage);
			ItemNBTHelper.putBoolean(stack, NBT_NBT_MATCH, nbt);
			ItemNBTHelper.putBoolean(stack, NBT_TAG_MATCH, tag);
		}
	}
	
	public static NonNullList<ItemStack> loadFilterStacks(ItemStack filter) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(10, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(ItemNBTHelper.getCompound(filter).getCompound("FilterItems"), stacks);
		return stacks;
	}
}
