package alec_wam.CrystalMod.tiles.chests.wooden;

import java.util.List;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TileEntityWoodenCrystalChest extends TileEntityInventory implements INamedContainerProvider
{
    private int ticksSinceSync = -1;
    public float prevLidAngle;
    public float lidAngle;
    private int numUsingPlayers;
    public WoodenCrystalChestType type;

    public TileEntityWoodenCrystalChest()
    {
    	this(WoodenCrystalChestType.BLUE);
    }

    public TileEntityWoodenCrystalChest(WoodenCrystalChestType type)
    {
        super(ModBlocks.woodenChestGroup.getTileType(type), "", type.size);
        this.type = type;
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt){
    	super.writeCustomNBT(nbt);
    	nbt.putInt("Type", type.ordinal());    	
    }
    
    @Override
    public void readCustomNBT(CompoundNBT nbt){
    	super.readCustomNBT(nbt);
    	type = WoodenCrystalChestType.values()[nbt.getInt("Type")];    	
    }
    
    @Override
    public boolean isUsableByPlayer(PlayerEntity entityplayer)
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
    public void tick()
    {
    	super.tick();
        // Resynchronize clients with the server state
        if (hasWorld() && !this.getWorld().isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
        {
            this.numUsingPlayers = 0;
            float var1 = 5.0F;
            List<PlayerEntity> var2 = this.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos.getX() - var1, pos.getY() - var1, pos.getZ() - var1, pos.getX() + 1 + var1, pos.getY() + 1 + var1, pos.getZ() + 1 + var1));

            for (PlayerEntity var4 : var2)
            {
                if (var4.openContainer instanceof WoodenCrystalChestContainer)
                {
                    ++this.numUsingPlayers;
                }
            }
        }

        if (hasWorld() && !getWorld().isRemote && ticksSinceSync < 0)
        {
            getWorld().addBlockEvent(pos, ModBlocks.woodenChestGroup.getBlock(type), 1, numUsingPlayers);
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
        } 
        return true;
    }

    @Override
    public void openInventory(PlayerEntity player)
    {
        if (!hasWorld())
        {
            return;
        }
        numUsingPlayers++;
        getWorld().addBlockEvent(pos, ModBlocks.woodenChestGroup.getBlock(type), 1, numUsingPlayers);
    }

    @Override
    public void closeInventory(PlayerEntity player)
    {
        if (!hasWorld())
        {
            return;
        }
        numUsingPlayers--;
        getWorld().addBlockEvent(pos, ModBlocks.woodenChestGroup.getBlock(type), 1, numUsingPlayers);
    }

    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(type.getName());
	}

    @Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerIn) {
		return new WoodenCrystalChestContainer(i, playerInventory, this, type, 0, 0);
	}

	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}
}