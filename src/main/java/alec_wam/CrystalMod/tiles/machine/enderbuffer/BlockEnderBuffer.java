package alec_wam.CrystalMod.tiles.machine.enderbuffer;

import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.FakeTileState;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnderBuffer extends BlockMachine implements ICustomModel
{
    public BlockEnderBuffer()
    {
        super(Material.IRON);
        this.setHardness(2f);
        this.setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void initModel(){
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));

    	ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "normal"), ModelEnderBuffer.INSTANCE);
    	ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "active=false,facing=north"), ModelEnderBuffer.INSTANCE);
    	ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "active=true,facing=north"), ModelEnderBuffer.INSTANCE);
    	ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "inventory"), ModelEnderBuffer.INSTANCE);
    }

    @Override
    protected ItemStack getNBTDrop(IBlockAccess world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this, 1, damageDropped(world.getBlockState(pos)));
		if(tileEntity !=null && tileEntity instanceof TileEntityEnderBuffer){
			TileEntityEnderBuffer buffer = (TileEntityEnderBuffer)tileEntity;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Code", buffer.code);
			UUID uuid = buffer.getPlayerBound();
			if(uuid !=null)nbt.setString("Owner", UUIDUtils.fromUUID(uuid));
			ItemNBTHelper.getCompound(stack).setTag(TILE_NBT_STACK, nbt);
		}
		return stack;
	}
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
        boolean update = false;
        if(ItemNBTHelper.verifyExistance(stack, TILE_NBT_STACK)){
        	if(tile !=null && tile instanceof TileEntityEnderBuffer){
        		TileEntityEnderBuffer buffer = (TileEntityEnderBuffer)tile;
        		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompoundTag(TILE_NBT_STACK);
        		buffer.setCode(nbt.getInteger("Code"));
        		if(nbt.hasKey("Owner")){
        			buffer.bindToPlayer(UUIDUtils.fromString(nbt.getString("Owner")));
        		}
        		update = true;
        	}
        }
        if(update)BlockUtil.markBlockForUpdate(world, pos);
    }
	
    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
        return new FakeTileState<TileEntityEnderBuffer>(state, world, pos, (tile !=null && tile instanceof TileEntityEnderBuffer) ? (TileEntityEnderBuffer)tile : null);
    }
    
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	if(worldIn.isRemote){
    		return true;
    	}
    	ItemStack stack = playerIn.getHeldItem(hand);
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityEnderBuffer){
        	TileEntityEnderBuffer buffer = (TileEntityEnderBuffer)tile;
        	
        	if(buffer.getPlayerBound() == null){
        		if(ItemStackTools.isValid(stack) && stack.getItem() == ModItems.lock){
        			if(!playerIn.capabilities.isCreativeMode){
        				ItemStackTools.incStackSize(stack, -1);
        			}
        			buffer.bindToPlayer(playerIn.getUniqueID());
        			CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "MarkDirty"), tile);
        			return true;
        		}
        	}
        	
        	
        	boolean isOwner = (buffer.getPlayerBound() == null || buffer.getPlayerBound().equals(playerIn.getUniqueID()));
        	
        	
        	
        	if(isOwner && playerIn.isSneaking() && ToolUtil.isToolEquipped(playerIn, hand)){
        		return ToolUtil.breakBlockWithTool(this, worldIn, pos, playerIn, hand);
        	}
        	
        	EnumDyeColor color = ItemUtil.getDyeColor(stack);
        	if (color !=null && isOwner)
            {
                int meta = color.getMetadata();

                //  5, 8, 11; +-1.5 
                // 3.5..6.5, 6.5..9.5,9.5..12.5

                float y = hitY;

                y *= 16;

                boolean hitSuccess = false;
                int oldId = buffer.code;
                int id = oldId;
                int color1 = id & 15;
                int color2 = (id >> 4) & 15;
                int color3 = (id >> 8) & 15;

                if (y >= 3.7 && y < 6.3)
                {
                    color3 = meta;
                    hitSuccess = true;
                }
                else if (y >= 6.7 && y < 9.3)
                {
                    color2 = meta;
                    hitSuccess = true;
                }
                else if (y >= 9.7 && y < 12.3)
                {
                    color1 = meta;
                    hitSuccess = true;
                }

                id = (color1) | (color2 << 4) | (color3 << 8);

                if (oldId != id)
                {
                	if (!playerIn.capabilities.isCreativeMode)
                    	ItemStackTools.incStackSize(stack, -1);
                    buffer.setCode(id);
                    CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "MarkDirty"), tile);
                }

                if (hitSuccess)
                {
                    return true;
                }
            }
        	
        	if(!buffer.hasBuffer() || !isOwner){
        		return false;
        	}
        	if(!worldIn.isRemote){
        		playerIn.openGui(CrystalMod.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        	}
        	return true;
        }
		return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEnderBuffer();
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }
	
	/**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
	@Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
}