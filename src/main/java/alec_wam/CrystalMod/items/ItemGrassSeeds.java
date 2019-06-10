package alec_wam.CrystalMod.items;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemGrassSeeds extends ItemVariant<ItemGrassSeeds.EnumGrassSeedItem> {

	public static enum EnumGrassSeedItem implements IStringSerializable {
		GRASS(Blocks.GRASS_BLOCK, true), PODZOL(Blocks.PODZOL, false), MYCELIUM(Blocks.MYCELIUM, true);

		public final Block block;
		private boolean selfSpread;
		EnumGrassSeedItem(Block grassBlock, boolean selfSpread){
			this.block = grassBlock;
			this.selfSpread = selfSpread;
		}
		
		public boolean isSelfSpread(){
			return selfSpread;
		}
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
	}
	
	private EnumGrassSeedItem type;
	public ItemGrassSeeds(EnumGrassSeedItem type, ItemVariantGroup<EnumGrassSeedItem, ItemGrassSeeds> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.type = type;
	}

	//TODO Add Particles
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
		if (blockitemusecontext.canPlace()) {
			World world = context.getWorld();
			ItemStack stack = context.getItem();
			BlockPos pos = context.getPos();
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() == Blocks.DIRT){
				if(type.isSelfSpread()){
					if(world.setBlockState(pos, type.block.getDefaultState(), 2)){
						BlockUtil.playPlaceSound(world, pos, SoundType.GROUND);
						stack.shrink(1);
						return ActionResultType.SUCCESS;
					}
				} else {
					//Do a 3x3 replacement of blocks that are dirt
					List<BlockPos> placeList = Lists.newArrayList();
					List<BlockPos> checkList = Lists.newArrayList();
					checkList.add(pos.north().west());
					checkList.add(pos.north());
					checkList.add(pos.north().east());
					checkList.add(pos.west());
					checkList.add(pos);
					checkList.add(pos.east());
					checkList.add(pos.south().west());
					checkList.add(pos.south());
					checkList.add(pos.south().east());
					for(BlockPos checkPos : checkList){
						BlockState otherState = world.getBlockState(checkPos);
						if(world.isBlockModifiable(context.getPlayer(), checkPos) && otherState.getBlock() == Blocks.DIRT){
							placeList.add(checkPos);
						}
					}
					
					if(!placeList.isEmpty()){
						for(BlockPos placePos : placeList){
							if(world.setBlockState(placePos, type.block.getDefaultState(), 2)){
								BlockUtil.playPlaceSound(world, placePos, SoundType.GROUND);
							}
						}
						stack.shrink(1);
						return ActionResultType.SUCCESS;
					}
				}
			}
		}
		return ActionResultType.FAIL;
	}
	
}
