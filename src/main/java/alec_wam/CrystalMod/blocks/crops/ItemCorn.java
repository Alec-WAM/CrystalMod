package alec_wam.CrystalMod.blocks.crops;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCorn extends Item implements ICustomModel {

	public ItemCorn(){
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabCrops);
		ModItems.registerItem(this, "cornitem");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(CornItemType type : CornItemType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMeta(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + CornItemType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < CornItemType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(ItemStackTools.isValid(stack) && stack.getMetadata() == CornItemType.KERNELS.getMeta()){
			if(facing != EnumFacing.UP){
				return EnumActionResult.PASS; 
			}
			if(playerIn.canPlayerEdit(pos, facing, stack) && playerIn.canPlayerEdit(pos.up(), facing, stack)){
				IBlockState cornState = ModBlocks.corn.getDefaultState().withProperty(BlockCorn.AGE, 0).withProperty(BlockCorn.TOP, false);
				IBlockState ground = worldIn.getBlockState(pos);
				if(ground.getBlock().canSustainPlant(ground, worldIn, pos, EnumFacing.UP, ModBlocks.corn) && worldIn.isAirBlock(pos.up())){
					worldIn.setBlockState(pos.up(), cornState, 3);
					SoundType soundtype = cornState.getBlock().getSoundType(cornState, worldIn, pos.up(), playerIn);
					worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					if(!playerIn.capabilities.isCreativeMode){
						ItemStackTools.incStackSize(stack, -1);
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
        return EnumActionResult.PASS;
    }
	
	public static enum CornItemType implements IStringSerializable, IEnumMeta
    {
        KERNELS(0, "kernels"),
        CORN(1, "corn");

        private static final CornItemType[] METADATA_LOOKUP = new CornItemType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private CornItemType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        @Override
		public int getMeta()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        @Override
		public String toString()
        {
            return this.unlocalizedName;
        }

        public static CornItemType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        @Override
		public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (CornItemType type : values())
            {
                METADATA_LOOKUP[type.getMeta()] = type;
            }
        }
    }
	
}
