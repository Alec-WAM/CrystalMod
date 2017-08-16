package alec_wam.CrystalMod.blocks.glass;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.proxy.ClientProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintedCrystalGlass extends BlockCrystalGlass {

	@Override
	public boolean canConnect(@Nonnull IBlockState original, @Nonnull IBlockState connected) {
    	if(connected.getBlock() == ModBlocks.crystalGlassTinted || connected.getBlock() == ModBlocks.crystalGlassPainted){
    		GlassType typeO = original.getValue(TYPE);
    		GlassType typeC = connected.getValue(TYPE);
    		return typeO == typeC;
    	}
    	return false;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new GlassBlockStateMapper());
    	ModelResourceLocation inv = new ModelResourceLocation(this.getRegistryName(), "inventory");
    	ClientProxy.registerCustomModel(inv, ModelPaintedGlass.INSTANCE);
		for(GlassType type : GlassType.values()){
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), inv);
	        ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "type="+type.getName()), ModelPaintedGlass.INSTANCE);
		}
    }
	
}
