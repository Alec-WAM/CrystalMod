package alec_wam.CrystalMod.tiles.workbench;

import java.util.Locale;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
import alec_wam.CrystalMod.util.ItemUtil;

public class BlockCrystalWorkbench extends EnumBlock<BlockCrystalWorkbench.WorkbenchType> implements ITileEntityProvider, ICustomModel  {

	public static final PropertyEnum<WorkbenchType> TYPE = PropertyEnum.<WorkbenchType>create("type", WorkbenchType.class);
	
	public BlockCrystalWorkbench() {
		super(Material.IRON, TYPE, WorkbenchType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f).setResistance(10F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, WorkbenchType.BLUE));
		setHarvestLevel("pickaxe", 0);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		for(WorkbenchType type : WorkbenchType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), "type="+type.getName()));
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing direction, float p_180639_6_, float p_180639_7_, float p_180639_8_)
    {
        TileEntity te = world.getTileEntity(pos);

        if (te == null || !(te instanceof TileEntityCrystalWorkbench))
        {
            return true;
        }

        if (world.isRemote)
        {
            return true;
        }

        player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCrystalWorkbench();
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
		TileEntity tileentitychest = world.getTileEntity(pos);
        if (tileentitychest != null && tileentitychest instanceof IInventory)
        {
        	ItemUtil.dropContent(0, (IInventory)tileentitychest, world, tileentitychest.getPos());
        }
        super.breakBlock(world, pos, blockState);
    }

	public static enum WorkbenchType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		BLUE, RED, GREEN, DARK;

		final int meta;
		
		WorkbenchType(){
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
