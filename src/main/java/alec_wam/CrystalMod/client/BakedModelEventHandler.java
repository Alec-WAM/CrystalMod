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
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.energy.battery.BlockBattery;
import alec_wam.CrystalMod.tiles.energy.battery.ModelBattery;
import alec_wam.CrystalMod.tiles.pipes.energy.cu.BlockPipeEnergyCU;
import alec_wam.CrystalMod.tiles.pipes.energy.rf.BlockPipeEnergyRF;
import alec_wam.CrystalMod.tiles.pipes.model.ModelPipeBaked;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
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
		/*event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/crystalshardblock"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/crate/void"));
		
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/item"));
		for(EnumCrystalColorSpecial color : EnumCrystalColorSpecial.values()){
			event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/energy_cu_"+color.getName().toLowerCase()));
			event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/energy_rf_"+color.getName().toLowerCase()));
		}
		
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/iron_cap"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/pipe/connector"));
		
		//Battery
		for(EnumCrystalColorSpecialWithCreative color : EnumCrystalColorSpecialWithCreative.values()){
			event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/battery/"+color.getName().toLowerCase()));
		}
		for(IOType io : IOType.values()){
			event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/battery/io_"+io.getName().toLowerCase()));
		}
		for(int i = 0; i < 9; i++){
			event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/battery/meter/"+i));
		}
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/battery/meter/uncharged"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/battery/meter/charged"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/battery/meter/creative"));
		

		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/fluid/milk_still"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/fluid/milk_flow"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/fluid/xp_still"));
		event.getMap().registerSprite(null, new ResourceLocation("crystalmod:block/fluid/xp_flow"));*/

	}
	
	@SubscribeEvent
    public static void onBakeModel(final ModelBakeEvent event) {
		createShardModels(event);
		
		ResourceLocation registryNamePipe = ModBlocks.pipeItem.getRegistryName();
		for(BlockState state : ModBlocks.pipeItem.getStateContainer().getValidStates()){
			ModelResourceLocation model = new ModelResourceLocation(registryNamePipe, BlockModelShapes.getPropertyMapString(state.getValues()));
			ModelPipeBaked pipeModel = new ModelPipeBaked(RenderUtil.getSprite("crystalmod:block/pipe/item"));
			event.getModelRegistry().put(model, pipeModel);
		}
		
		for(EnumCrystalColorSpecial color : EnumCrystalColorSpecial.values()){
			if(ModBlocks.pipeEnergyCUGroup.getBlocksMap() !=null){
				BlockPipeEnergyCU pipeBlockCU = ModBlocks.pipeEnergyCUGroup.getBlock(color);
				ResourceLocation registryNamePipeEnergy = pipeBlockCU.getRegistryName();
				for(BlockState state : pipeBlockCU.getStateContainer().getValidStates()){
					ModelResourceLocation model = new ModelResourceLocation(registryNamePipeEnergy, BlockModelShapes.getPropertyMapString(state.getValues()));
					ModelPipeBaked pipeModel = new ModelPipeBaked(RenderUtil.getSprite("crystalmod:block/pipe/energy_cu_"+color.getName().toLowerCase()));
					event.getModelRegistry().put(model, pipeModel);
				}
			}
			
			if(ModBlocks.pipeEnergyRFGroup.getBlocksMap() !=null){
				BlockPipeEnergyRF pipeBlockRF = ModBlocks.pipeEnergyRFGroup.getBlock(color);
				ResourceLocation registryNamePipeEnergy = pipeBlockRF.getRegistryName();
				for(BlockState state : pipeBlockRF.getStateContainer().getValidStates()){
					ModelResourceLocation model = new ModelResourceLocation(registryNamePipeEnergy, BlockModelShapes.getPropertyMapString(state.getValues()));
					ModelPipeBaked pipeModel = new ModelPipeBaked(RenderUtil.getSprite("crystalmod:block/pipe/energy_rf_"+color.getName().toLowerCase()));
					event.getModelRegistry().put(model, pipeModel);
				}
			}
		}
		
		for(EnumCrystalColorSpecialWithCreative color : EnumCrystalColorSpecialWithCreative.values()){
			BlockBattery batteryBlock = ModBlocks.batteryGroup.getBlock(color);
			ResourceLocation registryNameBattery = batteryBlock.getRegistryName();
			for(BlockState state : batteryBlock.getStateContainer().getValidStates()){
				ModelResourceLocation model = new ModelResourceLocation(registryNameBattery, BlockModelShapes.getPropertyMapString(state.getValues()));
				ModelBattery batteryModel = new ModelBattery(color);
				event.getModelRegistry().put(model, batteryModel);
			}
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
			            public List<BakedQuad> getQuads(BlockState blockState, Direction side, Random rand)
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
