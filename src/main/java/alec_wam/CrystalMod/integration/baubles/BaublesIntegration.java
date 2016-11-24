package alec_wam.CrystalMod.integration.baubles;

import alec_wam.CrystalMod.util.ItemStackTools;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

//Credit Goes to EnderIO
//https://github.com/SleepyTrousers/EnderIO/blob/1.10/src/main/java/crazypants/util/BaublesUtil.java

public class BaublesIntegration {

  public static enum WhoAmI {
    SPCLIENT, MPCLIENT, SPSERVER, MPSERVER, OTHER;
  
    public static BaublesIntegration.WhoAmI whoAmI(World world) {
      Side side = FMLCommonHandler.instance().getSide();
      if (side == Side.CLIENT) {
        if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
          if (world.isRemote) {
            return SPCLIENT;
          } else {
            return SPSERVER;
          }
        } else {
          return MPCLIENT;
        }
      } else if (side == Side.SERVER) {
        if (FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) {
          return MPSERVER;
        } else if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
          return SPSERVER;
        }
      }
      return OTHER;
    }
  }

  private static final BaublesIntegration instance = new BaublesIntegration();
  private static final boolean baublesLoaded;
  static {
    baublesLoaded = Loader.isModLoaded("Baubles");
  }

  private BaublesIntegration() {
  }

  public static BaublesIntegration instance() {
    return instance;
  }

  public boolean hasBaubles() {
    return baublesLoaded;
  }
  
  /*public IInventory getBaubles(EntityPlayer player) {
    return hasBaubles() ? getBaublesInvUnsafe(player) : null;
  }

  @SuppressWarnings("deprecation")
  private IInventory getBaublesInvUnsafe(EntityPlayer player) {
    return BaublesApi.getBaubles(player);
  }*/
  
  /**
   * Do NOT modify this inventory on the client side of a singleplayer game!
   * 
   * Wrap it in a WrapperInventory if you need to.
   */
  public IBaublesItemHandler getBaubles(EntityPlayer player){
	  return hasBaubles() ? getBaublesInvUnsafe(player) : null;
  }

  private IBaublesItemHandler getBaublesInvUnsafe(EntityPlayer player) {
	  return BaublesApi.getBaublesHandler(player);
  }
  
  public ItemStack getBauble(EntityPlayer player, BaubleType type) {
      
      final IBaublesItemHandler inv = getBaubles(player);
      
      for (final int slotId : type.getValidSlots())
          if (inv != null) {
              
              final ItemStack stack = inv.getStackInSlot(slotId);
              
              if (ItemStackTools.isValid(stack))
                  return stack;
          }
      
      return null;
  }
}
