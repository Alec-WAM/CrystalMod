package alec_wam.CrystalMod.entities.minecarts.chests;

import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import net.minecraft.world.World;

public class EntityCrystalChestMinecartDarkIron extends EntityCrystalChestMinecartBase {

	public EntityCrystalChestMinecartDarkIron(World worldIn) {
		super(worldIn);
	}
	
    public EntityCrystalChestMinecartDarkIron(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

	@Override
	public CrystalChestType getChestType() {
		return CrystalChestType.DARKIRON;
	}

}
