package alec_wam.CrystalMod.client.model.dynamic;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.connected.BlockConnectedTexture;
import alec_wam.CrystalMod.blocks.connected.ConnectedBlockState;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ModelConnectedTexture extends DynamicItemAndBlockModel {

	private final ConnectedBlockState state;
	private final ItemStack stack;
	public ModelConnectedTexture(){
		super(true, false);
		state = null;
		stack = null;
	}
	public ModelConnectedTexture(ItemStack stack){
		super(false, true);
		state = null;
		this.stack = stack;
	}
	public ModelConnectedTexture(ConnectedBlockState state){
		super(false, false);
		this.state = state;
		this.stack = ItemStackTools.getEmptyStack();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return super.getQuads(state, side, rand);
	}
	
	public boolean renderCenter(){
		return false;
	}
	
	@Override
	public List<BakedQuad> getGeneralQuads() {
		List<BakedQuad> list = Lists.newArrayList();
		boolean renderUp = true, renderD = true, renderN = true, renderS = true, renderW = true, renderE = true;
		ModelRotation rot = ModelRotation.X0_Y0;
		if(state !=null){
			renderUp = !state.getValue(BlockConnectedTexture.CONNECTED_UP);
			renderD = !state.getValue(BlockConnectedTexture.CONNECTED_DOWN);
			renderN = !state.getValue(BlockConnectedTexture.CONNECTED_NORTH);
			renderS = !state.getValue(BlockConnectedTexture.CONNECTED_SOUTH);
			renderW = !state.getValue(BlockConnectedTexture.CONNECTED_WEST);
			renderE = !state.getValue(BlockConnectedTexture.CONNECTED_EAST);
		}
		TextureAtlasSprite sprite = state !=null ? getTexture(state.state) : ItemStackTools.isValid(stack) ? getTexture(stack) : getClear();
		TextureAtlasSprite centersprite = state !=null ? getCenterTexture(state.state) : ItemStackTools.isValid(stack) ? getCenterTexture(stack) : getClear();
		float[] top = new float[] { 0.0f, 0.0f, 16.0f, 1.0f };
		float[] bottom = new float[] { 0.0f, 15.0f, 16.0f, 16.0f };
		float[] left = new float[] { 0.0f, 0.0f, 1.0f, 16.0f };
		float[] right = new float[] { 15.0f, 0.0f, 16.0f, 16.0f };
		float min = 1.0F;
		float max = 15.0F;
		if(state == null){
			final BlockFaceUV uv = new BlockFaceUV(new float[]{0, 0, 16F, 16F}, 0);
			new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
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
			
			/*if(renderCenter()){
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, true));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}*/
			return list;
		}
		boolean renderCenterUVLock = true;
		boolean uvLocked = true;
		boolean special = true;
		boolean special2 = true;
		if(renderUp){
			final BlockFaceUV uv = new BlockFaceUV(top, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
			boolean showN = !state.getValue(BlockConnectedTexture.CONNECTED_NORTH);
			boolean showS = !state.getValue(BlockConnectedTexture.CONNECTED_SOUTH);
			boolean showE = !state.getValue(BlockConnectedTexture.CONNECTED_EAST);
			boolean showW = !state.getValue(BlockConnectedTexture.CONNECTED_WEST);
			
			if(renderCenter()){
				float centerMinX = 1.0f;
				float centerMaxX = 15.0f;
				float centerMinY = 1.0f;
				float centerMaxY = 15.0f;
				
				float texMinX = min;
				float texMaxX = max;
				float texMinY = min;
				float texMaxY = max;
				
				if(special){
					if(!showN){
						centerMinY = 0.0f;
					}
					if(!showS){
						centerMaxY = 16.0f;
					}
					if(!showW){
						centerMinX = 0.0f;
					}
					if(!showE){
						centerMaxX = 16.0f;
					}
				}
				
				final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", new BlockFaceUV(new float[]{texMinX, texMinY, texMaxX, texMaxY}, 0));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(centerMinX, 16F, centerMinY), new Vector3f(centerMaxX, 16F, centerMaxY), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
			}
			
			if(showN){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(16f, 16, 1), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			} else{
				
				if(renderCenter() && !special){
					final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", new BlockFaceUV(new float[]{min, min, max, max}, 0));
					faceU.blockFaceUV.uvs = new float[]{1, 1, 15, 2};
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 16, 0), new Vector3f(15f, 16, 1), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
				
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUW))){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(1f, 16, 1), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				} 
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUE))){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 16, 0), new Vector3f(16f, 16, 1), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				}
			}
			
			if(showS){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 15f), new Vector3f(16f, 16, 16f), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			} else {
				if(renderCenter() && !special){
					final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", new BlockFaceUV(special ? new float[]{1, 15, 15, 16} : new float[] {min, min, max, max}, 0));
					faceU.blockFaceUV.uvs = new float[]{1, 14, 15, 15};
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 16, 15f), new Vector3f(15f, 16, 16F), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 15), new Vector3f(1f, 16, 16), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 16, 15), new Vector3f(16f, 16, 16), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			}
			
			if(showW){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(1f, 16, 16f), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			} else {
				if(renderCenter() && !special){
					final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", new BlockFaceUV(special ? new float[]{0, 1, 1, 15} : new float[] {min, min, max, max}, 0));
					faceU.blockFaceUV.uvs = new float[]{1, 1, 2, 15};
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 1), new Vector3f(1f, 16, 15f), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
			}
			
			if(showE){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 16, 0), new Vector3f(16f, 16, 16f), face, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			}else {
				if(renderCenter() && !special){
					final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", new BlockFaceUV(special ? new float[]{15, 1, 16, 15} : new float[] {min, min, max, max}, 0));
					faceU.blockFaceUV.uvs = new float[]{14, 1, 15, 15};
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15F, 16, 1), new Vector3f(16f, 16, 15f), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
			}
			
			if(renderCenter() && !special){
				final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", new BlockFaceUV(new float[]{min, min, max, max}, 0));
				if(!showN && !showW){
					faceU.blockFaceUV.uvs = new float[]{0, 0, 1, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{1, 1, 2, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 0), new Vector3f(1f, 16, 1), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
				if(!showN && !showE){
					faceU.blockFaceUV.uvs = new float[]{15, 0, 16, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{14, 1, 15, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 16, 0), new Vector3f(16f, 16, 1), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
				if(!showS && !showW){
					faceU.blockFaceUV.uvs = new float[]{0, 15, 1, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{1, 14, 2, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 16, 15), new Vector3f(1f, 16, 16), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
				if(!showS && !showE){
					faceU.blockFaceUV.uvs = new float[]{15, 15, 16, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{14, 14, 15, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 16, 15), new Vector3f(16f, 16, 16), faceU, centersprite, EnumFacing.UP, rot, (BlockPartRotation)null, renderCenterUVLock));
				}
			}
		}
		
		if(renderD){
			final BlockFaceUV uv = new BlockFaceUV(top, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.DOWN, 0, "", uv);
			
			boolean showN = !state.getValue(BlockConnectedTexture.CONNECTED_NORTH);
			boolean showS = !state.getValue(BlockConnectedTexture.CONNECTED_SOUTH);
			boolean showE = !state.getValue(BlockConnectedTexture.CONNECTED_EAST);
			boolean showW = !state.getValue(BlockConnectedTexture.CONNECTED_WEST);
			
			final BlockPartFace faceU = new BlockPartFace(EnumFacing.DOWN, 0, "", new BlockFaceUV(new float[]{min, min, max, max}, 0));
			if(renderCenter()){
				float centerMinX = 1.0f;
				float centerMaxX = 15.0f;
				float centerMinY = 1.0f;
				float centerMaxY = 15.0f;
				
				float texMinX = min;
				float texMaxX = max;
				float texMinY = min;
				float texMaxY = max;
				
				if(special){
					if(!showN){
						centerMinY = 0.0f;
					}
					if(!showS){
						centerMaxY = 16.0f;
					}
					if(!showW){
						centerMinX = 0.0f;
					}
					if(!showE){
						centerMaxX = 16.0f;
					}
				}				
				
				final BlockPartFace face2 = new BlockPartFace(EnumFacing.DOWN, 0, "", new BlockFaceUV(new float[]{texMinX, texMinY, texMaxX, texMaxY}, 0));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(centerMinX, 0.0F, centerMinY), new Vector3f(centerMaxX, 0.0F, centerMaxY), face2, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}
			
			if(showN){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(16f, 0, 1), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}else{
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 0, 0), new Vector3f(15f, 0, 1), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 0, 1), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 0, 1), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}
			if(showS){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15f), new Vector3f(16f, 0, 16f), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 0, 15f), new Vector3f(15f, 0, 16f), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15), new Vector3f(1f, 0, 16), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 15), new Vector3f(16f, 0, 16), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}
			
			if(showW){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 0, 16f), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			} else{
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 1), new Vector3f(1f, 0, 15f), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(showE){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 0, 16f), face, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
			}else{
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 1), new Vector3f(16f, 0, 15f), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(renderCenter() && !special){
				if(!showN && !showW){
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{1, 1, 2, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 0, 1), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
				if(!showN && !showE){
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{14, 1, 15, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 0, 0), new Vector3f(16f, 0, 1), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
				if(!showS && !showW){
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{1, 14, 2, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15), new Vector3f(1f, 0, 16), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
				if(!showS && !showE){
					if(special2){
						faceU.blockFaceUV.uvs = new float[]{14, 14, 15, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 0, 15), new Vector3f(16f, 0, 16), faceU, centersprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, true));
				}
			}
		}
		
		if(renderN){
			final BlockFaceUV uv = new BlockFaceUV(top, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
			
			boolean showU = !state.getValue(BlockConnectedTexture.CONNECTED_UP);
			boolean showD = !state.getValue(BlockConnectedTexture.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockConnectedTexture.CONNECTED_EAST);
			boolean showR = !state.getValue(BlockConnectedTexture.CONNECTED_WEST);
			
			final BlockPartFace faceU = new BlockPartFace(EnumFacing.NORTH, 0, "", new BlockFaceUV(new float[]{min, min, max, max}, 0));
			if(renderCenter()){
				float centerMinX = 1.0f;
				float centerMaxX = 15.0f;
				float centerMinY = 1.0f;
				float centerMaxY = 15.0f;
				
				float texMinX = min;
				float texMaxX = max;
				float texMinY = min;
				float texMaxY = max;
				
				if(special){
					if(!showD){
						centerMinY = 0.0f;
					}
					if(!showU){
						centerMaxY = 16.0f;
					}
					if(!showR){
						centerMinX = 0.0f;
					}
					if(!showL){
						centerMaxX = 16.0f;
					}
				}				
				
				final BlockPartFace face2 = new BlockPartFace(EnumFacing.DOWN, 0, "", new BlockFaceUV(new float[]{texMinX, texMinY, texMaxX, texMaxY}, 0));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(centerMinX, centerMinY, 0f), new Vector3f(centerMaxX, centerMaxY, 0f), face2, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 0), new Vector3f(16f, 16F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			} else{
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 15, 0), new Vector3f(15f, 16F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.UP).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.UP).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 0), new Vector3f(1f, 16, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 15, 0), new Vector3f(16f, 16, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}
			
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(16f, 1F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 0, 0), new Vector3f(15f, 1F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 1, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 1, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}
			
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 16F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 1, 0), new Vector3f(16f, 15F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(1f, 16F, 0), face, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
			}else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, 1, 0), new Vector3f(1f, 15F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(renderCenter() && !special){
				if(!showR && !showU){
					faceU.blockFaceUV.uvs = new float[] {15, 0, 16, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 1, 15, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, 15, 0), new Vector3f(1f, 16F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
				if(!showL && !showU){
					faceU.blockFaceUV.uvs = new float[] {0, 0, 1, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 1, 2, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 15, 0), new Vector3f(16f, 16F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
				if(!showR && !showD){
					faceU.blockFaceUV.uvs = new float[] {15, 15, 16, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 14, 15, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, 0, 0), new Vector3f(1f, 1F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
				if(!showL && !showD){
					faceU.blockFaceUV.uvs = new float[] {0, 15, 1, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 14, 2, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 0), new Vector3f(16f, 1F, 0), faceU, centersprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, true));
				}
			}
		}
		
		if(renderS){
			final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0F, 16.0f, 16.0f }, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
			
			boolean showU = !state.getValue(BlockConnectedTexture.CONNECTED_UP);
			boolean showD = !state.getValue(BlockConnectedTexture.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockConnectedTexture.CONNECTED_WEST);
			boolean showR = !state.getValue(BlockConnectedTexture.CONNECTED_EAST);
			
			final BlockPartFace faceU = new BlockPartFace(EnumFacing.SOUTH, 0, "", new BlockFaceUV(new float[]{min, min, max, max}, 0));
			if(renderCenter()){
				float centerMinX = 1.0f;
				float centerMaxX = 15.0f;
				float centerMinY = 1.0f;
				float centerMaxY = 15.0f;
				
				float texMinX = min;
				float texMaxX = max;
				float texMinY = min;
				float texMaxY = max;
				
				if(special){
					if(!showD){
						centerMinY = 0.0f;
					}
					if(!showU){
						centerMaxY = 16.0f;
					}
					if(!showL){
						centerMinX = 0.0f;
					}
					if(!showR){
						centerMaxX = 16.0f;
					}
				}				
				
				final BlockPartFace face2 = new BlockPartFace(EnumFacing.DOWN, 0, "", new BlockFaceUV(new float[]{texMinX, texMinY, texMaxX, texMaxY}, 0));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(centerMinX, centerMinY, 16f), new Vector3f(centerMaxX, centerMaxY, 16f), face2, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 16f), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 15, 16f), new Vector3f(15f, 16F, 16f), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = top;
				BlockPos posUW = state.pos.offset(EnumFacing.UP).offset(EnumFacing.WEST);
				BlockPos posUE = state.pos.offset(EnumFacing.UP).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 16f), new Vector3f(1f, 16, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 15, 16f), new Vector3f(16f, 16, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}
			
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(16f, 1F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(1, 0, 16f), new Vector3f(15f, 1F, 16f), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = bottom;
				BlockPos posDW = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.WEST);
				BlockPos posDE = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.EAST);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDW)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(1f, 1, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDE)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 16f), new Vector3f(16f, 1, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}
			
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(1f, 16F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 1, 16f), new Vector3f(1f, 15F, 16f), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 16f), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
			}else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 1, 16f), new Vector3f(16f, 15F, 16f), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(renderCenter() && !special){
				if(!showR && !showU){
					faceU.blockFaceUV.uvs = new float[] {15, 0, 16, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 1, 15, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 15, 16f), new Vector3f(16f, 16F, 16), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
				if(!showL && !showU){
					faceU.blockFaceUV.uvs = new float[] {0, 0, 1, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 1, 2, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, 15, 16f), new Vector3f(1f, 16F, 16), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
				if(!showR && !showD){
					faceU.blockFaceUV.uvs = new float[] {15, 15, 16, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 14, 15, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15f, 0, 16f), new Vector3f(16f, 1F, 16), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
				if(!showL && !showD){
					faceU.blockFaceUV.uvs = new float[] {0, 15, 1, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 14, 2, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, 0, 16f), new Vector3f(1f, 1F, 16), faceU, centersprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, true));
				}
			}
		}
		
		if(renderW){
			final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0F, 16.0f, 16.0f }, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.WEST, 0, "", uv);
			
			boolean showU = !state.getValue(BlockConnectedTexture.CONNECTED_UP);
			boolean showD = !state.getValue(BlockConnectedTexture.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockConnectedTexture.CONNECTED_NORTH);
			boolean showR = !state.getValue(BlockConnectedTexture.CONNECTED_SOUTH);
			
			final BlockPartFace faceU = new BlockPartFace(EnumFacing.WEST, 0, "", new BlockFaceUV(new float[]{min, min, max, max}, 0));
			if(renderCenter()){
				float centerMinX = 1.0f;
				float centerMaxX = 15.0f;
				float centerMinY = 1.0f;
				float centerMaxY = 15.0f;
				
				float texMinX = min;
				float texMaxX = max;
				float texMinY = min;
				float texMaxY = max;
				
				if(special){
					if(!showD){
						centerMinY = 0.0f;
					}
					if(!showU){
						centerMaxY = 16.0f;
					}
					if(!showL){
						centerMinX = 0.0f;
					}
					if(!showR){
						centerMaxX = 16.0f;
					}
				}				
				
				final BlockPartFace face2 = new BlockPartFace(EnumFacing.DOWN, 0, "", new BlockFaceUV(new float[]{texMinX, texMinY, texMaxX, texMaxY}, 0));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, centerMinY, centerMinX), new Vector3f(0f, centerMaxY, centerMaxX), face2, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			}
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 0), new Vector3f(0, 16F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 1), new Vector3f(0, 16F, 15f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = top;
				BlockPos posUN = state.pos.offset(EnumFacing.UP).offset(EnumFacing.NORTH);
				BlockPos posUS = state.pos.offset(EnumFacing.UP).offset(EnumFacing.SOUTH);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0f, 15, 0), new Vector3f(0, 16, 1), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 15f), new Vector3f(0, 16F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			}
			
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(0, 1F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 1), new Vector3f(0, 1F, 15f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = bottom;
				BlockPos posDN = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.NORTH);
				BlockPos posDS = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(0, 1, 1), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15), new Vector3f(0, 1, 16), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			}
			
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0f), new Vector3f(0, 16F, 1f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 1, 0), new Vector3f(0, 15F, 1f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15f), new Vector3f(0, 16F, 16f), face, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 1, 15F), new Vector3f(0, 15F, 16f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(renderCenter() && !special){
				if(!showL && !showU){
					faceU.blockFaceUV.uvs = new float[] {0, 0, 1, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 1, 2, 2};
					}					
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 0), new Vector3f(0, 16F, 1f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
				if(!showR && !showU){
					faceU.blockFaceUV.uvs = new float[] {15, 0, 16, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 1, 15, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, 15), new Vector3f(0, 16F, 16f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
				if(!showL && !showD){
					faceU.blockFaceUV.uvs = new float[] {0, 15, 1, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 14, 2, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(0, 1F, 1f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
				if(!showR && !showD){
					faceU.blockFaceUV.uvs = new float[] {15, 15, 16, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 14, 15, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 15), new Vector3f(0, 1F, 16f), faceU, centersprite, EnumFacing.WEST, rot, (BlockPartRotation)null, true));
				}
			}
		}
		
		if(renderE){
			final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0F, 16.0f, 16.0f }, 0);
			final BlockPartFace face = new BlockPartFace(EnumFacing.EAST, 0, "", uv);
			
			boolean showU = !state.getValue(BlockConnectedTexture.CONNECTED_UP);
			boolean showD = !state.getValue(BlockConnectedTexture.CONNECTED_DOWN);
			boolean showL = !state.getValue(BlockConnectedTexture.CONNECTED_SOUTH);
			boolean showR = !state.getValue(BlockConnectedTexture.CONNECTED_NORTH);
			
			final BlockPartFace faceU = new BlockPartFace(EnumFacing.EAST, 0, "", new BlockFaceUV(new float[]{min, min, max, max}, 0));
			if(renderCenter()){
				float centerMinX = 1.0f;
				float centerMaxX = 15.0f;
				float centerMinY = 1.0f;
				float centerMaxY = 15.0f;
				
				float texMinX = min;
				float texMaxX = max;
				float texMinY = min;
				float texMaxY = max;
				
				if(special){
					if(!showD){
						centerMinY = 0.0f;
					}
					if(!showU){
						centerMaxY = 16.0f;
					}
					if(!showR){
						centerMinX = 0.0f;
					}
					if(!showL){
						centerMaxX = 16.0f;
					}
				}				
				
				final BlockPartFace face2 = new BlockPartFace(EnumFacing.DOWN, 0, "", new BlockFaceUV(new float[]{texMinX, texMinY, texMaxX, texMaxY}, 0));
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, centerMinY, centerMinX), new Vector3f(16f, centerMaxY, centerMaxX), face2, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}
			
			if(showU){
				face.blockFaceUV.uvs = top;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 15, 0), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 15, 1), new Vector3f(16f, 16F, 15f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = top;
				BlockPos posUN = state.pos.offset(EnumFacing.UP).offset(EnumFacing.NORTH);
				BlockPos posUS = state.pos.offset(EnumFacing.UP).offset(EnumFacing.SOUTH);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 15, 0), new Vector3f(16, 16, 1), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posUS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 15, 15f), new Vector3f(16, 16F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}
			
			if(showD){
				face.blockFaceUV.uvs = bottom;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 0), new Vector3f(16f, 1F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 1), new Vector3f(16f, 1F, 15f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
				face.blockFaceUV.uvs = bottom;
				BlockPos posDN = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.NORTH);
				BlockPos posDS = state.pos.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH);
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDN)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 0, 0), new Vector3f(16, 1, 1), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				if(!((BlockConnectedTexture)state.getBlock()).canConnect(state, state.blockAccess.getBlockState(posDS)))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16, 0, 15), new Vector3f(16, 1, 16), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			}
			
			if(showL){
				face.blockFaceUV.uvs = left;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 15f), new Vector3f(16f, 16F, 16f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 1, 15f), new Vector3f(16f, 15F, 16f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(showR){
				face.blockFaceUV.uvs = right;
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 0f), new Vector3f(16f, 16F, 1f), face, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
			} else {
				if(renderCenter() && !special){
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 1, 0f), new Vector3f(16f, 15F, 1f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
			}
			
			if(renderCenter() && !special){
				if(!showL && !showU){
					faceU.blockFaceUV.uvs = new float[] {0, 0, 1, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 1, 2, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 15, 15), new Vector3f(16f, 16F, 16f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
				if(!showR && !showU){
					faceU.blockFaceUV.uvs = new float[] {15, 0, 16, 1};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 1, 15, 2};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 15, 0), new Vector3f(16f, 16F, 1f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
				if(!showL && !showD){
					faceU.blockFaceUV.uvs = new float[] {0, 15, 1, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {1, 14, 2, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 15), new Vector3f(16f, 1F, 16f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
				if(!showR && !showD){
					faceU.blockFaceUV.uvs = new float[] {15, 15, 16, 16};
					if(special2){
						faceU.blockFaceUV.uvs = new float[] {14, 14, 15, 15};
					}
					list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16f, 0, 0), new Vector3f(16f, 1F, 1f), faceU, centersprite, EnumFacing.EAST, rot, (BlockPartRotation)null, true));
				}
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
	
	public abstract TextureAtlasSprite getTexture(IBlockState state);

	public abstract TextureAtlasSprite getCenterTexture(IBlockState state);
	
	public abstract TextureAtlasSprite getTexture(ItemStack stack);

	public abstract TextureAtlasSprite getCenterTexture(ItemStack stack);
	
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return (state !=null && state instanceof ConnectedBlockState) ? createNewModel((ConnectedBlockState)state) : null;
	}
	
	public Map<Integer, ModelConnectedTexture> itemModels = Maps.newHashMap();
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		if(!itemModels.containsKey(stack.getMetadata())){
			itemModels.put(stack.getMetadata(),	createNewModel(stack));
		}
		return itemModels.get(stack.getMetadata());
	}
	
	public abstract ModelConnectedTexture createNewModel(ItemStack stack);
	public abstract ModelConnectedTexture createNewModel(ConnectedBlockState state);
	public abstract ModelConnectedTexture createNewModel();
}
