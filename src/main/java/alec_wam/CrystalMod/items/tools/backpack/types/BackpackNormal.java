package alec_wam.CrystalMod.items.tools.backpack.types;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;

public class BackpackNormal implements IBackpack {

	@Override
	public ResourceLocation getID() {
		return CrystalMod.resourceL("normal");
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(Item item){
		ModItems.initBasicModel(item);
	}
	
	@Override
	public void update(ItemStack stack, World world, Entity entity,	int itemSlot, boolean isSelected) {}

	@Override
	public EnumActionResult itemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> rightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		OpenType type = hand == EnumHand.MAIN_HAND ? OpenType.MAIN_HAND : OpenType.OFF_HAND;
		player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_BACKPACK, world, type.ordinal(), 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world, OpenType type) {
		return new GuiBackpackNormal(player.inventory, type);
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world, OpenType type) {
		return new ContainerBackpackNormal(player.inventory, type);
	}
	
	

}
