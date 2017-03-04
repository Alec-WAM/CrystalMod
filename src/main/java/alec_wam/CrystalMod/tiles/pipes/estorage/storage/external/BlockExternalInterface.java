package alec_wam.CrystalMod.tiles.pipes.estorage.storage.external;


import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockExternalInterface extends BlockContainer implements ICustomModel {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	
	public BlockExternalInterface() {
		super(Material.IRON);
		this.setHardness(1f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        StateMap.Builder ignorePower = new StateMap.Builder();
        ModelLoader.setCustomStateMapper(this, ignorePower.build());
    }
	
	@Override
    @SideOnly(Side.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null && te instanceof TileEntityExternalInterface) {
        	TileEntityExternalInterface interf = (TileEntityExternalInterface)te;
        	face = EnumFacing.getFront(interf.facing);
        }
        return state.withProperty(FACING, face);
    }
	
	@Override
    public void onNeighborChange( final IBlockAccess worldIn, final BlockPos pos, final BlockPos neighbor )
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && tile instanceof TileEntityExternalInterface) {
        	TileEntityExternalInterface interf = (TileEntityExternalInterface)tile;
        	interf.onNeighborChange();
		}
	}
	
	@Override
    public void neighborChanged(IBlockState state,World worldIn, BlockPos pos, Block neighborBlock, BlockPos from)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && tile instanceof TileEntityExternalInterface) {
        	TileEntityExternalInterface interf = (TileEntityExternalInterface)tile;
        	if(!worldIn.isRemote){
        		interf.onNeighborChange();
        	}
		}
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }
	
	@Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityExternalInterface)) {
        	TileEntityExternalInterface inter = (TileEntityExternalInterface)tile;
        	
        	
        	if(ItemStackTools.isEmpty(player.getHeldItem(hand))){
        		if(!world.isRemote){
        			if(inter.getNetwork() !=null){
                		if(!inter.getNetwork().hasAbility(player, NetworkAbility.SETTINGS)){
                			ChatUtil.sendNoSpam(player, Lang.localize("gui.networkability."+NetworkAbility.SETTINGS.getId()));
                			return false;
                		}
                	}
    	        	
		        	if(player.isSneaking()){
		        		inter.setPriority(inter.getPriority()-1);
		        	}else{
		        		inter.setPriority(inter.getPriority()+1);
		        	}
		        	ChatUtil.sendNoSpam(player, "Priority: "+inter.getPriority());
	        	}
	        	return true;
        	}
        }
    	return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityExternalInterface();
	}
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing face = getFacingFromEntity(pos, placer);
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityExternalInterface){
        	((TileEntityExternalInterface)tile).facing = face.getIndex();
        	BlockUtil.markBlockForUpdate(world, pos);
        }
    }
    
    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entityIn) {
        if (MathHelper.abs((float) entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.posY + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return EnumFacing.UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof TileEntityExternalInterface){
        	TileEntityExternalInterface bat = (TileEntityExternalInterface)te;
        	int next = bat.facing;
        	next++;
        	next%=6;
        	bat.facing = next;
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }
    
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
        return EnumFacing.VALUES;
    }
    
    public static EnumFacing getFacing(int meta) {
        return EnumFacing.values()[meta & 7];
    }

}
