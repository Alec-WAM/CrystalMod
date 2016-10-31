package alec_wam.CrystalMod.entities.minecarts.chests;

import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import net.minecraft.world.World;

public class EntityCrystalChestMinecartDark extends EntityCrystalChestMinecartBase {

	public EntityCrystalChestMinecartDark(World worldIn) {
		super(worldIn);
	}
	
    public EntityCrystalChestMinecartDark(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

	@Override
	public CrystalChestType getChestType() {
		return CrystalChestType.DARK;
	}

}
