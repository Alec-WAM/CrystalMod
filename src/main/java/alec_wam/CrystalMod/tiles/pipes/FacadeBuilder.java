package alec_wam.CrystalMod.tiles.pipes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import alec_wam.CrystalMod.client.util.ModelUVAverager;
import alec_wam.CrystalMod.tiles.pipes.CubeBuilder.UvVector;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;


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
		
		boolean test = true;
		
		if(test){
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

	private static AxisAlignedBB getFacadeBox( EnumFacing side, boolean thinFacades )
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
}
