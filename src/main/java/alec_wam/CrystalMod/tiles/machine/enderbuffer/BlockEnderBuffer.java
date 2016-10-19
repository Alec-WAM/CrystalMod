package alec_wam.CrystalMod.tiles.machine.enderbuffer;

import com.enderio.core.common.util.ItemUtil;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.tool.ToolUtil;

public class BlockEnderBuffer extends BlockMachine
{
    public BlockEnderBuffer()
    {
        super(Material.IRON);
        this.setHardness(2f);
        this.setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	if(worldIn.isRemote){
    		return true;
    	}
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityEnderBuffer){
        	TileEntityEnderBuffer buffer = (TileEntityEnderBuffer)tile;
        	
        	boolean isOwner = (buffer.getPlayerBound() == null || buffer.getPlayerBound().equals(playerIn.getUniqueID()));
        	
        	if(isOwner && playerIn.isSneaking() && ToolUtil.isToolEquipped(playerIn, hand)){
        		return ToolUtil.breakBlockWithTool(this, worldIn, pos, playerIn, hand);
        	}
        	
        	int dyeMeta = -1;
        	if(stack !=null){
	        	for(EnumDyeColor color : EnumDyeColor.values()){
	        		String cap = (color.getUnlocalizedName().substring(0, 1).toUpperCase()+color.getUnlocalizedName().substring(1));
	        		String oreID = "dye"+cap;
	        		if(ItemUtil.itemStackMatchesOredict(stack, oreID)){
	        			dyeMeta = color.getDyeDamage();
	        			break;
	        		}
	        	}
        	}
        	if (dyeMeta !=-1 && isOwner)
            {
                int meta = net.minecraft.item.EnumDyeColor.byDyeDamage(dyeMeta).getMetadata();

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

                if (y >= 3.5 && y < 6.5)
                {
                    color3 = meta;
                    hitSuccess = true;
                }
                else if (y >= 6.5 && y < 9.5)
                {
                    color2 = meta;
                    hitSuccess = true;
                }
                else if (y >= 9.5 && y < 12.5)
                {
                    color1 = meta;
                    hitSuccess = true;
                }

                id = (color1) | (color2 << 4) | (color3 << 8);

                if (oldId != id)
                {
                	EnumDyeColor c1 = EnumDyeColor.byMetadata(color1);
                    EnumDyeColor c2 = EnumDyeColor.byMetadata(color2);
                    EnumDyeColor c3 = EnumDyeColor.byMetadata(color3);
                    ChatUtil.sendNoSpam(playerIn, c1.getName()+" "+c2.getName()+" "+c3.getName());
                    if (!playerIn.capabilities.isCreativeMode)
                    	stack.stackSize--;
                    buffer.setCode(id);
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
	
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
	
	/**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
}