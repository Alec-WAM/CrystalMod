package alec_wam.CrystalMod.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;

public class BlockStateFacing extends BlockStateContainer
{
	public static final PropertyDirection facingProperty = PropertyDirection.create("facing");

	public BlockStateFacing(Block block, PropertyBool activeProperty)
	{
		super(block, facingProperty, activeProperty);
	}
	
	public BlockStateFacing(Block block, PropertyEnum<?> typeProperty)
	{
		super(block, facingProperty, typeProperty);
	}

	public BlockStateFacing(Block block, IProperty<?>... typeProperty)
	{
		super(block, typeProperty);
	}

	public BlockStateFacing(Block block)
	{
		super(block, facingProperty);
	}
}
