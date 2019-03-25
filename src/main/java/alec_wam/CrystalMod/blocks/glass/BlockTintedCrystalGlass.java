package alec_wam.CrystalMod.blocks.glass;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTintedCrystalGlass extends BlockCrystalGlass {

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public boolean canConnect(@Nonnull IBlockState original, @Nonnull IBlockState connected) {
    	if(connected.getBlock() == ModBlocks.crystalGlassTinted || connected.getBlock() == ModBlocks.crystalGlassPainted){
    		CrystalColors.Special typeO = original.getValue(CrystalColors.COLOR_SPECIAL);
    		CrystalColors.Special typeC = connected.getValue(CrystalColors.COLOR_SPECIAL);
    		return typeO == typeC;
    	}
    	return false;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new GlassBlockStateMapper());
    	ModelResourceLocation inv = new ModelResourceLocation(this.getRegistryName(), "inventory");
    	ClientProxy.registerCustomModel(inv, ModelTintedGlass.INSTANCE);
		for(CrystalColors.Special type : CrystalColors.Special.values()){
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), inv);
	        ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "color="+type.getName()), new ModelTintedGlass(type));
		}
    }
	
}
