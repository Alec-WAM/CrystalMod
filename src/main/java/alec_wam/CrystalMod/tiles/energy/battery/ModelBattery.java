package alec_wam.CrystalMod.tiles.energy.battery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;

@SuppressWarnings("deprecation")
public class ModelBattery implements IBakedModel 
{
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation("crystalmod:battery");
	public static FaceBakery faceBakery;
    private final ItemStack renderStack;
    private final EnumCrystalColorSpecialWithCreative color;
    
	public ModelBattery(EnumCrystalColorSpecialWithCreative color)
	{
		this.color = color;
		this.renderStack = ItemStackTools.getEmptyStack();
	}
	
	public ModelBattery(ItemStack stack, EnumCrystalColorSpecialWithCreative color)
	{
		this.color = color;
		this.renderStack = stack;
	}
    
    @Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand)
	{
    	if(side !=null || color == null)
		{
			return Collections.emptyList();
		}
		
		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		
		
		String batteryLoc = "crystalmod:block/battery/"+color.getName().toLowerCase();
        String io_blocked = "crystalmod:block/battery/io_blocked";
        String io_in = "crystalmod:block/battery/io_in";
        String io_out = "crystalmod:block/battery/io_out";

        final TextureAtlasSprite battery = RenderUtil.getSprite(batteryLoc);
        
        TextureAtlasSprite ioUp = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioDown = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioFront = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioBack = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioLeft = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioRight = RenderUtil.getSprite(io_in);
        
        EnumFacing facing = EnumFacing.NORTH;
        if(state !=null){
        	facing = state.get(BlockBattery.FACING);
    		IOType ioU = BlockBattery.getIOFromState(state, EnumFacing.UP);
    		ioUp = RenderUtil.getSprite(ioU == IOType.BLOCKED ? io_blocked : ioU == IOType.OUT ? io_out : io_in);
    		IOType ioD = BlockBattery.getIOFromState(state, EnumFacing.DOWN);
    		ioDown = RenderUtil.getSprite(ioD == IOType.BLOCKED ? io_blocked : ioD == IOType.OUT ? io_out : io_in);
    		IOType ioF = BlockBattery.getIOFromState(state, EnumFacing.NORTH);
    		ioFront = RenderUtil.getSprite(ioF == IOType.BLOCKED ? io_blocked : ioF == IOType.OUT ? io_out : io_in);
    		IOType ioB = BlockBattery.getIOFromState(state, EnumFacing.SOUTH);
    		ioBack = RenderUtil.getSprite(ioB == IOType.BLOCKED ? io_blocked : ioB == IOType.OUT ? io_out : io_in);
    		IOType ioL = BlockBattery.getIOFromState(state, EnumFacing.EAST);
    		ioLeft = RenderUtil.getSprite(ioL == IOType.BLOCKED ? io_blocked : ioL == IOType.OUT ? io_out : io_in);
    		IOType ioR = BlockBattery.getIOFromState(state, EnumFacing.WEST);
    		ioRight = RenderUtil.getSprite(ioR == IOType.BLOCKED ? io_blocked : ioR == IOType.OUT ? io_out : io_in);
        }
        ModelRotation modelRot = ModelRotation.X0_Y0;
        if(facing == EnumFacing.SOUTH){
        	modelRot = ModelRotation.X0_Y180;
        }
        if(facing == EnumFacing.WEST){
        	modelRot = ModelRotation.X0_Y270;
        }
        if(facing == EnumFacing.EAST){
        	modelRot = ModelRotation.X0_Y90;
        }
        if(facing == EnumFacing.UP){
        	modelRot = ModelRotation.X270_Y0;
        }
        if(facing == EnumFacing.DOWN){
        	modelRot = ModelRotation.X90_Y0;
        }
        
        BlockFaceUV uvBars = new BlockFaceUV(new float[] { 3.0f,13.0f,13.0f,16.0f }, 0);
        BlockPartFace faceBars = new BlockPartFace((EnumFacing)null, 0, "", uvBars);
        Vector3f min = new Vector3f(2.75f,13.25f,0f);
        Vector3f max = new Vector3f(13.25f,16f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));

        min = new Vector3f(2.75f,13.25f,13.25f);
        max = new Vector3f(13.25f,16f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        //455 Bottom Bars
        min = new Vector3f(2.75f,0f,13.25f);
        max = new Vector3f(13.25f,2.75f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(2.75f,0f,0f);
        max = new Vector3f(13.25f,2.75f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        BlockFaceUV uvBarCenterF = new BlockFaceUV(new float[] { 3.0f,0.5f,13.0f,6.5f }, 0);
        BlockPartFace faceBarCenterF = new BlockPartFace((EnumFacing)null, 0, "", uvBarCenterF);
        //413 Front Center Bar
        min = new Vector3f(2.75f,5.5f,0.5f-0.25f);
        max = new Vector3f(13.25f,10.5f,2.5f-0.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBarCenterF, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBarCenterF, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        
        //UD Bars
        BlockFaceUV uvBars2 = new BlockFaceUV(new float[] { 0.0f,3.0f,3.0f,13.0f }, 0);
        BlockPartFace faceBars2 = new BlockPartFace((EnumFacing)null, 0, "", uvBars2);
        min = new Vector3f(13.25f,2.75f,0f);
        max = new Vector3f(16f,13.25f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,2.75f,13.25f);
        max = new Vector3f(2.75f,13.25f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,2.75f,13.25f);
        max = new Vector3f(16f,13.25f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,2.75f,0f);
        max = new Vector3f(2.75f,13.25f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        //500 EW Bars
        min = new Vector3f(13.25f,0f,2.75f);
        max = new Vector3f(16f,2.75f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,0f,2.75f);
        max = new Vector3f(2.75f,2.75f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,13.25f,2.75f);
        max = new Vector3f(16f,16f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,13.25f,2.75f);
        max = new Vector3f(2.75f,16f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        //IO Cubes
        BlockFaceUV uvIOInside = new BlockFaceUV(new float[] { 0.0f,13.0f,3.0f,16.0f }, 0);
        BlockPartFace ioNone = new BlockPartFace((EnumFacing)null, 0, "", uvIOInside);
        
        BlockFaceUV uvBL = new BlockFaceUV(new float[] {0.0f,13.0f,3.0f,16.0f}, 0);
        BlockPartFace ioBL = new BlockPartFace((EnumFacing)null, 0, "", uvBL);
        
        BlockFaceUV uvBR = new BlockFaceUV(new float[] {13.0f,13.0f,16.0f,16.0f}, 0);
        BlockPartFace ioBR = new BlockPartFace((EnumFacing)null, 0, "", uvBR);
        
        BlockFaceUV uvTL = new BlockFaceUV(new float[] {0.0f,0.0f,3.0f,3.0f}, 0);
        BlockPartFace ioTL = new BlockPartFace((EnumFacing)null, 0, "", uvTL);
        
        BlockFaceUV uvTR = new BlockFaceUV(new float[] {13.0f,0.0f,16.0f,3.0f}, 0);
        BlockPartFace ioTR = new BlockPartFace((EnumFacing)null, 0, "", uvTR);
        
        
        min = new Vector3f(0f,13.25f,0f);
        max = new Vector3f(2.75f,16f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,13.25f,0f);
        max = new Vector3f(16f,16f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,13.25f,13.25f);
        max = new Vector3f(16f,16f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,13.25f,13.25f);
        max = new Vector3f(2.75f,16f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        //BOTTOM IO
        min = new Vector3f(0f,0f,0f);
        max = new Vector3f(2.75f,2.75f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,0f,13.25f);
        max = new Vector3f(16f,2.75f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,0f,13.25f);
        max = new Vector3f(2.75f,2.75f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,0f,0f);
        max = new Vector3f(16f,2.75f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
		return quads;
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
    	String texture = "blue";
    	if(color !=null)texture = color.getName().toLowerCase();
        
        String batteryLoc = "crystalmod:block/battery/"+texture;
        return RenderUtil.getSprite(batteryLoc);
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }   
    
    static {
        faceBakery = new FaceBakery();
    }
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.EMPTY;
	}
	
}

