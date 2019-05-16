package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem.FilterSettings;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.gui.GuiScreen;
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
import net.minecraft.util.NonNullList;
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
		FilterSettings settings = new FilterSettings(stack);
		if(!filterName.isEmpty()){
			tooltip.add(new TextComponentString(TextFormatting.GOLD + "" + TextFormatting.ITALIC + filterName));
		}
		if(settings.isBlacklist()){
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.blacklist"));
		} else {
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.whitelist"));
		}
		if(settings.isDamage()){
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.damage"));
		}
		if(settings.isNBT()){
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.nbt"));			
		}
		if(settings.useTag()){
			tooltip.add(new TextComponentTranslation("crystalmod.info.filter.tag"));			
		}
		
		if(ItemNBTHelper.verifyExistance(stack, "FilterItems")){
			tooltip.add(new TextComponentString(""));
			if(!GuiScreen.isShiftKeyDown()){
				tooltip.add(new TextComponentTranslation("crystalmod.info.filter.shift"));
			} else {
				tooltip.add(new TextComponentTranslation("crystalmod.info.filter.listheader"));
				buildFilterList(stack, tooltip);
			}
		}
	}
	
	private static void buildFilterList(ItemStack filter, List<ITextComponent> names){
		if(ItemNBTHelper.verifyExistance(filter, "FilterItems")){
			NonNullList<ItemStack> stacks = TileEntityPipeItem.loadFilterStacks(filter);
			for(ItemStack stack : stacks){
				if(ItemStackTools.isValid(stack)){
					if(stack.getItem() == ModItems.pipeFilter){
						//Load that filter
						String filterName = ItemNBTHelper.getString(stack, "FilterName", "");
						if(!filterName.isEmpty()){
							names.add(new TextComponentString(TextFormatting.GOLD + "" + TextFormatting.ITALIC + filterName));
							continue;
						} else {
							names.add(new TextComponentTranslation("crystalmod.info.filter.otherfilter"));
						}
						continue;
					} 
					names.add(stack.getDisplayName());
				}
			}
		}
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
