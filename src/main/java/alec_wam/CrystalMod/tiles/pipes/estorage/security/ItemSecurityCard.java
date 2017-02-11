package alec_wam.CrystalMod.tiles.pipes.estorage.security;

import java.util.List;
import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSecurityCard extends Item {
	
	private static final String NBT_ABILITY = "Ability_%s";
	private static final String NBT_UUID = "PlayerUUID";
	private static final String NBT_NAME = "PlayerName";
	
    public ItemSecurityCard() {
        super();
        setMaxStackSize(1);
    	this.setCreativeTab(CrystalMod.tabItems);
    	ModItems.registerItem(this, "securitycard");
    }

    @Override
    public void addInformation(ItemStack pattern, EntityPlayer player, List<String> list, boolean b) {
    	if (!pattern.hasTagCompound()) {
            return;
        }
    	list.add(getName(pattern));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
    	ItemStack stack = player.getHeldItem(hand);
    	if(!world.isRemote){
    		ItemNBTHelper.resetNBT(stack);
    		ItemNBTHelper.setString(stack, NBT_UUID, UUIDUtils.fromUUID(EntityPlayer.getUUID(player.getGameProfile())));
    		ItemNBTHelper.setString(stack, NBT_NAME, player.getGameProfile().getName());
    	}
    	return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
    
    public static UUID getUUID(ItemStack stack){
    	if(ItemNBTHelper.verifyExistance(stack, NBT_UUID)){
    		return UUIDUtils.fromString(ItemNBTHelper.getString(stack, NBT_UUID, ""));
    	}
    	return null;
    }
    
    public static String getName(ItemStack stack){
    	return ItemNBTHelper.getString(stack, NBT_NAME, "");
    }
    
    public static void setAbility(ItemStack stack, NetworkAbility ability, boolean value){
    	ItemNBTHelper.setBoolean(stack, String.format(NBT_ABILITY, ability.getId()), value);
    }
    
    public static boolean hasAbility(ItemStack stack, NetworkAbility ability){
    	return ItemNBTHelper.getBoolean(stack, String.format(NBT_ABILITY, ability.getId()), true);
    }
    
    public static boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_UUID);
    }
}
