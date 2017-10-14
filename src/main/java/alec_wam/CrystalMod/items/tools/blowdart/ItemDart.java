package alec_wam.CrystalMod.items.tools.blowdart;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDart extends Item implements ICustomModel {

	public ItemDart() {
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "dart");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(DartType type : DartType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + DartType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < DartType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
    
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if(stack.hasTagCompound() && ItemNBTHelper.verifyExistance(stack, "Potion")){
        	PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
        }
    }
	
	public EntityDart createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter)
    {
		EntityDart dart = new EntityDart(worldIn, shooter, DartType.byMetadata(stack.getMetadata()));
		dart.setPotionEffect(stack);
        return dart;
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.EntityPlayer player)
    {
        int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, bow);
        return enchant <= 0 ? false : this.getClass() == ItemDart.class;
    }
	
    public static enum DartType implements IStringSerializable, IEnumMetaItem
    {
        BASIC(0, "basic", 2.0F),
        BLUE(1, "blue", 3.0F),
        RED(2, "red", 3.0F),
        GREEN(3, "green", 3.0F),
        DARK(4, "dark", 4.0F),
        PURE(5, "pure", 5.0F);

        private static final DartType[] METADATA_LOOKUP = new DartType[values().length];
        private final int metadata;
        private final String unlocalizedName;
        private final float damage;

        private DartType(int dmg, String name, float damage)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
            this.damage = damage;
        }

        @Override
		public int getMetadata()
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

        public static DartType byMetadata(int metadata)
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

        public float getDamage() {
			return damage;
		}

		static
        {
            for (DartType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }

}
