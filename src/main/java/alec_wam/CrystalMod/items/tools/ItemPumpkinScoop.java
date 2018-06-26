package alec_wam.CrystalMod.items.tools;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyPumpkin;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyPumpkin.PumpkinType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPumpkinScoop extends Item {

	public ItemPumpkinScoop(){
		super();
		this.setMaxStackSize(1);
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "pumpkinscoop");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack otherHand = player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
		if(ItemStackTools.isValid(otherHand)){
			if(ToolUtil.isAxe(otherHand)){
				boolean isCreative = player.capabilities.isCreativeMode;
				IBlockState state = world.getBlockState(pos);
				boolean didAction = false;
				if(state.getBlock() == Blocks.PUMPKIN){
					world.setBlockState(pos, ModBlocks.fancyPumpkin.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)).withProperty(BlockFancyPumpkin.TYPE, PumpkinType.STEVE));
					didAction = true;
				}
				if(state.getBlock() == ModBlocks.fancyPumpkin){
					switch(state.getValue(BlockFancyPumpkin.TYPE)){
						case STEVE :{
							world.setBlockState(pos, ModBlocks.fancyPumpkin.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)).withProperty(BlockFancyPumpkin.TYPE, PumpkinType.ALEX));
							break;
						}
						case ALEX :{
							world.setBlockState(pos, ModBlocks.fancyPumpkin.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)).withProperty(BlockFancyPumpkin.TYPE, PumpkinType.CREEPER));
							break;
						}
						case CREEPER :{
							world.setBlockState(pos, ModBlocks.fancyPumpkin.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)).withProperty(BlockFancyPumpkin.TYPE, PumpkinType.SPIDER));
							break;
						}
						default:{
							world.setBlockState(pos, Blocks.PUMPKIN.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)));
							break;
						}
					}
					didAction = true;
				}
				
				if(didAction){
					if(!isCreative){
						otherHand.damageItem(1, player);
						player.swingArm(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
        return EnumActionResult.PASS;
    }
}
