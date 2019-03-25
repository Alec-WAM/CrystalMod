package alec_wam.CrystalMod.util.tool;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

public class TreeUtil {

	
	public static boolean isLog(ItemStack stack) {
		if(ItemStackTools.isNullStack(stack)) return false;
		return ItemUtil.itemStackMatchesOredict(stack, "logWood");
	}
	
	public static boolean isLog(IBlockState state) {
		if(state.getBlock() == Blocks.LOG || state.getBlock() == Blocks.LOG2)return true;
		ItemStack stack = ItemUtil.getItemFromBlock(state);
		if(ItemStackTools.isNullStack(stack)) return false;
		return ItemUtil.itemStackMatchesOredict(stack, "logWood");
	}

	public static List<ItemStack> doMultiHarvest(ItemStack held, World worldObj, BlockPos bc, Block refBlock, int fortune) {  
	    
		IBlockState bs = worldObj.getBlockState(bc);
	    Block block = bs.getBlock();
	    bs = bs.getActualState(worldObj, bc);
	    
	    EntityPlayer player = FakePlayerUtil.getPlayer((WorldServer)worldObj);
	    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
	    boolean hasSilk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, held) != 0;
	    
	    List<ItemStack> itemDrops = block.getDrops(worldObj, bc, bs, 0);
	    float chance = ForgeEventFactory.fireBlockHarvesting(itemDrops, worldObj, bc, bs,
	    		fortune, 1, hasSilk, player);
	    worldObj.setBlockToAir(bc);
	    List<ItemStack> realDrops = Lists.newArrayList();
	    if (itemDrops != null) {
	        for (ItemStack stack : itemDrops) {
	          if (worldObj.rand.nextFloat() <= chance) {
	        	  realDrops.add(stack);
	        	  if (block == refBlock) { // other wise leaves
	        		  held.getItem().onBlockDestroyed(held, worldObj, bs, bc, player);
	        	  } 
	          }
	        }
	    }
	    held = player.getHeldItemMainhand();
	    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
	    return realDrops;
	}
	
	public static void applyDamage(ItemStack stack, int damage, boolean isMultiharvest) {
		damage = stack.getItemDamage() + damage;
		if(damage >= stack.getMaxDamage()) {
			ItemStackTools.makeEmpty(stack);
		}
		stack.setItemDamage(damage);
	}
	
}
