package alec_wam.CrystalMod.api.energy;

import java.util.concurrent.Callable;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityCrystalEnergy
{
    @CapabilityInject(ICEnergyStorage.class)
    public static Capability<ICEnergyStorage> CENERGY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ICEnergyStorage.class, new IStorage<ICEnergyStorage>()
        {
            @Override
            public INBT writeNBT(Capability<ICEnergyStorage> capability, ICEnergyStorage instance, Direction side)
            {
                return new IntNBT(instance.getCEnergyStored());
            }

            @Override
            public void readNBT(Capability<ICEnergyStorage> capability, ICEnergyStorage instance, Direction side, INBT nbt)
            {
                if (!(instance instanceof CEnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((CEnergyStorage)instance).energy = ((IntNBT)nbt).getInt();
            }
        },
        new Callable<ICEnergyStorage>()
        {
            @Override
            public ICEnergyStorage call() throws Exception
            {
                return new CEnergyStorage(1000);
            }
        });
    }
}