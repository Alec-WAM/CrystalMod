package alec_wam.CrystalMod.tiles.pipes.covers;

import java.util.Arrays;

import alec_wam.CrystalMod.client.util.BaseModelReader;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class ModelUVReader extends BaseModelReader
{

	final float minU;
	final float maxUMinusMin;

	final float minV;
	final float maxVMinusMin;

	public final float[] quadUVs = new float[] { 0, 0, 0, 1, 1, 0, 1, 1 };

	int uCoord, vCoord;

	public ModelUVReader(
			final TextureAtlasSprite texture,
			final int uFaceCoord,
			final int vFaceCoord )
	{
		minU = texture.getMinU();
		maxUMinusMin = texture.getMaxU() - minU;

		minV = texture.getMinV();
		maxVMinusMin = texture.getMaxV() - minV;

		uCoord = uFaceCoord;
		vCoord = vFaceCoord;
	}

	private float pos[];
	private float uv[];
	public int corners;

	@Override
	public void put(
			final int element,
			final float... data )
	{
		final VertexFormat format = getVertexFormat();
		final VertexFormatElement ele = format.getElement( element );

		if ( ele.getUsage() == EnumUsage.UV && ele.getIndex() != 1 )
		{
			uv = Arrays.copyOf( data, data.length );
		}

		else if ( ele.getUsage() == EnumUsage.POSITION )
		{
			pos = Arrays.copyOf( data, data.length );
		}
		
		if ( element == format.getElementCount() - 1 )
		{
			ModLogger.info("New Put 2.0");
			
			if ( isZero( pos[uCoord] ) && isZero( pos[vCoord] ) )
			{
				//Top Left
				corners = corners | 0x1;
				quadUVs[0] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[1] = ( uv[1] - minV ) / maxVMinusMin;
			}
			else if ( isZero( pos[uCoord] ) && isOne( pos[vCoord] ) )
			{
				//Bottom Right
				corners = corners | 0x2;
				quadUVs[4] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[5] = ( uv[1] - minV ) / maxVMinusMin;
			}
			else if ( isOne( pos[uCoord] ) && isZero( pos[vCoord] ) )
			{
				//Top Right
				corners = corners | 0x4;
				quadUVs[2] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[3] = ( uv[1] - minV ) / maxVMinusMin;
			}
			else if ( isOne( pos[uCoord] ) && isOne( pos[vCoord] ) )
			{
				//Bottom Right
				corners = corners | 0x8;
				quadUVs[6] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[7] = ( uv[1] - minV ) / maxVMinusMin;
			}
			
			String posString = "";
			for(int i = 0; i < pos.length; i++){
				posString+=" "+pos[i];
			}
			
			ModLogger.info("Out:" +posString);
		}
	}
	
	public static boolean isOne(
			final float v )
	{
		/*float ab = Math.abs(v);
		float disOne = 1.0F-ab;
		float disZero = ab;
		
		return disZero > disOne;*/
		return Math.abs( v ) < 0.01;
	}

	public static boolean isZero(
			final float v )
	{
		/*float ab = Math.abs(v);
		float disOne = 1.0F-ab;
		float disZero = ab;
		
		return disZero < disOne;*/
		return Math.abs( v - 1.0f ) < 0.01;
	}
}
