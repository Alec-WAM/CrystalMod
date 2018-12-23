package alec_wam.CrystalMod.entities.minions.warrior;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.util.GuiButtonCustomIcon;
import alec_wam.CrystalMod.entities.minions.EnumMovementState;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiMinionWarrior extends GuiContainer {
	public final ResourceLocation INVENTORY = new ResourceLocation("crystalmod:textures/gui/entity/warrior_inventory.png");
	/** The old x position of the mouse pointer */
    private float oldMouseX;
    /** The old y position of the mouse pointer */
    private float oldMouseY;
    
    private GuiButtonCustomIcon buttonMove, buttonMethod, buttonTrigger, buttonTarget;
    
    public EntityMinionWarrior warrior;

    public GuiMinionWarrior(EntityPlayer player, EntityMinionWarrior warrior) {
		super(new ContainerMinionWarrior(player, warrior));
		this.warrior = warrior;
	}

	@Override
	public void initGui(){
		super.initGui();
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("ID", mc.player.getEntityId());
		CrystalModNetwork.sendToServer(new PacketEntityMessage(warrior, "REQUEST_COMBAT_SYNC", nbt));
		updateIcons();
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		MinionAICombat combatAI = warrior.getAIManager().getAI(MinionAICombat.class);
		if(button == buttonMove){
			EnumMovementState next = warrior.getMovementState();
			if(warrior.getMovementState() == EnumMovementState.STAY){
				next = EnumMovementState.GUARD;
			} else if(warrior.getMovementState() == EnumMovementState.GUARD){
				next = EnumMovementState.FOLLOW;
			} else if(warrior.getMovementState() == EnumMovementState.FOLLOW){
				next = EnumMovementState.STAY;
			}
			warrior.setMovementState(next);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("ID", next.getId());
			CrystalModNetwork.sendToServer(new PacketEntityMessage(warrior, "MOVEMENT_SET", nbt));
			updateIcons();
		}
		if(button == buttonMethod){
			final int next = combatAI.getNextMethodBehavior();
			combatAI.setMethodBehavior(next);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("ID", next);
			CrystalModNetwork.sendToServer(new PacketEntityMessage(warrior, "ATTACK_METHOD_SET", nbt));
			updateIcons();
		}
		if(button == buttonTrigger){
			final int next = combatAI.getNextTriggerBehavior();
			combatAI.setTriggerBehavior(next);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("ID", next);
			CrystalModNetwork.sendToServer(new PacketEntityMessage(warrior, "ATTACK_TRIGGER_SET", nbt));
			updateIcons();
		}
		if(button == buttonTarget){
			final int next = combatAI.getNextTargetBehavior();
			combatAI.setTargetBehavior(next);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("ID", next);
			CrystalModNetwork.sendToServer(new PacketEntityMessage(warrior, "ATTACK_TARGET_SET", nbt));
			updateIcons();
		}
	}
	
	public void updateIcons(){
		int x = guiLeft + 83;
		int y = guiTop + 7;
		int moveX = 192;
		int methodX = 208;
		int triggerX = 224;
		int targetX = 240;

		this.buttonList.clear();
		
		int moveIndex = 0;
		if(warrior.getMovementState() == EnumMovementState.STAY){
			moveIndex = 1;
		}
		if(warrior.getMovementState() == EnumMovementState.FOLLOW){
			moveIndex = 2;
		}
		buttonMove = new GuiButtonCustomIcon(0, x, y, 1.0F, moveX, moveIndex * 16, INVENTORY);
		this.buttonList.add(buttonMove);
		
		MinionAICombat combatAI = warrior.getAIManager().getAI(MinionAICombat.class);
		int methodIndex = 0;
		if(combatAI.getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_ONLY){
			methodIndex = 1;
		}
		if(combatAI.getMethodBehavior() == EnumCombatBehaviors.METHOD_RANGED_ONLY){
			methodIndex = 2;
		}
		if(combatAI.getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_AND_RANGED){
			methodIndex = 3;
		}
		buttonMethod = new GuiButtonCustomIcon(1, x + 20, y, 1.0F, methodX, methodIndex * 16, INVENTORY);
		this.buttonList.add(buttonMethod);
		
		
		int triggerIndex = 0;
		if(combatAI.getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_PLAYER_DEAL_DAMAGE){
			triggerIndex = 1;
		}
		if(combatAI.getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_PLAYER_TAKE_DAMAGE){
			triggerIndex = 2;
		}
		buttonTrigger = new GuiButtonCustomIcon(2, x + 40, y, 1.0F, triggerX, triggerIndex * 16, INVENTORY);
		this.buttonList.add(buttonTrigger);
		
		int targetIndex = 0;
		if(combatAI.getTargetBehavior() == EnumCombatBehaviors.TARGET_HOSTILE_MOBS){
			targetIndex = 1;
		}
		if(combatAI.getTargetBehavior() == EnumCombatBehaviors.TARGET_PASSIVE_MOBS){
			targetIndex = 2;
		}
		buttonTarget = new GuiButtonCustomIcon(3, x + 60, y, 1.0F, targetX, targetIndex * 16, INVENTORY);
		this.buttonList.add(buttonTarget);
	}
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
	@Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        GuiInventory.drawEntityOnScreen(i + 51, j + 74, 50, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, warrior);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ICONS);
        
        int x = 83;
        int y = 35;
        int armor = warrior.getTotalArmorValue();
        for (int k3 = 0; k3 < 10; ++k3)
        {
            if (armor > 0)
            {
                int l3 = x + k3 * 8;

                if (k3 * 2 + 1 < armor)
                {
                    this.drawTexturedModalRect(l3, y, 34, 9, 9, 9);
                }

                if (k3 * 2 + 1 == armor)
                {
                    this.drawTexturedModalRect(l3, y, 25, 9, 9, 9);
                }

                if (k3 * 2 + 1 > armor)
                {
                    this.drawTexturedModalRect(l3, y, 16, 9, 9, 9);
                }
            }
        }
        
        IAttributeInstance iattributeinstance = warrior.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        float maxhealth = (float)iattributeinstance.getAttributeValue();
        int health = MathHelper.ceil(warrior.getHealth());
        int l1 = MathHelper.ceil((maxhealth) / 2.0F / 10.0F);
        int i2 = Math.max(10 - (l1 - 2), 3);
        for (int j5 = MathHelper.ceil((maxhealth) / 2.0F) - 1; j5 >= 0; --j5)
        {
            int k5 = 16;

            if (warrior.isPotionActive(MobEffects.POISON))
            {
                k5 += 36;
            }
            else if (warrior.isPotionActive(MobEffects.WITHER))
            {
                k5 += 72;
            }

            int i4 = 0;

            int j4 = MathHelper.ceil((float)(j5 + 1) / 10.0F) - 1;
            int k4 = x + j5 % 10 * 8;
            int healthY = (y + 10) - j4 * i2;

            if (health <= 4)
            {
            	healthY += warrior.getRNG().nextInt(2);
            }

            int i5 = 0;

            this.drawTexturedModalRect(k4, healthY, 16 + i4 * 9, 9 * i5, 9, 9);
            
            if (j5 * 2 + 1 < health)
            {
            	this.drawTexturedModalRect(k4, healthY, k5 + 36, 9 * i5, 9, 9);
            }

            if (j5 * 2 + 1 == health)
            {
            	this.drawTexturedModalRect(k4, healthY, k5 + 45, 9 * i5, 9, 9);
            }
        }
        
        int mX = mouseX;
        int mY = mouseY;
        MinionAICombat combatAI = warrior.getAIManager().getAI(MinionAICombat.class);
		if(isPointInRegion(buttonMove.xPosition - guiLeft, buttonMove.yPosition - guiTop, 19, 19, mX, mY)){
        	List<String> lines = Lists.newArrayList();
        	lines.add(Lang.localize("gui.warrior.tooltip.move." + warrior.getMovementState().name().toLowerCase()));
        	drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
        }
        if(isPointInRegion(buttonMethod.xPosition - guiLeft, buttonMethod.yPosition - guiTop, 19, 19, mX, mY)){
        	List<String> lines = Lists.newArrayList();
        	lines.add(Lang.localize("gui.warrior.tooltip." + combatAI.getMethodBehavior().name().toLowerCase()));
        	drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
        }
        if(isPointInRegion(buttonTrigger.xPosition - guiLeft, buttonTrigger.yPosition - guiTop, 19, 19, mX, mY)){
        	List<String> lines = Lists.newArrayList();
        	lines.add(Lang.localize("gui.warrior.tooltip." + combatAI.getTriggerBehavior().name().toLowerCase()));
        	drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
        }
        if(isPointInRegion(buttonTarget.xPosition - guiLeft, buttonTarget.yPosition - guiTop, 19, 19, mX, mY)){
        	List<String> lines = Lists.newArrayList();
        	lines.add(Lang.localize("gui.warrior.tooltip." + combatAI.getTargetBehavior().name().toLowerCase()));
        	drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
        }
    }
}
