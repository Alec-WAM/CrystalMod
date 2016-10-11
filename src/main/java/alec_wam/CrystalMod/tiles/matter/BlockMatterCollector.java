package alec_wam.CrystalMod.tiles.matter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemUtil;

public class BlockMatterCollector extends BlockContainer
{
    public BlockMatterCollector()
    {
        super(Material.IRON);
        this.setHardness(1f);
		this.setCreativeTab(CrystalMod.tabBlocks);
    }

    public int getRenderType(){
    	return 3;
    }
    
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack held, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityMatterCollector){
        	TileEntityMatterCollector caul = (TileEntityMatterCollector) tile;
        	if(!worldIn.isRemote){
        		if(caul.stack == null){
        			ChatUtil.sendChat(playerIn, "Stack: null");
        			return true;
        		}
        		
        		if(held !=null){
        			if(MatterRegistry.getMatter(held) !=null){
        				MatterStack stack = MatterRegistry.getMatterStack(MatterRegistry.getMatter(held), held);
        				if(stack !=null && caul.stack.amount >= stack.amount && caul.stack.getMeta() == stack.getMeta()){
        					ChatUtil.sendChat(playerIn, "Crafting Stack amt = "+stack.amount);
        					ItemStack copy = held.copy();
        					copy.stackSize = 1;
        					ItemUtil.spawnItemInWorldWithRandomMotion(worldIn, copy, pos);
        					caul.stack.amount-=stack.amount;
        					if(caul.stack.amount <=0){
        						caul.stack = null;
        					}
        					caul.markDirty();
        					return true;
        				}
            			return true;
        			}
        		}
        		String stack = "Stack: "+caul.stack.getMatter().getUnlocalizedName(caul.stack);
        		int amount = caul.stack.amount;
        		String amt = "Amount: "+amount;
        		String n = "Nuggets: "+amount+" / "+MatterHelper.VALUE_Nugget+" = "+(amount/MatterHelper.VALUE_Nugget);
        		String i = "Ingots: "+amount+" / "+MatterHelper.VALUE_Ingot+" = "+(amount/MatterHelper.VALUE_Ingot);
        		String b = "Blocks: "+amount+" / "+MatterHelper.VALUE_Block+" = "+(amount/MatterHelper.VALUE_Block);
        		ChatUtil.sendNoSpam(playerIn, stack, amt, n, i, b);
        	}
        	return true;
        }
		return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMatterCollector();
	}
}