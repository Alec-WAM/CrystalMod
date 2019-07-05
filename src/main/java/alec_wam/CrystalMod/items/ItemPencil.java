package alec_wam.CrystalMod.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ItemPencil extends Item {
	
   public ItemPencil(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
    * the Item before the action is complete.
    */
   @Override
   public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) { 
	   BlockPos pos = context.getPos();
	   World world = context.getWorld();
	   PlayerEntity player = context.getPlayer();
	   if(player !=null){
		   TileEntity tile = world.getTileEntity(pos);
		   if(tile instanceof SignTileEntity){	
			   SignTileEntity sign = (SignTileEntity)tile;
			   if(!world.isRemote){
				   //Sets Editable to true
				   ObfuscationReflectionHelper.setPrivateValue(SignTileEntity.class, sign, true, 5);
				   player.openSignEditor(sign);
			   } 
			   stack.damageItem(1, player, (p_220045_0_) -> {
				   p_220045_0_.sendBreakAnimation(context.getHand());
			   });
			   return ActionResultType.SUCCESS;
		   }
	   }
	   return ActionResultType.PASS;
   }
}