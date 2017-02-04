package alec_wam.CrystalMod.items.guide;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.guide.GuidePages.LookupResult;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalGuide extends Item implements ICustomModel {

	public ItemCrystalGuide(){
		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "guide");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(GuideType type : GuideType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + GuideType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < GuideType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		/*RayTraceResult ray = EntityUtil.getRayTraceEntity(player, CrystalMod.proxy.getReachDistanceForPlayer(player), false);
		if(ray !=null){
			Entity entity = ray.entityHit;
			if(entity == null)return EnumActionResult.PASS;
			ItemStack entityItem = EntityUtil.getItemFromEntity(entity, ray);
			if(ItemStackTools.isValid(entityItem)){
				LookupResult result = GuidePages.getGuideData(player, entityItem);
				if(result !=null){
					CrystalMod.proxy.setForcedGuidePage(result);
					player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_GUIDE, world, 0, 0, 0);
					return EnumActionResult.SUCCESS;
				}
			}
		}*/
		/*ItemStack blockStack = ItemUtil.getItemFromBlock(world.getBlockState(pos));
		if(ItemStackTools.isValid(blockStack)){
			LookupResult result = GuidePages.getGuideData(player, blockStack);
			if(result !=null){
				CrystalMod.proxy.setForcedGuidePage(result);
				player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_GUIDE, world, 0, 0, 0);
				return EnumActionResult.SUCCESS;
			}
		}*/
		return EnumActionResult.PASS;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		if(playerIn.isSneaking() && worldIn.isRemote){
			GuidePages.createPages();
			ModLogger.info("Created Pages");

			ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(playerIn);
			if(exPlayer !=null){
				exPlayer.lastOpenBook = null;
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
		}
		playerIn.openGui(CrystalMod.instance, GuiHandler.GUI_ID_GUIDE, worldIn, 0, 0, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
    }
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
    }
	
	public static enum GuideType implements IStringSerializable, IEnumMetaItem
    {
        CRYSTAL(0, "crystal"),
        ESTORAGE(1, "estorage");

        private static final GuideType[] METADATA_LOOKUP = new GuideType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private GuideType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        public int getMetadata()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        public String toString()
        {
            return this.unlocalizedName;
        }

        public static GuideType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (GuideType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
	
}
