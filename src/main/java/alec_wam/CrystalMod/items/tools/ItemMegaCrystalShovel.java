package alec_wam.CrystalMod.items.tools;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.tools.IMegaTool;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMegaCrystalShovel extends ItemCrystalShovel implements IMegaTool {

	public ItemMegaCrystalShovel(ToolMaterial material) {
		super(material, "megacrystalshovel");
		setNoRepair();
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		for(BlockPos extraPos : getAOEBlocks(itemstack, player.getEntityWorld(), player, pos)) {
			BlockUtil.breakExtraBlock(itemstack, player.getEntityWorld(), player, extraPos, pos);
		}
		return super.onBlockStartBreak(itemstack, pos, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final Map<String, ModelResourceLocation> models = Maps.newHashMap();
		for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
			ModelResourceLocation loc = new ModelResourceLocation("crystalmod:tool/megashovel", "color="+color);
			models.put(color, loc);
			ModelBakery.registerItemVariants(this, loc);
		}
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	String color = ItemNBTHelper.getString(stack, "Color", "");
            	return models.get(color);
            }
        });
    }
	
	@Override
	public ImmutableList<BlockPos> getAOEBlocks(ItemStack tool, World world, EntityPlayer player, BlockPos pos) {
		return ToolUtil.calcAOEBlocks(tool, world, player, pos, 3, 3, 1);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
        	boolean didSomething = false;
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if (facing != EnumFacing.DOWN && worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR && block == Blocks.GRASS)
            {
                IBlockState iblockstate1 = Blocks.GRASS_PATH.getDefaultState();
                worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (!worldIn.isRemote)
                {
                    worldIn.setBlockState(pos, iblockstate1, 11);
                    itemstack.damageItem(1, player);
                }

                didSomething = true;
            }
            
        	for(BlockPos aoePos : getAOEBlocks(itemstack, worldIn, player, pos)){
            	if(player.canPlayerEdit(aoePos.offset(facing), facing, itemstack)){
	            	iblockstate = worldIn.getBlockState(aoePos);
	                block = iblockstate.getBlock();
	
	                if (facing != EnumFacing.DOWN && worldIn.getBlockState(aoePos.up()).getMaterial() == Material.AIR && block == Blocks.GRASS)
	                {
	                    IBlockState iblockstate1 = Blocks.GRASS_PATH.getDefaultState();
	                    worldIn.playSound(player, aoePos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
	
	                    if (!worldIn.isRemote)
	                    {
	                        worldIn.setBlockState(aoePos, iblockstate1, 11);
	                        itemstack.damageItem(1, player);
	                    }
	
	                    didSomething = true;
	                }
            	}
            }
            return didSomething ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
        }
    }
	
	@Override
	public int getMaxDamage(ItemStack stack)
    {
		//Triple the durability
		return super.getMaxDamage(stack) * 3;
    }

}
