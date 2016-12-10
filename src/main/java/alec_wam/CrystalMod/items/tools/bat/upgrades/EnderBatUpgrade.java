package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class EnderBatUpgrade extends BatUpgrade {

	public EnderBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("ender"), IPL, MIPL);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData value) {
		float val = getValue(value);
		if(val > 0){
			//Only Teleport Main Target
			ModLogger.info("Worked: "+teleportRandomly(entities.get(0)));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.DARK_PURPLE;
		String itemName = ENDER_EYE.getDisplayName();
		//Allways
		if(infoType == -1){
			list.add(formatting+(BatHelper.localizeName(this))+TextFormatting.RESET);
		}
		//Shift
		if(infoType == 0){
			list.add(formatting+(BatHelper.localizeName(this))+": "+getLevelInfo(data)+TextFormatting.RESET);
		}
		//Ctrl
		if(infoType == 1){
			list.add(formatting+(itemName)+": "+getBasicLevelInfo(amount)+TextFormatting.RESET);
		}
	}
	
	protected static boolean teleportRandomly(Entity ent)
    {
		if(ent == null)return false;
        double d0 = ent.posX + (ent.worldObj.rand.nextDouble() - 0.5D) * 32.0D;
        double d1 = ent.posY + (double)(ent.worldObj.rand.nextInt(64) - 32);
        double d2 = ent.posZ + (ent.worldObj.rand.nextDouble() - 0.5D) * 32.0D;
        return teleportTo(ent, d0, d1, d2);
    }
    
    /**
     * Teleport like enderman
     */
    protected static boolean teleportTo(Entity ent, double par1, double par3, double par5)
    {
        double d3 = ent.posX;
        double d4 = ent.posY;
        double d5 = ent.posZ;
        ent.posX =par1;
        ent.posY = par3;
        ent.posZ = par5;
        boolean flag = false;
        BlockPos entityPos = new BlockPos(ent);

        if (ent.worldObj.isBlockLoaded(entityPos))
        {
            boolean flag1 = false;

            while (!flag1 && entityPos.getY() > 0)
            {
                IBlockState state = ent.worldObj.getBlockState(entityPos.down());

                if (state.getMaterial().blocksMovement())
                {
                    flag1 = true;
                }
                else
                {
                    --ent.posY;
                    entityPos.add(0, -1, 0);
                }
            }

            if (flag1)
            {
            	ent.setPosition(ent.posX, ent.posY, ent.posZ);

                if (ent.worldObj.getCollisionBoxes(ent, ent.getEntityBoundingBox()).isEmpty())
                {
                    flag = true;
                }
            }
        }

        if (!flag)
        {
        	ent.setPosition(d3, d4, d5);
            return false;
        }
        else
        {
            short short1 = 128;

            for (int l = 0; l < short1; ++l)
            {
                double d6 = (double)l / ((double)short1 - 1.0D);
                float f = (ent.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
                float f1 = (ent.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
                float f2 = (ent.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (ent.posX - d3) * d6 + (ent.worldObj.rand.nextDouble() - 0.5D) * (double)ent.width * 2.0D;
                double d8 = d4 + (ent.posY - d4) * d6 + ent.worldObj.rand.nextDouble() * (double)ent.height;
                double d9 = d5 + (ent.posZ - d5) * d6 + (ent.worldObj.rand.nextDouble() - 0.5D) * (double)ent.width * 2.0D;
                ent.worldObj.spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, (double)f, (double)f1, (double)f2);
            }

            ent.worldObj.playSound(null, d3, d4, d5, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            ent.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
	@Override
	public void render(ItemStack bat, UpgradeData data) {
		ItemStack enderpearl = ENDER_EYE;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.51, 2.36, 0.35);
		GlStateManager.scale(0.6, 0.6, 0.6);
		RenderUtil.renderItem(enderpearl, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.49, 2.36, 0.65);
		GlStateManager.rotate(180, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		RenderUtil.renderItem(enderpearl, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.65, 2.36, 0.51);
		GlStateManager.rotate(90*3, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		RenderUtil.renderItem(enderpearl, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.35, 2.36, 0.49);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		RenderUtil.renderItem(enderpearl, TransformType.GROUND);
		GlStateManager.popMatrix();
	}

    public final ItemStack ENDER_EYE = new ItemStack(Items.ENDER_EYE);
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, ENDER_EYE)){
			return 1;
		}
		return 0;
	}
	
	@Override
	public boolean canBeAdded(ItemStack bat, List<IBatUpgrade> upgrades, UpgradeData dataToBeAdded){
		IBatUpgrade upgrade = BatHelper.getUpgrade(CrystalMod.resourceL("poison"));
		return upgrade == null ? true : !upgrades.contains(upgrade);
	}

}
