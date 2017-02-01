package alec_wam.CrystalMod.util.tool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;

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
	  return !ItemStackTools.isNullStack(stack) && stack.getItem() !=null && stack.getItem().getHarvestLevel(stack, "axe", null, null) >= 0;
  }
  
  public static boolean isBrokenTinkerTool(ItemStack item) {
      return !ItemStackTools.isNullStack(item) && item.hasTagCompound() && item.getTagCompound().hasKey("Stats") && item.getTagCompound().getCompoundTag("Stats").getBoolean("Broken");
  }
  
  public static boolean isRfTool(ItemStack stack){
	  if(ItemStackTools.isNullStack(stack) || stack.getItem() == null) {
		  return false;
	  }
	  if(stack.hasCapability(CapabilityEnergy.ENERGY, null)){
		  return true;
	  }
	  return false;
  }
  
  public static boolean isEmptyRfTool(ItemStack stack) {
	  if(ItemStackTools.isNullStack(stack) || stack.getItem() == null) {
		  return false;
	  }
	  
	  if(stack.hasCapability(CapabilityEnergy.ENERGY, null)){
		  net.minecraftforge.energy.IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		  if(storage !=null){
			  return storage.getMaxEnergyStored() > 0 && storage.getEnergyStored() <=0;
		  }
	  }
	  return false;
  }
  
  public static boolean isWeapon(ItemStack stack){
	  if(isTool(stack))return false;
	  boolean valid = false;

	  String[] validWeaponNames = {
			  "sword", "dagger", "sabre", "rapier", "cutlass", "bow", "whip"	
	  };

	  if (ItemStackTools.isValid(stack) && stack.getMaxStackSize() == 1)
	  {
		  Item item = stack.getItem();
		  String name = stack.getUnlocalizedName().toLowerCase();
		  // Vanilla
		  if (item instanceof ItemSword || item instanceof ItemBow)
		  {
			  return true;
		  }

		  // Just for extra compatibility and/or security and/or less annoyance
		  for (String toolName : validWeaponNames)
		  {
			  String a = toolName;
			  if (name.contains(toolName)) return true;
		  }
		  //And also this because I'm awesome
		  try
		  {
			  // Tinker's Construct
			  if (item.getClass().getName().contains("tconstruct.items.tools")) return true;
		  } catch (Exception oops)
		  {
			  //  oops.printStackTrace();
		  }
	  }

	  return valid;
  }
  
  public static boolean isTool(ItemStack stack){
	  boolean valid = false;

	  String[] validToolNames = {
			  "wrench", "hammer", "axe", "shovel", "grafter", "scoop", "crowbar", "mattock", "drill", "pickaxe"/*"hatchet","excavator","chisel"*/
	  };

	  String[] invalidToolNames = {
			  "bucket", "sword", "dagger", "sabre", "rapier", "shield", "cutlass", "bow", "whip"
	  };

	  if (ItemStackTools.isValid(stack) && stack.getMaxStackSize() == 1)
	  {
		  Item item = stack.getItem();
		  String name = item.getUnlocalizedName().toLowerCase();

		  // Vanilla
		  if (item instanceof ItemTool || item instanceof ItemHoe || item instanceof ItemShears || item instanceof ItemPickaxe || item instanceof ItemFishingRod || item instanceof ItemFlintAndSteel)
		  {
			  return true;
		  }

		  // Just for extra compatibility and/or security and/or less annoyance
		  for (String toolName : validToolNames)
		  {
			  String a = toolName;
			  if (name.contains(toolName)) return true;
		  }

		  for (String toolName : invalidToolNames)
		  {
			  String a = toolName;
			  if (name.contains(toolName)) return false;
		  }

		  //And also this because I'm awesome
		  try
		  {
			  // Tinker's Construct
			  if (item.getClass().getName().contains("tconstruct.items.tools")) return true;
			  if(isBrokenTinkerTool(stack))return true;
		  } catch (Exception oops)
		  {
			  //  oops.printStackTrace();
		  }
		  try
		  {
			  //Wrench
			  if (isWrench(stack)) return true;
		  } catch (Exception oops)
		  {
			  //  oops.printStackTrace();
		  }
		  try
		  {
			  //IndustrialCraft
			  if (java.lang.Class.forName("ic2.api.item.IElectricItem").isInstance(item)) return true;
		  } catch (Exception oops)
		  {
			  //  oops.printStackTrace();
		  }
		  try{
			  //Extra-Utilites
			  if(java.lang.Class.forName("com.rwtema.extrautils.item.ItemBuildersWand").isInstance(item))return true;
		  }catch (Exception oops)
		  {
			  // oops.printStackTrace();
		  }
		  try{
			  //Thaumcraft
			  if(java.lang.Class.forName("thaumcraft.common.items.wands.ItemWandCasting").isInstance(item))return true;
		  }catch (Exception oops)
		  {
			  // oops.printStackTrace();
		  }
		  try
		  {
			  //Thermal Expansion
			  if (java.lang.Class.forName("cofh.core.item.tool").isInstance(item)) return true;
			  if (java.lang.Class.forName("thermalexpansion.item.tool").isInstance(item)) return true;
			  if(ToolUtil.isRfTool(stack))return true;
		  } catch (Exception oops)
		  {
			  // oops.printStackTrace();
		  }
		  
		  try{
			  //EnderIO
			  if (java.lang.Class.forName("crazypants.enderio.power.IInternalPoweredItem").isInstance(item)) return true;
		  } catch(Exception e){
			  
		  }

	  }

	  return valid;
  }
}
