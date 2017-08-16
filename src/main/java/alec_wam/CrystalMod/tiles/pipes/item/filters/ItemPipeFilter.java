package alec_wam.CrystalMod.tiles.pipes.item.filters;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class ItemPipeFilter extends Item implements ICustomModel {
	
	public static enum FilterType{
		NORMAL, MOD, CAMERA;
	}
	
	public ItemPipeFilter(){
    	super();
    	this.setHasSubtypes(true);
    	this.setMaxDamage(0);
    	this.setCreativeTab(CrystalMod.tabItems);
    	ModItems.registerItem(this, "pipefilter");
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(FilterType type : FilterType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.ordinal(), new ModelResourceLocation(getRegistryName(), type.name().toLowerCase()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		return super.getUnlocalizedName(stack)+"."+FilterType.values()[stack.getMetadata() % FilterType.values().length].name().toLowerCase();
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		for(int m = 0; m < FilterType.values().length; m++){
			list.add(new ItemStack(this, 1, m));
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv){
		if(stack.getMetadata() == FilterType.NORMAL.ordinal()){
			boolean black = ItemNBTHelper.getBoolean(stack, "BlackList", false);
			boolean meta = ItemNBTHelper.getBoolean(stack, "MetaMatch", true);
			boolean nbtMatch = ItemNBTHelper.getBoolean(stack, "NBTMatch", true);
			boolean oreMatch = ItemNBTHelper.getBoolean(stack, "OreMatch", false);
			list.add("Mode: "+(black?"Block":"Allow"));
			list.add("Match Metadata: "+(meta?"Enabled":"Disabled"));
			list.add("Match NBT: "+(nbtMatch?"Enabled":"Disabled"));
			list.add("Use Ore Dictionary: "+(oreMatch?"Enabled":"Disabled"));
		}
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(ItemStackTools.isValid(stack) && stack.getMetadata() == FilterType.CAMERA.ordinal()){
			IItemHandler inv = ItemUtil.getExternalItemHandler(worldIn, pos, facing);
			if(inv !=null){
				if(worldIn.isRemote){
					return EnumActionResult.SUCCESS;
				}
				CameraFilterInventory filterInv = new CameraFilterInventory(stack, "");
				filterInv.clear();
				int slots = inv.getSlots();
				if(slots > 0){
					for(int slot = 0; slot < slots; slot++){
						ItemStack invStack = inv.getStackInSlot(slot);
						if(ItemStackTools.isValid(invStack)){
							filterInv.addItem(invStack);
						}
					}
				}
			}
		}
        return EnumActionResult.PASS;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if(itemStackIn.getMetadata() == FilterType.NORMAL.ordinal() || itemStackIn.getMetadata() == FilterType.MOD.ordinal()){
			playerIn.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, worldIn, 0, -1, hand.ordinal());
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn); 
		}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }
}
