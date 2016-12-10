package alec_wam.CrystalMod.blocks.crops;

import java.util.List;

import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;

public class ItemBlockReeds extends ItemBlockSpecial {

	public ItemBlockReeds(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		for(PlantType type : PlantType.values()){
			list.add(new ItemStack(item, 1, type.getMeta()));
		}
	}

}
