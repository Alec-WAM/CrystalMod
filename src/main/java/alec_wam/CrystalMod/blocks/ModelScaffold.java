package alec_wam.CrystalMod.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

public class ModelScaffold implements IPerspectiveAwareModel 
{
	static FaceBakery faceBakery;
    WoodType type;
    ItemStack stack;
    
    public ModelScaffold(WoodType type) {
    	this.type = type;
    }

    public ModelScaffold(ItemStack itemStack) {
        this.type = WoodType.byMetadata(itemStack.getMetadata());
        this.stack = itemStack;
    }
    
    @Override
    public List<BakedQuad> getQuads( @Nullable IBlockState state, @Nullable EnumFacing side, long rand ) {
        if(!BLOCK_QUADS.containsKey(type)){
        	buildType(type);
        }
        
        List<BakedQuad> list = new ArrayList<BakedQuad>();
        if(ItemStackTools.isValid(stack)){
        	for(EnumFacing face : EnumFacing.VALUES){
        		list.addAll(BLOCK_QUADS.get(type).get(face));
        	}
        } else if(state !=null){
        	for(EnumFacing face : EnumFacing.VALUES){
        		//if(ModBlocks.scaffold.shouldSideBeRendered(state, state.blockAccess, state.pos, face)){
        			list.addAll(BLOCK_QUADS.get(type).get(face));
        		//}
        	}
        }
        return list;
    }
    
    public static void buildType(WoodType type){
    	ModLogger.info("ModelScaffold: Building for "+type);
    	Map<EnumFacing, List<BakedQuad>> quads = Maps.newHashMap();
        TextureAtlasSprite texture = RenderUtil.getSprite("crystalmod:blocks/scaffold/"+type.getName()+"_scaffolding");
        for(EnumFacing face : EnumFacing.VALUES){
        	List<BakedQuad> list = new ArrayList<BakedQuad>();
        	addQuadsForSide(list, face, texture);
        	quads.put(face, list);
        }
        BLOCK_QUADS.put(type, quads);
    }
    
    public static void addQuadsForSide(List<BakedQuad> list, EnumFacing face, TextureAtlasSprite sprite){
    	int widthX = sprite.getIconWidth();
    	int widthY = sprite.getIconHeight();
    	float pixel = 1.0F /*/ (float)widthX*/; 
    	//BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0F, 0.0F, 16.0F, 16.0F}, 0);
        //BlockPartFace textureFace = new BlockPartFace((EnumFacing)null, 0, "", uv);
    	for (int f = 0; f < sprite.getFrameCount(); ++f)
        {
    		int[] pixelData = sprite.getFrameTextureData(f)[0];
	    	for(int x = 0; x < widthX; x++){
	    		for(int y = 0; y < widthY; y++){
	    			boolean visible = !isPixelTransparent(pixelData, x, y, widthX, widthY);
	    			if(visible){
	    				boolean up = false;
	    				boolean down = false;
	    				boolean north = false;
	    				boolean south = false;
	    				boolean east = false;
	    				boolean west = false;
	    				Vector3f min = new Vector3f(0, 0, 0);
    					Vector3f max = new Vector3f(pixel, pixel, pixel);
    					int texRot = 0;
	    				if(face == EnumFacing.UP){
	    					up = x > 0 && y > 0 && x < widthX-1 && y < widthY-1;
	    					down = true;
	    					south = y < widthY-1 && isPixelTransparent(pixelData, x, y+1, widthX, widthY);
	    					north = y > 0 && isPixelTransparent(pixelData, x, y-1, widthX, widthY);
	    					east = x < widthX-1 && isPixelTransparent(pixelData, x+1, y, widthX, widthY);
	    					west = x > 0 && isPixelTransparent(pixelData, x-1, y, widthX, widthY);
	    					
	    					
	    					min = new Vector3f((x) * pixel, 15.0F, (y) * pixel);
	    					max = new Vector3f((x + 1) * pixel, 16.0F, (y + 1) * pixel);
	    				}
	    				if(face == EnumFacing.DOWN){
	    					up = true;
	    					down = x > 0 && y > 0 && x < widthX-1 && y < widthY-1;
	    					south = y < widthY-1 && isPixelTransparent(pixelData, x, y+1, widthX, widthY);
	    					north = y > 0 && isPixelTransparent(pixelData, x, y-1, widthX, widthY);
	    					east = x < widthX-1 && isPixelTransparent(pixelData, x+1, y, widthX, widthY);
	    					west = x > 0 && isPixelTransparent(pixelData, x-1, y, widthX, widthY);
	    					
	    					min = new Vector3f((x) * pixel, 0.0F, (y) * pixel);
	    					max = new Vector3f((x + 1) * pixel, 1.0F, (y + 1) * pixel);
	    				}
	    				if(face == EnumFacing.NORTH){
	    					texRot = 90;
	    					north = true;
	    					south = true;
	    					up = y > 0 && isPixelTransparent(pixelData, x, y+1, widthX, widthY);
	    					down = y < widthY-1 && isPixelTransparent(pixelData, x, y-1, widthX, widthY);
	    					east = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x+1, y, widthX, widthY);
	    					west = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x-1, y, widthX, widthY);
	    					
	    					
	    					min = new Vector3f(16.0F - (x) * pixel, 16.0F - (y) * pixel, 0.0F);
	    					max = new Vector3f(16.0F - (x + 1) * pixel, 16.0F - (y + 1) * pixel, 1.0F);
	    				}
	    				if(face == EnumFacing.SOUTH){
	    					north = true;
	    					south = true;
	    					up = y > 0 && isPixelTransparent(pixelData, x, y+1, widthX, widthY);
	    					down =  y < widthY-1 && isPixelTransparent(pixelData, x, y-1, widthX, widthY);
	    					east = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x+1, y, widthX, widthY);
	    					west = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x-1, y, widthX, widthY);
	    					min = new Vector3f(16.0F - (x) * pixel, 16.0F - (y) * pixel, 15.0F);
	    					max = new Vector3f(16.0F - (x + 1) * pixel, 16.0F - (y + 1) * pixel, 16.0F);
	    				}
	    				if(face == EnumFacing.EAST){
	    					east = true;
	    					west = true;
	    					up = y > 0 && isPixelTransparent(pixelData, x, y+1, widthX, widthY);
	    					down = y < widthY-1 && isPixelTransparent(pixelData, x, y-1, widthX, widthY);
	    					north = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x-1, y, widthX, widthY);
	    					south = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x+1, y, widthX, widthY);
	    					
	    					min = new Vector3f(15.0F, 16.0F - (y) * pixel, 16.0F - (x) * pixel);
	    					max = new Vector3f(16.0F, 16.0F - (y + 1) * pixel, 16.0F - (x + 1) * pixel);
	    				}
	    				if(face == EnumFacing.WEST){
	    					east = true;
	    					west = true;
	    					up = y > 0 && isPixelTransparent(pixelData, x, y+1, widthX, widthY);
	    					down = y < widthY-1 && isPixelTransparent(pixelData, x, y-1, widthX, widthY);
	    					north = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x-1, y, widthX, widthY);
	    					south = x > 0 && x < widthX-1 && isPixelTransparent(pixelData, x+1, y, widthX, widthY);
	    					
	    					min = new Vector3f(0.0F, 16.0F - (y) * pixel, 16.0F - (x) * pixel);
	    					max = new Vector3f(1.0F, 16.0F - (y + 1) * pixel, 16.0F - (x + 1) * pixel);
	    				}

		    			BlockFaceUV uv = new BlockFaceUV(new float[] { x * pixel, y * pixel, (x+1) * pixel, (y+1) * pixel}, texRot);
		    	        BlockPartFace textureFace = new BlockPartFace(face, 0, "", uv);
    					boolean shade = true;
	    				if(up){
	    					list.add(faceBakery.makeBakedQuad(min, max, textureFace, sprite, EnumFacing.UP, ModelRotation.X0_Y0, null, true, shade));
	    				}
	    				if(down){
	    					list.add(faceBakery.makeBakedQuad(min, max, textureFace, sprite, EnumFacing.DOWN, ModelRotation.X0_Y0, null, true, shade));
	    				}
	    				if(north){
	    					list.add(faceBakery.makeBakedQuad(min, max, textureFace, sprite, EnumFacing.NORTH, ModelRotation.X0_Y0, null, true, shade));
	    				}
	    				if(south){
	    					list.add(faceBakery.makeBakedQuad(min, max, textureFace, sprite, EnumFacing.SOUTH, ModelRotation.X0_Y0, null, true, shade));
	    				}
	    				if(east){
	    					list.add(faceBakery.makeBakedQuad(min, max, textureFace, sprite, EnumFacing.EAST, ModelRotation.X0_Y0, null, true, shade));
	    				}
	    				if(west){
	    					list.add(faceBakery.makeBakedQuad(min, max, textureFace, sprite, EnumFacing.WEST, ModelRotation.X0_Y0, null, true, shade));
	    				}
	    			}
	        	}
	    	}
        }
    }
    
    private static boolean isPixelTransparent(int[] data, int x, int y, int maxX, int maxY)
    {
        return x >= 0 && y >= 0 && x < maxX && y < maxY ? (data[y * maxX + x] >> 24 & 255) == 0 : true;
    }
    
    @Override
	public boolean isAmbientOcclusion() {
        return true;
    }
    
    @Override
	public boolean isGui3d() {
        return true;
    }
    
    @Override
	public boolean isBuiltInRenderer() {
        return false;
    }
    
    @Override
	public TextureAtlasSprite getParticleTexture() {
    	WoodType type = WoodType.OAK;
    	if(this.type !=null){
    		type = this.type;
        }
        return RenderUtil.getSprite("crystalmod:blocks/scaffold/"+type.getName()+"_scaffolding");
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    public static final Map<Integer, ModelScaffold> ITEMMODELS = Maps.newHashMap();
    public static final Map<WoodType, Map<EnumFacing, List<BakedQuad>>> BLOCK_QUADS = Maps.newHashMap();
    public static final Cache<WoodType, ModelScaffold> modelcache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).maximumSize(500).<WoodType, ModelScaffold>build();
    
    static {
        ModelScaffold.faceBakery = new FaceBakery();
        for(WoodType type : WoodType.values()){
			BLOCK_QUADS.put(type, Maps.newHashMap());
		}
    }

	public static void reloadModels() {
		ITEMMODELS.clear();
		BLOCK_QUADS.clear();
	}
	
	public static ModelScaffold getModelForType(WoodType type){
		try {
			return modelcache.get(type, new Callable<ModelScaffold>(){

				@Override
				public ModelScaffold call() throws Exception {
					return new ModelScaffold(type);
				}
				
			});
		}
		catch (ExecutionException e) {
			e.printStackTrace();
			return new ModelScaffold(type);
		}
	}
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return new ItemOverrideList(new ArrayList<ItemOverride>()){
			@Override
			public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity )
			{
				if( stack.getItem() != Item.getItemFromBlock(ModBlocks.scaffold))
				{
					return originalModel;
				}
				return new ModelScaffold(stack);
			}
		};
	}

	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
    	return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS, cameraTransformType);
    }

	
}

