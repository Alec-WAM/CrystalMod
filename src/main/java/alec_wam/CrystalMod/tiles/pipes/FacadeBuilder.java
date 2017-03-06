package alec_wam.CrystalMod.tiles.pipes;

import java.awt.Color;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.tiles.pipes.covers.IFaceBuilder;
import alec_wam.CrystalMod.tiles.pipes.covers.ModelQuadLayer;
import alec_wam.CrystalMod.tiles.pipes.covers.ModelQuadLayer.ModelQuadLayerBuilder;
import alec_wam.CrystalMod.tiles.pipes.covers.ModelUVReader;
import alec_wam.CrystalMod.tiles.pipes.covers.ModelVertexRange;
import alec_wam.CrystalMod.tiles.pipes.covers.UnpackedQuadBuilderWrapper;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.StringUtils;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.VertexBuffer.State;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;


/**
 * Handles creating the quads for facades attached to cable busses.
 */
class FacadeBuilder
{

	private final BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

	private final VertexFormat format;
	
	private final BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

	FacadeBuilder(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
	{
		this.format = format;
	}

	void addFacades(FakeState state, BlockRenderLayer layer, Map<EnumFacing, CoverData> facadesState, List<AxisAlignedBB> partBoxes, long rand, List<BakedQuad> quads )
	{
		boolean thinFacades = isUseThinFacades( partBoxes );

		CubeBuilder builder = new CubeBuilder( format, quads );
		for(EnumFacing side : facadesState.keySet()){
			AxisAlignedBB facadeBox = getFacadeBox( side, thinFacades );
			AxisAlignedBB cutOutBox = getCutOutBox( facadeBox, partBoxes );
			
			try
			{
				addFacade(state, layer, facadesState, side, cutOutBox, thinFacades, false, rand, builder);
			}
			catch( Throwable t )
			{
				t.printStackTrace();
				ModLogger.warning(t.getMessage());
			}
		}
	}

	private void addFacade(FakeState state, BlockRenderLayer layer, Map<EnumFacing, CoverData> facades, EnumFacing side, AxisAlignedBB busBounds, boolean thinFacades, boolean renderStilt, long rand, CubeBuilder builder )
	{

		CoverData facadeState = facades.get( side );
		if(facadeState == null)return;
		IBlockState blockState = facadeState.getBlockState();
		
		if(state !=null && state.blockAccess !=null && state.pos !=null){
			IBlockAccess world = new PipeBlockAccessWrapper(state.blockAccess, state.pos, side);
			blockState = facadeState.getBlockState().getBlock().getActualState(blockState, world, state.pos);
		}
		if(!blockState.getBlock().canRenderInLayer(blockState, layer))return;
		
		builder.setDrawFaces( EnumSet.allOf( EnumFacing.class ) );

		final float thickness = thinFacades ? 1 : 2;

		IBakedModel blockModel = blockRendererDispatcher.getModelForState( blockState );
		if(state !=null && state.blockAccess !=null && state.pos !=null){
			IBlockAccess world = new PipeBlockAccessWrapper(state.blockAccess, state.pos, side);
			blockState = blockState.getBlock().getExtendedState(blockState, world, state.pos);
		}
		
		
		EnumSet<EnumFacing> openFaces = EnumSet.allOf(EnumFacing.class);//calculateFaceOpenFaces(state, side);
		Map<Integer, Map<EnumFacing, TextureAtlasSprite>> textures = Maps.newHashMap();
		Map<Integer, Map<EnumFacing, Integer>> tints = Maps.newHashMap();
		Map<Integer, Map<EnumFacing, int[]>> customQuads = Maps.newHashMap();
		for( EnumFacing facing : openFaces )
		{
			List<BakedQuad> quads = blockModel.getQuads(blockState, facing, 0);
			int index = 0;
			for(BakedQuad quad : quads){
				Map<EnumFacing, TextureAtlasSprite> map = textures.getOrDefault(index, Maps.newHashMap());
				map.put(facing, quad.getSprite());
				textures.put(index, map);
				Map<EnumFacing, int[]> mapQuads = customQuads.getOrDefault(index, Maps.newHashMap());
				if(quad.hasTintIndex() && quad.getFace() !=null){
					Map<EnumFacing, Integer> listTint = tints.getOrDefault(index, Maps.newHashMap());
					listTint.put(quad.getFace(), quad.getTintIndex());
					tints.put(index, listTint);
				}
				mapQuads.put(facing, quad.getVertexData());
				customQuads.put(index, mapQuads);
				index++;
			}
		}
		/*for(EnumFacing face : customQuads.keySet()){
			ModLogger.info(face.getName()+" "+customQuads.get(face).toString());
		}*/
		//ModLogger.info(customQuads.toString());
		//ModLogger.info(tints.toString());
		//ModLogger.info(textures.toString());
		
		boolean test1 = false;
		boolean test2 = false;
		if(test1){
			AxisAlignedBB box = getFacadeBox( side, thinFacades );
			for(EnumFacing dir : EnumFacing.VALUES){
				for(int index : customQuads.keySet()){
					Map<EnumFacing, int[]> quadmap = customQuads.get(index);
					Map<EnumFacing, TextureAtlasSprite> textureMap = textures.get(index);
					if(quadmap.containsKey(dir)){
						int[] vecData = quadmap.get(dir);
						EnumFacing face = FaceBakery.getFacingFromVertexData(vecData);
						
						TextureAtlasSprite texture = RenderUtil.getMissingSprite();
						if(textureMap !=null){
							texture = textureMap.getOrDefault(face, RenderUtil.getMissingSprite());
						}
						
						UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder( format );
						quadBuilder.setTexture( texture );
						quadBuilder.setQuadOrientation( face );
						
						//ModLogger.info("("+index+") "+face);
						for(int i = 0; i < 4; i++){
							int storeIndex = i * 7;
							float x = Float.intBitsToFloat(vecData[storeIndex]);
							float y = Float.intBitsToFloat(vecData[storeIndex + 1]);
							float z = Float.intBitsToFloat(vecData[storeIndex + 2]);
							
							float offset = thickness/16f;
							
							if(side == EnumFacing.UP){
								if(y < box.maxY-offset && face !=side){
									y = (float)(box.maxY-offset);
								}
							}
							if(side == EnumFacing.DOWN){
								if(y > box.minY+offset && face !=side){
									y = (float)(box.minY+offset);
								}
							}
							
							if(side == EnumFacing.NORTH){
								if(z > box.minZ+offset && face != side){
									z = (float)(box.minZ+offset);
								}
								if( facades.containsKey( EnumFacing.UP ) )
								{
									if(y == box.maxY)y -= offset;
								}
				
								if( facades.containsKey( EnumFacing.DOWN ) )
								{
									if(y == box.minY)y += offset;
								}
							}
							if(side == EnumFacing.SOUTH){
								if(z < box.maxZ-offset && face != side){
									z = (float)(box.maxZ-offset);
								}
							}
							if(side == EnumFacing.EAST){
								if(x < box.maxX-offset && face != side){
									x = (float)(box.maxX-offset);
								}
								if( facades.containsKey( EnumFacing.SOUTH ) )
								{
									if(z == box.maxZ)z -= offset;
								}
				
								if( facades.containsKey( EnumFacing.NORTH ) )
								{
									if(z == box.minZ)z += offset;
								}
							}
							if(side == EnumFacing.WEST){
								if(x > box.minX+offset && face != side){
									x = (float)(box.minX+offset);
								}
								if( facades.containsKey( EnumFacing.SOUTH ) )
								{
									if(z == box.maxZ)z -= offset;
								}
				
								if( facades.containsKey( EnumFacing.NORTH ) )
								{
									if(z == box.minZ)z += offset;
								}
							}
							
							float u = Float.intBitsToFloat(vecData[storeIndex + 4]);
							float v = Float.intBitsToFloat(vecData[storeIndex + 5]);
							builder.resetColors();
							if(tints.containsKey(index)){
								Map<EnumFacing, Integer> tintMap = tints.get(index);
								for(EnumFacing face2 : tintMap.keySet()){
									IBlockAccess world = state !=null ? state.blockAccess : null;
									BlockPos pos = state !=null ? state.pos : null;
									int color = getBlockColor(blockState, world, pos, tintMap.get(face2));
									if(color !=-1)builder.setColorRGB(face2, color);
								}
							} 
							
							builder.putVertex(quadBuilder, face, x, y, z, u, v);
							//ModLogger.info("("+i+") "+x+" "+y+" "+z+" U:"+u+" V: "+v);
						}
						
						
						int[] buildData = quadBuilder.build().getVertexData();
						builder.getOutput().add( new BakedQuad( buildData, -1, face, texture, true, format ) );
					}
				}
			}
			
			
			//builder.addCube(0f, 1f, 0f, 0f, 1f, 0.0625f);
			return;
		}
		
		if(test2){
			
			final int[] to = new int[3];
			final int[] from = new int[3];
			final float[] uvs = new float[8];
			final float[] pos = new float[3];
			IFaceBuilder faceBuilder = new UnpackedQuadBuilderWrapper();
			EnumFacing[] faceArray = {EnumFacing.NORTH};
			for(EnumFacing facing : faceArray){
				
				AxisAlignedBB box = getFacadeBox( side, thinFacades );
				
				putPosition( to, (int)(32.0F), (int)(32.0F), (int)(32.0F), facing, 1 );
				putPosition( from, (int)(0), (int)(0), (int)(0), facing, -1 );
				IBlockAccess world = state !=null ? state.blockAccess : null;
				BlockPos blockpos = state !=null ? state.pos : null;
				final ModelQuadLayer[] mpc = getCachedFace( world, blockpos, Block.getStateId(facadeState.getBlockState()), 0L, facing, layer );
				
				for ( final ModelQuadLayer pc : mpc )
				{
					faceBuilder.begin( format );
					faceBuilder.setFace( facing, pc.tint );

					final float maxLightmap = 32.0f / 0xffff;
					getFaceUvs( uvs, facing, from, to, pc.uvs );

					// build it.
					for ( int vertNum = 0; vertNum < 4; vertNum++ )
					{
						for ( int elementIndex = 0; elementIndex < format.getElementCount(); elementIndex++ )
						{
							final VertexFormatElement element = format.getElement( elementIndex );
							switch ( element.getUsage() )
							{
								case POSITION:
									getVertexPos( pos, facing, vertNum, to, from );
									faceBuilder.put( elementIndex, pos[0], pos[1], pos[2] );
									break;

								case COLOR:
									final int cb = pc.color;
									faceBuilder.put( elementIndex, byteToFloat( cb >> 16 ), byteToFloat( cb >> 8 ), byteToFloat( cb ), byteToFloat( cb >> 24 ) );
									break;

								case NORMAL:
									// this fixes a bug with Forge AO?? and
									// solid blocks.. I have no idea why...
									final float normalShift = 0.999f;
									faceBuilder.put( elementIndex, normalShift * facing.getFrontOffsetX(), normalShift * facing.getFrontOffsetY(), normalShift * facing.getFrontOffsetZ() );
									break;

								case UV:
									if ( element.getIndex() == 1 )
									{
										final float v = maxLightmap * Math.max( 0, Math.min( 15, pc.light ) );
										faceBuilder.put( elementIndex, v, v );
									}
									else
									{
										//final float u = uvs[/*faceVertMap[facing.getIndex()][vertNum] * 2*/vertNum + 0];
										//final float v = uvs[/*faceVertMap[facing.getIndex()][vertNum] * 2*/vertNum + 1];
										final float u = uvs[faceVertMap[facing.getIndex()][vertNum] * 2 + 0];
										final float v = uvs[faceVertMap[facing.getIndex()][vertNum] * 2 + 1];
										faceBuilder.put( elementIndex, pc.sprite.getInterpolatedU( u ), pc.sprite.getInterpolatedV( v ) );
									}
									break;

								default:
									faceBuilder.put( elementIndex );
									break;
							}
						}
					}

					builder.getOutput().add( faceBuilder.create( pc.sprite ) );
				}
			}
			
			return;
		}
		
		for(int p : textures.keySet()){
			builder.resetColors();
			if(tints.containsKey(p)){
				Map<EnumFacing, Integer> tintMap = tints.get(p);
				for(EnumFacing face2 : tintMap.keySet()){
					IBlockAccess world = state !=null ? state.blockAccess : null;
					BlockPos pos = state !=null ? state.pos : null;
					int color = getBlockColor(blockState, world, pos, tintMap.get(face2));
					if(color !=-1)builder.setColorRGB(face2, color);
				}
			} 

			Map<EnumFacing, TextureAtlasSprite> map = textures.get(p);
			TextureAtlasSprite up = map.get(EnumFacing.UP);
			TextureAtlasSprite down = map.get(EnumFacing.DOWN);
			TextureAtlasSprite n = map.get(EnumFacing.NORTH);
			TextureAtlasSprite s = map.get(EnumFacing.SOUTH);
			TextureAtlasSprite e = map.get(EnumFacing.EAST);
			TextureAtlasSprite w = map.get(EnumFacing.WEST);
			builder.setTextures(up, down, n, s, e, w);
			
			List<EnumFacing> list = new ArrayList<EnumFacing>();
			list.add(side);
			list.add(side.getOpposite());
			list.add(EnumFacing.UP);
			builder.setDrawFaces( EnumSet.copyOf(list));
	
			AxisAlignedBB box = getFacadeBox( side, thinFacades );
			double minX = box.minX;
			double maxX = box.maxX;
			double minY = box.minY;
			double maxY = box.maxY;
			/*if(customQuads.containsKey(p)){
				Map<EnumFacing, List<Vector3f>> quadmap = customQuads.get(p);
				if(quadmap.containsKey(side)){
					List<Vector3f> vecs = quadmap.get(side);
					float mX = 1.0F;
					float MX = 0.0F;
					float mY = 1.0F;
					float MY = 0.0F;
					for(int v = 0; v < vecs.size(); v++){
						Vector3f vec = vecs.get(v);
						//ModLogger.info(v+" "+vec.field_189982_i+" "+vec.field_189983_j);
						if(vec.x < mX){
							mX = vec.x;
						} 
						if(vec.x > MX){
							MX = vec.x;
						} 
						if(vec.y < mY){
							mY = vec.y;
						} 
						if(vec.y > MY){
							MY = vec.y;
						} 
					}
					//ModLogger.info("MIN X: "+mX+" MAX X: "+MX);
					//ModLogger.info("MIN Y: "+mY+" MAX Y: "+MY);
					minX = mX;
					minY = mY;
					maxX = MX;
					maxY = MY;
				}
			}*/
			AxisAlignedBB primaryBox = null;
			if(side == EnumFacing.SOUTH || side == EnumFacing.NORTH){
				primaryBox = new AxisAlignedBB(minX, minY, box.minZ, maxX, maxY, box.maxZ);
			} else {
				primaryBox = new AxisAlignedBB(box.minX, minY, minX, box.maxX, maxY, maxX);
			}
			Vector3f min = new Vector3f(
					(float) primaryBox.minX * 16,
					(float) primaryBox.minY * 16,
					(float) primaryBox.minZ * 16
			);
			Vector3f max = new Vector3f(
					(float) primaryBox.maxX * 16,
					(float) primaryBox.maxY * 16,
					(float) primaryBox.maxZ * 16
			);
	
			if( busBounds == null )
			{
				// Adjust the facade for neighboring facades so that facade cubes dont overlap with each other
				/*if( side == EnumFacing.NORTH || side == EnumFacing.SOUTH )
				{
					if( facades.containsKey( EnumFacing.UP ) )
					{
						max.y -= thickness;
					}
	
					if( facades.containsKey( EnumFacing.DOWN ) )
					{
						min.y += thickness;
					}
				}
				else if( side == EnumFacing.EAST || side == EnumFacing.WEST )
				{
					if( facades.containsKey( EnumFacing.UP ) )
					{
						max.y -= thickness;
					}
	
					if( facades.containsKey( EnumFacing.DOWN ) )
					{
						min.y += thickness;
					}
	
					if( facades.containsKey( EnumFacing.SOUTH ) )
					{
						max.z -= thickness;
					}
	
					if( facades.containsKey( EnumFacing.NORTH ) )
					{
						min.z += thickness;
					}
				}*/
	
				builder.addCube( min.x, min.y, min.z, max.x, max.y, max.z );
			}
			else
			{
				Vector3f busMin = new Vector3f( (float) busBounds.minX * 16, (float) busBounds.minY	* 16, (float) busBounds.minZ * 16 );
				Vector3f busMax = new Vector3f( (float) busBounds.maxX * 16, (float) busBounds.maxY	* 16, (float) busBounds.maxZ * 16 );
	
				if( side == EnumFacing.UP || side == EnumFacing.DOWN )
				{
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, 0.0f, busMax.z, 16.0f, 16.0f, 16.0f );
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, 0.0f, 0.0f, 16.0f, 16.0f, busMin.z );
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, 0.0f, busMin.z, busMin.x, 16.0f, busMax.z );
					this.renderSegmentBlockCurrentBounds( builder, min, max, busMax.x, 0.0f, busMin.z, 16.0f, 16.0f, busMax.z );
				}
				else if( side == EnumFacing.NORTH || side == EnumFacing.SOUTH )
				{
					if( facades.get( EnumFacing.UP ) != null )
					{
						max.y -= thickness;
					}
	
					if( facades.get( EnumFacing.DOWN ) != null )
					{
						min.y += thickness;
					}
	
					this.renderSegmentBlockCurrentBounds( builder, min, max, busMax.x, 0.0f, 0.0f, 16.0f, 16.0f, 16.0f );
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, 0.0f, 0.0f, busMin.x, 16.0f, 16.0f );
					this.renderSegmentBlockCurrentBounds( builder, min, max, busMin.x, 0.0f, 0.0f, busMax.x, busMin.y, 16.0f );
					this.renderSegmentBlockCurrentBounds( builder, min, max, busMin.x, busMax.y, 0.0f, busMax.x, 16.0f, 16.0f );
				}
				else
				{
					if( facades.get( EnumFacing.UP ) != null )
					{
						max.y -= thickness;
					}
	
					if( facades.get( EnumFacing.DOWN ) != null )
					{
						min.y += thickness;
					}
	
					if( facades.get( EnumFacing.SOUTH ) != null )
					{
						max.z -= thickness;
					}
	
					if( facades.get( EnumFacing.NORTH ) != null )
					{
						min.z += thickness;
					}
	
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, 0.0f, busMax.z, 16.0f, 16.0f, 16.0f );
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, 0.0f, 0.0f, 16.0f, 16.0f, busMin.z );
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, 0.0f, busMin.z, 16.0f, busMin.y, busMax.z );
					this.renderSegmentBlockCurrentBounds( builder, min, max, 0.0f, busMax.y, busMin.z, 16.0f, 16.0f, busMax.z );
				}
			}
		}
	}

	public static int getBlockColor(IBlockState state, IBlockAccess world, BlockPos pos, int pass){
		try
		{
			int color2 = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, world, pos, pass);
			if(color2 !=-1){
				return color2;
			}
		}
		catch( final Throwable ignored ){}
		return -1;
	}
	
	private void renderSegmentBlockCurrentBounds( CubeBuilder builder, Vector3f min, Vector3f max,
			float minX, float minY, float minZ, float maxX, float maxY, float maxZ ) 
	{
		minX = Math.max( min.x, minX );
		minY = Math.max( min.y, minY );
		minZ = Math.max( min.z, minZ );
		maxX = Math.min( max.x, maxX );
		maxY = Math.min( max.y, maxY );
		maxZ = Math.min( max.z, maxZ );

		// don't draw it if its not at least a pixel wide...
		if( maxX - minX >= 1.0 && maxY - minY >= 1.0  && maxZ - minZ >= 1.0 )
		{
			builder.addCube( minX, minY, minZ, maxX, maxY, maxZ );
		}

	}

	/**
	 * Given the actual facade bounding box, and the bounding boxes of all parts, determine the biggest union of AABB that intersect with the
	 * facade's bounding box. This AABB will need to be "cut out" when the facade is rendered.
	 */
	@Nullable
	private static AxisAlignedBB getCutOutBox( AxisAlignedBB facadeBox, List<AxisAlignedBB> partBoxes )
	{
		AxisAlignedBB b = null;
		for( AxisAlignedBB bb : partBoxes )
		{
			if( bb.intersectsWith( facadeBox ) )
			{
				if( b == null )
				{
					b = bb;
				}
				else
				{
					double maxX = Math.max( b.maxX, bb.maxX );
					double maxY = Math.max( b.maxY, bb.maxY );
					double maxZ = Math.max( b.maxZ, bb.maxZ );
					double minX = Math.min( b.minX, bb.minX );
					double minY = Math.min( b.minY, bb.minY );
					double minZ = Math.min( b.minZ, bb.minZ );
					b = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
				}
			}
		}
		return b;
	}

	/**
	 * Determines if any of the part's bounding boxes intersects with the outside 2 voxel wide layer.
	 * If so, we should use thinner facades (1 voxel deep).
	 */
	private static boolean isUseThinFacades( List<AxisAlignedBB> partBoxes )
	{
		final double min = 2.0 / 16.0;
		final double max = 14.0 / 16.0;

		for( AxisAlignedBB bb : partBoxes )
		{
			int o = 0;
			o += bb.maxX > max ? 1 : 0;
			o += bb.maxY > max ? 1 : 0;
			o += bb.maxZ > max ? 1 : 0;
			o += bb.minX < min ? 1 : 0;
			o += bb.minY < min ? 1 : 0;
			o += bb.minZ < min ? 1 : 0;

			if( o >= 2 )
			{
				return true;
			}
		}
		return false;
	}

	public static AxisAlignedBB getFacadeBox( EnumFacing side, boolean thinFacades )
	{
		int thickness = thinFacades ? 1 : 2;

		switch( side )
		{
			case DOWN:
				return new AxisAlignedBB( 0.0, 0.0, 0.0, 1.0, ( thickness ) / 16.0, 1.0 );
			case EAST:
				return new AxisAlignedBB( ( 16.0 - thickness ) / 16.0, 0.0, 0.0, 1.0, 1.0, 1.0 );
			case NORTH:
				return new AxisAlignedBB( 0.0, 0.0, 0.0, 1.0, 1.0, ( thickness ) / 16.0 );
			case SOUTH:
				return new AxisAlignedBB( 0.0, 0.0, ( 16.0 - thickness ) / 16.0, 1.0, 1.0, 1.0 );
			case UP:
				return new AxisAlignedBB( 0.0, ( 16.0 - thickness ) / 16.0, 0.0, 1.0, 1.0, 1.0 );
			case WEST:
				return new AxisAlignedBB( 0.0, 0.0, 0.0, ( thickness ) / 16.0, 1.0, 1.0 );
			default:
				throw new IllegalArgumentException( "Unsupported face: " + side );
		}
	}
	
	private EnumSet<EnumFacing> calculateFaceOpenFaces(FakeState state,  EnumFacing side )
	{
		
		
		final EnumSet<EnumFacing> out = EnumSet.of( side, side.getOpposite() );
		if(state == null || state.pipe == null){
			final EnumSet<EnumFacing> out2 = EnumSet.allOf(EnumFacing.class);
			for( final EnumFacing it : EnumFacing.values() )
			{
				if( !out2.contains( it ) )
				{
					out2.add( it );
				}
			}
			return out;
		}
		TileEntityPipe pipe = state.pipe;
		final CoverData facade = pipe.getCoverData( side );

		IBlockAccess blockAccess = pipe.getWorld();
		BlockPos pos = pipe.getPos();
		for( final EnumFacing it : EnumFacing.values() )
		{
			if( !out.contains( it ) && this.hasAlphaDiff( blockAccess.getTileEntity( pos.offset( it ) ), side, facade ) )
			{
				out.add( it );
			}
		}

		if( out.contains( EnumFacing.UP ) && ( side.getFrontOffsetX() != 0 || side.getFrontOffsetZ() != 0 ) )
		{
			final CoverData fp = pipe.getCoverData( EnumFacing.UP );
			if( fp != null && ( fp.isTransparent() == facade.isTransparent() ))
			{
				out.remove( EnumFacing.UP );
			}
		}

		if( out.contains( EnumFacing.DOWN ) && ( side.getFrontOffsetX() != 0 || side.getFrontOffsetZ() != 0 ) )
		{
			final CoverData fp = pipe.getCoverData( EnumFacing.DOWN );
			if( fp != null && ( fp.isTransparent() == facade.isTransparent() ) )
			{
				out.remove( EnumFacing.DOWN );
			}
		}

		if( out.contains( EnumFacing.SOUTH ) && ( side.getFrontOffsetX() != 0 ) )
		{
			final CoverData fp = pipe.getCoverData( EnumFacing.SOUTH );
			if( fp != null && ( fp.isTransparent() == facade.isTransparent() ) )
			{
				out.remove( EnumFacing.SOUTH );
			}
		}

		if( out.contains( EnumFacing.NORTH ) && ( side.getFrontOffsetX() != 0 ) )
		{
			final CoverData fp = pipe.getCoverData( EnumFacing.NORTH );
			if( fp != null && ( fp.isTransparent() == facade.isTransparent() ) )
			{
				out.remove( EnumFacing.NORTH );
			}
		}

		return out;
	}

	private boolean hasAlphaDiff( final TileEntity tileEntity, final EnumFacing side, final CoverData facade )
	{
		if( tileEntity instanceof TileEntityPipe )
		{
			final TileEntityPipe ph = (TileEntityPipe) tileEntity;
			final CoverData fp = ph.getCoverData(  side  );

			return fp == null || ( fp.isTransparent() != facade.isTransparent() );
		}

		return true;
	}
	
	//Test Functions
	private void putPosition(final int[] result, final int toX, final int toY, final int toZ, final EnumFacing f, final int d)
	{

		int leftX = 0;
		final int leftY = 0;
		int leftZ = 0;

		final int upX = 0;
		int upY = 0;
		int upZ = 0;

		switch ( f )
		{
			case DOWN:
				leftX = 1;
				upZ = 1;
				break;
			case EAST:
				leftZ = 1;
				upY = 1;
				break;
			case NORTH:
				leftX = 1;
				upY = 1;
				break;
			case SOUTH:
				leftX = 1;
				upY = 1;
				break;
			case UP:
				leftX = 1;
				upZ = 1;
				break;
			case WEST:
				leftZ = 1;
				upY = 1;
				break;
			default:
				break;
		}

		result[0] = ( toX + leftX * d + upX * d ) / 2;
		result[1] = ( toY + leftY * d + upY * d ) / 2;
		result[2] = ( toZ + leftZ * d + upZ * d ) / 2;
	}
	
	private HashMap<Integer, ModelQuadLayer[]> cache = new HashMap<Integer, ModelQuadLayer[]>();
	public ModelQuadLayer[] getCachedFace(@Nullable final IBlockAccess world, @Nullable final BlockPos pos, final int stateID, final long weight,	final EnumFacing face, final BlockRenderLayer layer )
	{
		final int cacheVal = stateID << 4 | face.ordinal();

		final ModelQuadLayer[] mpc = cache.get( cacheVal );
		/*if ( mpc != null )
		{
			return mpc;
		}*/
		
		IBlockState state = Block.getStateById( stateID );
		
		if(world !=null && pos !=null){
			IBlockAccess worldWrapper = new PipeBlockAccessWrapper(world, pos, face);
			state = state.getBlock().getActualState(state, worldWrapper, pos);
			//ModLogger.info("Actual State = "+state.toString());
		}

		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState( state );
		if(world !=null && pos !=null){
			IBlockAccess worldWrapper = new PipeBlockAccessWrapper(world, pos, face);
			state = state.getBlock().getExtendedState(state, worldWrapper, pos);
			//ModLogger.info("Extended State = "+state.toString());
		}

		//IBlockState state = Block.getStateById( stateID );
		//IBakedModel model = solveModel( state, weight, Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState( state ) );

		final HashMap<EnumFacing, ArrayList<ModelQuadLayerBuilder>> tmp = new HashMap<EnumFacing, ArrayList<ModelQuadLayerBuilder>>();
		final int color = getBlockColor(state, world, pos, 0);

		for ( final EnumFacing f : EnumFacing.VALUES )
		{
			tmp.put( f, new ArrayList<ModelQuadLayer.ModelQuadLayerBuilder>() );
		}

		if ( model != null )
		{
			for ( final EnumFacing f : EnumFacing.VALUES )
			{
				final List<BakedQuad> quads = getModelQuads( model, state, f, 0 );
				processFaces( tmp, quads );
			}

			processFaces( tmp, getModelQuads( model, state, null, 0 ) );
		}

		for ( final EnumFacing f : EnumFacing.VALUES )
		{
			final int cacheV = stateID << 4 | f.ordinal();
			final ArrayList<ModelQuadLayerBuilder> x = tmp.get( f );
			final ModelQuadLayer[] mp = new ModelQuadLayer[x.size()];

			for ( int z = 0; z < x.size(); z++ )
			{
				mp[z] = x.get( z ).build( Block.getStateId(state), color, state.getBlock().getLightValue( state ), state.getBlock() == Blocks.GRASS || state.getBlock() instanceof BlockLeaves );
			}

			cache.put( cacheV, mp );
		}

		return cache.get( cacheVal );
	}
	
	private void processFaces(
			final HashMap<EnumFacing, ArrayList<ModelQuadLayerBuilder>> tmp,
			final List<BakedQuad> quads )
	{
		//ModLogger.info("Processing "+quads.size());
		for ( final BakedQuad q : quads )
		{
			final EnumFacing face = q.getFace();

			if ( face == null )
			{
				continue;
			}

			try
			{
				final TextureAtlasSprite sprite = findQuadTexture( q );
				final ArrayList<ModelQuadLayerBuilder> l = tmp.get( face );

				ModelQuadLayerBuilder b = null;
				search : for ( final ModelQuadLayerBuilder lx : l )
				{
					if ( lx.cache.sprite == sprite )
					{
						b = lx;
						break search;
					}
				}

				if ( b == null )
				{
					// top/bottom
					int uCoord = 0;
					int vCoord = 2;

					switch ( face )
					{
						case NORTH:
						case SOUTH:
							uCoord = 0;
							vCoord = 1;
							break;
						case EAST:
						case WEST:
							uCoord = 1;
							vCoord = 2;
							break;
						default:
					}

					b = new ModelQuadLayerBuilder( sprite, uCoord, vCoord );
					b.cache.tint = q.getTintIndex();
					l.add( b );
				}

				q.pipe( b.uvr );
				q.pipe( b.lv );
			}
			catch ( final Exception e )
			{

			}
		}
	}
	
	public static TextureAtlasSprite findQuadTexture(
			final BakedQuad q ) throws IllegalArgumentException, IllegalAccessException, NullPointerException
	{
		/*final TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		final Map<String, TextureAtlasSprite> mapRegisteredSprites = ReflectionWrapper.instance.getRegSprite( map );

		if ( mapRegisteredSprites == null )
		{
			throw new RuntimeException( "Unable to lookup textures." );
		}

		final ModelUVAverager av = new ModelUVAverager();
		q.pipe( av );

		final float U = av.getU();
		final float V = av.getV();

		final Iterator<?> iterator1 = mapRegisteredSprites.values().iterator();
		while ( iterator1.hasNext() )
		{
			final TextureAtlasSprite sprite = (TextureAtlasSprite) iterator1.next();
			if ( sprite.getMinU() <= U && U <= sprite.getMaxU() && sprite.getMinV() <= V && V <= sprite.getMaxV() )
			{
				return sprite;
			}
		}

		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();*/
		TextureAtlasSprite sprite = q.getSprite();
		return sprite == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : sprite;
	}

	public IBakedModel solveModel(
			final IBlockState state,
			final long weight,
			final IBakedModel originalModel )
	{
		boolean hasFaces = false;

		try
		{
			hasFaces = hasFaces( originalModel, state, null, weight );

			for ( final EnumFacing f : EnumFacing.VALUES )
			{
				hasFaces = hasFaces || hasFaces( originalModel, state, f, weight );
			}
		}
		catch ( final Exception e )
		{
			// an exception was thrown.. use the item model and hope...
			hasFaces = false;
		}

		return originalModel;
	}

	private boolean hasFaces(
			final IBakedModel model,
			final IBlockState state,
			final EnumFacing f,
			final long weight )
	{
		final List<BakedQuad> l = getModelQuads( model, state, f, weight );
		if ( l == null || l.isEmpty() )
		{
			return false;
		}

		final ModelVertexRange mvr = new ModelVertexRange();

		for ( final BakedQuad q : l )
		{
			q.pipe( mvr );
		}

		return mvr.getLargestRange() > 0;
	}
	
	private List<BakedQuad> getModelQuads(
			final IBakedModel model,
			final IBlockState state,
			final EnumFacing f,
			final long rand )
	{
		try
		{
			// try to get block model...
			return model.getQuads( state, f, rand );
		}
		catch ( final Throwable t )
		{

		}

		try
		{
			// try to get item model?
			return model.getQuads( null, f, rand );
		}
		catch ( final Throwable t )
		{

		}

		// try to not crash...
		return Collections.emptyList();
	}
	
	private float byteToFloat(
			final int i )
	{
		return ( i & 0xff ) / 255.0f;
	}
	
	private void getFaceUvs(
			final float[] uvs,
			final EnumFacing face,
			final int[] from,
			final int[] to,
			final float[] quadsUV )
	{
		float to_u = 0;
		float to_v = 0;
		float from_u = 0;
		float from_v = 0;

		switch ( face )
		{
			case UP:
				to_u = to[0] / 16.0f;
				to_v = to[2] / 16.0f;
				from_u = from[0] / 16.0f;
				from_v = from[2] / 16.0f;
				break;
			case DOWN:
				to_u = to[0] / 16.0f;
				to_v = to[2] / 16.0f;
				from_u = from[0] / 16.0f;
				from_v = from[2] / 16.0f;
				break;
			case SOUTH:
				to_u = to[0] / 16.0f;
				to_v = to[1] / 16.0f;
				from_u = from[0] / 16.0f;
				from_v = from[1] / 16.0f;
				break;
			case NORTH:
				to_u = to[0] / 16.0f;
				to_v = to[1] / 16.0f;
				from_u = from[0] / 16.0f;
				from_v = from[1] / 16.0f;
				break;
			case EAST:
				to_u = to[1] / 16.0f;
				to_v = to[2] / 16.0f;
				from_u = from[1] / 16.0f;
				from_v = from[2] / 16.0f;
				break;
			case WEST:
				to_u = to[1] / 16.0f;
				to_v = to[2] / 16.0f;
				from_u = from[1] / 16.0f;
				from_v = from[2] / 16.0f;
				break;
			default:
		}

		uvs[0] = 16.0f * u( quadsUV, to_u, to_v ); // 0
		uvs[1] = 16.0f * v( quadsUV, to_u, to_v ); // 1

		uvs[2] = 16.0f * u( quadsUV, from_u, to_v ); // 2
		uvs[3] = 16.0f * v( quadsUV, from_u, to_v ); // 3

		uvs[4] = 16.0f * u( quadsUV, from_u, from_v ); // 2
		uvs[5] = 16.0f * v( quadsUV, from_u, from_v ); // 3

		uvs[6] = 16.0f * u( quadsUV, to_u, from_v ); // 0
		uvs[7] = 16.0f * v( quadsUV, to_u, from_v ); // 1
	}

	float u(
			final float[] src,
			final float inU,
			final float inV )
	{
		final float inv = 1.0f - inU;
		final float u1 = src[0] * inU + inv * src[2];
		final float u2 = src[4] * inU + inv * src[6];
		return u1 * inV + ( 1.0f - inV ) * u2;
	}

	float v(
			final float[] src,
			final float inU,
			final float inV )
	{
		final float inv = 1.0f - inU;
		final float v1 = src[1] * inU + inv * src[3];
		final float v2 = src[5] * inU + inv * src[7];
		return v1 * inV + ( 1.0f - inV ) * v2;
	}
	
	private void getVertexPos(
			final float[] pos,
			final EnumFacing side,
			final int vertNum,
			final int[] to,
			final int[] from )
	{
		final float[] interpos = quadMapping[side.ordinal()][vertNum];

		pos[0] = to[0] * interpos[0] + from[0] * interpos[1];
		pos[1] = to[1] * interpos[2] + from[1] * interpos[3];
		pos[2] = to[2] * interpos[4] + from[2] * interpos[5];
	}
	
	private final static int[][] faceVertMap = new int[6][4];
	private final static float[][][] quadMapping = new float[6][4][6];
	
	// Analyze FaceBakery / makeBakedQuad and prepare static data for face gen.
		static
		{
			final Vector3f to = new Vector3f( 0, 0, 0 );
			final Vector3f from = new Vector3f( 16, 16, 16 );

			for ( final EnumFacing myFace : EnumFacing.VALUES )
			{
				final FaceBakery faceBakery = new FaceBakery();

				final BlockPartRotation bpr = null;
				final ModelRotation mr = ModelRotation.X0_Y0;

				final float[] defUVs = new float[] { 0, 0, 1, 1 };
				final BlockFaceUV uv = new BlockFaceUV( defUVs, 0 );
				final BlockPartFace bpf = new BlockPartFace( myFace, 0, "", uv );

				final TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
				final BakedQuad q = faceBakery.makeBakedQuad( to, from, bpf, texture, myFace, mr, bpr, true, true );

				final int[] vertData = q.getVertexData();

				int a = 0;
				int b = 2;

				switch ( myFace )
				{
					case NORTH:
					case SOUTH:
						a = 0;
						b = 1;
						break;
					case EAST:
					case WEST:
						a = 1;
						b = 2;
						break;
					default:
				}

				final int p = vertData.length / 4;
				for ( int vertNum = 0; vertNum < 4; vertNum++ )
				{
					final float A = Float.intBitsToFloat( vertData[vertNum * p + a] );
					final float B = Float.intBitsToFloat( vertData[vertNum * p + b] );

					for ( int o = 0; o < 3; o++ )
					{
						final float v = Float.intBitsToFloat( vertData[vertNum * p + o] );
						final float scaler = 1.0f / 16.0f; // pos start in the 0-16
						quadMapping[myFace.ordinal()][vertNum][o * 2] = v * scaler;
						quadMapping[myFace.ordinal()][vertNum][o * 2 + 1] = ( 1.0f - v ) * scaler;
					}

					if ( ModelUVReader.isZero( A ) && ModelUVReader.isZero( B ) )
					{
						faceVertMap[myFace.getIndex()][vertNum] = 0;
					}
					else if ( ModelUVReader.isZero( A ) && ModelUVReader.isOne( B ) )
					{
						faceVertMap[myFace.getIndex()][vertNum] = 3;
					}
					else if ( ModelUVReader.isOne( A ) && ModelUVReader.isZero( B ) )
					{
						faceVertMap[myFace.getIndex()][vertNum] = 1;
					}
					else
					{
						faceVertMap[myFace.getIndex()][vertNum] = 2;
					}
				}
			}
		}
		
		//TEST
		//private static RenderBlocks facadeRenderBlocks = new RenderBlocks();
		//public static RenderBlocks renderBlocks = new RenderBlocks();

		public static int VERTEX_SIZE = 8;

		public static final float size = 1 / 512F;

		final static int[] sideOffsets = { 1, 1, 2, 2, 0, 0 };
		final static float[] sideBound1 = { 0, 1 - size, 0, 1 - size, 0, 1 - size };
		final static float[] sideBound2 = { size, 1, size, 1, size, 1 };

		final static float[] sideSoftBounds = { 0, 1, 0, 1, 0, 1 };

		private final static float FACADE_RENDER_OFFSET = ((float) (1.0D / 1024.0D)) * 2;
		private final static float FACADE_RENDER_OFFSET2 = 1 - FACADE_RENDER_OFFSET;

		//Copyied from COFH Thermal Dynamics
		
		public static boolean renderCover(IBlockAccess blockAccess, BlockPos pos, int side, IBlockState state, AxisAlignedBB bounds, boolean addNormals, boolean addTrans) {

			//facadeRenderBlocks.blockAccess = CoverBlockAccess.getInstance(renderBlocks.blockAccess, x, y, z, side, block, meta);

			Tessellator tess = Tessellator.getInstance();
			VertexBuffer buffer = tess.getBuffer();
			//int rawBufferIndex = tess.rawBufferIndex;

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			boolean rendered = Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(state, pos, blockAccess, buffer);
			
			
			/*if (hollowCover != null) {
				CoverHoleRender.holify(rawBufferIndex, x, y, z, side, hollowCover);
			}*/

			//int rawBufferIndex2 = tess.rawBufferIndex;

			if (!rendered) {
				int[] rb = buffer.getVertexState().getRawBuffer();
				String data = "[";
				for(int i = 0; i < rb.length; i++){
					data+=""+rb[i]+", ";
				}
				data+="]";
				//ModLogger.info(data);

				boolean flag, flag2;

				double buffetXOffset = ObfuscationReflectionHelper.getPrivateValue(VertexBuffer.class, buffer, 10);
				double buffetYOffset = ObfuscationReflectionHelper.getPrivateValue(VertexBuffer.class, buffer, 11);
				double buffetZOffset = ObfuscationReflectionHelper.getPrivateValue(VertexBuffer.class, buffer, 12);

				float dx = (float) buffetXOffset;
				float dy = (float) buffetYOffset;
				float dz = (float) buffetZOffset;

				float quad[][] = new float[4][3];
				float vec[] = new float[3];
				boolean flat[] = new boolean[3];

				int intNormal = 0;

				//IIcon icon = RenderDuct.coverBase;
				TextureAtlasSprite sprite = RenderUtil.getTexture(Blocks.STONE.getDefaultState());
				
				final int vertexSize = buffer.getVertexFormat().getIntegerSize();
				final int verticiesPerFace = 4, incrementAmt = vertexSize * verticiesPerFace;

				for (int k = 0; k < 1; k += incrementAmt) {
					flag = flag2 = false;
					for (int i = 0; i < 3; i++) {
						flat[i] = true;
					}

					for (int k2 = 0; k2 < verticiesPerFace; k2++) {
						int i = k + k2 * vertexSize;
						quad[k2][0] = Float.intBitsToFloat(rb[i]) - dx - pos.getX();
						quad[k2][1] = Float.intBitsToFloat(rb[i + 1]) - dy - pos.getY();
						quad[k2][2] = Float.intBitsToFloat(rb[i + 2]) - dz - pos.getZ();

						flag = flag || quad[k2][sideOffsets[side]] != sideSoftBounds[side];
						flag2 = flag2 || quad[k2][sideOffsets[side]] != (1 - sideSoftBounds[side]);

						if (k2 == 0) {
							System.arraycopy(quad[k2], 0, vec, 0, 3);
						} else {
							for (int vi = 0; vi < 3; vi++) {
								flat[vi] = flat[vi] && quad[k2][vi] == vec[vi];
							}
						}
					}

					int s = -1;

					if (flag && flag2) {
						for (int vi = 0; vi < 3; vi++) {
							if (flat[vi]) {
								if (vi != sideOffsets[side]) {
									s = vi;
									break;
								} else {
									flag = false;
								}
							}
						}
					}

					if (addNormals) {
						intNormal = -64 << 8;
					}

					for (int k2 = 0; k2 < verticiesPerFace; k2++) {
						boolean flag3 = quad[k2][sideOffsets[side]] != sideSoftBounds[side];
						for (int j = 0; j < 3; j++) {
							if (j == sideOffsets[side]) {
								quad[k2][j] = clampF(quad[k2][j], bounds, j);
							} else {
								if (flag && flag2 && flag3) {
									// TODO: only clamp here when covers[] != null && has a cover on the side this vertex is on
									quad[k2][j] = MathHelper.clamp(quad[k2][j], FACADE_RENDER_OFFSET, FACADE_RENDER_OFFSET2);
								}
							}
						}

						int i = k + k2 * vertexSize;
						rb[i] = Float.floatToRawIntBits(quad[k2][0] + dx + pos.getX());
						rb[i + 1] = Float.floatToRawIntBits(quad[k2][1] + dy + pos.getY());
						rb[i + 2] = Float.floatToRawIntBits(quad[k2][2] + dz + pos.getZ());

						if (s != -1) {
							float u, v;

							if (s == 0) {
								u = quad[k2][1];
								v = quad[k2][2];
							} else if (s == 1) {
								u = quad[k2][0];
								v = quad[k2][2];
							} else {
								u = quad[k2][0];
								v = quad[k2][1];
							}

							u = MathHelper.clamp(u, 0, 1) * 16;
							v = MathHelper.clamp(v, 0, 1) * 16;

							u = sprite.getInterpolatedU(u);
							v = sprite.getInterpolatedV(v);

							rb[i + 3] = Float.floatToRawIntBits(u);
							rb[i + 4] = Float.floatToRawIntBits(v);
						}

						if (addNormals) {

							rb[i + 6] = intNormal;
						}
						if (addTrans) {
							if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
								rb[i + 5] = rb[i + 5] & 0x00FFFFFF | (((rb[i + 5] & 0xFF000000) >>> 1) & 0xFF000000);
							} else {
								rb[i + 5] = rb[i + 5] & 0xFFFFFF00 | (((rb[i + 5] & 0x000000FF) >>> 1) & 0x000000FF);
							}
						}
					}
				}
				//buffer.setVertexState(new State(rb, buffer.getVertexFormat()));
				
				try {
					IntBuffer rawIntBuffer = ObfuscationReflectionHelper.getPrivateValue(VertexBuffer.class, buffer, 2);
					rawIntBuffer.clear();
					Method grow = VertexBuffer.class.getDeclaredMethod("growBuffer", Integer.TYPE);
					if(grow !=null){
						grow.setAccessible(true);
						grow.invoke(buffer, (rb.length * 4));
					}
					rawIntBuffer.put(rb);
					ObfuscationReflectionHelper.setPrivateValue(VertexBuffer.class, buffer, (rb.length / buffer.getVertexFormat().getIntegerSize()), 5);
				} catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
			tess.draw();
			//facadeRenderBlocks.blockAccess = null;
				
			return rendered;

		}

		private static float clampF(float x, AxisAlignedBB bounds, int j) {
			float l = (float) getSide(sides[j][0], bounds);
			float u = (float) getSide(sides[j][1], bounds);

			if (x < l) {
				return l - (l - x) * 0.001953125f;
			} else if (x > u) {
				return u + (x - u) * 0.001953125f;
			} else {
				return x;
			}
		}
		
		public static double getSide(int s, AxisAlignedBB bounds) {

			switch (s) {
			case 0:
				return bounds.minY;
			case 1:
				return bounds.maxY;
			case 2:
				return bounds.minZ;
			case 3:
				return bounds.maxZ;
			case 4:
				return bounds.minX;
			case 5:
				return bounds.maxX;
			}
			throw new IndexOutOfBoundsException("Switch Falloff");
		}

		private final static int[][] sides = { { 4, 5 }, { 0, 1 }, { 2, 3 } };
		

		@SuppressWarnings("unused")
		private static float clampF(float vec, int side) {

			return MathHelper.clamp(sideSoftBounds[side] + (vec - sideSoftBounds[side]) * size, sideBound1[side], sideBound2[side]);
		}

		public static boolean noFacade(IBlockAccess world, BlockPos pos, int side) {

			return !world.isSideSolid(pos, EnumFacing.values()[side], false);
		}

		public static boolean notSolid(IBlockAccess world, BlockPos pos, int side) {

			EnumFacing dir = EnumFacing.values()[side];
			IBlockState block2 = world.getBlockState(pos);

			return block2.shouldSideBeRendered(world, pos.add(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ()), dir);
		}
}
