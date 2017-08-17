package alec_wam.CrystalMod.tiles.pipes.covers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.util.ModelUVAverager;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ReflectionWrapper;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unchecked")
public class CoverUtil {
	public static final HashMap<Integer, String>[] blockToTexture;
	static{
		blockToTexture = new HashMap[EnumFacing.VALUES.length];
        for (int x = 0; x < EnumFacing.VALUES.length; ++x) {
            blockToTexture[x] = new HashMap<Integer, String>();
        }
	}
	
	public static class CoverData{
		private final IBlockState state;
		
		public CoverData(IBlockState state){
			this.state = state;
		}
		
		public IBlockState getBlockState(){
			return state;
		}
		
		public void writeToNBT(NBTTagCompound nbt) {
            if (state != null) {
                nbt.setString("block", BlockUtil.getNameForBlock(state.getBlock()));
                nbt.setByte("metadata", (byte) state.getBlock().getMetaFromState(state));
            }
        }
		@SuppressWarnings("deprecation")
		public static CoverData readFromNBT(NBTTagCompound nbt) {
            String blockID = nbt.getString("block");
            Block block = Block.getBlockFromName(blockID);
            if(block == null)return null;
            int meta = nbt.getByte("metadata");
            return new CoverData(block.getStateFromMeta(meta));
        }
		
		@Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CoverData other = (CoverData) obj;
            if (state == null) {
                if (other.state != null) return false;
            } else if (!state.equals(other.state)) return false;
            return true;
        }

		public boolean isTransparent() {
			return state !=null && !state.isOpaqueCube();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite findTexture(final int BlockRef, final IBakedModel model, final EnumFacing myFace) {
        if (CoverUtil.blockToTexture[myFace.ordinal()].containsKey(BlockRef)) {
            final String textureName = CoverUtil.blockToTexture[myFace.ordinal()].get(BlockRef);
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureName);
        }
        TextureAtlasSprite texture = null;
        final IBlockState state = Block.getStateById( BlockRef );
        if ( model != null )
		{
			try
			{
				texture = findTexture( texture, getModelQuads( model, state, myFace, 0 ), myFace );

				if ( texture == null )
				{
					for ( final EnumFacing side : EnumFacing.VALUES )
					{
						texture = findTexture( texture, getModelQuads( model, state, side, 0 ), side );
					}

					texture = findTexture( texture, getModelQuads( model, state, null, 0 ), null );
				}
			}
			catch ( final Exception errr )
			{
			}
		}
        if (texture == null) {
            try {
                if (texture == null) {
                    texture = model.getParticleTexture();
                }
            }
            catch (Exception ex2) {}
        }
        if (texture == null) {
            texture = RenderUtil.getMissingSprite();
        }
        CoverUtil.blockToTexture[myFace.ordinal()].put(BlockRef, texture.getIconName());
        return texture;
    }
	
	private static TextureAtlasSprite findTexture(
			TextureAtlasSprite texture,
			final List<BakedQuad> faceQuads,
			final EnumFacing myFace ) throws IllegalArgumentException, IllegalAccessException, NullPointerException
	{
		for ( final BakedQuad q : faceQuads )
		{
			if ( q.getFace() == myFace )
			{
				texture = findQuadTexture( q );
			}
		}

		return texture;
	}
	
	public static TextureAtlasSprite findQuadTexture(
			final BakedQuad q ) throws IllegalArgumentException, IllegalAccessException, NullPointerException
	{
		final TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
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

		return RenderUtil.getMissingSprite();
	}
	
	private static List<BakedQuad> getModelQuads(
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

		final IBakedModel secondModel = model.getOverrides().handleItemState( model, ItemUtil.getItemFromBlock( state ), CrystalMod.proxy.getClientWorld(), CrystalMod.proxy.getClientPlayer());

		if ( secondModel != null )
		{
			try
			{
				return secondModel.getQuads( null, f, rand );
			}
			catch ( final Throwable t )
			{

			}

		}

		// try to not crash...
		return Collections.emptyList();
	}

	public static AxisAlignedBB getCoverBoundingBox( EnumFacing side, boolean thinFacades )
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

}
