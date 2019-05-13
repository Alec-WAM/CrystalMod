package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemPipeFilter extends Item {

	public ItemPipeFilter(Properties properties) {
		super(properties);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		String filterName = ItemNBTHelper.getString(stack, "FilterName", "");
		boolean blacklist = ItemNBTHelper.getBoolean(stack, "BlackList", false);
		boolean meta = ItemNBTHelper.getBoolean(stack, "MetaMatch", false);
		boolean nbt = ItemNBTHelper.getBoolean(stack, "NBTMatch", false);
		if(!filterName.isEmpty()){
			tooltip.add(new TextComponentString(TextFormatting.GOLD + "" + TextFormatting.ITALIC + filterName));
		}
		if(blacklist){
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.blacklist"));
		} else {
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.whitelist"));
		}
		if(meta){
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.damage"));
		}
		if(nbt){
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.nbt"));			
		}
		
		//TODO Add Items to list
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);
		if(world.isRemote){
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else {
			if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
	        {
	            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;

	            NetworkHooks.openGui(entityPlayerMP, new FilterGui(stack, handIn), buf -> buf.writeEnumValue(handIn));
	            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	        }
		}		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}
	
	public class FilterGui implements IInteractionObject {

		private ItemStack stack;
		private EnumHand hand;
		public FilterGui(ItemStack stack, EnumHand hand){
			this.stack = stack;
			this.hand = hand;
		}
		
		@Override
		public ITextComponent getName() {
			return stack.getDisplayName();
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getCustomName() {
			return null;
		}

		@Override
		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new ContainerPipeFilter(playerIn, stack, hand);
		}

		@Override
		public String getGuiID() {
			return GuiHandler.ITEM_NORMAL.toString();
		}
		
	}

}
