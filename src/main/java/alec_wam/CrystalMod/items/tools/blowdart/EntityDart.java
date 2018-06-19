package alec_wam.CrystalMod.items.tools.blowdart;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.blowdart.ItemDart.DartType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDart extends EntityArrow
{
	private static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityDart.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityTippedArrow.class, DataSerializers.VARINT);
    private PotionType potion = PotionTypes.EMPTY;
    private final Set<PotionEffect> customPotionEffects = Sets.<PotionEffect>newHashSet();
    private boolean hasColor;

	public EntityDart(World worldIn)
    {
        this(worldIn, DartType.BASIC);
    }
	
    public EntityDart(World worldIn, DartType type)
    {
        super(worldIn);
        setType(type);
    }

    public EntityDart(World worldIn, EntityLivingBase shooter, DartType type)
    {
        super(worldIn, shooter);
        setType(type);
    }

    public EntityDart(World worldIn, double x, double y, double z, DartType type)
    {
        super(worldIn, x, y, z);
        setType(type);
    }
    
    @Override
	protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(TYPE, (byte)0);
        this.dataManager.register(COLOR, Integer.valueOf(-1));
    }
    
    public void setType(DartType type){
    	this.setDamage(type.getDamage());
        this.dataManager.set(TYPE, (byte)type.getMeta());
    }
    
    public DartType getType(){
    	return DartType.byMetadata(dataManager.get(TYPE)); 
    }

    protected ItemStack getArrowStack()
    {
        ItemStack dart = new ItemStack(ModItems.dart, 1, getType().getMeta());
        if(potion !=PotionTypes.EMPTY){
        	PotionUtils.addPotionToItemStack(dart, this.potion);
            PotionUtils.appendEffects(dart, this.customPotionEffects);

            if (this.hasColor)
            {
                NBTTagCompound nbttagcompound = dart.getTagCompound();

                if (nbttagcompound == null)
                {
                    nbttagcompound = new NBTTagCompound();
                    dart.setTagCompound(nbttagcompound);
                }

                nbttagcompound.setInteger("CustomPotionColor", this.getColor());
            }
        }
        return dart;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("Type"))
        {
            setType(DartType.byMetadata(compound.getInteger("Type")));
        }
        
        if (compound.hasKey("Potion", 8))
        {
            this.potion = PotionUtils.getPotionTypeFromNBT(compound);
            for (PotionEffect potioneffect : PotionUtils.getFullEffectsFromTag(compound))
            {
                this.addEffect(potioneffect);
            }

            if (compound.hasKey("Color", 99))
            {
                this.setCustomColor(compound.getInteger("Color"));
            }
            else
            {
                this.refreshColor();
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("Type", getType().getMeta());
        
        if (this.potion != PotionTypes.EMPTY && this.potion != null)
        {
            compound.setString("Potion", ((ResourceLocation)PotionType.REGISTRY.getNameForObject(this.potion)).toString());
            if (this.hasColor)
            {
                compound.setInteger("Color", this.getColor());
            }

            if (!this.customPotionEffects.isEmpty())
            {
                NBTTagList nbttaglist = new NBTTagList();

                for (PotionEffect potioneffect : this.customPotionEffects)
                {
                    nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
                }

                compound.setTag("CustomPotionEffects", nbttaglist);
            }
        }
    }
    
    public void setPotionEffect(ItemStack stack)
    {
        if (stack.hasTagCompound() && ItemNBTHelper.verifyExistance(stack, "Potion"))
        {
            this.potion = PotionUtils.getPotionFromItem(stack);
            Collection<PotionEffect> collection = PotionUtils.getFullEffectsFromItem(stack);

            if (!collection.isEmpty())
            {
                for (PotionEffect potioneffect : collection)
                {
                    this.customPotionEffects.add(new PotionEffect(potioneffect));
                }
            }

            int i = getCustomColor(stack);

            if (i == -1)
            {
                this.refreshColor();
            }
            else
            {
                this.setCustomColor(i);
            }
        }
        else
        {
            this.potion = PotionTypes.EMPTY;
            this.customPotionEffects.clear();
            this.dataManager.set(COLOR, Integer.valueOf(-1));
        }
    }

    public static int getCustomColor(ItemStack stack)
    {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        return nbttagcompound != null && nbttagcompound.hasKey("CustomPotionColor", 99) ? nbttagcompound.getInteger("CustomPotionColor") : -1;
    }

    private void refreshColor()
    {
        this.hasColor = false;
        this.dataManager.set(COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
    }

    public void addEffect(PotionEffect effect)
    {
        this.customPotionEffects.add(effect);
        this.getDataManager().set(COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.world.isRemote)
        {
            if (this.inGround)
            {
                if (this.timeInGround % 5 == 0)
                {
                    this.spawnPotionParticles(1);
                }
            }
            else
            {
                this.spawnPotionParticles(2);
            }
        }
        else if (this.inGround && this.timeInGround != 0 && !this.customPotionEffects.isEmpty() && this.timeInGround >= 600)
        {
            this.world.setEntityState(this, (byte)0);
            this.potion = PotionTypes.EMPTY;
            this.customPotionEffects.clear();
            this.dataManager.set(COLOR, Integer.valueOf(-1));
        }
    }

    private void spawnPotionParticles(int particleCount)
    {
        int i = this.getColor();

        if (i != -1 && particleCount > 0)
        {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for (int j = 0; j < particleCount; ++j)
            {
                this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2, new int[0]);
            }
        }
    }

    public int getColor()
    {
        return ((Integer)this.dataManager.get(COLOR)).intValue();
    }

    private void setCustomColor(int p_191507_1_)
    {
        this.hasColor = true;
        this.dataManager.set(COLOR, Integer.valueOf(p_191507_1_));
    }

    @Override
    protected void arrowHit(EntityLivingBase living)
    {
        super.arrowHit(living);

        for (PotionEffect potioneffect : this.potion.getEffects())
        {
            living.addPotionEffect(new PotionEffect(potioneffect.getPotion(), Math.max(potioneffect.getDuration() / 8, 1), potioneffect.getAmplifier(), potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
        }

        if (!this.customPotionEffects.isEmpty())
        {
            for (PotionEffect potioneffect1 : this.customPotionEffects)
            {
                living.addPotionEffect(potioneffect1);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 0)
        {
            int i = this.getColor();

            if (i != -1)
            {
                double d0 = (double)(i >> 16 & 255) / 255.0D;
                double d1 = (double)(i >> 8 & 255) / 255.0D;
                double d2 = (double)(i >> 0 & 255) / 255.0D;

                for (int j = 0; j < 20; ++j)
                {
                    this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2, new int[0]);
                }
            }
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }
}