package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGlowBerry extends Item implements ICustomModel {
	public ItemGlowBerry()
    {
    	this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabCrops);
		ModItems.registerItem(this, "glowberry");
    }
    
	@Override
	public boolean hasEffect(ItemStack stack){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(PlantType type : PlantType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMeta(), new ModelResourceLocation(getRegistryName(), type.getName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + PlantType.values()[i].getName();
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < PlantType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	ItemStack stack = playerIn.getHeldItem(hand);
    	if (side == EnumFacing.UP)
        {
            return EnumActionResult.PASS;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return EnumActionResult.PASS;
        }
    	IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (block.isSideSolid(iblockstate, worldIn, pos, side))
        {
        	pos = pos.offset(side);

            if (worldIn.isAirBlock(pos))
            {
            	Block glowBlock = ModBlocks.glowBerryBlue;
            	PlantType type = PlantType.values()[MathHelper.clamp(stack.getMetadata(), 0, PlantType.values().length-1)];
            	if(type == PlantType.RED){
            		glowBlock = ModBlocks.glowBerryRed;
            	}
            	if(type == PlantType.GREEN){
            		glowBlock = ModBlocks.glowBerryGreen;
            	}
            	if(type == PlantType.DARK){
            		glowBlock = ModBlocks.glowBerryDark;
            	}
                @SuppressWarnings("deprecation")
				IBlockState iblockstate1 = glowBlock.getStateForPlacement(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
                worldIn.setBlockState(pos, iblockstate1, 10);
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                
                if (!playerIn.capabilities.isCreativeMode)
                {
                	ItemStackTools.incStackSize(stack, -1);
                }
            }

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}