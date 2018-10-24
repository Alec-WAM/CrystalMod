package alec_wam.CrystalMod.entities.explosives.grenade;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityNormalGrenade extends EntityGrenade {

	public EntityNormalGrenade(World worldIn) {
		super(worldIn);
	}
	
	public EntityNormalGrenade(World worldIn, EntityLivingBase throwerIn)
    {
        super(worldIn, throwerIn);
    }

    public EntityNormalGrenade(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    @Override
    public int getFuse(){
    	return 5;
    }
    
	@Override
	public void explodeServer() {
		this.setExploded(true);
	}

	@Override
	public void explodeClient() {
		
	}

}
