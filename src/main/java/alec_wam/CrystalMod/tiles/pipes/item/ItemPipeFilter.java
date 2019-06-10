package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem.FilterSettings;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

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
			tooltip.add(new StringTextComponent(TextFormatting.GOLD + "" + TextFormatting.ITALIC + filterName));
		}
		if(settings.isBlacklist()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.blacklist"));
		} else {
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.whitelist"));
		}
		if(settings.isDamage()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.damage"));
		}
		if(settings.isNBT()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.nbt"));			
		}
		if(settings.useTag()){
			tooltip.add(new TranslationTextComponent("crystalmod.info.filter.tag"));			
		}
		
		if(ItemNBTHelper.verifyExistance(stack, "FilterItems")){
			tooltip.add(new StringTextComponent(""));
			if(!Screen.hasShiftDown()){
				tooltip.add(new TranslationTextComponent("crystalmod.info.filter.shift"));
			} else {
				tooltip.add(new TranslationTextComponent("crystalmod.info.filter.listheader"));
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
							names.add(new StringTextComponent(TextFormatting.GOLD + "" + TextFormatting.ITALIC + filterName));
							continue;
						} else {
							names.add(new TranslationTextComponent("crystalmod.info.filter.otherfilter"));
						}
						continue;
					} 
					names.add(stack.getDisplayName());
				}
			}
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn) {
		ItemStack stack = player.getHeldItem(handIn);
		if(world.isRemote){
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
		} else {
			if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer))
	        {
	            ServerPlayerEntity entityPlayerMP = (ServerPlayerEntity) player;

	            GuiHandler.openCustomGui(GuiHandler.ITEM_NORMAL, entityPlayerMP, new FilterGui(stack, handIn), buf -> buf.writeEnumValue(handIn));
	            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
	        }
		}		
		return new ActionResult<ItemStack>(ActionResultType.PASS, stack);
	}
	
	public class FilterGui implements INamedContainerProvider {

		private ItemStack stack;
		private Hand hand;
		public FilterGui(ItemStack stack, Hand hand){
			this.stack = stack;
			this.hand = hand;
		}
		
		@Override
		public ITextComponent getDisplayName() {
			return stack.getDisplayName();
		}

		@Override
		public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerIn) {
			return new ContainerPipeFilter(i, playerIn, stack, hand);
		}
		
	}

}
