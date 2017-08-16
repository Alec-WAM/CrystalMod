package alec_wam.CrystalMod.items;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCursedBone extends Item implements ICustomModel {

	public ItemCursedBone(){
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "cursedbone");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(BoneType type : BoneType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + BoneType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < BoneType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack stack = player.getHeldItem(hand);
		if(ItemStackTools.isValid(stack) && stack.getMetadata() == BoneType.BONEMEAL.getMetadata()){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() == Blocks.NETHER_WART){
				int i = state.getValue(BlockNetherWart.AGE).intValue();

		        if (i < 3 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, true))
		        {
		            state = state.withProperty(BlockNetherWart.AGE, Integer.valueOf(i + 1));
		            world.setBlockState(pos, state, 2);
		            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
		            
		            if(!player.capabilities.isCreativeMode){
		            	player.setHeldItem(hand, ItemUtil.consumeItem(stack));
		            }		            
		            return EnumActionResult.SUCCESS;
		        }
			}
		}
		return EnumActionResult.PASS;
    }
	
	public static enum BoneType implements IStringSerializable, IEnumMetaItem
    {
        BONE(0, "bone"),
        BONEMEAL(1, "bonemeal");

        private static final BoneType[] METADATA_LOOKUP = new BoneType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private BoneType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        @Override
		public int getMetadata()
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

        public static BoneType byMetadata(int metadata)
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
            for (BoneType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
	
}
