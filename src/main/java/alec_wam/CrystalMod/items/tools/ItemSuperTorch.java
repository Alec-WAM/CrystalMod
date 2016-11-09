package alec_wam.CrystalMod.items.tools;

import java.util.List;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSuperTorch extends Item implements ICustomModel {

	public static final String NBT_TORCH_COUNT = "TorchCount";
	public static final String NBT_AUTO_LIGHT = "LightLevel";
	public static final String NBT_ON = "On";
	
	public ItemSuperTorch(){
		super();
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "supertorch");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final ModelResourceLocation off = new ModelResourceLocation(getRegistryName(), "on=false");
		ModelBakery.registerItemVariants(this, off);
		final ModelResourceLocation on = new ModelResourceLocation(getRegistryName(), "on=true");
		ModelBakery.registerItemVariants(this, on);
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	return ItemNBTHelper.getBoolean(stack, NBT_ON, false) ? on : off;
            }
        });
    }
	
	public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }
	
	public double getDurabilityForDisplay(ItemStack stack){
		return ((1.0D/Config.superTorchMaxCount))*(Config.superTorchMaxCount-ItemNBTHelper.getInteger(stack, NBT_TORCH_COUNT, 0));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		tooltip.add("On: "+ItemNBTHelper.getBoolean(stack, NBT_ON, false));
		tooltip.add("Light Level Needed: "+ItemNBTHelper.getInteger(stack, NBT_AUTO_LIGHT, 0));
		tooltip.add("Torches Left: "+ItemNBTHelper.getInteger(stack, NBT_TORCH_COUNT, 0)+" / "+Config.superTorchMaxCount);
    }
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		if(entityIn !=null && entityIn instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)entityIn;
			if(!worldIn.isRemote && ItemNBTHelper.getBoolean(stack, NBT_ON, false)){
				int torchCount = ItemNBTHelper.getInteger(stack, NBT_TORCH_COUNT, 0);
				if(torchCount > 0){
					BlockPos pos = new BlockPos(player);
					int neededLight = ItemNBTHelper.getInteger(stack, NBT_AUTO_LIGHT, 0);
					int currentLight = player.worldObj.getLightFromNeighbors(pos);
					if(currentLight <= neededLight){
						placeTorch(player, stack, pos);
					}
				}
			}
		}
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand){
		if(!world.isRemote){
			if(!player.isSneaking()){
				boolean oldOn = ItemNBTHelper.getBoolean(stack, NBT_ON, false);
				ItemNBTHelper.setBoolean(stack, NBT_ON, !oldOn);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			} else {
				int oldLight = ItemNBTHelper.getInteger(stack, NBT_AUTO_LIGHT, 0);
				int newLight = oldLight;
				newLight++;
				if(newLight >=16){
					newLight = 0;
				}
				ItemNBTHelper.setInteger(stack, NBT_AUTO_LIGHT, newLight);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		return super.onItemRightClick(stack, world, player, hand);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hX, float hY, float hZ){
		if(world.isRemote){
			return EnumActionResult.SUCCESS;
		}
		
		if(placeTorch(player, stack, pos)){
			return EnumActionResult.SUCCESS;
		}
		
		return EnumActionResult.PASS;
	}
	
	public boolean placeTorch(EntityPlayer player, ItemStack stack, BlockPos pos){
		boolean creative = false;//player.capabilities.isCreativeMode;
		int torchCount = ItemNBTHelper.getInteger(stack, NBT_TORCH_COUNT, 0);
		if(torchCount > 0 || creative){
			Item torchItem = Item.getItemFromBlock(Blocks.TORCH);
			ItemStack torchStack = new ItemStack(torchItem);
			boolean placed = false;
			
			for(EnumFacing facing : EnumFacing.VALUES){
				if(torchStack.onItemUse(player, player.worldObj, pos.offset(facing), EnumHand.MAIN_HAND, facing.getOpposite(), 0, 0, 0) == EnumActionResult.SUCCESS){
					placed = true;
					break;
				}
			}
			
			if(placed){
				if(!creative){
					torchCount--;
					ItemNBTHelper.setInteger(stack, NBT_TORCH_COUNT, torchCount);
				}
				return true;
			}
		}
		return false;
	}
	
}
