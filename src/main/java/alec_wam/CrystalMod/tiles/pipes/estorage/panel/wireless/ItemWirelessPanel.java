package alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless;

import java.util.List;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.CommonProxy;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;

public class ItemWirelessPanel extends Item implements ICustomModel {

	public ItemWirelessPanel(){
		super();
		setMaxStackSize(1);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "wirelessPanelReceiver");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final ModelResourceLocation loc = new ModelResourceLocation("crystalmod:item_wirelesspanel", "active=false");
		ModelBakery.registerItemVariants(this, loc);
		final ModelResourceLocation active = new ModelResourceLocation("crystalmod:item_wirelesspanel", "active=true");
		ModelBakery.registerItemVariants(this, active);
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	return isValid(stack) ? active : loc;
            }
        });
    }
	
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		/*if(ItemNBTHelper.verifyExistance(stack, "PanelDim")){
			int dim = ItemNBTHelper.getInteger(stack, "PanelDim", 0);
			boolean oldAct = ItemNBTHelper.getBoolean(stack, "PanelActive", false);
			if(entityIn.dimension !=dim){
				if(oldAct == true){
					ItemNBTHelper.setBoolean(stack, "PanelActive", false);
				}
			}else{
				if(oldAct == true){
					if(isValid(stack)){
						BlockPos pos = getBlockPos(stack);
						if(!worldIn.isAreaLoaded(pos, pos.add(1,1,1))){
							ItemNBTHelper.setBoolean(stack, "PanelActive", false);
						}else{
							TileEntity tile = worldIn.getTileEntity(pos);
							if(tile == null || !(tile instanceof TileEntityWirelessPanel)){
								ItemNBTHelper.setBoolean(stack, "PanelActive", false);
							}
						}
					}else{
						ItemNBTHelper.setBoolean(stack, "PanelActive", false);
					}
				}else{
					if(isValid(stack)){
						BlockPos pos = getBlockPos(stack);
						if(worldIn.isAreaLoaded(pos, pos.add(1,1,1))){
							TileEntity tile = worldIn.getTileEntity(pos);
							if(tile != null && (tile instanceof TileEntityWirelessPanel)){
								ItemNBTHelper.setBoolean(stack, "PanelActive", true);
							}
						}
					}
				}
			}
		}*/
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		if(itemStackIn !=null){
			if(isValid(itemStackIn)){
				if(!worldIn.isRemote){
					World panelWorld = worldIn;
					BlockPos pos = getBlockPos(itemStackIn);
					int dim = ItemNBTHelper.getInteger(itemStackIn, "PanelDim", playerIn.dimension);
					boolean interDim = true;
					
					
					if(playerIn.dimension !=dim && !interDim){
						ChatUtil.sendNoSpam(playerIn, "Unable to reach target panel. It is in another dimension");
						return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
					}
					
					panelWorld = DimensionManager.getWorld(dim);
					
					if(panelWorld !=null){
						if(!panelWorld.isAreaLoaded(pos, pos.add(1,1,1))){
							ChatUtil.sendNoSpam(playerIn, "Unable to reach target panel. It is not loaded");
							return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
						}
						TileEntity tile = panelWorld.getTileEntity(pos);
						if(tile == null || !(tile instanceof TileEntityWirelessPanel)){
							ChatUtil.sendNoSpam(playerIn, "There is no wireless panel at this location");
							return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
						}
						playerIn.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, panelWorld, 0, 0, hand.ordinal());
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
					}
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if(stack !=null){
			TileEntity tile = worldIn.getTileEntity(pos);
			if(tile !=null && tile instanceof TileEntityWirelessPanel){
				ItemNBTHelper.setInteger(stack, "PanelX", pos.getX());
				ItemNBTHelper.setInteger(stack, "PanelY", pos.getY());
				ItemNBTHelper.setInteger(stack, "PanelZ", pos.getZ());
				ItemNBTHelper.setInteger(stack, "PanelDim", worldIn.provider.getDimension());
				return EnumActionResult.SUCCESS;
			}
		}
        return EnumActionResult.PASS;
    }
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv){
		super.addInformation(stack, player, list, adv);
		if(isValid(stack)){
			BlockPos pos = getBlockPos(stack);
			list.add("Panel location {"+pos.getX()+", "+pos.getY()+", "+pos.getZ()+"}");
			
			int dim = ItemNBTHelper.getInteger(stack, "PanelDim", 0);
			String name = ""+dim;
			if (!DimensionManager.isDimensionRegistered(dim)) {
		      name = Integer.toString(dim);
		    }else {
			    DimensionType type = DimensionManager.getProviderType(dim);
			    if (type == null) {
			      name = Integer.toString(dim);
			    }else {
				    name = type.getName();
				    int[] dims = DimensionManager.getDimensions(type);
				    if (dims != null && dims.length > 1) {
				      name += " " + dim;
				    }
			    }
		    }
			
			list.add("Panel Dimension: "+name);
		}
	}
	
	public static boolean isValid(ItemStack stack){
		return ItemNBTHelper.verifyExistance(stack, "PanelX") && ItemNBTHelper.verifyExistance(stack, "PanelY") && ItemNBTHelper.verifyExistance(stack, "PanelZ");
	}
	
	public static BlockPos getBlockPos(ItemStack stack){
		if(!isValid(stack)) return BlockPos.ORIGIN;
		return new BlockPos(ItemNBTHelper.getInteger(stack, "PanelX", 0), ItemNBTHelper.getInteger(stack, "PanelY", -1), ItemNBTHelper.getInteger(stack, "PanelZ", 0));
	}
	
}
