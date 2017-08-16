package alec_wam.CrystalMod.tiles.spawner;

import java.util.List;

import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityEssenceInstance<T extends EntityLivingBase> {

	protected final String ID;
	protected Class<? extends T> entityClass;
	protected final int neededKills;
	
	public EntityEssenceInstance(String id, Class<T> entityClass){
		this(id, entityClass, 1);
	}
	
	public EntityEssenceInstance(String id, Class<T> entityClass, int neededKills){
		this.ID = id;
		this.entityClass = entityClass;
		this.neededKills = neededKills;
	}
	
	public String getID(){
		return ID;
	}
	
	public Class<? extends T> getEntityClass(){
		return entityClass;
	}
	
	public int getNeededKills(){
		return neededKills;
	}
	
	public T createEntity(World world){
		try {
			T entityliving = entityClass.getConstructor(new Class[]{World.class}).newInstance(world);
			preSpawn(entityliving);
			return entityliving;
        } catch (Throwable ignored) {
        	//ignored.printStackTrace();
        }
		return null;
	}

	public T createRenderEntity(World world){
		return createEntity(world);
	}
	
	public void preSpawn(T entity){}
	
	public boolean useInitialSpawn(){
		return true;
	}
	
	public void addInfo(List<String> list){
		net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(entityClass);
		if(entry !=null)list.add(Lang.translateToLocal("entity." + entry.getName() + ".name"));
		else list.add(Lang.translateToLocal("entity.generic.name"));
	}
	
	public float getRenderScale(TransformType type){
		return type == TransformType.GUI ? 1.5F : type == TransformType.FIXED ? 3.0f : 1.0F;
	}
	
	public Vec3d getRenderOffset(TransformType type){
		return new Vec3d(0, -0.8, 0);
	}
	
	public boolean shouldSpinDuringRender(){
		return true;
	}

	public boolean isValid(Entity entity) {
		return entityClass.equals(entity.getClass());
	}
	
}
