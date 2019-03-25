package alec_wam.CrystalMod.blocks.crops;

import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBlockShieldMushroom extends ItemBlock {

	public static final float SHIELD_VALUE = ExtendedPlayer.DEFAULT_MAX_BLUE_SHIELD / 20;
	
	public ItemBlockShieldMushroom(Block block) {
		super(block);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
        if (ePlayer.getBlueShield() < ePlayer.getMaxBlueShield())
        {
            player.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        else
        {
            return super.onItemRightClick(world, player, hand);
        }
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.EAT;
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
		boolean creative = false;
		if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entityLiving;
            ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
            ePlayer.addShield(SHIELD_VALUE);
            if(!worldIn.isRemote){
            	ePlayer.needsSync = true;
            }
            creative = player.capabilities.isCreativeMode;
        }

        if(!creative)stack.shrink(1);
        return stack;
    }

}
