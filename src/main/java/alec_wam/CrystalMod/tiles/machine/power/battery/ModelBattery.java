package alec_wam.CrystalMod.tiles.machine.power.battery;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.*;
import net.minecraft.util.*;

import java.util.*;

import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.model.dynamic.DelegatingDynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ModelBattery extends DelegatingDynamicItemAndBlockModel 
{
	public static final ModelBattery INSTANCE = new ModelBattery();
	public static final TileEntityBattery BATTERY = new TileEntityBattery();
    static FaceBakery faceBakery;
    
    public ModelBattery() {
    	super();
    	state = null;
    }
    
    public FakeBatteryState state;
    
    public ModelBattery(FakeBatteryState state, EnumFacing facing, long rand) {
        super(state, facing, rand);
        this.state = state;
    }

    public ModelBattery(ItemStack itemStack, World world, EntityLivingBase entity) {
        super(itemStack, world, entity);
        state = null;
    }
    
    public List<BakedQuad> getFaceQuads(final EnumFacing p_177551_1_) {
        return new ArrayList<BakedQuad>();
    }
    
    public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        
        //if(state !=null && state.pos !=BlockPos.ORIGIN)return list;
        
        String color = "blue";
        if(state !=null){
        	BatteryType type = state.state.getValue(BlockBattery.TYPE);
        	if(type !=null){
        		color = type.getName().toLowerCase();
        	}
        }
        
        String batteryLoc = "crystalmod:blocks/machine/battery/battery_"+color;
        String io_blocked = "crystalmod:blocks/machine/battery/io_blocked";
        String io_in = "crystalmod:blocks/machine/battery/io_in";
        String io_out = "crystalmod:blocks/machine/battery/io_out";

        final TextureAtlasSprite battery = RenderUtil.getSprite(batteryLoc);
        
        TextureAtlasSprite ioUp = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioDown = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioFront = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioBack = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioLeft = RenderUtil.getSprite(io_in);
        TextureAtlasSprite ioRight = RenderUtil.getSprite(io_in);
        
        EnumFacing facing = EnumFacing.NORTH;
        if(state !=null){
        	if(state.battery !=null){
        		facing = EnumFacing.getFront(state.battery.facing);
        		IOType ioU = state.battery.getIO(EnumFacing.UP);
        		ioUp = RenderUtil.getSprite(ioU == IOType.BLOCKED ? io_blocked : ioU == IOType.OUT ? io_out : io_in);
        		IOType ioD = state.battery.getIO(EnumFacing.DOWN);
        		ioDown = RenderUtil.getSprite(ioD == IOType.BLOCKED ? io_blocked : ioD == IOType.OUT ? io_out : io_in);
        		IOType ioF = state.battery.getIO(EnumFacing.NORTH);
        		ioFront = RenderUtil.getSprite(ioF == IOType.BLOCKED ? io_blocked : ioF == IOType.OUT ? io_out : io_in);
        		IOType ioB = state.battery.getIO(EnumFacing.SOUTH);
        		ioBack = RenderUtil.getSprite(ioB == IOType.BLOCKED ? io_blocked : ioB == IOType.OUT ? io_out : io_in);
        		IOType ioL = state.battery.getIO(EnumFacing.EAST);
        		ioLeft = RenderUtil.getSprite(ioL == IOType.BLOCKED ? io_blocked : ioL == IOType.OUT ? io_out : io_in);
        		IOType ioR = state.battery.getIO(EnumFacing.WEST);
        		ioRight = RenderUtil.getSprite(ioR == IOType.BLOCKED ? io_blocked : ioR == IOType.OUT ? io_out : io_in);
        	}
        }
        ModelRotation modelRot = ModelRotation.X0_Y180;
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
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));

        min = new Vector3f(2.75f,13.25f,13.25f);
        max = new Vector3f(13.25f,16f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        //455 Bottom Bars
        min = new Vector3f(2.75f,0f,13.25f);
        max = new Vector3f(13.25f,2.75f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(2.75f,0f,0f);
        max = new Vector3f(13.25f,2.75f,2.75f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        BlockFaceUV uvBarCenterF = new BlockFaceUV(new float[] { 3.0f,0.5f,13.0f,6.5f }, 0);
        BlockPartFace faceBarCenterF = new BlockPartFace((EnumFacing)null, 0, "", uvBarCenterF);
        //413 Front Center Bar
        min = new Vector3f(2.75f,5.5f,0.5f-0.25f);
        max = new Vector3f(13.25f,10.5f,2.5f-0.25f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBarCenterF, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBarCenterF, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        
        //UD Bars
        BlockFaceUV uvBars2 = new BlockFaceUV(new float[] { 0.0f,3.0f,3.0f,13.0f }, 0);
        BlockPartFace faceBars2 = new BlockPartFace((EnumFacing)null, 0, "", uvBars2);
        min = new Vector3f(13.25f,2.75f,0f);
        max = new Vector3f(16f,13.25f,2.75f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,2.75f,13.25f);
        max = new Vector3f(2.75f,13.25f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,2.75f,13.25f);
        max = new Vector3f(16f,13.25f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,2.75f,0f);
        max = new Vector3f(2.75f,13.25f,2.75f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        
        //500 EW Bars
        min = new Vector3f(13.25f,0f,2.75f);
        max = new Vector3f(16f,2.75f,13.25f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,0f,2.75f);
        max = new Vector3f(2.75f,2.75f,13.25f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,13.25f,2.75f);
        max = new Vector3f(16f,16f,13.25f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,13.25f,2.75f);
        max = new Vector3f(2.75f,16f,13.25f);
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars2, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, faceBars, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
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
        list.add(faceBakery.makeBakedQuad(min, max, ioBR, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTR, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTL, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,13.25f,0f);
        max = new Vector3f(16f,16f,2.75f);
        list.add(faceBakery.makeBakedQuad(min, max, ioBL, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTR, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTR, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,13.25f,13.25f);
        max = new Vector3f(16f,16f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, ioTL, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTR, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTL, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,13.25f,13.25f);
        max = new Vector3f(2.75f,16f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, ioTR, ioUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTL, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTR, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        //BOTTOM IO
        min = new Vector3f(0f,0f,0f);
        max = new Vector3f(2.75f,2.75f,2.75f);
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBR, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBR, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBL, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,0f,13.25f);
        max = new Vector3f(16f,2.75f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBR, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBR, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBL, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(0f,0f,13.25f);
        max = new Vector3f(2.75f,2.75f,16f);
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBL, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBL, ioBack, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBR, ioRight, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(13.25f,0f,0f);
        max = new Vector3f(16f,2.75f,2.75f);
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioTL, ioDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBL, ioFront, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioNone, battery, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, ioBR, ioLeft, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        
        
        //METER
        
        if(state != null && state.pos == BlockPos.ORIGIN && state.battery !=null){
        	list.addAll(getMeterQuads(facing, state.battery.energyStorage.getCEnergyStored(), state.battery.energyStorage.getMaxCEnergyStored()));
        }
        
        return list;
    }
    
    public List<BakedQuad> getMeterQuads(EnumFacing face, int power, int maxPower){
    	Vector3f min = new Vector3f(2.75f,2.75f,2.75f);
    	Vector3f max = new Vector3f(13.25f,13.25f,13.25f);

        String meter = "crystalmod:blocks/machine/battery/meter/";
        String meterC = "crystalmod:blocks/machine/battery/meter/charged";
        String meterU = "crystalmod:blocks/machine/battery/meter/uncharged";
        TextureAtlasSprite meterUp = RenderUtil.getSprite(meterU);
        TextureAtlasSprite meterDown = RenderUtil.getSprite(meterU);
        TextureAtlasSprite meterSide = RenderUtil.getSprite(meter+"0");
    	
        
        if(power > 0){
			meterDown = RenderUtil.getSprite(meterC);
			meterSide = RenderUtil.getSprite(meter+(Math.min(8, state.battery.getScaledEnergyStored(9))));
		}
		if(power >= maxPower){
			meterUp = RenderUtil.getSprite(meterC);
		}
        
		ModelRotation modelRot = ModelRotation.X0_Y0;
        if(face == EnumFacing.SOUTH){
        	modelRot = ModelRotation.X0_Y180;
        }
        if(face == EnumFacing.WEST){
        	modelRot = ModelRotation.X0_Y270;
        }
        if(face == EnumFacing.EAST){
        	modelRot = ModelRotation.X0_Y90;
        }
        if(face == EnumFacing.UP){
        	modelRot = ModelRotation.X270_Y0;
        }
        if(face == EnumFacing.DOWN){
        	modelRot = ModelRotation.X90_Y0;
        }
        
        BlockFaceUV uvMeter = new BlockFaceUV(new float[] { 4f,4f,12f,12f }, 0);
        BlockPartFace meterFace = new BlockPartFace((EnumFacing)null, 0, "", uvMeter);
		
    	List<BakedQuad> list = Lists.newArrayList();
        list.add(faceBakery.makeBakedQuad(min, max, meterFace, meterUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, meterFace, meterDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, meterFace, meterSide, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, meterFace, meterSide, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, meterFace, meterSide, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        list.add(faceBakery.makeBakedQuad(min, max, meterFace, meterSide, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
		return list;
    }
    
    public boolean isAmbientOcclusion() {
        return false;
    }
    
    public boolean isGui3d() {
        return true;
    }
    
    public boolean isBuiltInRenderer() {
        return false;
    }
    
    public TextureAtlasSprite getParticleTexture() {
        return RenderUtil.getTexture(Blocks.IRON_BLOCK.getDefaultState());
    }
    
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    static Map<BatteryType, ModelBattery> models = Maps.newHashMap();
    
    public static final ModelBattery ITEMMODEL = new ModelBattery();
    
    public void resetBattery(TileEntityBattery battery){
    	if(battery == null)return;
    	for(EnumFacing face : EnumFacing.VALUES)
    		battery.setIO(face, IOType.IN);
    	battery.energyStorage.setEnergyStored(0);
    }
    
    static {
        ModelBattery.faceBakery = new FaceBakery();
    }

	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return (state instanceof FakeBatteryState) ? new ModelBattery((FakeBatteryState)state, side, rand) : null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		/*if(models !=null && models.state !=null && models.state.battery !=null){
    		resetBattery(models.state.battery);
        	if(ItemNBTHelper.verifyExistance(stack, "BatteryData")){
        		models.state.battery.readCustomNBT(stack.getTagCompound().getCompoundTag("BatteryData"));
        	}
        	models.state.battery.facing = EnumFacing.NORTH.ordinal();
    		return models.get(arg0);
    	}*/
    	resetBattery(BATTERY);
    	if(ItemNBTHelper.verifyExistance(stack, "BatteryData")){
    		BATTERY.readCustomNBT(stack.getTagCompound().getCompoundTag("BatteryData"));
    	}
    	BATTERY.facing = EnumFacing.EAST.ordinal();
    	FakeBatteryState state = new FakeBatteryState(ModBlocks.battery.getStateFromMeta(stack.getItemDamage()), CrystalMod.proxy.getClientWorld(), BlockPos.ORIGIN, BATTERY);
    	ModelBattery model = new ModelBattery(state, EnumFacing.EAST, 0L);
    	return model;
	}

	
}

