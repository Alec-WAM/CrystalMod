package alec_wam.CrystalMod.items.guide;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ModLogger;
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

public class ItemCrystalGuide extends Item {

	public ItemCrystalGuide(){
		setMaxStackSize(1);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "guide");
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		/*RayTraceResult ray = EntityUtil.getRayTraceEntity(player, CrystalMod.proxy.getReachDistanceForPlayer(player), false);
		if(ray !=null){
			Entity entity = ray.entityHit;
			if(entity == null)return EnumActionResult.PASS;
			ItemStack entityItem = EntityUtil.getItemFromEntity(entity, ray);
			if(ItemStackTools.isValid(entityItem)){
				LookupResult result = GuidePages.getGuideData(player, entityItem);
				if(result !=null){
					CrystalMod.proxy.setForcedGuidePage(result);
					player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_GUIDE, world, 0, 0, 0);
					return EnumActionResult.SUCCESS;
				}
			}
		}*/
		/*ItemStack blockStack = ItemUtil.getItemFromBlock(world.getBlockState(pos));
		if(ItemStackTools.isValid(blockStack)){
			LookupResult result = GuidePages.getGuideData(player, blockStack);
			if(result !=null){
				CrystalMod.proxy.setForcedGuidePage(result);
				player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_GUIDE, world, 0, 0, 0);
				return EnumActionResult.SUCCESS;
			}
		}*/
		return EnumActionResult.PASS;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		if(playerIn.isSneaking() && worldIn.isRemote){
			GuidePages.createPages();
			ModLogger.info("Created Pages");

			ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(playerIn);
			if(exPlayer !=null){
				exPlayer.lastOpenBook = null;
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
		}
		playerIn.openGui(CrystalMod.instance, GuiHandler.GUI_ID_GUIDE, worldIn, 0, 0, 0);
		playerIn.playSound(ModSounds.book_open, 0.5F, 1.0F);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
    }
	
}
