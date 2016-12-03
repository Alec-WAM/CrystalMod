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

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackInventory;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackNormal;

public class BackpackNormal implements IBackpackInventory {

	public final ResourceLocation ID = CrystalMod.resourceL("normal");
	public final ResourceLocation TEXTURE = CrystalMod.resourceL("textures/model/backpack/normal.png");
	public final Map<CrystalBackpackType, ResourceLocation> TEXTURES = Maps.newHashMap();
	
	public BackpackNormal(){
		for(CrystalBackpackType type : CrystalBackpackType.values()){
			TEXTURES.put(type, CrystalMod.resourceL("textures/model/backpack/normal_"+type.getUnlocalizedName()+".png"));
		}
		TEXTURES.put(CrystalBackpackType.NORMAL, TEXTURE);
	}
	
	@Override
	public ResourceLocation getID() {
		return ID;
	}
	
	@Override
	public ResourceLocation getTexture(ItemStack stack, int type) {
		return TEXTURES.get(CrystalBackpackType.byMetadata(stack.getMetadata()));
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
		return BackpackUtil.handleBackpackOpening(stack, world, player, hand, false);
	}

	public InventoryBackpack getInventory(EntityPlayer player, ItemStack backpack){
		
		int size = 27+(9*backpack.getItemDamage());
		return new InventoryBackpack(player, backpack, Math.min(size, 72));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world) {
		return new GuiBackpackNormal(getInventory(player, BackpackUtil.getPlayerBackpack(player)));
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world) {
		return new ContainerBackpackNormal(getInventory(player, BackpackUtil.getPlayerBackpack(player)));
	}
	
	

}
