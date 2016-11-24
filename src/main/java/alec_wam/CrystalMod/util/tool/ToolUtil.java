package alec_wam.CrystalMod.util.tool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyStorage;

public class ToolUtil {

  public static final String[] wrenchInterfaces = {
	  "resonant.core.content.ItemScrewdriver",        //Resonant Induction
      "ic2.core.item.tool.ItemToolWrench",            //IC2
      "ic2.core.item.tool.ItemToolWrenchElectric",    //IC2 (more)
      "mrtjp.projectred.api.IScrewdriver",            //Project Red
      "mods.railcraft.api.core.items.IToolCrowbar",   //Railcraft
      "com.bluepowermod.items.ItemScrewdriver",       //BluePower
      "appeng.items.tools.quartz.ToolQuartzWrench",   //Applied Energistics
      "crazypants.enderio.api.tool.ITool",            //Ender IO
      "mekanism.api.IMekWrench",                      //Mekanism
      "pneumaticCraft.common.item.ItemPneumaticWrench",
      "powercrystals.minefactoryreloaded.api.IToolHammer"
  };
  public static final Class<?>[] wrenchClasses;

  static {
	  wrenchClasses = new Class[wrenchInterfaces.length];
	  for (int i = 0; i < wrenchClasses.length; i++) {
		  try {
			  wrenchClasses[i] = Class.forName(wrenchInterfaces[i]);
		  } catch (ClassNotFoundException ignore) {
			  wrenchClasses[i] = null;
		  }
	  }
  }
	
  public static boolean isToolEquipped(EntityPlayer player, EnumHand hand) {
    return getInstance().isToolEquippedImpl(player, hand);
  }

  public static ITool getEquippedTool(EntityPlayer player, EnumHand hand) {
    return getInstance().getEquippedToolImpl(player, hand);
  }
  
  public static boolean breakBlockWithTool(Block block, World world, int x, int y, int z, EntityPlayer entityPlayer, EnumHand hand) {
    return breakBlockWithTool(block, world, new BlockPos(x,y,z), entityPlayer, hand);
  }
  
  public static boolean breakBlockWithTool(Block block, World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand) {
    return breakBlockWithTool(block, world, pos, entityPlayer, entityPlayer.getHeldItem(hand));
  }

  public static boolean breakBlockWithTool(Block block, World world, BlockPos pos, EntityPlayer entityPlayer, ItemStack heldItem) {
    ITool tool = ToolUtil.getInstance().getToolImpl(heldItem);
    if (entityPlayer.isSneaking() && (tool !=null && tool.canUse(heldItem, entityPlayer, pos) || (tool == null && isWrench(heldItem)))) {
      IBlockState bs = world.getBlockState(pos);;
      if(block.removedByPlayer(bs, world, pos, entityPlayer, true)) {
        block.harvestBlock(world, entityPlayer, pos, world.getBlockState(pos), world.getTileEntity(pos), heldItem);
      }
      if(tool !=null)tool.used(heldItem, entityPlayer, pos);
      return true;
    }
    return false;
  }

  private static ToolUtil instance;

  private static ToolUtil getInstance() {
    if(instance == null) {
      instance = new ToolUtil();
    }
    return instance;
  }

  private final List<IToolProvider> toolProviders = new ArrayList<IToolProvider>();
  private final List<IToolImpl> toolImpls = new ArrayList<IToolImpl>();

  private ToolUtil() {

    try {
      Object obj = Class.forName("alec_wam.CrystalMod.util.tool.BuildCraftToolProvider").newInstance();
      toolProviders.add((IToolProvider) obj);
      toolImpls.add((IToolImpl) obj);
    } catch (Exception e) {
      
    }
  }

  public void registerToolProvider(IToolProvider toolProvider) {
    toolProviders.add(toolProvider);
  }

  private boolean isToolEquippedImpl(EntityPlayer player, EnumHand hand) {
    return hasWrench(player, hand) ? true : getEquippedToolImpl(player, hand) != null;
  }
  
  private boolean hasWrench(EntityPlayer player, EnumHand hand) {
	  player = player == null ? CrystalMod.proxy.getClientPlayer() : player;
	  if(player == null) {
		  return false;
	  }
	  ItemStack equipped = player.getHeldItem(hand);
	  if(isWrench(equipped))return true;
	  return false;
  }
  
  public static boolean isWrench(ItemStack equipped){
	  if(ItemStackTools.isNullStack(equipped)) {
		  return false;
	  }
	  return isWrenchItem(equipped.getItem());
  }
  
  public static boolean isWrenchItem(Item equipped){
	  if(equipped == null)return false;
	  if(equipped == ModItems.wrench) return true;
	  for (Class<?> c : wrenchClasses) {
          if (c != null && c.isAssignableFrom(equipped.getClass()))
              return true;
      }
	  return false;
  }

  private ITool getEquippedToolImpl(EntityPlayer player, EnumHand hand) {
    player = player == null ? CrystalMod.proxy.getClientPlayer() : player;
    if(player == null) {
      return null;
    }
    ItemStack equipped = player.getHeldItem(hand);
    if(ItemStackTools.isNullStack(equipped)) {
      return null;
    }
    if(equipped.getItem() instanceof ITool) {
      return (ITool) equipped.getItem();
    }
    return getToolImpl(equipped);

  }

  private ITool getToolImpl(ItemStack equipped) {
    for (IToolProvider provider : toolProviders) {
      ITool result = provider.getTool(equipped);
      if(result != null) {
        return result;
      }
    }
    return null;
  }
  
  public static boolean isAxe(ItemStack stack){
	  return !ItemStackTools.isNullStack(stack) && stack.getItem() !=null && stack.getItem().getHarvestLevel(stack, "axe") >= 0;
  }
  
  public static boolean isBrokenTinkerTool(ItemStack item) {
      return !ItemStackTools.isNullStack(item) && item.hasTagCompound() && item.getTagCompound().hasKey("Stats") && item.getTagCompound().getCompoundTag("Stats").getBoolean("Broken");
  }
  
  public static boolean isEmptyRfTool(ItemStack stack) {
	  if(ItemStackTools.isNullStack(stack) || stack.getItem() == null || !(stack.getItem() instanceof IEnergyContainerItem)) {
		  return false;
	  }
	  IEnergyContainerItem container = (IEnergyContainerItem)stack.getItem();
	  return container.getMaxEnergyStored(stack) > 0 && container.getEnergyStored(stack) <= 0;
  }
}
