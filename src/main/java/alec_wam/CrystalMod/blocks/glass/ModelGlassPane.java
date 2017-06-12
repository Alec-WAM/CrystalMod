package alec_wam.CrystalMod.blocks.glass;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.BlockPane;
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

public class ModelGlassPane extends DynamicItemAndBlockModel {

	public static final ModelGlassPane INSTANCE = new ModelGlassPane();
	private final GlassBlockState state;
	private final ItemStack stack;
	public ModelGlassPane(){
		super(true, false);
		state = null;
		stack = null;
	}
	public ModelGlassPane(ItemStack stack){
		super(false, true);
		state = null;
		this.stack = stack;
	}
	public ModelGlassPane(GlassBlockState state){
		super(false, false);
		this.state = state;
		this.stack = ItemStackTools.getEmptyStack();
	}

	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return super.getQuads(state, side, rand);
	}
	
	@Override
	public List<BakedQuad> getGeneralQuads() {
		List<BakedQuad> list = Lists.newArrayList();
		GlassType type = GlassType.BLUE;
		if(ItemStackTools.isValid(stack)){
			type = GlassType.values()[stack.getMetadata() % (GlassType.values().length)];
		}else if(state !=null){
			type = state.getValue(BlockCrystalGlass.TYPE);
		}
		TextureAtlasSprite sprite = getTexture(type);
		float[] top = new float[] { 0.0f, 0.0f, 16.0f, 1.0f };
		float[] bottom = new float[] { 0.0f, 15.0f, 16.0f, 16.0f };
		float[] left = new float[] { 0.0f, 0.0f, 1.0f, 16.0f };
		float[] right = new float[] { 15.0f, 0.0f, 16.0f, 16.0f };
		if(state == null){
			final BlockFaceUV uv = new BlockFaceUV(top, 0);
			final BlockPartFace faceS = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
			
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 16f), new Vector3f(16f, 16f, 16f), faceS, sprite, EnumFacing.NORTH, ModelRotation.X0_Y90, (BlockPartRotation)null, true));
			return list;
		} else {
			BlockPos posU = state.pos.up();
			BlockPos posD = state.pos.down();
			boolean renderUp = true;
			boolean renderDown = true;
			boolean renderNorth = true, renderSouth = true, renderEast = true, renderWest = true;
			boolean connectedN = state.getValue(BlockCrystalGlassPane.NORTH);
			boolean connectedS = state.getValue(BlockCrystalGlassPane.SOUTH);
			boolean connectedE = state.getValue(BlockCrystalGlassPane.EAST);
			boolean connectedW = state.getValue(BlockCrystalGlassPane.WEST);

			if(renderUp){
				final BlockFaceUV uv = new BlockFaceUV(top, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.UP, 0, "", uv);
				face.blockFaceUV.uvs = new float[]{0f, 0f, 1f, 1f};
				if(!BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 16, 7), new Vector3f(9, 16, 9), face, sprite, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
				
				face.blockFaceUV.uvs = new float[]{1f, 0f, 8f, 1f};
				
				IBlockState stateUp = state.blockAccess.getBlockState(posU);
				boolean valid = stateUp.getBlock() == state.getBlock();
				if(valid){
					stateUp = stateUp.getActualState(state.blockAccess, posU);
				}
				boolean matchN = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, EnumFacing.NORTH);
				boolean matchS = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, EnumFacing.SOUTH);
				boolean matchE = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, EnumFacing.EAST);
				boolean matchW = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, EnumFacing.WEST);

				boolean offsetE = valid && stateUp.getValue(BlockPane.EAST);
				boolean offsetW = valid && stateUp.getValue(BlockPane.WEST);
				boolean offsetN = valid && stateUp.getValue(BlockPane.NORTH);
				boolean offsetS = valid && stateUp.getValue(BlockPane.SOUTH);
				
				if(!matchN && !offsetN && connectedN)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 16, 0), new Vector3f(9, 16, 7), face, sprite, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
				if(!matchS && !offsetS && connectedS)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 16, 0), new Vector3f(9, 16, 7), face, sprite, EnumFacing.UP, ModelRotation.X0_Y180, (BlockPartRotation)null, true));
				if(!matchE && !offsetE && connectedE)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 16, 0), new Vector3f(9, 16, 7), face, sprite, EnumFacing.UP, ModelRotation.X0_Y90, (BlockPartRotation)null, true));
				if(!matchW && !offsetW && connectedW)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 16, 0), new Vector3f(9, 16, 7), face, sprite, EnumFacing.UP, ModelRotation.X0_Y270, (BlockPartRotation)null, true));
			}
			if(renderDown){
				final BlockFaceUV uv = new BlockFaceUV(top, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.UP, 0, "", uv);
				face.blockFaceUV.uvs = new float[]{0f, 0f, 1f, 1f};
				if(!BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN))list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 7), new Vector3f(9, 0, 9), face, sprite, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
				
				IBlockState stateDown = state.blockAccess.getBlockState(posD);
				boolean valid = stateDown.getBlock() == state.getBlock();
				if(valid){
					stateDown = stateDown.getActualState(state.blockAccess, posD);
				}
				boolean matchN = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, EnumFacing.NORTH);
				boolean matchS = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, EnumFacing.SOUTH);
				boolean matchE = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, EnumFacing.EAST);
				boolean matchW = valid && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, EnumFacing.WEST);
				
				boolean offsetE = valid && stateDown.getValue(BlockPane.EAST);
				boolean offsetW = valid && stateDown.getValue(BlockPane.WEST);
				boolean offsetN = valid && stateDown.getValue(BlockPane.NORTH);
				boolean offsetS = valid && stateDown.getValue(BlockPane.SOUTH);
				
				face.blockFaceUV.uvs = new float[]{1f, 0f, 8f, 1f};
				if(!matchN && !offsetN && connectedN)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 0), new Vector3f(9, 0, 7), face, sprite, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
				if(!matchS && !offsetS && connectedS)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 0), new Vector3f(9, 0, 7), face, sprite, EnumFacing.DOWN, ModelRotation.X0_Y180, (BlockPartRotation)null, true));
				if(!matchE && !offsetE && connectedE)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 0), new Vector3f(9, 0, 7), face, sprite, EnumFacing.DOWN, ModelRotation.X0_Y90, (BlockPartRotation)null, true));
				if(!matchW && !offsetW && connectedW)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 0), new Vector3f(9, 0, 7), face, sprite, EnumFacing.DOWN, ModelRotation.X0_Y270, (BlockPartRotation)null, true));
			}
			if(renderNorth){
				final BlockFaceUV uv = new BlockFaceUV(left, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
				if(!connectedN && !connectedE && !connectedW)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 7), new Vector3f(9, 16, 9), face, sprite, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
			}
			boolean renderConnected = true;
			if(renderConnected){
				addNSQuads(list, state, EnumFacing.NORTH, type);
				addNSQuads(list, state, EnumFacing.SOUTH, type);
				addEWQuads(list, state, EnumFacing.EAST, type);
				addEWQuads(list, state, EnumFacing.WEST, type);
			}
			if(renderSouth){
				final BlockFaceUV uv = new BlockFaceUV(left, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
				if(!connectedS && !connectedE && !connectedW)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 7), new Vector3f(9, 16, 9), face, sprite, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
				
			}
			if(renderEast){
				final BlockFaceUV uv = new BlockFaceUV(left, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.EAST, 0, "", uv);
				if(!connectedE && !connectedN && !connectedS)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 7), new Vector3f(9, 16, 9), face, sprite, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
			}
			if(renderWest){
				final BlockFaceUV uv = new BlockFaceUV(left, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.WEST, 0, "", uv);
				if(!connectedW && !connectedN && !connectedS)list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, 7), new Vector3f(9, 16, 9), face, sprite, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, true));
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
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		if(state !=null){
			GlassType type = state.getValue(BlockCrystalGlass.TYPE);
			if(type !=null)return getTexture(type);
		}
		return getClear();
	}

	public TextureAtlasSprite getClear(){
		return RenderUtil.getSprite("crystalmod:blocks/blank");
	}
	
	public static TextureAtlasSprite getTexture(GlassType type){
		return RenderUtil.getSprite("crystalmod:blocks/crystal_"+type.getName()+"_glass");
	}
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return (state !=null && state instanceof GlassBlockState) ? new ModelGlassPane((GlassBlockState)state) : null;
	}
	
	public Map<Integer, ModelGlassPane> itemModels = Maps.newHashMap();
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		if(!itemModels.containsKey(stack.getMetadata())){
			itemModels.put(stack.getMetadata(),	new ModelGlassPane(stack));
		}
		return itemModels.get(stack.getMetadata());
	}
	
	public static void addNSQuads(List<BakedQuad> list, GlassBlockState state, EnumFacing glassFace, GlassType type){
		BlockPos posU = state.pos.up();
		BlockPos posD = state.pos.down();
		IBlockState stateUp = state.blockAccess.getBlockState(posU);
		boolean validU = stateUp.getBlock() == state.getBlock();
		if(validU){
			stateUp = stateUp.getActualState(state.blockAccess, posU);
		}
		IBlockState stateDown = state.blockAccess.getBlockState(posD);
		boolean validD = stateDown.getBlock() == state.getBlock();
		if(validD){
			stateDown = stateDown.getActualState(state.blockAccess, posD);
		}
		
		ModelRotation rot = ModelRotation.X0_Y0;
		EnumFacing leftFacing = EnumFacing.EAST; EnumFacing rightFacing = EnumFacing.WEST;
		
		boolean upConnectedLeft = validU && stateUp.getValue(BlockPane.EAST);
		boolean upConnectedRight = validU && stateUp.getValue(BlockPane.WEST);
		boolean downConnectedLeft = validD && stateDown.getValue(BlockPane.EAST);
		boolean downConnectedRight = validD && stateDown.getValue(BlockPane.WEST);
		
		boolean connectedE = state.getValue(BlockCrystalGlassPane.EAST);
		boolean connectedW = state.getValue(BlockCrystalGlassPane.WEST);
		EnumFacing renderFace = glassFace;
		
		boolean mT = false;
		boolean mT2 = false;
		boolean rT = false;
		boolean rB = false;
		boolean lT = false;
		boolean lB = false;

		boolean tLineL = false;
		boolean tLineR = false;
		boolean mB = false;
		boolean mB2 = false;
		boolean bL = false;
		boolean bLineL = false;
		boolean bLineR = false;
		boolean lineL = false;
		boolean lineR = false;
		boolean lineM = false;
		boolean lineM2 = false;

		//Corners
		boolean bRC = false, bLC = false, tRC = false, tLC = false;

		lineR = connectedW && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
		lineL = connectedE && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);

		if(!BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP)) {
			mT2 = !connectedE && connectedW;
			//mT = !(connectedE && connectedW) && !mT2;
			tLineL = connectedE;
			tLineR = connectedW;
		}

		if(!BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN)) {
			mB2 = !connectedE && connectedW;
			mB = !(connectedE && connectedW) && !mB2;
			bLineL = connectedE;
			bLineR = connectedW;
		}

		//tLineR = connectedW && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing) && validU ?  : true;
		if(lineR){
			mT = mB = false;
			//Connected but not glass
			rT = connectedW && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
			rB = connectedW && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
		}

		if(connectedW && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing)){
			if(!(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP) && upConnectedRight)){
				tLineR = true;
			} 
			if(!(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN) && downConnectedRight)){
				bLineR = true;
			}
		}

		if(connectedW && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing)){
			if(!(validU && upConnectedRight)){
				tLineR = true;
			} 
			if(!(validD && downConnectedRight)){
				bLineR = true;
			}
		}

		if(lineL){
			mT = mB = false;
			//Connected but not glass
			lT = connectedE && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);
			lB = connectedE && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);
		}

		if(connectedE && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing)){
			if(!(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP) && upConnectedLeft)){
				tLineL = true;
			}
			if(!(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN) && downConnectedLeft)){
				bLineL = true;
			}
		}

		if(connectedE && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing)){
			if(!(validU && upConnectedLeft)){
				tLineL = true;
			}
			if(!(validD && downConnectedLeft)){
				bLineL = true;
			}
		}

		if(connectedE && !connectedW){
			mT = lineM = mB = !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
		}
		if(connectedW && !connectedE){
			mT2 = lineM2 = mB2 = !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);
		}

		if(connectedW){
			if(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP)){
				tRC = upConnectedRight && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, rightFacing);
			}
			if(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN)){
				bRC = downConnectedRight && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, rightFacing);
			}
		}
		if(connectedE){
			if(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP)){
				tLC = upConnectedLeft && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, leftFacing);
			}
			if(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN)){
				bLC = downConnectedLeft && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, leftFacing);
			}
		}
		
		if(mB){
			if(state.getValue(BlockPane.NORTH) && glassFace == EnumFacing.NORTH){
				mB = false;
			}
			if(state.getValue(BlockPane.SOUTH) && glassFace == EnumFacing.SOUTH){
				mB = false;
			}
		}
		
		int z = 7;
		if(renderFace == EnumFacing.SOUTH){
			z = 9;
		}
		TextureAtlasSprite sprite = getTexture(type);
		final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 1.0f, 16.0f }, 0);
		final BlockPartFace face = new BlockPartFace(renderFace, 0, "", uv);
		
		if(mT) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 15, z), new Vector3f(8, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(rT) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, z), new Vector3f(1, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(lT) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 15, z), new Vector3f(16, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(mT2) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(8, 15, z), new Vector3f(9, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(mB)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 0, z), new Vector3f(8, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(rB)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, z), new Vector3f(1, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(lB)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 0, z), new Vector3f(16, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(mB2)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(8, 0, z), new Vector3f(9, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(bL)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 0, z), new Vector3f(16, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(tLC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 15, z), new Vector3f(16, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(bLC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 0, z), new Vector3f(16, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(tRC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, z), new Vector3f(1, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(bRC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, z), new Vector3f(1, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));

		if(tLineL){
			face.blockFaceUV.uvs = new float[]{1f, 0f, 7f, 1f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(8, 15, z), new Vector3f(16, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(bLineL){
			face.blockFaceUV.uvs = new float[]{1f, 15f, 7f, 16f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(8, 0, z), new Vector3f(16, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(tLineR){
			face.blockFaceUV.uvs = new float[]{1f, 0f, 7f, 1f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 15, z), new Vector3f(8, 16, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(bLineR){
			face.blockFaceUV.uvs = new float[]{1f, 15f, 7f, 16f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, z), new Vector3f(8, 1, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineR){
			face.blockFaceUV.uvs = new float[]{15f, 1f, 16f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 1, z), new Vector3f(1, 15, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineL){
			face.blockFaceUV.uvs = new float[]{0f, 1f, 1f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(15, 1, z), new Vector3f(16, 15, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineM){
			face.blockFaceUV.uvs = new float[]{15f, 1f, 16f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(7, 1, z), new Vector3f(8, 15, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineM2){
			face.blockFaceUV.uvs = new float[]{15f, 1f, 16f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(8, 1, z), new Vector3f(9, 15, z), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
	}
	
	public static void addEWQuads(List<BakedQuad> list, GlassBlockState state, EnumFacing glassFace, GlassType type){
		BlockPos posU = state.pos.up();
		BlockPos posD = state.pos.down();
		IBlockState stateUp = state.blockAccess.getBlockState(posU);
		boolean validU = stateUp.getBlock() == state.getBlock();
		if(validU){
			stateUp = stateUp.getActualState(state.blockAccess, posU);
		}
		IBlockState stateDown = state.blockAccess.getBlockState(posD);
		boolean validD = stateDown.getBlock() == state.getBlock();
		if(validD){
			stateDown = stateDown.getActualState(state.blockAccess, posD);
		}
		
		ModelRotation rot = ModelRotation.X0_Y0;
		EnumFacing leftFacing = EnumFacing.SOUTH; EnumFacing rightFacing = EnumFacing.NORTH;
		
		boolean upConnectedLeft = validU && stateUp.getValue(BlockPane.SOUTH);
		boolean upConnectedRight = validU && stateUp.getValue(BlockPane.NORTH);
		boolean downConnectedLeft = validD && stateDown.getValue(BlockPane.SOUTH);
		boolean downConnectedRight = validD && stateDown.getValue(BlockPane.NORTH);
		
		boolean connectedL = state.getValue(BlockCrystalGlassPane.SOUTH);
		boolean connectedR = state.getValue(BlockCrystalGlassPane.NORTH);
		EnumFacing renderFace = glassFace;
		
		boolean mT = false;
		boolean mT2 = false;
		boolean rT = false;
		boolean rB = false;
		boolean lT = false;
		boolean lB = false;

		boolean tLineL = false;
		boolean tLineR = false;
		boolean mB = false;
		boolean mB2 = false;
		boolean bL = false;
		boolean bLineL = false;
		boolean bLineR = false;
		boolean lineL = false;
		boolean lineR = false;
		boolean lineM = false;
		boolean lineM2 = false;

		//Corners
		boolean bRC = false, bLC = false, tRC = false, tLC = false;

		lineR = connectedR && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
		lineL = connectedL && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);

		if(!BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP)) {
			mT2 = !connectedL && connectedR;
			//mT = !(connectedL && connectedR) && !mT2;
			tLineL = connectedL;
			tLineR = connectedR;
		}

		if(!BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN)) {
			mB2 = !connectedL && connectedR;
			mB = !(connectedL && connectedR) && !mB2;
			bLineL = connectedL;
			bLineR = connectedR;
		}

		//tLineR = connectedR && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing) && validU ?  : true;
		if(lineR){
			mT = mB = false;
			//Connected but not glass
			rT = connectedR && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
			rB = connectedR && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
		}

		if(connectedR && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing)){
			if(!(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP) && upConnectedRight)){
				tLineR = true;
			} 
			if(!(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN) && downConnectedRight)){
				bLineR = true;
			}
		}

		if(connectedR && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing)){
			if(!(validU && upConnectedRight)){
				tLineR = true;
			} 
			if(!(validD && downConnectedRight)){
				bLineR = true;
			}
		}

		if(lineL){
			mT = mB = false;
			//Connected but not glass
			lT = connectedL && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);
			lB = connectedL && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);
		}

		if(connectedL && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing)){
			if(!(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP) && upConnectedLeft)){
				tLineL = true;
			}
			if(!(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN) && downConnectedLeft)){
				bLineL = true;
			}
		}

		if(connectedL && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing)){
			if(!(validU && upConnectedLeft)){
				tLineL = true;
			}
			if(!(validD && downConnectedLeft)){
				bLineL = true;
			}
		}

		if(connectedL && !connectedR){
			mT = lineM = mB = !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, rightFacing);
		}
		if(connectedR && !connectedL){
			mT2 = lineM2 = mB2 = !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, leftFacing);
		}

		if(connectedR){
			if(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP)){
				tRC = upConnectedRight && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, rightFacing);
			}
			if(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN)){
				bRC = downConnectedRight && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, rightFacing);
			}
		}
		if(connectedL){
			if(validU && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.UP)){
				tLC = upConnectedLeft && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posU, leftFacing);
			}
			if(validD && BlockCrystalGlassPane.isSideConnectable(state.blockAccess, state.pos, EnumFacing.DOWN)){
				bLC = downConnectedLeft && !BlockCrystalGlassPane.isSideConnectable(state.blockAccess, posD, leftFacing);
			}
		}
		
		if(mB){
			if(state.getValue(BlockPane.EAST) && glassFace == EnumFacing.EAST){
				mB = false;
			}
			if(state.getValue(BlockPane.WEST) && glassFace == EnumFacing.WEST){
				mB = false;
			}
		}
		
		int x = 7;
		if(renderFace == EnumFacing.EAST){
			x = 9;
		}
		TextureAtlasSprite sprite = getTexture(type);
		final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 1.0f, 16.0f }, 0);
		final BlockPartFace face = new BlockPartFace(renderFace, 0, "", uv);
		
		if(mT) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 7), new Vector3f(x, 16, 8), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(rT) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 0), new Vector3f(x, 16, 1), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(lT) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 15), new Vector3f(x, 16, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(mT2) list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 8), new Vector3f(x, 16, 9), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(mB)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 7), new Vector3f(x, 1, 8), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(rB)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 0), new Vector3f(x, 1, 1), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(lB)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 15), new Vector3f(x, 1, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(mB2)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 8), new Vector3f(x, 1, 9), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(bL)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 15), new Vector3f(x, 1, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(tLC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 15), new Vector3f(x, 16, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(bLC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 15), new Vector3f(x, 1, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(tRC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 0), new Vector3f(x, 16, 1), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		if(bRC)	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 0), new Vector3f(x, 1, 1), face, sprite, renderFace, rot, (BlockPartRotation)null, true));

		if(tLineL){
			face.blockFaceUV.uvs = new float[]{1f, 0f, 7f, 1f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 8), new Vector3f(x, 16, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(bLineL){
			face.blockFaceUV.uvs = new float[]{1f, 15f, 7f, 16f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 8), new Vector3f(x, 1, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(tLineR){
			face.blockFaceUV.uvs = new float[]{1f, 0f, 7f, 1f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 15, 0), new Vector3f(x, 16, 8), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(bLineR){
			face.blockFaceUV.uvs = new float[]{1f, 15f, 7f, 16f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 0, 0), new Vector3f(x, 1, 8), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineR){
			face.blockFaceUV.uvs = new float[]{15f, 1f, 16f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 1, 0), new Vector3f(x, 15, 1), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineL){
			face.blockFaceUV.uvs = new float[]{0f, 1f, 1f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 1, 15), new Vector3f(x, 15, 16), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineM){
			face.blockFaceUV.uvs = new float[]{15f, 1f, 16f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 1, 7), new Vector3f(x, 15, 8), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
		if(lineM2){
			face.blockFaceUV.uvs = new float[]{15f, 1f, 16f, 15f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x, 1, 8), new Vector3f(x, 15, 9), face, sprite, renderFace, rot, (BlockPartRotation)null, true));
		}
	}
}
