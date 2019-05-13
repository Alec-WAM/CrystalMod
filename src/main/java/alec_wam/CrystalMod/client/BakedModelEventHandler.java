package alec_wam.CrystalMod.client;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import javax.vecmath.Vector3f;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystalShard;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.pipes.model.ModelPipeBaked;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, value = Dist.CLIENT)
public class BakedModelEventHandler {
	public static void registerOBJ(){
		OBJLoader.INSTANCE.addDomain(CrystalMod.MODID);
		ModelLoaderRegistry.registerLoader(BakedModelLoader.INSTANCE);
	}
	
	@SubscribeEvent
    public static void onStitch(final TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/crystalshardblock"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/crate/void"));
		
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/item"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/io_in"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/io_out"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/io_inout"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/iron_cap"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/connector"));
	}
	
	@SubscribeEvent
    public static void onBakeModel(final ModelBakeEvent event) {
		createShardModels(event);
		
		ResourceLocation registryNamePipe = ModBlocks.pipeItem.getRegistryName();
		for(IBlockState state : ModBlocks.pipeItem.getStateContainer().getValidStates()){
			ModelResourceLocation model = new ModelResourceLocation(registryNamePipe, BlockModelShapes.getPropertyMapString(state.getValues()));
			ModelPipeBaked pipeModel = new ModelPipeBaked();
			event.getModelRegistry().put(model, pipeModel);
		}
	}
	
	public static void createShardModels(ModelBakeEvent event){
		float modelSize = 0.8f;
        final TRSRTransformation trans0 = new TRSRTransformation(new Vector3f(0.5f, 0, 0.5f), null, new Vector3f(modelSize, modelSize, modelSize), null);
        final TRSRTransformation trans1 = new TRSRTransformation(new Vector3f(0.5f, 0, 0.5f), TRSRTransformation.quatFromXYZDegrees(new Vector3f(0.0F, 40.0F, 0.0F)), new Vector3f(modelSize, modelSize, modelSize), null);
        final TRSRTransformation trans2 = new TRSRTransformation(new Vector3f(0.5f, 0, 0.5f), TRSRTransformation.quatFromXYZDegrees(new Vector3f(0.0F, 90.0F, 0.0F)), new Vector3f(modelSize, modelSize, modelSize), null);
        for(EnumCrystalColor color : EnumCrystalColor.values()){
			Block block = ModBlocks.crystalShardBlock.getBlock(color);
			ResourceLocation registryName = block.getRegistryName();
			//TRSRTransformation trans2 = new TRSRTransformation(new Vector3f(0.5f, 0, 0.5f), null, new Vector3f(modelSize, modelSize, modelSize), null);
	        //TRSRTransformation trans3 = new TRSRTransformation(new Vector3f(0.5f, 0, 0.5f), null, new Vector3f(modelSize, modelSize, modelSize), null);
	        for(int i = 1; i < 4; i++){
				ModelResourceLocation model = new ModelResourceLocation(registryName, BlockModelShapes.getPropertyMapString(block.getDefaultState().with(BlockCrystalShard.SHARDS_1_3, Integer.valueOf(i)).getValues()));
				try {
					OBJModel modelUb = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(CrystalMod.MODID, "models/block/obj/crystalshard_"+i+".obj"));
					Function<ResourceLocation, TextureAtlasSprite> textureGetter;
			        textureGetter = location -> Minecraft.getInstance().getTextureMap().getAtlasSprite("crystalmod:block/"+registryName.getPath());
			        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
			        builder.put(ModelLoader.White.LOCATION.toString(), ModelLoader.White.INSTANCE);
			        TextureAtlasSprite missing = textureGetter.apply(new ResourceLocation("missingno"));
			        for (String e : modelUb.getMatLib().getMaterialNames())
			        {
			            builder.put(e, textureGetter.apply(modelUb.getMatLib().getMaterial(e).getTexture().getTextureLocation()));
			        }
			        builder.put("missingno", missing);
			        ImmutableMap<String, TextureAtlasSprite> textures = builder.build();
			        IBakedModel bakedModel = modelUb.new OBJBakedModel(modelUb, trans0, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK, textures){
			        	private ImmutableList<BakedQuad> quads2;
			        	@Override
			            public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, Random rand)
			            {
			        		int randInt = rand.nextInt(3);
			        		if(randInt == 1){
			        			if(side == null){
			        				if(quads2 == null){
			        					quads2 = BakedModelHelper.buildQuads(modelUb, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK, trans1, textures);
			        				}
			        				return quads2;
			        			}
			        		}
			        		if(randInt == 2){
			        			if(side == null){
			        				if(quads2 == null){
			        					quads2 = BakedModelHelper.buildQuads(modelUb, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK, trans2, textures);
			        				}
			        				return quads2;
			        			}
			        		}
			        		return super.getQuads(blockState, side, rand);
			            }
			        };
			        
			        
			        event.getModelRegistry().put(model, bakedModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}   
	}
}