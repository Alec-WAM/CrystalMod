package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
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

public interface IBackpack {

	public ResourceLocation getID();

	//0 = item, 1 = back
	public ResourceLocation getTexture(int renderType);
	
	@SideOnly(Side.CLIENT)
	public void initModel(Item item);
	
	public void update(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected);

	public EnumActionResult itemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ);
	
	public ActionResult<ItemStack> rightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand);
	
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(EntityPlayer player, World world);
	
	public Object getServerGuiElement(EntityPlayer player, World world);

}
