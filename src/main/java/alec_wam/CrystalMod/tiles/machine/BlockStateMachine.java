package alec_wam.CrystalMod.tiles.machine;

import alec_wam.CrystalMod.tiles.BlockStateFacing;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;

public class BlockStateMachine extends BlockStateFacing
{
	public static final PropertyBool activeProperty = PropertyBool.create("active");
	
	public BlockStateMachine(BlockMachine block)
	{
		super(block, facingProperty, activeProperty);
	}
	
	public BlockStateMachine(BlockMachine block, PropertyEnum<?> prop)
	{
		super(block, prop, facingProperty, activeProperty);
	}
	
	public BlockStateMachine(Block block, IProperty<?>... typeProperty)
	{
		super(block, typeProperty);
	}
}
