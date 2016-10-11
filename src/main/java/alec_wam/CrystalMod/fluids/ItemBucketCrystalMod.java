package alec_wam.CrystalMod.fluids;

import org.apache.commons.lang3.StringUtils;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBucketCrystalMod extends ItemBucket implements ICustomModel {

	public static ItemBucketCrystalMod create(BlockFluidClassic block, Fluid fluid) {
	    ItemBucketCrystalMod b = new ItemBucketCrystalMod(block != null ? block : Blocks.AIR, fluid.getName());
	    b.init();
	    //FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(b), new ItemStack(Items.BUCKET));
	    /*if (block != null) {
	      BucketHandler.instance.registerFluid(block, b);
	    }*/
	
	    return b;
	}
	  
	private String itemName;

	protected ItemBucketCrystalMod(Block block, String fluidName) {
	    super(block);  
	    setCreativeTab(CrystalMod.tabItems);
	    setContainerItem(Items.BUCKET);
	    itemName = "bucket" + StringUtils.capitalize(fluidName);
	    setUnlocalizedName(itemName);
	}

	protected void init() {
		ModItems.registerItem(this, itemName);
	}
	  
	public String getItemName() {
	    return itemName;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
	}

}
