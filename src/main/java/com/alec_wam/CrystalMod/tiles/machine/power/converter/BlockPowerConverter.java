package com.alec_wam.CrystalMod.tiles.machine.power.converter;

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

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.EnumBlock;
import com.alec_wam.CrystalMod.blocks.ICustomModel;
import com.alec_wam.CrystalMod.util.ChatUtil;

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
	
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEnergyConverterRFtoCU){
			TileEnergyConverterRFtoCU con = (TileEnergyConverterRFtoCU)tile;
			if(!world.isRemote){
				ChatUtil.sendNoSpam(player, TextFormatting.AQUA+""+(con.getCEnergyStored(side))+" / "+(con.getMaxCEnergyStored(side)+" CU"));
			}
			return true;
		}
		if(tile !=null && tile instanceof TileEnergyConverterCUtoRF){
			TileEnergyConverterCUtoRF con = (TileEnergyConverterCUtoRF)tile;
			if(!world.isRemote){
				ChatUtil.sendNoSpam(player, TextFormatting.RED+""+(con.getEnergyStored(side))+" / "+(con.getMaxEnergyStored(side)+" RF"));
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
	
	public static enum ConverterType implements IStringSerializable, com.alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
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
