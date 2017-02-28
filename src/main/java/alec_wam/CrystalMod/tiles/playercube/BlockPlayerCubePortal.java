package alec_wam.CrystalMod.tiles.playercube;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard.CardType;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import alec_wam.CrystalMod.world.ModDimensions;

import com.google.common.base.Strings;

public class BlockPlayerCubePortal extends BlockContainer
{
    public BlockPlayerCubePortal()
    {
        super(Material.IRON);
        this.setHardness(2f).setResistance(20F);
		this.setCreativeTab(CrystalMod.tabBlocks);
    }

    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.INVISIBLE;
    }
    
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	ItemStack held = playerIn.getHeldItem(hand);
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityPlayerCubePortal){
        	TileEntityPlayerCubePortal portal = (TileEntityPlayerCubePortal)tile;
        	if(!worldIn.isRemote){
        		CubeManager cubeManger;
        		if(portal.getOwner() == null){
        			portal.setOwner(playerIn);
        		}
        		
        		if(ItemStackTools.isValid(held) && held.getItem() == ModItems.miscCard && CardType.byMetadata(held.getMetadata()) == CardType.CUBE){
        			if(held.hasDisplayName()){
        				portal.cubeID = held.getDisplayName();
        				ChatUtil.sendNoSpam(playerIn, "Cube Name: "+portal.cubeID);
        				portal.markDirty();
        				return true;
        			}
        		}
        		
        		if(ItemStackTools.isValid(held) && held.getItem() == Items.STICK){
        			TileEntitiesMessage msg2 = new TileEntitiesMessage(portal.mobileChunk);
                    CrystalModNetwork.sendToAllAround(msg2, portal);
        			return true;
        		}

        		if(ToolUtil.isToolEquipped(playerIn, hand)){
        			if(playerIn.isSneaking()){
        				portal.clearCube();
        			}
        			portal.assemble();
        			return true;
        		}
        		
        		if(playerIn.isSneaking()){
        			String name = "Cube Name: "+portal.cubeID;
        			if(portal.getCube() !=null){
        				ChatUtil.sendNoSpam(playerIn, name, "Cube Coord: "+portal.getCube().minBlock);
        			}else{
        				ChatUtil.sendNoSpam(playerIn, name);
        			}
    				return true;
        		}
        		
    			if ((cubeManger = CubeManager.getInstance()) != null)
    			{
    				if (worldIn.provider.getDimensionType().getId() != ModDimensions.CUBE_ID)
    				{
    					if(!Strings.isNullOrEmpty(portal.cubeID))
    					cubeManger.teleportPlayerToPlayerCube((EntityPlayerMP) playerIn, portal.getOwner(), portal.cubeID);
    				}
    				else
    				{
    					cubeManger.teleportPlayerBack((EntityPlayerMP) playerIn);
    				}
    			}
        	}
        	return true;
        }
		return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPlayerCubePortal();
	}
}