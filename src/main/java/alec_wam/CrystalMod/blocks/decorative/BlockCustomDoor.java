package alec_wam.CrystalMod.blocks.decorative;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomDoor extends BlockDoor implements ICustomModel {

	public final Item doorItem;
	public BlockCustomDoor(Material materialIn, SoundType sound, Item item) {
		super(materialIn);
		this.doorItem = item;
		this.setSoundType(sound);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : doorItem;
    }
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(doorItem);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		IStateMapper stateMapper = new StateMap.Builder().ignore(new IProperty[] {BlockDoor.POWERED}).build();
		ModelLoader.setCustomStateMapper(this, stateMapper);
		ModBlocks.initBasicModel(this);
	}

}
