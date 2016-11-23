package alec_wam.CrystalMod.util.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.util.Util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

public class DownloadedTextures {

	public static Map<String, PlayerSkin> skins = new HashMap<String, PlayerSkin>();
	public static Map<String, MCAPIResource> special = new HashMap<String, MCAPIResource>();
	
	public static enum MCAPIType{
		Two("2d"), Thr("3d");
		public String ending;
		MCAPIType(String type){
			ending = type;
		}
	}
	
	public static class MCAPIResource{
		
		public final UUID uuid;
		//private String username;
		
		private ResourceLocation specialFace2d;
		private ResourceLocation specialFace3d;
		private ResourceLocation face2d;
		private ResourceLocation face3d;
		
		private ResourceLocation specialPlayer2d;
		private ResourceLocation specialPlayer3d;
		private ResourceLocation player2d;
		private ResourceLocation player3d;
		
		public MCAPIResource(UUID uuid){
			this.uuid = uuid;
		}
		
		public String getUsername(){
			return ProfileUtil.getUsername(uuid);
		}
		
		public ResourceLocation getFace2d(){
			if(face2d == null){
				face2d = getLocationFace(UUIDUtils.fromUUID(uuid), MCAPIType.Two, true);
				getDownloadImageFace(face2d, getUsername(), MCAPIType.Two, 32, true);
			}
			return face2d;
		}
		
		public ResourceLocation getFace3d(){
			if(face3d == null){
				face3d = getLocationFace(UUIDUtils.fromUUID(uuid), MCAPIType.Thr, true);
				getDownloadImageFace(face3d, getUsername(), MCAPIType.Thr, 32, true);
			}
			return face3d;
		}
		
		public ResourceLocation getSpecialFace2d(){
			if(specialFace2d == null){
				specialFace2d = getLocationFace(UUIDUtils.fromUUID(uuid), MCAPIType.Two, false);
				getDownloadImageFace(specialFace2d, getUsername(), MCAPIType.Two, 32, false);
			}
			return specialFace2d;
		}
		
		public ResourceLocation getSpecialFace3d(){
			if(specialFace3d == null){
				specialFace3d = getLocationFace(UUIDUtils.fromUUID(uuid), MCAPIType.Thr, false);
				getDownloadImageFace(specialFace3d, getUsername(), MCAPIType.Thr, 32, false);
			}
			return specialFace3d;
		}
		
		public ResourceLocation getPlayer2d(){
			if(player2d == null){
				player2d = getLocationPlayer(UUIDUtils.fromUUID(uuid), MCAPIType.Two, true);
				getDownloadImagePlayer(player2d, getUsername(), MCAPIType.Two, 32, true);
			}
			return player2d;
		}
		
		public ResourceLocation getPlayer3d(){
			if(player3d == null){
				player3d = getLocationPlayer(UUIDUtils.fromUUID(uuid), MCAPIType.Thr, true);
				getDownloadImagePlayer(player3d, getUsername(), MCAPIType.Thr, 32, true);
			}
			return player3d;
		}
		
		public ResourceLocation getSpecialPlayer2d(){
			if(specialPlayer2d == null){
				specialPlayer2d = getLocationPlayer(UUIDUtils.fromUUID(uuid), MCAPIType.Two, false);
				getDownloadImagePlayer(specialPlayer2d, getUsername(), MCAPIType.Two, 32, false);
			}
			return specialPlayer2d;
		}
		
		public ResourceLocation getSpecialPlayer3d(){
			if(specialPlayer3d == null){
				specialPlayer3d = getLocationPlayer(UUIDUtils.fromUUID(uuid), MCAPIType.Thr, false);
				getDownloadImagePlayer(specialPlayer3d, getUsername(), MCAPIType.Thr, 32, false);
			}
			return specialPlayer3d;
		}
		
	}
	
	public static PlayerSkin getPlayerSkin(String name){
		if(!skins.containsKey(name)){
			skins.put(name, new PlayerSkin(name));
		}
		return skins.get(name);
	}
	
	public static MCAPIResource getSpecialResource(UUID name){
		if(!special.containsKey(UUIDUtils.fromUUID(name))){
			special.put(UUIDUtils.fromUUID(name), new MCAPIResource(name));
		}
		return special.get(UUIDUtils.fromUUID(name));
	}
	
	/*public static ResourceLocation getSkin(String name){
		return getSkin(UUIDHelper.getUUID(name));
	}*/
	
	public static ResourceLocation getSkin(String name){
		return getPlayerSkin(name).skin;
	}
	
	public static ResourceLocation getCape(String name){
		return getPlayerSkin(name).cape;
	}
	
	public static boolean hasCape(String name){
		return getPlayerSkin(name).uploadedCape;
	}
	
	public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation par0ResourceLocation, String par1Str)
    {
		UUID uuid = ProfileUtil.getUUID(par1Str);
		ResourceLocation skin = null;
		if(uuid !=null){
			skin = DefaultPlayerSkin.getDefaultSkin(uuid);
		} else {
			skin = DefaultPlayerSkin.getDefaultSkinLegacy();
		}
        return getDownloadImage(par0ResourceLocation, getSkinUrl(par1Str), skin, new ImageBufferDownload());
    }
	
	public static ThreadDownloadImageData getDownloadImageFace(ResourceLocation par0ResourceLocation, String par1Str, MCAPIType type, int size, boolean helmet)
    {
		ResourceLocation defaultF = new ResourceLocation(String.format("crystalmod:textures/misc/steve_face_%s.png", type.ending));
        return getDownloadImage(par0ResourceLocation, getFaceUrl(par1Str, type, size, helmet), defaultF, new ImageBufferFaceLarge(size, size));
    }
	
	public static ThreadDownloadImageData getDownloadImagePlayer(ResourceLocation par0ResourceLocation, String par1Str, MCAPIType type, int size, boolean helmet)
    {
		ResourceLocation defaultF = new ResourceLocation(String.format("crystalmod:textures/misc/steve_%s.png", type.ending));
        return getDownloadImage(par0ResourceLocation, getPlayerUrl(par1Str, type, size, helmet), defaultF, new ImageBufferFaceLarge(size, size*2));
    }

    public static ThreadDownloadImageData getDownloadImageCape(ResourceLocation par0ResourceLocation, String par1Str)
    {
        return getDownloadImage(par0ResourceLocation, getCapeUrl(par1Str), (ResourceLocation)null, (IImageBuffer)null);
    }

    public static ThreadDownloadImageData getDownloadImage(ResourceLocation par0ResourceLocation, String par1Str, ResourceLocation par2ResourceLocation, IImageBuffer par3IImageBuffer)
    {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        Object object = texturemanager.getTexture(par0ResourceLocation);

        if (object == null)
        {
            object = new ThreadDownloadImageData(null, par1Str, par2ResourceLocation, par3IImageBuffer);
            texturemanager.loadTexture(par0ResourceLocation, (ITextureObject)object);
        }

        return (ThreadDownloadImageData)object;
    }

    public static String getSkinUrl(String par0Str)
    {
        return String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] {StringUtils.stripControlCodes(par0Str)});
    }
    
    public static String getFaceUrl(String par0Str, MCAPIType type, int size, boolean helmet)
    {
    	String bool = ""+helmet;
    	return String.format("https://mcapi.ca/avatar/%s/%s/%s/%s", new Object[] {type.ending, StringUtils.stripControlCodes(par0Str), ""+size, ""+bool});
    	/*if(type == MCAPIType.Thr){
    		if(!helmet){
        		return "https://crafatar.com/renders/head/"+StringUtils.stripControlCodes(par0Str)+"?size="+size;
        	}
        	return "https://crafatar.com/renders/head/"+StringUtils.stripControlCodes(par0Str)+"?overlay&size="+size;
    	}
    	if(!helmet){
    		return "https://crafatar.com/avatars/"+StringUtils.stripControlCodes(par0Str)+"?size="+size;
    	}
    	return "https://crafatar.com/avatars/"+StringUtils.stripControlCodes(par0Str)+"?overlay&size="+size;*/
    }
    
    public static String getPlayerUrl(String par0Str, MCAPIType type, int size, boolean helmet)
    {
    	String bool = ""+helmet;
    	return String.format("https://mcapi.ca/skin/%s/%s/%s/%s", new Object[] {type.ending, StringUtils.stripControlCodes(par0Str), ""+size, ""+bool});
    }

    public static String getCapeUrl(String par0Str)
    {
        return String.format("http://skins.minecraft.net/MinecraftCloaks/%s.png", new Object[] {StringUtils.stripControlCodes(par0Str)});
    }
    
    public static ResourceLocation getLocationSkin(String par0Str)
    {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(par0Str));
    }
    
    public static ResourceLocation getLocationFace(String par0Str, MCAPIType type, boolean special)
    {
    	String fin = StringUtils.stripControlCodes(par0Str)+":"+special;
    	return new ResourceLocation(String.format("faces/%s/%s", new Object[] {type.ending, fin}));
    }
    
    public static ResourceLocation getLocationPlayer(String par0Str, MCAPIType type, boolean special)
    {
    	String fin = StringUtils.stripControlCodes(par0Str)+":"+special;
    	return new ResourceLocation(String.format("players/%s/%s", new Object[] {type.ending, fin}));
    }

    public static ResourceLocation getLocationCape(String par0Str)
    {
        return new ResourceLocation("cloaks/" + StringUtils.stripControlCodes(par0Str));
    }
	public static class PlayerSkin implements SkinManager.SkinAvailableCallback{
		public ResourceLocation skin;
		public ResourceLocation cape;
		public boolean uploadedCape;
		public String name;
		public UUID uuid;
		
		public PlayerSkin(String player){
			name = player;
			skin = getLocationSkin(name);
		    getDownloadImageSkin(skin, name);
		    cape = getLocationCape(name);
		    ThreadDownloadImageData data = getDownloadImageCape(cape, name);
		    uploadedCape = Util.isImageDataUploaded(data);
		}

		@Override
		public void skinAvailable(Type p_152121_1_, ResourceLocation p_152121_2_, MinecraftProfileTexture profileTexture) {
			switch (DownloadedTextures.SwitchType.field_152630_a[p_152121_1_.ordinal()])
	        {
	            case 1:
	                this.skin = p_152121_2_;
	                ModLogger.info("Loading "+this.name+"'s Skin");
	                break;
	            case 2:{
	                this.cape = p_152121_2_;
	                ModLogger.info("Loading "+this.name+"'s Cape");
	                this.uploadedCape = true;
	            }
	        }
		}
	}
	@SideOnly(Side.CLIENT)
    static final class SwitchType
        {
            static final int[] field_152630_a = new int[Type.values().length];
            //private static final String __OBFID = "CL_00001832";

            static
            {
                try
                {
                    field_152630_a[Type.SKIN.ordinal()] = 1;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    field_152630_a[Type.CAPE.ordinal()] = 2;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
	
}
