package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockCrystalOre extends BlockVariant<EnumCrystalColor>{
	
	public BlockCrystalOre(EnumCrystalColor type, BlockVariantGroup<? extends Enum<EnumCrystalColor>, ? extends BlockVariant<EnumCrystalColor>> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
	}
	
	//Silk Touch
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(this);
	}
	
	/*@Override
	public IItemProvider getItemDropped(BlockState state, World worldIn, BlockPos pos, int fortune) {
		return ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.convert(type));
	}
	
	@Override
	public int getItemsToDropCount(BlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
		if (fortune > 0 && this != getItemDropped(this.getStateContainer().getValidStates().iterator().next(), worldIn, pos, fortune)) {
			int i = random.nextInt(fortune + 2) - 1;
			if (i < 0) {
				i = 0;
			}
			return 1 * (i + 1);
		} else {
			return 1;
		}
	}*/
	
	//TODO Re-add random shard drop
	/*@Override
	public void getDrops(BlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
		super.getDrops(state, drops, world, pos, fortune);
		int fortOffset = 0;
        if (fortune > 0)
        {
        	fortOffset = world.rand.nextInt(fortune + 2) - 1;

            if (fortOffset < 0)
            {
            	fortOffset = 0;
            }
        }
        //Drop extra shard randomly
        int shardChance = 5-fortOffset;
        if(shardChance <= 0 || world.rand.nextInt(shardChance) == 0){
        	drops.add(new ItemStack(ModItems.crystalShardGroup.getItem(EnumCrystalColorSpecial.convert(type))));
        }
    }*/
	
	/*@Override
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune)
    {
		World world = reader instanceof World ? (World)reader : null;
		if (world == null || getItemDropped(state, world, pos, fortune) != this) {
			return MathHelper.nextInt(RANDOM, 3, 7);
        }
        return 0;
    }*/

}
