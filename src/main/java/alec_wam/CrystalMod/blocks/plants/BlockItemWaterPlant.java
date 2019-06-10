package alec_wam.CrystalMod.blocks.plants;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockItemWaterPlant extends BlockItem {
   public BlockItemWaterPlant(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   @Override
   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
	   return ActionResultType.PASS;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   @Override
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      RayTraceResult raytraceresult = func_219968_a(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
      if (raytraceresult == null) {
         return new ActionResult<>(ActionResultType.PASS, itemstack);
      } else {
         if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
        	BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
    	    BlockPos blockpos = blockraytraceresult.getPos();
            if (!worldIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(blockraytraceresult.getFace()), blockraytraceresult.getFace(), itemstack)) {
               return new ActionResult<>(ActionResultType.FAIL, itemstack);
            }

            BlockPos blockpos1 = blockpos.up();
            BlockState iblockstate = worldIn.getBlockState(blockpos);
            Material material = iblockstate.getMaterial();
            IFluidState ifluidstate = worldIn.getFluidState(blockpos);
            if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE) && worldIn.isAirBlock(blockpos1)) {

               // special case for handling block placement with water lilies
               net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, blockpos1);
               worldIn.setBlockState(blockpos1, getBlock().getDefaultState(), 11);
               if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot, net.minecraft.util.Direction.UP)) {
                  blocksnapshot.restore(true, false);
                  return new ActionResult<ItemStack>(ActionResultType.FAIL, itemstack);
               }

               if (playerIn instanceof ServerPlayerEntity) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, blockpos1, itemstack);
               }

               if (!playerIn.playerAbilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               playerIn.addStat(Stats.ITEM_USED.get(this));
               worldIn.playSound(playerIn, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
               return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
            }
         }

         return new ActionResult<>(ActionResultType.FAIL, itemstack);
      }
   }
}