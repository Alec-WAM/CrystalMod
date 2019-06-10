package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCrystalShard extends ItemVariant<EnumCrystalColorSpecial> {

	private EnumCrystalColorSpecial type;
	public ItemCrystalShard(EnumCrystalColorSpecial type, ItemVariantGroup<EnumCrystalColorSpecial, ItemCrystalShard> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.type = type;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if(type == EnumCrystalColorSpecial.PURE)return ActionResultType.FAIL;
		BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
		if (blockitemusecontext.canPlace()) {
			World world = context.getWorld();
			ItemStack stack = context.getItem();
			BlockPos pos = context.getPos();
			BlockState state = world.getBlockState(pos);
			if(Block.func_220056_d(state, world, pos, Direction.UP) && state.getMaterial() == Material.ROCK){
				if(world.setBlockState(pos.up(), ModBlocks.crystalShardBlock.getBlock(EnumCrystalColor.convert(type)).getDefaultState(), 2)){
					PlayerEntity entityplayer = blockitemusecontext.getPlayer();
		            if (entityplayer instanceof ServerPlayerEntity) {
		               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)entityplayer, pos, stack);
		            }
					stack.shrink(1);
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.FAIL;
	}
	
}
