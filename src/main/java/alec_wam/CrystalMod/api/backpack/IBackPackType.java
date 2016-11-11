package alec_wam.CrystalMod.api.backpack;

import java.util.List;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBackPackType {

	public String getType(ItemStack stack);
	
	public ResourceLocation getID();
	
	@SideOnly(Side.CLIENT)
	public void renderDynamic(TransformType type);
	
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> list, boolean adv);
	
	public static enum OpenContext{
		BACK, MAIN, OFF;
	}
	
	public boolean canOpen(EntityPlayer player, ItemStack backpack, OpenContext type);
	
	public boolean canEquip(EntityPlayer player, ItemStack backpack);
	
	public void onBackOpen(EntityPlayer player, ItemStack backpack);
	
	public Object getContainer(EntityPlayer player, ItemStack backpack, OpenContext type);
	
	public Object getGui(EntityPlayer player, ItemStack backpack, OpenContext type);
	
	public void onEquipped(World world, EntityPlayer player, ItemStack stack);
	
	public void onUnequipped(World world, EntityPlayer player, ItemStack stack);
	
	public void update(ItemStack backpack, World world, Entity entity, int par4, boolean par5);
	
	public boolean onUse(ItemStack stack, EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ);
	
	public ItemStack onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand);
	
}
