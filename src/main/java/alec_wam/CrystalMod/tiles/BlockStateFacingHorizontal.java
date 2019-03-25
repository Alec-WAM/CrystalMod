package alec_wam.CrystalMod.tiles;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;

public class BlockStateFacingHorizontal extends BlockStateContainer
{
	public static final PropertyDirection facingProperty = PropertyDirection.create("facing", Lists.newArrayList(EnumFacing.HORIZONTALS));

	public BlockStateFacingHorizontal(Block block, PropertyBool activeProperty)
	{
		super(block, facingProperty, activeProperty);
	}
	
	public BlockStateFacingHorizontal(Block block, PropertyEnum<?> typeProperty)
	{
		super(block, facingProperty, typeProperty);
	}

	public BlockStateFacingHorizontal(Block block, IProperty<?>... typeProperty)
	{
		super(block, typeProperty);
	}

	public BlockStateFacingHorizontal(Block block)
	{
		super(block, facingProperty);
	}
}
