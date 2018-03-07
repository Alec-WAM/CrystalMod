package alec_wam.CrystalMod.entities.boatflume.rails;

import alec_wam.CrystalMod.blocks.FakeBlockStateWithData;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ModelFlumeRailRaisedGroundPowered extends ModelFlumeRailRaisedGround {

	public final PropertyBool poweredProp;
	public ModelFlumeRailRaisedGroundPowered(String type, PropertyBool poweredProp) {
		super(type);
		this.poweredProp = poweredProp;
	}
	
	public ModelFlumeRailRaisedGroundPowered(String type, PropertyBool poweredProp, FakeBlockStateWithData state, EnumFacing facing, long rand) {
        super(type, state, facing, rand);
        this.poweredProp = poweredProp;
    }
	
	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return (state instanceof FakeBlockStateWithData) ? new ModelFlumeRailRaisedGroundPowered(railType, poweredProp, (FakeBlockStateWithData)state, side, rand) : null;
	}
	
	@Override
	public TextureAtlasSprite getRailSprite(){
		if(state !=null){
			if(state.getValue(poweredProp).booleanValue()){
				return RenderUtil.getSprite("crystalmod:blocks/flume/"+railType+"_powered"); 
			}
		}
    	return RenderUtil.getSprite("crystalmod:blocks/flume/"+railType);
    } 

}
