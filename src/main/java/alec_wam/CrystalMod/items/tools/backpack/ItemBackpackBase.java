package alec_wam.CrystalMod.items.tools.backpack;

import java.util.List;
import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.client.model.dynamic.ItemRenderDragonWings;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
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
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new ItemRenderBackpack());
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
		backpack.update(stack, world, entity, itemSlot, isSelected);
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		return backpack.itemUse(player.getHeldItem(hand), player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		return backpack.rightClick(playerIn.getHeldItem(hand), worldIn, playerIn, hand);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		UUID ownerUUID = BackpackUtil.getOwner(stack);
		if(ownerUUID !=null){
			String owner = UUIDUtils.fromUUID(ownerUUID);
			if(ProfileUtil.getUsername(ownerUUID) !=ProfileUtil.ERROR){
				owner = ProfileUtil.getUsername(ownerUUID);
			}
			tooltip.add("Owner: "+owner);
		}
    }
	
	@Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, net.minecraft.nbt.NBTTagCompound nbt) {
		return backpack.initCapabilities(stack, nbt);
    }
}
