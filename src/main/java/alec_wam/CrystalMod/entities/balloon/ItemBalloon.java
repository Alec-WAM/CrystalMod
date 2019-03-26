package alec_wam.CrystalMod.entities.balloon;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBalloon extends Item implements ICustomModel {
	
	public ItemBalloon(){
		super();
		setCreativeTab(CrystalMod.tabItems);
		setMaxStackSize(16);
		setHasSubtypes(true);
		setMaxDamage(0);
		ModItems.registerItem(this, "balloon");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(EnumDyeColor type : EnumDyeColor.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), "color="+type.getUnlocalizedName()));
        }
        
        RenderEntityBalloon.ItemRender renderer = new RenderEntityBalloon.ItemRender();
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), renderer);
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(player.isSneaking()){
			if(world.isAirBlock(pos.up())){
				if(!world.isRemote){
					ItemStack stack = player.getHeldItem(hand);
					EntityBalloon balloon = new EntityBalloon(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, EnumDyeColor.byMetadata(stack.getMetadata()));
					world.spawnEntity(balloon);
				}
				return EnumActionResult.SUCCESS;
			}
		}
        return EnumActionResult.PASS;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		ItemStack stack = player.getHeldItem(hand);
		
		if(!player.isBeingRidden() && !player.isSneaking()){
			EntityBalloon balloon = new EntityBalloon(world, player.posX + 0.5, player.eyeHeight + 1.8, player.posZ + 0.5, EnumDyeColor.byMetadata(stack.getMetadata()));
			if(world.getCollisionBoxes(balloon, player.getEntityBoundingBox().addCoord(0, player.eyeHeight + 1.8, 0)).isEmpty()){
				if(!world.isRemote){
					world.spawnEntity(balloon);
				}
				balloon.startRiding(player);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
	
	@Override
	public String getItemStackDisplayName(ItemStack stack){
		String name = ItemUtil.getDyeName(EnumDyeColor.byMetadata(stack.getMetadata()));
		return String.format(Lang.translateToLocal(getUnlocalizedName() + ".name"), name);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < EnumDyeColor.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
}
