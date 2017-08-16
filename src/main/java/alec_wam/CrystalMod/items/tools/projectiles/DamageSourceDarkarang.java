package alec_wam.CrystalMod.items.tools.projectiles;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class DamageSourceDarkarang extends EntityDamageSource
{
    private final Entity indirectEntity;

    public DamageSourceDarkarang(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn)
    {
        super(damageTypeIn, source);
        this.indirectEntity = indirectEntityIn;
    }

    @Override
	@Nullable
    public Entity getSourceOfDamage()
    {
        return this.damageSourceEntity;
    }

    @Override
	@Nullable
    public Entity getEntity()
    {
        return this.indirectEntity;
    }

    public static final ItemStack DAMAGESTACK = new ItemStack(ModItems.darkarang);
    
    @Override
	public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn)
    {
        ITextComponent itextcomponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
        ItemStack itemstack = DAMAGESTACK;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return new TextComponentTranslation(s1, new Object[] {entityLivingBaseIn.getDisplayName(), itextcomponent, itemstack.getTextComponent()});
    }
}