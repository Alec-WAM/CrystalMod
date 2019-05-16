package alec_wam.CrystalMod.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ItemTagHelper {

	public static List<ResourceLocation> getTags(ItemStack stack){
		return getTags(stack.getItem());
	}
	
	/***
	 * Checks the TagCollection for tags that hold the provided item
	 * @param item
	 * @return List is Tags that have the item in them
	 */
	public static List<ResourceLocation> getTags(Item item){
		List<ResourceLocation> list = Lists.newArrayList();
		TagCollection<Item> collection = ItemTags.getCollection();
		for(ResourceLocation res : collection.getRegisteredTags()){
			Tag<Item> tag = collection.get(res);
			if(tag !=null){
				if(tag.contains(item)){
					list.add(res);
				}
			}
		}
		return list;
	}
	
	private static final NonNullList<Item> EMPTY_LIST = NonNullList.withSize(1, Items.AIR);
	/***
	 * Get all the items in a tag
	 * @param ResourceLocation tagID Tag registry name
	 * @return NonNullList of items in tag
	 */
	public static NonNullList<Item> getItemsInTag(ResourceLocation tagID){
		Tag<Item> tag = ItemTags.getCollection().get(tagID);
		if(tag !=null){
			NonNullList<Item> items = NonNullList.withSize(tag.getAllElements().size(), Items.AIR);
			for(Item item : tag.getAllElements()){
				items.add(item);
			}
			return items;
		}
		return EMPTY_LIST;
	}
	
	public static boolean isItemInTag(ItemStack stack, ResourceLocation tagID){
		return isItemInTag(stack.getItem(), tagID);
	}
	
	public static boolean isItemInTag(Item item, ResourceLocation tagID){
		Tag<Item> tag = ItemTags.getCollection().get(tagID);
		if(tag !=null){
			return tag.contains(item);
		}
		return false;
	}
	
}
