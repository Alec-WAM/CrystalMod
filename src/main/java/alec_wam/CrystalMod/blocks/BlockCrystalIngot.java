package alec_wam.CrystalMod.blocks;

import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCrystalIngot extends EnumBlock<BlockCrystalIngot.CrystalIngotBlockType> {

	public static final PropertyEnum<CrystalIngotBlockType> TYPE = PropertyEnum.<CrystalIngotBlockType>create("type", CrystalIngotBlockType.class);
	
	public BlockCrystalIngot() {
		super(Material.IRON, TYPE, CrystalIngotBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f).setResistance(10F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CrystalIngotBlockType.BLUE));
	}
	
	@Override
 	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
 		return true;
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
	
	public static enum CrystalIngotBlockType implements IStringSerializable, alec_wam.CrystalMod.util.IEnumMeta{
		BLUE, RED, GREEN, DARK, PURE, DARKIRON;

		final int meta;
		
		CrystalIngotBlockType(){
			meta = ordinal();
		}
		
		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.toString().toLowerCase(Locale.US);
		}
		
	}

}
