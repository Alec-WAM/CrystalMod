package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class ItemCrystalShard extends ItemVariant<EnumCrystalColorSpecial> implements IPlantable {

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
			if(Block.hasSolidSide(state, world, pos, Direction.UP) && state.getMaterial() == Material.ROCK){
				if(world.setBlockState(pos.up(), ModBlocks.crystalShardBlock.getBlock(EnumCrystalColor.convert(type)).getDefaultState(), 2)){
					PlayerEntity entityplayer = blockitemusecontext.getPlayer();
		            if (entityplayer instanceof ServerPlayerEntity) {
		               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)entityplayer, pos, stack);
		            }
		            SoundType soundtype = SoundType.GLASS;
					world.playSound(entityplayer, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					stack.shrink(1);
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.FAIL;
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return ModBlocks.crystalShardBlock.getBlock(EnumCrystalColor.convert(type)).getDefaultState();
	}
	
	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.Cave;
    }
	
}
