package alec_wam.CrystalMod.tiles.energy.battery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.block.BlockState;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

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
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
	{
    	if(side !=null || color == null)
		{
			return Collections.emptyList();
		}
		
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
        
        Direction facing = Direction.NORTH;
        if(state !=null){
        	facing = state.get(BlockBattery.FACING);
    		IOType ioU = BlockBattery.getIOFromState(state, Direction.UP);
    		ioUp = RenderUtil.getSprite(ioU == IOType.BLOCKED ? io_blocked : ioU == IOType.OUT ? io_out : io_in);
    		IOType ioD = BlockBattery.getIOFromState(state, Direction.DOWN);
    		ioDown = RenderUtil.getSprite(ioD == IOType.BLOCKED ? io_blocked : ioD == IOType.OUT ? io_out : io_in);
    		IOType ioF = BlockBattery.getIOFromState(state, Direction.NORTH);
    		ioFront = RenderUtil.getSprite(ioF == IOType.BLOCKED ? io_blocked : ioF == IOType.OUT ? io_out : io_in);
    		IOType ioB = BlockBattery.getIOFromState(state, Direction.SOUTH);
    		ioBack = RenderUtil.getSprite(ioB == IOType.BLOCKED ? io_blocked : ioB == IOType.OUT ? io_out : io_in);
    		IOType ioL = BlockBattery.getIOFromState(state, Direction.EAST);
    		ioLeft = RenderUtil.getSprite(ioL == IOType.BLOCKED ? io_blocked : ioL == IOType.OUT ? io_out : io_in);
    		IOType ioR = BlockBattery.getIOFromState(state, Direction.WEST);
    		ioRight = RenderUtil.getSprite(ioR == IOType.BLOCKED ? io_blocked : ioR == IOType.OUT ? io_out : io_in);
        }
        if(ItemStackTools.isValid(renderStack)){
        	facing = Direction.SOUTH;
        	if(ItemNBTHelper.verifyExistance(renderStack, TileEntityBattery.NBT_DATA)){
        		CompoundNBT nbt = ItemNBTHelper.getCompound(renderStack).getCompound(TileEntityBattery.NBT_DATA);
        		IOType ioU = IOType.values()[nbt.getByte("io.up")];
        		ioUp = RenderUtil.getSprite(ioU == IOType.BLOCKED ? io_blocked : ioU == IOType.OUT ? io_out : io_in);
        		IOType ioD = IOType.values()[nbt.getByte("io.down")];
        		ioDown = RenderUtil.getSprite(ioD == IOType.BLOCKED ? io_blocked : ioD == IOType.OUT ? io_out : io_in);
        		IOType ioF = IOType.values()[nbt.getByte("io.north")];
        		ioFront = RenderUtil.getSprite(ioF == IOType.BLOCKED ? io_blocked : ioF == IOType.OUT ? io_out : io_in);
        		IOType ioB = IOType.values()[nbt.getByte("io.south")];
        		ioBack = RenderUtil.getSprite(ioB == IOType.BLOCKED ? io_blocked : ioB == IOType.OUT ? io_out : io_in);
        		IOType ioL = IOType.values()[nbt.getByte("io.east")];
        		ioLeft = RenderUtil.getSprite(ioL == IOType.BLOCKED ? io_blocked : ioL == IOType.OUT ? io_out : io_in);
        		IOType ioR = IOType.values()[nbt.getByte("io.west")];
        		ioRight = RenderUtil.getSprite(ioR == IOType.BLOCKED ? io_blocked : ioR == IOType.OUT ? io_out : io_in);
        	}
        }
        ModelRotation modelRot = ModelRotation.X0_Y0;
        if(facing == Direction.SOUTH){
        	modelRot = ModelRotation.X0_Y180;
        }
        if(facing == Direction.WEST){
        	modelRot = ModelRotation.X0_Y270;
        }
        if(facing == Direction.EAST){
        	modelRot = ModelRotation.X0_Y90;
        }
        if(facing == Direction.UP){
        	modelRot = ModelRotation.X270_Y0;
        }
        if(facing == Direction.DOWN){
        	modelRot = ModelRotation.X90_Y0;
        }
        
        BlockFaceUV uvBars = new BlockFaceUV(new float[] { 3.0f,13.0f,13.0f,16.0f }, 0);
        BlockPartFace faceBars = new BlockPartFace((Direction)null, 0, "", uvBars);
        Vector3f min = new Vector3f(2.75f,13.25f,0f);
        Vector3f max = new Vector3f(13.25f,16f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));

        min = new Vector3f(2.75f,13.25f,13.25f);
        max = new Vector3f(13.25f,16f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        //455 Bottom Bars
        min = new Vector3f(2.75f,0f,13.25f);
        max = new Vector3f(13.25f,2.75f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(2.75f,0f,0f);
        max = new Vector3f(13.25f,2.75f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        BlockFaceUV uvBarCenterF = new BlockFaceUV(new float[] { 3.0f,0.5f,13.0f,6.5f }, 0);
        BlockPartFace faceBarCenterF = new BlockPartFace((Direction)null, 0, "", uvBarCenterF);
        //413 Front Center Bar
        min = new Vector3f(2.75f,5.5f,0.5f-0.25f);
        max = new Vector3f(13.25f,10.5f,2.5f-0.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBarCenterF, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBarCenterF, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        
        //UD Bars
        BlockFaceUV uvBars2 = new BlockFaceUV(new float[] { 0.0f,3.0f,3.0f,13.0f }, 0);
        BlockPartFace faceBars2 = new BlockPartFace((Direction)null, 0, "", uvBars2);
        min = new Vector3f(13.25f,2.75f,0f);
        max = new Vector3f(16f,13.25f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(0f,2.75f,13.25f);
        max = new Vector3f(2.75f,13.25f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(13.25f,2.75f,13.25f);
        max = new Vector3f(16f,13.25f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(0f,2.75f,0f);
        max = new Vector3f(2.75f,13.25f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        
        //500 EW Bars
        min = new Vector3f(13.25f,0f,2.75f);
        max = new Vector3f(16f,2.75f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(0f,0f,2.75f);
        max = new Vector3f(2.75f,2.75f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(13.25f,13.25f,2.75f);
        max = new Vector3f(16f,16f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(0f,13.25f,2.75f);
        max = new Vector3f(2.75f,16f,13.25f);
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        //IO Cubes
        BlockFaceUV uvIOInside = new BlockFaceUV(new float[] { 0.0f,13.0f,3.0f,16.0f }, 0);
        BlockPartFace ioNone = new BlockPartFace((Direction)null, 0, "", uvIOInside);
        
        BlockFaceUV uvBL = new BlockFaceUV(new float[] {0.0f,13.0f,3.0f,16.0f}, 0);
        BlockPartFace ioBL = new BlockPartFace((Direction)null, 0, "", uvBL);
        
        BlockFaceUV uvBR = new BlockFaceUV(new float[] {13.0f,13.0f,16.0f,16.0f}, 0);
        BlockPartFace ioBR = new BlockPartFace((Direction)null, 0, "", uvBR);
        
        BlockFaceUV uvTL = new BlockFaceUV(new float[] {0.0f,0.0f,3.0f,3.0f}, 0);
        BlockPartFace ioTL = new BlockPartFace((Direction)null, 0, "", uvTL);
        
        BlockFaceUV uvTR = new BlockFaceUV(new float[] {13.0f,0.0f,16.0f,3.0f}, 0);
        BlockPartFace ioTR = new BlockPartFace((Direction)null, 0, "", uvTR);
        
        
        min = new Vector3f(0f,13.25f,0f);
        max = new Vector3f(2.75f,16f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioUp, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioFront, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioRight, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(13.25f,13.25f,0f);
        max = new Vector3f(16f,16f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioUp, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioFront, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioLeft, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(13.25f,13.25f,13.25f);
        max = new Vector3f(16f,16f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioUp, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioBack, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioLeft, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(0f,13.25f,13.25f);
        max = new Vector3f(2.75f,16f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioUp, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioBack, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTR, ioRight, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        //BOTTOM IO
        min = new Vector3f(0f,0f,0f);
        max = new Vector3f(2.75f,2.75f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioDown, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioFront, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioRight, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(13.25f,0f,13.25f);
        max = new Vector3f(16f,2.75f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioDown, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioBack, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioLeft, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(0f,0f,13.25f);
        max = new Vector3f(2.75f,2.75f,16f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioDown, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioBack, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioRight, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
        min = new Vector3f(13.25f,0f,0f);
        max = new Vector3f(16f,2.75f,2.75f);
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.UP, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioTL, ioDown, Direction.DOWN, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBL, ioFront, Direction.NORTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.SOUTH, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, Direction.WEST, modelRot, (BlockPartRotation)null, false));
        quads.add(faceBakery.makeBakedQuad(min, max, ioBR, ioLeft, Direction.EAST, modelRot, (BlockPartRotation)null, false));
        
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

