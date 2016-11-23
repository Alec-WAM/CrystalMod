package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBackpackBase extends Item implements ICustomModel {

	private final IBackpack backpack;
	
	public ItemBackpackBase(IBackpack backpack){
		this.backpack = backpack;
		this.setMaxStackSize(1);
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "backpack_"+backpack.getID().getResourcePath());
	}
	
	public IBackpack getBackpack(){
		return backpack;
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		backpack.initModel(this);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
		backpack.update(stack, world, entity, itemSlot, isSelected);
    }
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		return backpack.itemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		return backpack.rightClick(itemStackIn, worldIn, playerIn, hand);
    }
}
