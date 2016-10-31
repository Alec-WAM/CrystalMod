package alec_wam.CrystalMod.entities.minecarts.chests;

import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import net.minecraft.world.World;

public class EntityCrystalChestMinecartBlue extends EntityCrystalChestMinecartBase {

	public EntityCrystalChestMinecartBlue(World worldIn) {
		super(worldIn);
	}
	
    public EntityCrystalChestMinecartBlue(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

	@Override
	public CrystalChestType getChestType() {
		return CrystalChestType.BLUE;
	}

}
