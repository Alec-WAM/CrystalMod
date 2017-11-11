package alec_wam.CrystalMod.entities.misc;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.items.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrystalModPainting extends EntityHanging implements IEntityAdditionalSpawnData
{
    public EntityCrystalModPainting.EnumArt art;

    public EntityCrystalModPainting(World worldIn)
    {
        super(worldIn);
    }

    public EntityCrystalModPainting(World worldIn, BlockPos pos, EnumFacing facing)
    {
        super(worldIn, pos);
        List<EntityCrystalModPainting.EnumArt> list = Lists.<EntityCrystalModPainting.EnumArt>newArrayList();

        for (EntityCrystalModPainting.EnumArt entitypainting$enumart : EntityCrystalModPainting.EnumArt.values())
        {
            this.art = entitypainting$enumart;
            this.updateFacingWithBoundingBox(facing);

            if (this.onValidSurface())
            {
                list.add(entitypainting$enumart);
            }
        }

        if (!list.isEmpty())
        {
            this.art = (EntityCrystalModPainting.EnumArt)list.get(this.rand.nextInt(list.size()));
            //ModLogger.info("Creating "+art.title+" Painting");
        }

        this.updateFacingWithBoundingBox(facing);
    }

    @SideOnly(Side.CLIENT)
    public EntityCrystalModPainting(World worldIn, BlockPos pos, EnumFacing facing, String title)
    {
        this(worldIn, pos, facing);

        for (EntityCrystalModPainting.EnumArt entitypainting$enumart : EntityCrystalModPainting.EnumArt.values())
        {
            if (entitypainting$enumart.title.equals(title))
            {
                this.art = entitypainting$enumart;
                break;
            }
        }

        this.updateFacingWithBoundingBox(facing);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setString("Motive", this.art.title);
        super.writeEntityToNBT(compound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        String s = compound.getString("Motive");

        for (EntityCrystalModPainting.EnumArt entitypainting$enumart : EntityCrystalModPainting.EnumArt.values())
        {
            if (entitypainting$enumart.title.equals(s))
            {
                this.art = entitypainting$enumart;
            }
        }

        if (this.art == null)
        {
            this.art = EntityCrystalModPainting.EnumArt.CRYSTAL_POP;
        }

        super.readEntityFromNBT(compound);
    }

    public int getWidthPixels()
    {
        return this.art.blockWidth * 16;
    }

    public int getHeightPixels()
    {
        return this.art.blockHeight * 16;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(@Nullable Entity brokenEntity)
    {
        if (this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);

            if (brokenEntity instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)brokenEntity;

                if (entityplayer.capabilities.isCreativeMode)
                {
                    return;
                }
            }

            this.entityDropItem(new ItemStack(ModItems.crystalmodPainting), 0.0F);
        }
    }

    public void playPlaceSound()
    {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
    {
        this.setPosition(x, y, z);
    }

    /**
     * Set the position and rotation values directly without any clamping.
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        BlockPos blockpos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
        this.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
    }

    public static enum EnumArt
    {
        CRYSTAL_POP("CrystalPop", "crystal_pop", 1, 1), 
        DUELING_BLADES_L("DuelingBladesL", "dueling_blades_light", 2, 1),
        DUELING_BLADES_D("DuelingBladesD", "dueling_blades_dark", 2, 1),
        LOGO_NORMAL("LogoNormal", "logo_normal", 4, 1),
        LOGO_SPECIAL("LogoSpecial", "logo_special", 4, 1), 
        ANGEL("Angel", "angel", 1, 2),
        DEVIL("Devil", "devil", 1, 2),
        DRAGON("Dragon", "dragon", 4, 2), 
        ELDER_GUARDIAN("ElderGuardian", "elder_guardian", 2, 2),
        PURPLE_SUN("PurpleSun", "purple_sun", 2, 2),
        HALLWAY("Hallway", "hallway", 4, 4),
        TEAM_LOGO("TeamLogo", "team_logo", 4, 4);

        public final String title;
        public final String textureName;
        public final int blockWidth;
        public final int blockHeight;

        private EnumArt(String title, String textureName, int width, int height)
        {
            this.title = title;
            this.textureName = textureName;
            this.blockWidth = width;
            this.blockHeight = height;
        }
    }

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		PacketBuffer buf = new PacketBuffer(buffer);
		buf.writeString(art.title);
		buf.writeBlockPos(getHangingPosition());
		buf.writeByte(facingDirection.getHorizontalIndex());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		PacketBuffer buf = new PacketBuffer(additionalData);
		String title = buf.readString(15);
		for (EntityCrystalModPainting.EnumArt entitypainting$enumart : EntityCrystalModPainting.EnumArt.values())
        {
            if (entitypainting$enumart.title.equals(title))
            {
                this.art = entitypainting$enumart;
                break;
            }
        }
		BlockPos hanging = buf.readBlockPos();
		this.setPosition(hanging.getX(), hanging.getY(), hanging.getZ());
		this.facingDirection = EnumFacing.getHorizontal(buf.readByte());
		this.updateFacingWithBoundingBox(facingDirection);
	}
}
