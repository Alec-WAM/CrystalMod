package alec_wam.CrystalMod.tiles.machine.mobGrinder;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMobGrinder extends BlockMachine {

	public BlockMobGrinder() {
		super(Material.IRON);
		this.setResistance(2000.0f);
		this.setHardness(2f);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityMobGrinder){
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMobGrinder();
	}

}
