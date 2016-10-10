package com.alec_wam.CrystalMod.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Util {

	public static Vec3d getEyePosition(EntityPlayer player) {
	    double y = player.posY;
	    y += player.getEyeHeight();
	    return new Vec3d(player.posX, y, player.posZ);
	  }

	  public static Vector3d getEyePositionCM(EntityPlayer player) {
	    Vector3d res = new Vector3d(player.posX, player.posY, player.posZ);
	    res.y += player.getEyeHeight();
	    return res;
	  }

	  public static Vector3d getLookVecEio(EntityPlayer player) {
	    Vec3d lv = player.getLookVec();
	    return new Vector3d(lv.xCoord, lv.yCoord, lv.zCoord);
	  }
	  
	  public static String getNameForBlock(Block block) {
	        Object obj = Block.REGISTRY.getNameForObject(block);
	        if (obj == null) {
	            return null;
	        }
	        return obj.toString();
	    }

	public static ItemStack getContainerItem(ItemStack stack) {
		if( stack == null )
		{
			return null;
		}

		final Item i = stack.getItem();
		if( i == null || !i.hasContainerItem( stack ) )
		{
			if( stack.stackSize > 1 )
			{
				stack.stackSize--;
				return stack;
			}
			return null;
		}

		ItemStack ci = i.getContainerItem( stack.copy() );
		if( ci != null && ci.isItemStackDamageable() && ci.getItemDamage() == ci.getMaxDamage() )
		{
			ci = null;
		}

		return ci;
	}

	public static boolean notNullAndInstanceOf(Object object, Class<?> clazz)
    {
        return object != null && clazz.isInstance(object);
    }

	public static RayTraceResult getEntityLookedObject(EntityLivingBase entity){
        return getEntityLookedObject(entity, (float)(4.5));
    }

    public static RayTraceResult getEntityLookedObject(EntityLivingBase entity, float maxDistance){
        Pair<Vec3d, Vec3d> vecs = getStartAndEndLookVec(entity, maxDistance);
        return entity.worldObj.rayTraceBlocks(vecs.getLeft(), vecs.getRight());
    }

    public static Pair<Vec3d, Vec3d> getStartAndEndLookVec(EntityLivingBase entity){
        return getStartAndEndLookVec(entity, (float)(4.5));
    }

    public static Pair<Vec3d, Vec3d> getStartAndEndLookVec(EntityLivingBase entity, float maxDistance){
        Vec3d entityVec = null;
        if(entity instanceof EntityPlayer) {
            entityVec = getEyePosition((EntityPlayer) entity);
        } else {
            entityVec = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight() - entity.getYOffset() - (entity.isSneaking() ? 0.08 : 0), entity.posZ);
        }
        Vec3d entityLookVec = entity.getLook(1.0F);
        Vec3d maxDistVec = entityVec.addVector(entityLookVec.xCoord * maxDistance, entityLookVec.yCoord * maxDistance, entityLookVec.zCoord * maxDistance);
        return new ImmutablePair<Vec3d, Vec3d>(entityVec, maxDistVec);
    }

    public static BlockPos getEntityLookedBlock(EntityLivingBase entity, float maxDistance){
        RayTraceResult hit = getEntityLookedObject(entity, maxDistance);
        if(hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return null;
        }
        return hit.getBlockPos();
    }

    /**
	 * Checks if Minecraft is running in offline mode.
	 * @return if mod is running in offline mode.
	 */
    public static boolean isInternetAvailable()
    {
        try {
			return isHostAvailable("http://www.google.com") || isHostAvailable("http://www.amazon.com")
			        || isHostAvailable("http://www.facebook.com")|| isHostAvailable("http://www.apple.com");
		} catch (IOException e) {
			return false;
		}
    }

    private static boolean isHostAvailable(String hostName) throws IOException
    {
    	try {
			new URL(hostName).openConnection().connect();
			return true;
		}
    	catch (MalformedURLException e) 
		{
			e.printStackTrace();
			return false;
		}
    	catch (IOException e) {
    		e.printStackTrace();
			return false;
		}
    }
    
    public static boolean isImageDataUploaded(ThreadDownloadImageData data) {
		if(data !=null){
			boolean trying = false;
			try{
				return ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, data, 7);
			}catch(Exception e2){}
			return trying;
		}
		return false;
	}
    
	public static class UserNameCheckGSon{
		String username = null;
		Boolean premium = null;
		
		public String getUsername() {
	      return username;
	    }
	    public void setUsername(String username) {
	      this.username = username;
	    }
	    
	    public Boolean getPremium() {
	      return premium;
	    }
	    public void setPremium(Boolean premium) {
	      this.premium = premium;
	    }
	}

	public static double getDistanceToEntity(Entity entity1, Entity entity2)
	{
		return getDistanceToXYZ(entity1.posX, entity1.posY, entity1.posZ, entity2.posX, entity2.posY, entity2.posZ);
	}

	public static double getDistanceToXYZ(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		final double deltaX = x2 - x1;
		final double deltaY = y2 - y1;
		final double deltaZ = z2 - z1;

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	/**
	 * Gets a list containing instances of all entities around the specified coordinates up to the specified distance away.
	 * 
	 * @param worldObj The world that the entity should be in.
	 * @param posX The X position to begin searching at.
	 * @param posY The Y position to begin searching at.
	 * @param posZ The Z position to begin searching at.
	 * @param maxDistanceAway The maximum distance away from the points to search.
	 * @return List containing all entities within the specified distance of the specified entity.
	 */
	public static List<Entity> getAllEntitiesWithinDistanceOfCoordinates(World worldObj, double posX, double posY, double posZ, int maxDistanceAway)
	{
		final List<Entity> entitiesAroundMe = worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(posX - maxDistanceAway, posY - maxDistanceAway, posZ - maxDistanceAway, posX + maxDistanceAway, posY + maxDistanceAway, posZ + maxDistanceAway));
		return entitiesAroundMe;
	}

	public static boolean isMultipleOf(int input, int mult) {
		return input % mult == 0;
	}
	
	
}
