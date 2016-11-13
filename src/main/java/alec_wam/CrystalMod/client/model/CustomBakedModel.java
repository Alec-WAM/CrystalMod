package alec_wam.CrystalMod.client.model;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public class CustomBakedModel {

	private ModelResourceLocation modelLoc;
	private IBakedModel model;
	
	public CustomBakedModel(ModelResourceLocation loc, IBakedModel model){
		this.modelLoc = loc;
		this.model = model;
	}

	/**
	 * @return the modelLoc
	 */
	public ModelResourceLocation getModelLoc() {
		return modelLoc;
	}

	/**
	 * @return the model
	 */
	public IBakedModel getModel() {
		return model;
	}
	
	public void preModelRegister(){}
	
	public void postModelRegister(){}
	
}
