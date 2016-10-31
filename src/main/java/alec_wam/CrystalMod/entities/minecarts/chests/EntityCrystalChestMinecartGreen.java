package alec_wam.CrystalMod.entities.minecarts.chests;

import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import net.minecraft.world.World;

public class EntityCrystalChestMinecartGreen extends EntityCrystalChestMinecartBase {

	public EntityCrystalChestMinecartGreen(World worldIn) {
		super(worldIn);
	}
	
    public EntityCrystalChestMinecartGreen(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

	@Override
	public CrystalChestType getChestType() {
		return CrystalChestType.GREEN;
	}

}
