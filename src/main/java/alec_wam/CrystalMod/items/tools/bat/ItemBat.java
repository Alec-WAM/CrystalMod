package alec_wam.CrystalMod.items.tools.bat;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.ItemSpecialSword;
import alec_wam.CrystalMod.proxy.ClientProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBat extends ItemSpecialSword implements ICustomModel {

	public ItemBat(){
		super();
		setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "baseballbat");
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new ItemBatRenderer());
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		list.addAll(BatHelper.getCreativeListBats(item));
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		tooltip.addAll(BatHelper.getInformation(stack, playerIn, advanced));
    }
	
	@Override
    public void onUpdate (ItemStack stack, World world, Entity entity, int slot, boolean equipped)
    {
    	super.onUpdate(stack, world, entity, slot, equipped);
        BatHelper.onBatUpdate(stack, world, entity, slot, equipped);
    }
    
    // Attacking
    @Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        return BatHelper.onLeftClickEntity(stack, player, entity);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
        	IBatType type = BatHelper.getBat(stack);
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)type.getBaseDamage(), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0));
        }

        return multimap;
    }
    
    /* Proper stack damage */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if(!stack.hasTagCompound())
            return false;
        
        return !BatHelper.isBroken(stack) && getDamage(stack) > 0;
    }

    @Override
    public int getMaxDamage (ItemStack stack)
    {
        return 100;
    }

    @Override
    public int getDamage(ItemStack stack) {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags == null)
        {
            return 0;
        }
        int dur = BatHelper.getBatData(stack).getInteger("Damage");
        int max = BatHelper.getBatData(stack).getInteger("TotalDurability");
        int damage = 0;
        if(max > 0)
            damage = ((dur*100)/max);

        // rounding.
        if(damage == 0 && dur > 0)
            damage = 1;


        // synchronize values with stack..
        super.setDamage(stack, damage);
        return damage;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
    	NBTTagCompound tags = stack.getTagCompound();
        if (tags == null)
        {
            return 1.0;
        }
    	int dur = BatHelper.getBatData(stack).getInteger("Damage");
        int max = BatHelper.getBatData(stack).getInteger("TotalDurability");
    	return (double)(dur) / (double)max;
    }
    
    @Override
    public void setDamage(ItemStack stack, int damage) {
        BatHelper.damageTool(stack, damage - stack.getItemDamage(), null);
        getDamage(stack); // called to synchronize with itemstack value
    }
	
    @Override
    public boolean isEnchantable(ItemStack stack){
    	return false;
    }
    
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return false;
    }
    
}
