package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFancyPumpkin extends BlockPumpkin implements ICustomModel {

	public static final PropertyEnum<PumpkinType> TYPE = PropertyEnum.<PumpkinType>create("type", PumpkinType.class);
    
    public static enum PumpkinType implements IEnumMeta, IStringSerializable {
    	STEVE, ALEX, CREEPER, SPIDER;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		@Override
		public int getMeta() {
			return ordinal();
		}

		public static PumpkinType byMetadata(int meta)
        {
            if (meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }
    }
    
    public BlockFancyPumpkin(){
    	super();
    	setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    	setHardness(1.0F);
		this.setHarvestLevel("axe", 0);
    	setSoundType(SoundType.WOOD);
    	setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, PumpkinType.STEVE));
    }
    
    @SideOnly(Side.CLIENT)
    public void initModel(){
    	IStateMapper stateMapper = new StateMap.Builder().build();
		ModelLoader.setCustomStateMapper(this, stateMapper);
		for(PumpkinType type : PumpkinType.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(getRegistryName(), "facing=north,type="+type.getName()));
		}
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
      for(PumpkinType type : PumpkinType.values()) {
    	  list.add(new ItemStack(this, 1, type.getMeta()));
      }
    }
	
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(TYPE, PumpkinType.byMetadata(meta));
    }
    
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
    	PumpkinType type = PumpkinType.byMetadata((meta & 3) % 4);
        EnumFacing enumfacing = EnumFacing.NORTH;

        switch (meta & 12)
        {
            case 0:
            	enumfacing = EnumFacing.SOUTH;
                break;
            case 4:
            	enumfacing = EnumFacing.WEST;
                break;
            case 8:
            	enumfacing = EnumFacing.EAST;
                break;
            default:
            	enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(TYPE, type).withProperty(FACING, enumfacing);
    }
    
    @Override
	public int getMetaFromState(IBlockState state)
    {
    	int i = 0;
        i = i | state.getValue(TYPE).getMeta();

        switch (state.getValue(FACING))
        {
            case SOUTH:
                i |= 4;
                break;
            case WEST:
                i |= 8;
                break;
            case EAST:
                i |= 12;
			default:
				break;
        }
        return i;
    }
    
    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, TYPE});
    }
}
