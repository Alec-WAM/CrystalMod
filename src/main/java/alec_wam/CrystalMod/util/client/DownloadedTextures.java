package alec_wam.CrystalMod.util.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.util.Util;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class DownloadedTextures {

	public static Map<UUID, PlayerSkin> skins = new HashMap<UUID, PlayerSkin>();
	public static Map<UUID, MCAPIResource> special = new HashMap<UUID, MCAPIResource>();
	
	public static enum MCAPIType{
		Two("2d"), Thr("3d");
		public String ending;
		MCAPIType(String type){
			ending = type;
		}
	}
	
	public static class MCAPIResource{
		
		public final UUID uuid;
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
				getDownloadImageFace(face2d, UUIDUtils.fromUUID(uuid), MCAPIType.Two, 32, true);
			}
			return face2d;
		}
		
		public ResourceLocation getFace3d(){
			if(face3d == null){
				face3d = getLocationFace(UUIDUtils.fromUUID(uuid), MCAPIType.Thr, true);
				getDownloadImageFace(face3d, UUIDUtils.fromUUID(uuid), MCAPIType.Thr, 32, true);
			}
			return face3d;
		}
		
		public ResourceLocation getSpecialFace2d(){
			if(specialFace2d == null){
				specialFace2d = getLocationFace(UUIDUtils.fromUUID(uuid), MCAPIType.Two, false);
				getDownloadImageFace(specialFace2d, UUIDUtils.fromUUID(uuid), MCAPIType.Two, 32, false);
			}
			return specialFace2d;
		}
		
		public ResourceLocation getSpecialFace3d(){
			if(specialFace3d == null){
				specialFace3d = getLocationFace(UUIDUtils.fromUUID(uuid), MCAPIType.Thr, false);
				getDownloadImageFace(specialFace3d, UUIDUtils.fromUUID(uuid), MCAPIType.Thr, 32, false);
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
	
	public static PlayerSkin getPlayerSkin(UUID uuid){
		if(uuid == null)return null;
		//if(!skins.containsKey(uuid)){
			//skins.put(uuid, new PlayerSkin(uuid));
		//}
		return new PlayerSkin(uuid);//skins.get(uuid);
	}
	
	public static MCAPIResource getSpecialResource(UUID uuid){
		if(uuid == null)return null;
		if(!special.containsKey(uuid)){
			special.put(uuid, new MCAPIResource(uuid));
		}
		return special.get(uuid);
	}
	
	public static ResourceLocation getSkin(UUID uuid){
		return getPlayerSkin(uuid).getLocationSkin();
	}
	
	public static ResourceLocation getCape(UUID uuid){
		return getPlayerSkin(uuid).getLocationCape();
	}
	
	public static boolean hasCape(UUID uuid){
		return getPlayerSkin(uuid).getLocationSkin() !=null;
	}
	
	public static Map<Type, ResourceLocation> downloadPlayerTextures(GameProfile profile){
		Map<Type, ResourceLocation> textures = Maps.newHashMap();
		String username = profile.getName();
		ResourceLocation skin = new ResourceLocation("skins/"+username);
		getDownloadImage(skin, "http://skins.minecraft.net/MinecraftSkins/"+username+".png", DefaultPlayerSkin.getDefaultSkin(profile.getId()), new ImageBufferDownload());
		textures.put(Type.SKIN, skin);

		ResourceLocation cloak = new ResourceLocation("cloaks/"+username);
		if(Util.isImageDataUploaded(getDownloadImage(cloak, "http://skins.minecraft.net/MinecraftCloaks/"+username+".png", null, null))){
			textures.put(Type.CAPE, cloak);
		}
		return textures;
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
    
    public static String getFaceUrl(String par0Str, MCAPIType type, int size, boolean helmet)
    {
    	String bool = ""+helmet;
    	
    	if(type == MCAPIType.Thr){
    		return String.format("https://crafatar.com/renders/head/%s?size="+size+(helmet ? "&overlay" : ""));
    	}
    	if(type == MCAPIType.Two){
    		return String.format("https://crafatar.com/renders/avatars/%s?size="+size+(helmet ? "&overlay" : ""));
    	}
    	//Fallback
    	return String.format("https://mcapi.ca/avatar/%s/%s/%s/%s", new Object[] {type.ending, StringUtils.stripControlCodes(par0Str), ""+size, ""+bool});
    }
    
    public static String getPlayerUrl(String par0Str, MCAPIType type, int size, boolean helmet)
    {
    	String bool = ""+helmet;
    	return String.format("https://mcapi.ca/skin/%s/%s/%s/%s", new Object[] {type.ending, StringUtils.stripControlCodes(par0Str), ""+size, ""+bool});
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
    
	public static class PlayerSkin implements SkinManager.SkinAvailableCallback{
		private String skinType;
		private GameProfile profile;
		private Map<Type, ResourceLocation> playerTextures = Maps.newEnumMap(Type.class);
		
		public PlayerSkin(UUID uuid){
			profile = new GameProfile(uuid, ProfileUtil.getUsername(uuid));
			playerTextures = downloadPlayerTextures(profile);
		}

		public String getSkinType()
	    {
	        return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.profile.getId()) : this.skinType;
	    }

	    public ResourceLocation getLocationSkin()
	    {
	        return (ResourceLocation)Objects.firstNonNull(this.playerTextures.get(Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.profile.getId()));
	    }

	    @Nullable
	    public ResourceLocation getLocationCape()
	    {
	        return (ResourceLocation)this.playerTextures.get(Type.CAPE);
	    }
	    
	    public Map<Type, ResourceLocation> getPlayerTextures(){
	    	return playerTextures;
	    }
		
		@Override
		public void skinAvailable(Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture)
        {
			//ModLogger.info("Skin Available "+typeIn+" "+location);
            switch (typeIn)
            {
                case SKIN:
                    this.playerTextures.put(Type.SKIN, location);
                    this.skinType = profileTexture.getMetadata("model");

                    if (this.skinType == null)
                    {
                        this.skinType = "default";
                    }

                    break;
                case CAPE:
                    this.playerTextures.put(Type.CAPE, location);
                    break;
                case ELYTRA:
                    this.playerTextures.put(Type.ELYTRA, location);
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
