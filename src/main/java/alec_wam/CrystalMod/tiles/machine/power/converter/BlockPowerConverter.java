package alec_wam.CrystalMod.tiles.machine.power.converter;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;

public class BlockPowerConverter extends EnumBlock<BlockPowerConverter.ConverterType> implements ITileEntityProvider, ICustomModel {

	public static final PropertyEnum<ConverterType> TYPE = PropertyEnum.<ConverterType>create("energy", ConverterType.class);
	
	public BlockPowerConverter() {
		super(Material.IRON, TYPE, ConverterType.class);
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, ConverterType.RF));
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		for(ConverterType type : ConverterType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), "energy"+"="+type.getName()));
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
		if(ItemStackTools.isValid(stack)){
			int meta = stack.getMetadata();
			if(meta == ConverterType.CU.getMeta()){
				tooltip.add("CU Value: "+PowerUnits.RF.conversionRation+":"+PowerUnits.CU.conversionRation);
			}
			if(meta == ConverterType.RF.getMeta()){
				tooltip.add("RF Value: "+PowerUnits.CU.conversionRation+":"+PowerUnits.RF.conversionRation);
			}
		}
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEnergyConverterRFtoCU){
			TileEnergyConverterRFtoCU con = (TileEnergyConverterRFtoCU)tile;
			if(!world.isRemote){
				ChatUtil.sendNoSpam(player, TextFormatting.AQUA+""+(con.energyStorage.getCEnergyStored())+" / "+(con.energyStorage.getMaxCEnergyStored()+" CU"));
			}
			return true;
		}
		if(tile !=null && tile instanceof TileEnergyConverterCUtoRF){
			TileEnergyConverterCUtoRF con = (TileEnergyConverterCUtoRF)tile;
			if(!world.isRemote){
				ChatUtil.sendNoSpam(player, TextFormatting.RED+""+(con.energyStorage.getEnergyStored())+" / "+(con.energyStorage.getMaxEnergyStored()+" RF"));
			}
			return true;
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(meta == 1)return new TileEnergyConverterCUtoRF();
		return new TileEnergyConverterRFtoCU();
	}
	
	public static enum ConverterType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		RF, CU;

		final int meta;
		
		ConverterType(){
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
