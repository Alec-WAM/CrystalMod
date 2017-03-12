package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class BlockPatternEncoder extends EnumBlock<BlockPatternEncoder.EncoderType> implements ICustomModel  {

	public static enum EncoderType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		NORMAL, PROCESSING;

		private final int meta;
		
		EncoderType(){
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
	public static final PropertyEnum<EncoderType> TYPE = PropertyEnum.<EncoderType>create("type", EncoderType.class);
	public BlockPatternEncoder() {
		super(Material.IRON, TYPE, EncoderType.class);
		this.setHardness(2f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
		for(EncoderType type : EncoderType.values())
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), "type"+"="+type.getName()));
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!player.isSneaking()){
			player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}
	
	@Override
    public boolean hasTileEntity(IBlockState state){
    	return true;
    }
    
	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state) {
		EncoderType type = state.getValue(TYPE);
		if(type == EncoderType.PROCESSING){
			return new TileProcessingPatternEncoder();
		}
		return new TilePatternEncoder();
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof IInventory)
        {
            ItemUtil.dropContent(0, (IInventory)tile, world, tile.getPos());
        }
        IItemHandler handler = ItemUtil.getExternalItemHandler(world, pos, EnumFacing.UP);
        if(handler !=null){
        	ItemUtil.dropContent(0, handler, world, pos);
        }
        super.breakBlock(world, pos, blockState);
    }

}
