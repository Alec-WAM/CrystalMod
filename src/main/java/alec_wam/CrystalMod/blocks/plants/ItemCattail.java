package alec_wam.CrystalMod.blocks.plants;

import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class ItemCattail extends Item implements IPlantable {

	public ItemCattail(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
		if (blockitemusecontext.canPlace() && blockitemusecontext.getFace() == Direction.UP) {
			World world = context.getWorld();
			ItemStack stack = context.getItem();
			BlockPos pos = context.getPos();
			BlockState placement = ModBlocks.cattail.getStateForPlacement(blockitemusecontext);
			if(canPlace(blockitemusecontext, placement)){
				if(world.setBlockState(pos.up(), placement, 2)){
					PlayerEntity entityplayer = blockitemusecontext.getPlayer();
		            if (entityplayer instanceof ServerPlayerEntity) {
		               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)entityplayer, pos, stack);
		            }
		            SoundType soundtype = placement.getSoundType(world, pos, blockitemusecontext.getPlayer());
					world.playSound(entityplayer, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					stack.shrink(1);
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.FAIL;
	}
	
	protected boolean canPlace(BlockItemUseContext p_195944_1_, BlockState p_195944_2_) {
		PlayerEntity playerentity = p_195944_1_.getPlayer();
		ISelectionContext iselectioncontext = playerentity == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(playerentity);
		return (p_195944_2_.isValidPosition(p_195944_1_.getWorld(), p_195944_1_.getPos())) && p_195944_1_.getWorld().func_217350_a(p_195944_2_, p_195944_1_.getPos(), iselectioncontext);
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return ModBlocks.cattail.getDefaultState();
	}
	
	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.Plains;
    }
}
