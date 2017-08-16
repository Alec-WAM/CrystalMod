package alec_wam.CrystalMod.util.tool;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

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
			  "bow", "whip"	
	  };

	  if (ItemStackTools.isValid(stack) && stack.getMaxStackSize() == 1)
	  {
		  Item item = stack.getItem();
		  String name = stack.getUnlocalizedName().toLowerCase();
		  // Vanilla
		  if (isSword(stack) || item instanceof ItemBow)
		  {
			  return true;
		  }

		  // Just for extra compatibility and/or security and/or less annoyance
		  for (String toolName : validWeaponNames)
		  {
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
  
  public static boolean isMeleeWeapon(ItemStack stack){
	  if (ItemStackTools.isValid(stack) && stack.getMaxStackSize() == 1)
	  {
		  Item item = stack.getItem();
		  // Vanilla
		  if (isSword(stack) || isAxe(stack))
		  {
			  return true;
		  }
		  //And also this because I'm awesome
		  try
		  {
			  // Tinker's Construct
			  if (item.getClass().getName().contains("tconstruct.items.tools.melee")) return true;
		  } catch (Exception oops)
		  {
			  //  oops.printStackTrace();
		  }
	  }
	  return false;
  }
  
  public static boolean isSword(ItemStack stack){
	  String[] validWeaponNames = {
			  "sword", "dagger", "sabre", "rapier", "cutlass"	
	  };

	  if (ItemStackTools.isValid(stack) && stack.getMaxStackSize() == 1)
	  {
		  Item item = stack.getItem();
		  String name = stack.getUnlocalizedName().toLowerCase();
		  // Vanilla
		  if (item instanceof ItemSword)
		  {
			  return true;
		  }

		  // Just for extra compatibility and/or security and/or less annoyance
		  for (String toolName : validWeaponNames)
		  {
			  if (name.contains(toolName)) return true;
		  }
	  }
	  return false;
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
			  if (name.contains(toolName)) return true;
		  }

		  for (String toolName : invalidToolNames)
		  {
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
  
  public static boolean isToolEffective(ItemStack stack, IBlockState state) {
	  // check material
	  for(String type : stack.getItem().getToolClasses(stack)) {
		  if(state.getBlock().isToolEffective(type, state)) {
			  return true;
		  }
	  }

	  return stack.getItem().canHarvestBlock(state, stack);
  }
  
  //Item Raytrace method
  public static RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
  {
      float f = playerIn.rotationPitch;
      float f1 = playerIn.rotationYaw;
      double d0 = playerIn.posX;
      double d1 = playerIn.posY + playerIn.getEyeHeight();
      double d2 = playerIn.posZ;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
      float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
      float f4 = -MathHelper.cos(-f * 0.017453292F);
      float f5 = MathHelper.sin(-f * 0.017453292F);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d3 = 5.0D;
      if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP)
      {
          d3 = ((net.minecraft.entity.player.EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
      }
      Vec3d vec3d1 = vec3d.addVector(f6 * d3, f5 * d3, f7 * d3);
      return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
  }
  
  //https://github.com/SlimeKnights/TinkersConstruct/blob/master/src/main/java/slimeknights/tconstruct/library/utils/ToolHelper.java
  public static ImmutableList<BlockPos> calcAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth) {
	  return calcAOEBlocks(stack, world, player, origin, width, height, depth, -1);
  }

  public static ImmutableList<BlockPos> calcAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, int distance) {
	  IBlockState state = world.getBlockState(origin);

	  if(!isToolEffective(stack, state)) {
		  return ImmutableList.of();
	  }

	  if(world.isAirBlock(origin) || state.getMaterial() == Material.AIR) {
		  return ImmutableList.of();
	  }

	  RayTraceResult mop = rayTrace(world, player, true);
	  if(mop == null || !origin.equals(mop.getBlockPos())) {
		  mop = rayTrace(world, player, false);
		  if(mop == null || !origin.equals(mop.getBlockPos())) {
			  return ImmutableList.of();
		  }
	  }

	  int x, y, z;
	  BlockPos start = origin;
	  switch(mop.sideHit) {
	  case DOWN:
	  case UP:
		  // x y depends on the angle we look?
		  Vec3i vec = player.getHorizontalFacing().getDirectionVec();
		  x = vec.getX() * height + vec.getZ() * width;
		  y = mop.sideHit.getAxisDirection().getOffset() * -depth;
		  z = vec.getX() * width + vec.getZ() * height;
		  start = start.add(-x / 2, 0, -z / 2);
		  if(x % 2 == 0) {
			  if(x > 0 && mop.hitVec.xCoord - mop.getBlockPos().getX() > 0.5d) {
				  start = start.add(1, 0, 0);
			  }
			  else if(x < 0 && mop.hitVec.xCoord - mop.getBlockPos().getX() < 0.5d) {
				  start = start.add(-1, 0, 0);
			  }
		  }
		  if(z % 2 == 0) {
			  if(z > 0 && mop.hitVec.zCoord - mop.getBlockPos().getZ() > 0.5d) {
				  start = start.add(0, 0, 1);
			  }
			  else if(z < 0 && mop.hitVec.zCoord - mop.getBlockPos().getZ() < 0.5d) {
				  start = start.add(0, 0, -1);
			  }
		  }
		  break;
	  case NORTH:
	  case SOUTH:
		  x = width;
		  y = height;
		  z = mop.sideHit.getAxisDirection().getOffset() * -depth;
		  start = start.add(-x / 2, -y / 2, 0);
		  if(x % 2 == 0 && mop.hitVec.xCoord - mop.getBlockPos().getX() > 0.5d) {
			  start = start.add(1, 0, 0);
		  }
		  if(y % 2 == 0 && mop.hitVec.yCoord - mop.getBlockPos().getY() > 0.5d) {
			  start = start.add(0, 1, 0);
		  }
		  break;
	  case WEST:
	  case EAST:
		  x = mop.sideHit.getAxisDirection().getOffset() * -depth;
		  y = height;
		  z = width;
		  start = start.add(-0, -y / 2, -z / 2);
		  if(y % 2 == 0 && mop.hitVec.yCoord - mop.getBlockPos().getY() > 0.5d) {
			  start = start.add(0, 1, 0);
		  }
		  if(z % 2 == 0 && mop.hitVec.zCoord - mop.getBlockPos().getZ() > 0.5d) {
			  start = start.add(0, 0, 1);
		  }
		  break;
	  default:
		  x = y = z = 0;
	  }

	  ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
	  for(int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
		  for(int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
			  for(int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
				  // don't add the origin block
				  if(xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
					  continue;
				  }
				  if(distance > 0 && MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY()) + MathHelper.abs(
						  zp - origin.getZ()) > distance) {
					  continue;
				  }
				  BlockPos pos = new BlockPos(xp, yp, zp);
				  if(isToolEffective(stack, world.getBlockState(pos))) {
					  builder.add(pos);
				  }
			  }
		  }
	  }

	  return builder.build();
  }

  public static ItemStack getBestTool(EntityPlayer player, IBlockState blockState) {
	  if(ItemStackTools.isValid(player.getHeldItemOffhand())){
		  if(ToolUtil.isToolEffective(player.getHeldItemOffhand(), blockState)){
			  return player.getHeldItemOffhand();
		  }
	  }
	  return player.getHeldItemMainhand();
  }
}
