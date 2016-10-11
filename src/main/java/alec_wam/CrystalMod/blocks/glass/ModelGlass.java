package alec_wam.CrystalMod.blocks.glass;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModelGlass extends DynamicItemAndBlockModel {

	public static final ModelGlass INSTANCE = new ModelGlass();
	private final GlassBlockState state;
	private final ItemStack stack;
	public ModelGlass(){
		super(true, false);
		state = null;
		stack = null;
	}
	public ModelGlass(ItemStack stack){
		super(false, true);
		state = null;
		this.stack = stack;
	}
	public ModelGlass(GlassBlockState state){
		super(false, false);
		this.state = state;
		this.stack = null;
	}

	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return super.getQuads(state, side, rand);
	}
	
	@Override
	public List<BakedQuad> getGeneralQuads() {
		List<BakedQuad> list = Lists.newArrayList();
		
		boolean renderUp = true, renderD = true, renderN = true, renderS = true, renderW = true, renderE = true;
		ModelRotation rot = ModelRotation.X0_Y0;
		GlassType type = GlassType.BLUE;
		if(stack !=null){
			type = GlassType.values()[stack.getMetadata() % (GlassType.values().length)];
		}else if(state !=null){
			type = state.getValue(BlockCrystalGlass.TYPE);
			renderUp = !state.getValue(BlockCrystalGlass.CONNECTED_UP);
			renderD = !state.getValue(BlockCrystalGlass.CONNECTED_DOWN);
			renderN = !state.getValue(BlockCrystalGlass.CONNECTED_NORTH);
			renderS = !state.getValue(BlockCrystalGlass.CONNECTED_SOUTH);
			renderW = !state.getValue(BlockCrystalGlass.CONNECTED_WEST);
			renderE = !state.getValue(BlockCrystalGlass.CONNECTED_EAST);
		}
		TextureAtlasSprite sprite = getTexture(type);
		float[] top = new float[] { 0.0f, 0.0f, 16.0f, 1.0f };
		float[] bottom = new float[] { 0.0f, 15.0f, 16.0f, 16.0f };
		float[] left = new float[] { 0.0f, 0.0f, 1.0f, 16.0f };
		float[] right = new float[] { 15.0f, 0.0f, 16.0f, 16.0f };
		
		if(state == null){
			final BlockFaceUV uv = new BlockFaceUV(new float[]{0, 0, 16, 16}, 0);
			final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", uv);
			final BlockPartFace faceD = new BlockPartFace(EnumFacing.DOWN, 0, "", uv);
			final BlockPartFace faceN = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
			final BlockPartFace faceS = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
			final BlockPartFace faceW = new BlockPartFace(EnumFacing.WEST, 0, "", uv);
			final BlockPartFace faceE = new BlockPartFace(EnumFacing.EAST, 0, "", uv);
			
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(16f, 16, 16f), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(16f, 0, 16f), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(16f, 16f, 0f), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(16f, 16f, 16f), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(0, 16f, 16f), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 0), new Vector3f(16f, 16f, 16f), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			return list;
		}
		
		if(renderUp){
			final BlockFaceUV uv = new BlockFaceUV(new float[]{0, 0, 16, 16}, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.UP, 0, "", uv);
			boolean showN = !state.getValue(BlockCrystalGlass.CONNECTED_NORTH);
			boolean showS = !state.getValue(BlockCrystalGlass.CONNECTED_SOUTH);
			boolean showE = !state.getValue(BlockCrystalGlass.CONNECTED_WEST);
			boolean showW = !state.getValue(BlockCrystalGlass.CONNECTED_EAST);
			
			if(showN){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(16f, 16, 1), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
			} else{
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(1f, 16, 1), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 16, 0), new Vector3f(16f, 16, 1), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
			}
			if(showS){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 15f), new Vector3f(16f, 16, 16f), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 15), new Vector3f(1f, 16, 16), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 16, 15), new Vector3f(16f, 16, 16), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
			}
			if(showE){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(1f, 16, 16f), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
			}
			if(showW){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 16, 0), new Vector3f(16f, 16, 16f), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
			}
		}
		if(renderD){
			final BlockFaceUV uv = new BlockFaceUV(top, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.DOWN, 0, "", uv);
			
			boolean showN = !state.getValue(BlockCrystalGlass.CONNECTED_NORTH);
			boolean showS = !state.getValue(BlockCrystalGlass.CONNECTED_SOUTH);
			boolean showE = !state.getValue(BlockCrystalGlass.CONNECTED_WEST);
			boolean showW = !state.getValue(BlockCrystalGlass.CONNECTED_EAST);
			
			if(showN){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(16f, 0, 1), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}else{
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 0, 1), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 0, 1), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}
			if(showS){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15f), new Vector3f(16f, 0, 16f), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15), new Vector3f(1f, 0, 16), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 15), new Vector3f(16f, 0, 16), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}
			if(showE){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 0, 16f), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}
			if(showW){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 0, 16f), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}
		}
		if(renderN){
			final BlockFaceUV uv = new BlockFaceUV(top, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
			
			boolean showU = !state.getValue(BlockCrystalGlass.CONNECTED_UP);
			boolean showD = !state.getValue(BlockCrystalGlass.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockCrystalGlass.CONNECTED_EAST);
			boolean showR = !state.getValue(BlockCrystalGlass.CONNECTED_WEST);
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 0), new Vector3f(16f, 16F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			} else{
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.UP).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.UP).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 0), new Vector3f(1f, 16, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 15, 0), new Vector3f(16f, 16, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(16f, 1F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 1, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 1, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 16F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 16F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}
		}
		if(renderS){
			final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0F, 16.0f, 16.0f }, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
			
			boolean showU = !state.getValue(BlockCrystalGlass.CONNECTED_UP);
			boolean showD = !state.getValue(BlockCrystalGlass.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockCrystalGlass.CONNECTED_WEST);
			boolean showR = !state.getValue(BlockCrystalGlass.CONNECTED_EAST);
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 16f), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.UP).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.UP).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 16f), new Vector3f(1f, 16, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 15, 16f), new Vector3f(16f, 16, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(16f, 1F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.EAST);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(1f, 1, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 16f), new Vector3f(16f, 1, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(1f, 16F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 16f), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}
		}
		if(renderW){
			final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0F, 16.0f, 16.0f }, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.WEST, 0, "", uv);
			
			boolean showU = !state.getValue(BlockCrystalGlass.CONNECTED_UP);
			boolean showD = !state.getValue(BlockCrystalGlass.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockCrystalGlass.CONNECTED_NORTH);
			boolean showR = !state.getValue(BlockCrystalGlass.CONNECTED_SOUTH);
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 0), new Vector3f(0, 16F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = top;
				BlockPos posUN = state.pos.offset(EnumFacing.UP).offset(EnumFacing.NORTH);
				BlockPos posUS = state.pos.offset(EnumFacing.UP).offset(EnumFacing.SOUTH);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, 15, 0), new Vector3f(0, 16, 1), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 15f), new Vector3f(0, 16F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			}
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(0, 1F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = bottom;
				BlockPos posDN = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.NORTH);
				BlockPos posDS = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(0, 1, 1), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15), new Vector3f(0, 1, 16), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			}
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0f), new Vector3f(0, 16F, 1f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			}
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15f), new Vector3f(0, 16F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			}
		}
		if(renderE){
			final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0F, 16.0f, 16.0f }, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.EAST, 0, "", uv);
			
			boolean showU = !state.getValue(BlockCrystalGlass.CONNECTED_UP);
			boolean showD = !state.getValue(BlockCrystalGlass.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockCrystalGlass.CONNECTED_SOUTH);
			boolean showR = !state.getValue(BlockCrystalGlass.CONNECTED_NORTH);
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 15, 0), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = top;
				BlockPos posUN = state.pos.offset(EnumFacing.UP).offset(EnumFacing.NORTH);
				BlockPos posUS = state.pos.offset(EnumFacing.UP).offset(EnumFacing.SOUTH);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 15, 0), new Vector3f(16, 16, 1), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posUS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 15, 15f), new Vector3f(16, 16F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 0), new Vector3f(16f, 1F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			} else {
				face.blockFaceUV.uvs = bottom;
				BlockPos posDN = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.NORTH);
				BlockPos posDS = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH);
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 0, 0), new Vector3f(16, 1, 1), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				if(!BlockCrystalGlass.canConnect(state, state.blockAccess.getBlockState(posDS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 0, 15), new Vector3f(16, 1, 16), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 15f), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 0f), new Vector3f(16f, 16F, 1f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}
		}
		
		return list;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
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
		return getClear();
	}

	public TextureAtlasSprite getClear(){
		return RenderUtil.getSprite("crystalmod:blocks/blank");
	}
	
	public TextureAtlasSprite getTexture(GlassType type){
		return RenderUtil.getSprite("crystalmod:blocks/crystal_"+type.getName()+"_glass");
	}
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return (state !=null && state instanceof GlassBlockState) ? new ModelGlass((GlassBlockState)state) : null;
	}
	
	public Map<Integer, ModelGlass> itemModels = Maps.newHashMap();
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		if(!itemModels.containsKey(stack.getMetadata())){
			itemModels.put(stack.getMetadata(),	new ModelGlass(stack));
		}
		return itemModels.get(stack.getMetadata());
	}
}
