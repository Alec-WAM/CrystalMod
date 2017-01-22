package alec_wam.CrystalMod.tiles.chest;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.Lists;

public class BlockCrystalChest extends BlockContainer implements ICustomModel 
{
    public static final PropertyEnum<CrystalChestType> VARIANT_PROP = PropertyEnum.create("variant", CrystalChestType.class);

    public BlockCrystalChest()
    {
        super(Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT_PROP, CrystalChestType.BLUE));

        this.setHardness(3.0F);
        this.setCreativeTab(CrystalMod.tabBlocks);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos){
    	return new AxisAlignedBB(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }
    
    @SideOnly(Side.CLIENT)
    public void initModel(){
    	for(CrystalChestType type : CrystalChestType.values()){
    		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.ordinal(), new ModelResourceLocation(getRegistryName(), "variant="+type.getName().toLowerCase()));
			registerTileEntitySpecialRenderer(type.clazz);
    	}
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @SideOnly(Side.CLIENT)
    public <T extends TileEntityBlueCrystalChest> void registerTileEntitySpecialRenderer(Class<T> type)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(type, new TileEntityBlueCrystalChestRenderer<T>(type));
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand, EnumFacing direction, float p_180639_6_, float p_180639_7_, float p_180639_8_)
    {
        TileEntity te = world.getTileEntity(pos);

        if (te == null || !(te instanceof TileEntityBlueCrystalChest))
        {
            return true;
        }

        if (world.isSideSolid(pos.add(0, 1, 0), EnumFacing.DOWN))
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
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return CrystalChestType.makeEntity(metadata);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for (CrystalChestType type : CrystalChestType.values())
        {
            if (type.isValidForCreativeMode())
            {
                list.add(new ItemStack(itemIn, 1, type.ordinal()));
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT_PROP, CrystalChestType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState blockState)
    {
        return ((CrystalChestType) blockState.getValue(VARIANT_PROP)).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty<?>[] { VARIANT_PROP });
    }

    @Override
    public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        ArrayList<ItemStack> items = Lists.newArrayList();
        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));
        items.add(stack);
        return items;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState blockState)
    {
        super.onBlockAdded(world, pos, blockState);
        world.notifyNeighborsOfStateChange(pos, this, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, EntityLivingBase entityliving, ItemStack itemStack)
    {
        byte chestFacing = 0;
        int facing = MathHelper.floor((entityliving.rotationYaw * 4F) / 360F + 0.5D) & 3;
        if (facing == 0)
        {
            chestFacing = 2;
        }
        if (facing == 1)
        {
            chestFacing = 5;
        }
        if (facing == 2)
        {
            chestFacing = 3;
        }
        if (facing == 3)
        {
            chestFacing = 4;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityBlueCrystalChest)
        {
            TileEntityBlueCrystalChest teic = (TileEntityBlueCrystalChest) te;
            teic.wasPlaced(entityliving, itemStack);
            teic.setFacing(chestFacing);
            BlockUtil.markBlockForUpdate(world, pos);
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return CrystalChestType.validateMeta(((CrystalChestType) state.getValue(VARIANT_PROP)).ordinal());
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
        TileEntityBlueCrystalChest tileentitychest = (TileEntityBlueCrystalChest) world.getTileEntity(pos);
        if (tileentitychest != null)
        {
            tileentitychest.removeAdornments();
            ItemUtil.dropContent(0, tileentitychest, world, tileentitychest.getPos());
        }
        super.breakBlock(world, pos, blockState);
    }

    

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBlueCrystalChest)
        {
            TileEntityBlueCrystalChest teic = (TileEntityBlueCrystalChest) te;
            if (teic.getType().isExplosionResistant())
            {
                return 10000F;
            }
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInventory)
        {
            return Container.calcRedstoneFromInventory((IInventory) te);
        }
        return 0;
    }

    private static final EnumFacing[] validRotationAxes = new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN };

    @Override
    public EnumFacing[] getValidRotations(World worldObj, BlockPos pos)
    {
        return validRotationAxes;
    }

    @Override
    public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis)
    {
        if (worldObj.isRemote)
        {
            return false;
        }
        if (axis == EnumFacing.UP || axis == EnumFacing.DOWN)
        {
            TileEntity tileEntity = worldObj.getTileEntity(pos);
            if (tileEntity instanceof TileEntityBlueCrystalChest)
            {
                TileEntityBlueCrystalChest icte = (TileEntityBlueCrystalChest) tileEntity;
                icte.rotateAround();
            }
            return true;
        }
        return false;
    }
}
