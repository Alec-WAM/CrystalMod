package alec_wam.CrystalMod.tiles.chest;
import java.util.List;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

public class TileEntityBlueCrystalChest extends TileEntityInventory
{
    private int ticksSinceSync = -1;
    public float prevLidAngle;
    public float lidAngle;
    private int numUsingPlayers;
    private CrystalChestType type;
    private byte facing;
    private boolean inventoryTouched;
    private boolean hadStuff;
    private String customName;

    public TileEntityBlueCrystalChest()
    {
        this(CrystalChestType.BLUE);
    }

    protected TileEntityBlueCrystalChest(CrystalChestType type)
    {
        super("", type.size);
        this.type = type;
    }

    public int getFacing()
    {
        return this.facing;
    }

    public CrystalChestType getType()
    {
        return type;
    }

    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : type.name();
    }

    @Override
    public boolean hasCustomName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String name)
    {
        this.customName = name;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbttagcompound)
    {
        super.readCustomNBT(nbttagcompound);

        if (nbttagcompound.hasKey("CustomName", Constants.NBT.TAG_STRING))
        {
            this.customName = nbttagcompound.getString("CustomName");
        }
        facing = nbttagcompound.getByte("facing");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbttagcompound)
    {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setByte("facing", facing);

        if (this.hasCustomName())
        {
            nbttagcompound.setString("CustomName", this.customName);
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer)
    {
        if (!hasWorld())
        {
            return true;
        }
        if (getWorld().getTileEntity(pos) != this)
        {
            return false;
        }
        return entityplayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
    }

    @Override
    public void update()
    {
    	super.update();
        // Resynchronize clients with the server state
        if (hasWorld() && !this.getWorld().isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
        {
            this.numUsingPlayers = 0;
            float var1 = 5.0F;
            List<EntityPlayer> var2 = this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - var1, pos.getY() - var1, pos.getZ() - var1, pos.getX() + 1 + var1, pos.getY() + 1 + var1, pos.getZ() + 1 + var1));

            for (EntityPlayer var4 : var2)
            {
                if (var4.openContainer instanceof ContainerCrystalChest)
                {
                    ++this.numUsingPlayers;
                }
            }
        }

        if (hasWorld() && !getWorld().isRemote && ticksSinceSync < 0)
        {
            getWorld().addBlockEvent(pos, ModBlocks.crystalChest, 3, ((numUsingPlayers << 3) & 0xF8) | (facing & 0x7));
        }
        if (!getWorld().isRemote && inventoryTouched)
        {
            inventoryTouched = false;
        }

        this.ticksSinceSync++;
        prevLidAngle = lidAngle;
        float f = 0.1F;
        if (numUsingPlayers > 0 && lidAngle == 0.0F)
        {
            double d = pos.getX() + 0.5D;
            double d1 = pos.getZ() + 0.5D;
            getWorld().playSound(null, d, pos.getY() + 0.5D, d1, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, getWorld().rand.nextFloat() * 0.1F + 0.9F);
        }
        if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F)
        {
            float f1 = lidAngle;
            if (numUsingPlayers > 0)
            {
                lidAngle += f;
            } else
            {
                lidAngle -= f;
            }
            if (lidAngle > 1.0F)
            {
                lidAngle = 1.0F;
            }
            float f2 = 0.5F;
            if (lidAngle < f2 && f1 >= f2)
            {
                double d2 = pos.getX() + 0.5D;
                double d3 = pos.getZ() + 0.5D;
                getWorld().playSound(null, d2, pos.getY() + 0.5D, d3, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, getWorld().rand.nextFloat() * 0.1F + 0.9F);
            }
            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int i, int j)
    {
        if (i == 1)
        {
            numUsingPlayers = j;
        } else if (i == 2)
        {
            facing = (byte) j;
        } else if (i == 3)
        {
            facing = (byte) (j & 0x7);
            numUsingPlayers = (j & 0xF8) >> 3;
        }
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        if (!hasWorld())
        {
            return;
        }
        numUsingPlayers++;
        getWorld().addBlockEvent(pos, ModBlocks.crystalChest, 1, numUsingPlayers);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        if (!hasWorld())
        {
            return;
        }
        numUsingPlayers--;
        getWorld().addBlockEvent(pos, ModBlocks.crystalChest, 1, numUsingPlayers);
    }

    public void setFacing(byte facing2)
    {
        this.facing = facing2;
    }

    public TileEntityBlueCrystalChest updateFromMetadata(int l)
    {
        if (hasWorld() && getWorld().isRemote)
        {
            if (l != type.ordinal())
            {
            	getWorld().setTileEntity(pos, CrystalChestType.makeEntity(l));
                return (TileEntityBlueCrystalChest) getWorld().getTileEntity(pos);
            }
        }
        return this;
    }

    public void rotateAround()
    {
        facing++;
        if (facing > EnumFacing.EAST.ordinal())
        {
            facing = (byte) EnumFacing.NORTH.ordinal();
        }
        setFacing(facing);
        getWorld().addBlockEvent(pos, ModBlocks.crystalChest, 2, facing);
    }

    public void wasPlaced(EntityLivingBase entityliving, ItemStack itemStack)
    {
    }

    public void removeAdornments()
    {
    }

    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }
}