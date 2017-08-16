package alec_wam.CrystalMod.items.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.tools.IMegaTool;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.HarvestResult;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.tool.MultiHarvestComparator;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import alec_wam.CrystalMod.util.tool.TreeHarvestUtil;
import alec_wam.CrystalMod.util.tool.TreeHarvestUtil.BaseHarvestTarget;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMegaCrystalAxe extends ItemCrystalAxe implements IMegaTool {

	public ItemMegaCrystalAxe(ToolMaterial material) {
		super(material, "megacrystalaxe");
		setNoRepair();
	}
	
	private static final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		Block block = player.getEntityWorld().getBlockState(pos).getBlock();
		if(TreeHarvestUtil.isFullTree(player.getEntityWorld(), pos, new BaseHarvestTarget(block)).isValid()){
			//if(!player.getEntityWorld().isRemote){
			TreeHarvestUtil harvester = new TreeHarvestUtil();
			HarvestResult res = new HarvestResult();
			harvester.harvest(player.getEntityWorld(), pos, res);
			List<BlockPos> sortedTargets = new ArrayList<BlockPos>(res.getHarvestedBlocks());
			harvestComparator.refPoint = pos;
			Collections.sort(sortedTargets, harvestComparator);

			ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
			for(BlockPos extraPos : sortedTargets){
				if(extraPos.getX() == pos.getX() && extraPos.getY() == pos.getY() && extraPos.getZ() == pos.getZ()) {
					continue;
				}
				if(ToolUtil.isToolEffective(itemstack, player.getEntityWorld().getBlockState(extraPos))){
					builder.add(extraPos);
				}
			}
			ImmutableList<BlockPos> list = builder.build();
			for(BlockPos extraPos : list){
				BlockUtil.breakExtraBlock(itemstack, player.getEntityWorld(), player, extraPos, pos);
			}
			//}
		} else {
			for(BlockPos extraPos : getAOEBlocks(itemstack, player.getEntityWorld(), player, pos)) {
				BlockUtil.breakExtraBlock(itemstack, player.getEntityWorld(), player, extraPos, pos);
			}
		}
		return super.onBlockStartBreak(itemstack, pos, player);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final Map<String, ModelResourceLocation> models = Maps.newHashMap();
		for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
			ModelResourceLocation loc = new ModelResourceLocation("crystalmod:tool/megaaxe", "color="+color);
			models.put(color, loc);
			ModelBakery.registerItemVariants(this, loc);
		}
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	String color = ItemNBTHelper.getString(stack, "Color", "");
            	return models.get(color);
            }
        });
    }
	
	@Override
	public ImmutableList<BlockPos> getAOEBlocks(ItemStack tool, World world, EntityPlayer player, BlockPos pos) {
		world.getBlockState(pos).getBlock();
		
		return ToolUtil.calcAOEBlocks(tool, world, player, pos, 3, 3, 1);
	}
	
	@Override
	public int getMaxDamage(ItemStack stack)
    {
		//Triple the durability
		return super.getMaxDamage(stack) * 3;
    }

}
