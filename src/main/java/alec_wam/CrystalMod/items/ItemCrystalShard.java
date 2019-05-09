package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCrystalShard extends ItemVariant<EnumCrystalColorSpecial> {

	private EnumCrystalColorSpecial type;
	public ItemCrystalShard(EnumCrystalColorSpecial type, ItemVariantGroup<EnumCrystalColorSpecial, ItemCrystalShard> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.type = type;
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext context) {
		if(type == EnumCrystalColorSpecial.PURE)return EnumActionResult.FAIL;
		BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
		if (blockitemusecontext.canPlace()) {
			World world = context.getWorld();
			ItemStack stack = context.getItem();
			BlockPos pos = context.getPos();
			IBlockState state = world.getBlockState(pos);
			if(state.isTopSolid(world, pos) && state.getMaterial() == Material.ROCK){
				if(world.setBlockState(pos.up(), ModBlocks.crystalShardBlock.getBlock(EnumCrystalColor.convert(type)).getDefaultState(), 2)){
					EntityPlayer entityplayer = blockitemusecontext.getPlayer();
		            if (entityplayer instanceof EntityPlayerMP) {
		               CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, pos, stack);
		            }
					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.FAIL;
	}
	
}
