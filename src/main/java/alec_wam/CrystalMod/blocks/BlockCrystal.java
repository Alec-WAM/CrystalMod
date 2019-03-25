package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant;
import alec_wam.CrystalMod.util.IEnumMeta;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCrystal extends EnumBlock<BlockCrystal.CrystalBlockType> {

	public static final PropertyEnum<CrystalBlockType> TYPE = PropertyEnum.<CrystalBlockType>create("type", CrystalBlockType.class);
	
	public BlockCrystal() {
		super(Material.ROCK, TYPE, CrystalBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
        this.setResistance(10.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CrystalBlockType.BLUE));
	}
    
	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
    {
        IBlockState plant = plantable.getPlant(world, pos.offset(direction));
        net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));
        if(plantType == ModBlocks.crystalPlantType){
        	if(plant.getBlock() instanceof BlockCrystalPlant){
        		BlockCrystalPlant.PlantType color = (BlockCrystalPlant.PlantType) plant.getValue(BlockCrystalPlant.TYPE);
        		BlockCrystalPlant.PlantType type = BlockCrystalPlant.getTypeFromBlock(state);
        		if(type !=null && type == color){
        			return true;
        		}
        	}
        }
		return super.canSustainPlant(state, world, pos, direction, plantable);
	}
	
    public static enum CrystalBlockType implements IStringSerializable, IEnumMeta {
		BLUE("blue"),
		RED("red"),
		GREEN("green"),
		DARK("dark"),
		PURE("pure"),
		BLUE_CHISELED("blue_chiseled"),
		RED_CHISELED("red_chiseled"),
		GREEN_CHISELED("green_chiseled"),
		DARK_CHISELED("dark_chiseled"),
		PURE_CHISELED("pure_chiseled"),
		BLUE_BRICK("blue_brick"),
		RED_BRICK("red_brick"),
		GREEN_BRICK("green_brick"),
		DARK_BRICK("dark_brick"),
		PURE_BRICK("pure_brick");

		private final String unlocalizedName;
		public final int meta;

	    CrystalBlockType(String name) {
	      meta = ordinal();
	      unlocalizedName = name;
	    }

	    @Override
	    public String getName() {
	      return unlocalizedName;
	    }

	    @Override
	    public int getMeta() {
	      return meta;
	    }
    	
    }

}
