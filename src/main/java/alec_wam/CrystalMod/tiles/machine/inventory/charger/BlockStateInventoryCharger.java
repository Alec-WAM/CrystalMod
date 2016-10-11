package alec_wam.CrystalMod.tiles.machine.inventory.charger;

import alec_wam.CrystalMod.tiles.BlockStateFacing;
import net.minecraft.block.properties.PropertyEnum;

public class BlockStateInventoryCharger extends BlockStateFacing
{
	public static final PropertyEnum<BlockInventoryCharger.ChargerBlockType> typeProperty = PropertyEnum.create("type", BlockInventoryCharger.ChargerBlockType.class);
	
	public BlockStateInventoryCharger(BlockInventoryCharger block)
	{
		super(block, typeProperty);
	}
}