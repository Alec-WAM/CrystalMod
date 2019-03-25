package alec_wam.CrystalMod.tiles.tooltable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnhancementTable extends BlockContainer {

	public BlockEnhancementTable() {
		super(Material.IRON);
		this.setSoundType(SoundType.METAL);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f).setResistance(15F);
		setHarvestLevel("pickaxe", 0);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand, EnumFacing direction, float p_180639_6_, float p_180639_7_, float p_180639_8_)
    {
        TileEntity te = world.getTileEntity(pos);

        if (te == null || !(te instanceof TileEnhancementTable))
        {
            return true;
        }

        if (world.isRemote)
        {
            return true;
        }
        
        if(player.isSneaking()) return false;

        player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEnhancementTable();
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
		TileEntity tileentitychest = world.getTileEntity(pos);
        if (tileentitychest != null && tileentitychest instanceof TileEnhancementTable)
        {
        	if(ItemStackTools.isValid(((TileEnhancementTable)tileentitychest).getStackInSlot(0)))ItemUtil.spawnItemInWorldWithRandomMotion(world, ((TileEnhancementTable)tileentitychest).getStackInSlot(0), pos);;
        }
        super.breakBlock(world, pos, blockState);
    }
}
