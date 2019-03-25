package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.util.IEnumMeta;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemShieldPotion extends Item implements ICustomModel {

	public static final float SHIELD_VALUE_MINI = ExtendedPlayer.DEFAULT_MAX_BLUE_SHIELD / 4;
	public static final float MAX_SHIELD_MINI = ExtendedPlayer.DEFAULT_MAX_BLUE_SHIELD / 2;
	public static final float SHIELD_VALUE_LARGE = ExtendedPlayer.DEFAULT_MAX_BLUE_SHIELD / 2;
	
	public ItemShieldPotion(){
		super();
		this.setHasSubtypes(true);
		this.setMaxStackSize(4);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.BREWING);
		ModItems.registerItem(this, "shieldpotion");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(PotionType type : PotionType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMeta(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
        boolean canDrink = false;
        if(stack.getMetadata() == PotionType.MINI.getMeta()){
        	canDrink =  ePlayer.getBlueShield() < MAX_SHIELD_MINI;
        }
        if(stack.getMetadata() == PotionType.LARGE.getMeta()){
        	canDrink = ePlayer.getBlueShield() < ePlayer.getMaxBlueShield();
        }
        if (canDrink)
        {
            player.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        else
        {
            return super.onItemRightClick(world, player, hand);
        }
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
		boolean creative = false;
		if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entityLiving;
            ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
            
            if(stack.getMetadata() == PotionType.MINI.getMeta()){
            	ePlayer.addShield(SHIELD_VALUE_MINI, MAX_SHIELD_MINI);
            }
            if(stack.getMetadata() == PotionType.LARGE.getMeta()){
            	ePlayer.addShield(SHIELD_VALUE_LARGE);
            }
            
            if(!worldIn.isRemote){
            	ePlayer.needsSync = true;
            }
            creative = player.capabilities.isCreativeMode;
        }

        if(!creative)stack.shrink(1);
        return stack;
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + PotionType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < PotionType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	public static enum PotionType implements IStringSerializable, IEnumMeta
    {
        MINI(0, "mini"),
        LARGE(1, "large");

        private static final PotionType[] METADATA_LOOKUP = new PotionType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private PotionType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        @Override
		public int getMeta()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        @Override
		public String toString()
        {
            return this.unlocalizedName;
        }

        public static PotionType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        @Override
		public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (PotionType type : values())
            {
                METADATA_LOOKUP[type.getMeta()] = type;
            }
        }
    }
	
}
