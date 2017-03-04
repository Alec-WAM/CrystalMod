package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;


import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHDDInterface extends BlockContainer implements ICustomModel {

	public enum HDDType implements IStringSerializable{
		EMPTY, BLUE, RED, GREEN, DARK, PURE;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

	}

	public static final PropertyEnum<HDDType> HDD = PropertyEnum.create("hdd", HDDType.class);
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	
	public BlockHDDInterface() {
		super(Material.IRON);
		this.setHardness(2f);
		setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        StateMap.Builder ignorePower = new StateMap.Builder();
        ModelLoader.setCustomStateMapper(this, ignorePower.build());
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        HDDType hdd = HDDType.EMPTY;
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null && te instanceof TileEntityHDDInterface) {
        	TileEntityHDDInterface interf = (TileEntityHDDInterface)te;
        	face = EnumFacing.getFront(interf.facing);
        	if(interf.getStackInSlot(0) !=null && interf.getStackInSlot(0).getItem() instanceof ItemHDD){
        		hdd = HDDType.values()[(interf.getStackInSlot(0).getMetadata()+1)%(HDDType.values().length)];
        	}
        }
        return state.withProperty(HDD, hdd).withProperty(FACING, face);
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
        return new BlockStateContainer(this, FACING, HDD);
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityHDDInterface)) {
        	TileEntityHDDInterface inter = (TileEntityHDDInterface)tile;
        	if(!world.isRemote){
        		if(inter.getNetwork() == null || inter.getNetwork().hasAbility(player, NetworkAbility.VIEW)){
        			if(ToolUtil.isToolEquipped(player, hand)){
        				if(player.isSneaking()){
        					if(!ItemStackTools.isNullStack(inter.getStackInSlot(0))){
        						EnumFacing face = EnumFacing.getFront(inter.facing);
        						ItemUtil.spawnItemInWorldWithoutMotion(world, inter.getStackInSlot(0), pos.offset(face));
        						inter.setInventorySlotContents(0, ItemStackTools.getEmptyStack());
        						return true;
        					}
        				}
        				return true;
        			}
        			ItemStack stack = player.getHeldItem(hand);
        			if(ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemHDD && ItemStackTools.isNullStack(inter.getStackInSlot(0))){
        				inter.setInventorySlotContents(0, stack);
        				player.setHeldItem(hand, ItemStackTools.getEmptyStack());
        				return true;
        			}

        			player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        			return true;
        		} else {
        			ChatUtil.sendNoSpam(player, Lang.localize("gui.networkability."+NetworkAbility.VIEW.getId()));
        		}
        	}
        }
    	return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHDDInterface();
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
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing face = getFacingFromEntity(pos, placer);
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityHDDInterface){
        	((TileEntityHDDInterface)tile).facing = face.getIndex();
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
        if(te !=null && te instanceof TileEntityHDDInterface){
        	TileEntityHDDInterface bat = (TileEntityHDDInterface)te;
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
