package alec_wam.CrystalMod.tiles.cluster;


import java.awt.Color;
import java.util.List;

import com.google.common.base.Function;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.items.ItemIngot;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster.EnumClusterType;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster.ClusterData;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.Vector3d;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.FMLClientHandler;

public class RenderTileCrystalCluster extends TileEntitySpecialRenderer<TileCrystalCluster> implements ICustomItemRenderer {

	public static IBakedModel bakedCluster = null;
	
	@Override
    public void renderTileEntityAt(TileCrystalCluster te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	GlStateManager.translate(x + 0.5, y, z + 0.5);
    	GlStateManager.scale(0.5, 0.5, 0.5);
    	te.getWorld().theProfiler.startSection("crystalmod-cluster");
    	
    	EnumFacing facing = EnumFacing.getFront(te.getFacing());
    	if(facing == EnumFacing.DOWN){
    		GlStateManager.rotate(180, 1, 0, 0);
    		GlStateManager.translate(0, -2, 0);
    	}
    	if(facing == EnumFacing.NORTH){
    		GlStateManager.rotate(-90, 1, 0, 0);
    		GlStateManager.translate(0, -1, 1);
    	}
    	if(facing == EnumFacing.SOUTH){
    		GlStateManager.rotate(90, 1, 0, 0);
    		GlStateManager.translate(0, -1, -1);
    	}
    	if(facing == EnumFacing.EAST){
    		GlStateManager.rotate(-90, 0, 0, 1);
    		GlStateManager.translate(-1, -1, 0);
    	}
    	if(facing == EnumFacing.WEST){
    		GlStateManager.rotate(90, 0, 0, 1);
    		GlStateManager.translate(1, -1, 0);
    	}
    	
    	EnumClusterType type = te.getWorld().getBlockState(te.getPos()).getValue(BlockCrystalCluster.TYPE);
		Vector3d color = getColor(type);
		
		boolean debug = false;
		if(debug){
			GlStateManager.disableDepth();
		}
		renderCluster(te.getClusterData(), te.getHealth(), color, type !=EnumClusterType.DARK);
		if(debug){
			GlStateManager.enableDepth();
		}
    	te.getWorld().theProfiler.endSection();
        GlStateManager.popMatrix();
    }
	
	public static void renderCluster(ClusterData data, int health, Vector3d colorVec, boolean darkToLight){
		if(getBakedCluster() == null)return;
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	GlStateManager.pushMatrix();
    	
    	GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        
        float brightness = (float)((float)health / (float)TimeUtil.MINECRAFT_DAY_TICKS);
		if(!darkToLight)brightness = (float)((float)(TimeUtil.MINECRAFT_DAY_TICKS-health) / (float)TimeUtil.MINECRAFT_DAY_TICKS);
        float red = (float) colorVec.x;
        float green = (float) colorVec.y;
        float blue = (float) colorVec.z;
        
        List<BakedQuad> listQuads = getBakedCluster().getQuads(null, (EnumFacing)null, 0L);
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        int i = 0;

        for (int j = listQuads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = (BakedQuad)listQuads.get(i);
            vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
            vertexbuffer.addVertexData(bakedquad.getVertexData());

            vertexbuffer.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);

            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            vertexbuffer.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            tessellator.draw();
        }
        
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        
    	GlStateManager.popMatrix();
	}
	
	public static IBakedModel getBakedCluster(){
		if(bakedCluster == null){
			IModel model;
			try {
				model = OBJLoader.INSTANCE.loadModel(CrystalMod.resourceL("models/block/obj/cluster.obj"));
				Function<ResourceLocation, TextureAtlasSprite> textureGetter;
		        textureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
		        {
		            public TextureAtlasSprite apply(ResourceLocation location)
		            {
		                return RenderUtil.getSprite(location);
		            }
		        };
		
		        bakedCluster = model.bake(new SimpleModelState(DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS), DefaultVertexFormats.BLOCK, textureGetter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bakedCluster;
	}

	@Override
	public void render(ItemStack stack) {
		if(ItemStackTools.isValid(stack)){
			if(ItemNBTHelper.verifyExistance(stack, BlockCrystalCluster.TILE_NBT_STACK)){
				NBTTagCompound tileNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(BlockCrystalCluster.TILE_NBT_STACK);
				int health = tileNBT.getInteger("Health");
				ClusterData data = new ClusterData(0, 0);
				data.deserializeNBT(tileNBT.getCompoundTag("ClusterData"));
				if(lastTransform == TransformType.GUI){
					GlStateManager.translate(0, -0.8, 0);
					GlStateManager.scale(0.8, 0.8, 0.8);
				}
				EnumClusterType type = EnumClusterType.values()[stack.getMetadata()];
				Vector3d color = getColor(type);
				renderCluster(data, health, color, type !=EnumClusterType.DARK);
			}
		}
	}

	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}
	
	public Vector3d getColor(EnumClusterType type){
		switch(type){
			default : case BLUE : return new Vector3d(0, 1, 1);
			case RED : return new Vector3d(1, 0, 0);
			case GREEN : return new Vector3d(0, 1, 0);
			case DARK : return new Vector3d(1, 1, 1);
		}
	}
}
