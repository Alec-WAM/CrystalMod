package alec_wam.CrystalMod.enchantment;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantmentDoubleJump extends Enchantment {

	private static final @Nonnull String NAME = "doubleJump";
	
	public EnchantmentDoubleJump() {
		super(Enchantment.Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
		setName(NAME);
		setRegistryName(CrystalMod.resourceL(NAME));
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
    {
        return enchantmentLevel * 10;
    }

    @Override
	public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }
	
	@Override
	public int getMaxLevel()
    {
        return 1;
    }
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void playerTickClient(PlayerTickEvent event) {
		if (event.side == Side.SERVER) return;
		EntityPlayer player = event.player;
		if(FMLClientHandler.instance().getClient().gameSettings.keyBindJump.isPressed()){
			if(!player.onGround){
				ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
				if(ItemStackTools.isValid(boots)){
					if(EnchantmentHelper.getEnchantmentLevel(ModEnchantments.jump, boots) > 0){
						CrystalModNetwork.sendToServer(new PacketEntityMessage(player, "#Jump#"));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerTickServer(PlayerTickEvent event) {
		if (event.side == Side.CLIENT) return;
		EntityPlayer player = event.player;
		if(player.onGround){
			ExtendedPlayer eplayer = ExtendedPlayerProvider.getExtendedPlayer(player);
			if(eplayer !=null && eplayer.hasJumped){
				eplayer.hasJumped = false;
			}
		}
	}

}
