package alec_wam.CrystalMod.api.energy;

import java.util.concurrent.Callable;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
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
            public INBTBase writeNBT(Capability<ICEnergyStorage> capability, ICEnergyStorage instance, EnumFacing side)
            {
                return new NBTTagInt(instance.getCEnergyStored());
            }

            @Override
            public void readNBT(Capability<ICEnergyStorage> capability, ICEnergyStorage instance, EnumFacing side, INBTBase nbt)
            {
                if (!(instance instanceof CEnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((CEnergyStorage)instance).energy = ((NBTTagInt)nbt).getInt();
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