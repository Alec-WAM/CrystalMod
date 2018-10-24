package alec_wam.CrystalMod.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.IRETURN;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Sets;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer
{
	Logger logger = LogManager.getLogger("CrystalModCore");

	final String asmHandler = "com/alec_wam/CrystalMod/handler/AsmHandler";

	public ClassTransformer()
	{
		logger.log(Level.INFO, "Starting Class Transformation");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals("net.minecraft.client.renderer.RenderItem"))
		{
			return patchRenderItem(basicClass);
		}
		if (transformedName.equals("net.minecraft.entity.item.EntityItem"))
		{
			return patchEntityItem(basicClass);
		}
		return basicClass;
	}

	private byte[] patchRenderItem(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.INFO, "Found RenderItem Class: " + classNode.name);

		MethodNode renderItem = null;
		MethodNode renderModel = null;

		for (MethodNode mn : classNode.methods)
		{
			if (Sets.newHashSet(ObfuscatedNames.RenderItem_renderItem).contains(mn.name) && mn.desc.equals("(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"))
			{
				renderItem = mn;
			}
			if ((mn.name.contains("renderModel") || mn.name.contains("func_175035_a") || mn.name.contains("func_175036_a") || mn.name.contains("func_175045_a")) && mn.desc.equals("(Lnet/minecraft/client/renderer/block/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V"))
			{
				renderModel = mn;
			}
		}
		
		if (renderItem != null)
		{
			logger.log(Level.INFO, "- Found renderItem (1/2) (" + renderItem.desc + ")");
			logger.log(Level.INFO, "- Inserting Custom Item Renderer in renderItem");
			boolean patched = false;
			LabelNode l = new LabelNode(new Label());
			InsnList toInsert = new InsnList();

			toInsert.add(new FieldInsnNode(GETSTATIC, "alec_wam/CrystalMod/asm/ASMMethods", "instance", "Lalec_wam/CrystalMod/asm/ASMMethods;"));
			toInsert.add(new VarInsnNode(ALOAD, 1));
			toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, "alec_wam/CrystalMod/asm/ASMMethods", "overrideRender", "(Lnet/minecraft/item/ItemStack;)Z", false));
			toInsert.add(new JumpInsnNode(IFGT, l));
			toInsert.add(new InsnNode(RETURN));
			toInsert.add(l);
			patched = true;

			renderItem.instructions.insert(toInsert);
			
			if(!patched){
				throw new RuntimeException("Unable to patch Render Item");
			}
		} else {
			throw new RuntimeException("Unable to find Render Item");
		}
		
		if (renderModel != null)
		{
			logger.log(Level.INFO, "- Found renderModel (2/2) (" + renderModel.desc + ")");
			logger.log(Level.INFO, "- Inserting Custom Item Renderer in renderModel");
			boolean patched = false;
			
			LabelNode l = new LabelNode(new Label());
			InsnList toInsert = new InsnList();

			toInsert.add(new FieldInsnNode(GETSTATIC, "alec_wam/CrystalMod/asm/ASMMethods", "instance", "Lalec_wam/CrystalMod/asm/ASMMethods;"));
			toInsert.add(new VarInsnNode(ALOAD, 3));
			toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, "alec_wam/CrystalMod/asm/ASMMethods", "overrideRender", "(Lnet/minecraft/item/ItemStack;)Z", false));
			toInsert.add(new JumpInsnNode(IFGT, l));
			toInsert.add(new InsnNode(RETURN));
			toInsert.add(l);
			patched = true;

			renderModel.instructions.insert(toInsert);
			
			if(!patched){
				throw new RuntimeException("Unable to patch Render Item Model");
			}
		} else {
			throw new RuntimeException("Unable to find Render Model");
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	private byte[] patchEntityItem(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		MethodNode attackEntityFrom = null;

		for (MethodNode mn : classNode.methods)
		{
			if (Sets.newHashSet(ObfuscatedNames.EntityItem_attackEntityFrom).contains(mn.name) && mn.desc.equals("(Lnet/minecraft/util/DamageSource;F)Z"))
			{
				attackEntityFrom = mn;
			}
		}
		
		if (attackEntityFrom != null)
		{
			logger.log(Level.INFO, "- Found attackEntityFrom (" + attackEntityFrom.desc + ")");
			logger.log(Level.INFO, "- Inserting Damage Handler");
			boolean patched = false;
			InsnList toInsert = new InsnList();
			LabelNode initLisitener = new LabelNode();
			toInsert.add(new FieldInsnNode(GETSTATIC, "alec_wam/CrystalMod/asm/ASMMethods", "instance", "Lalec_wam/CrystalMod/asm/ASMMethods;"));
			toInsert.add(new VarInsnNode(ALOAD, 0));
			toInsert.add(new VarInsnNode(ALOAD, 1));
			toInsert.add(new MethodInsnNode(INVOKESTATIC, "alec_wam/CrystalMod/asm/ASMMethods", "onEntityItemAttacked", "(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/DamageSource;)Z", false));
			toInsert.add(new JumpInsnNode(IFGT, initLisitener));
			toInsert.add(new InsnNode(Opcodes.ICONST_0));
			toInsert.add(new InsnNode(IRETURN));
			toInsert.add(initLisitener);
			attackEntityFrom.instructions.insert(toInsert);
			patched = true;
			if(!patched){
				throw new RuntimeException("Unable to patch EntityItem attackEntityFrom");
			}
		} else {
			throw new RuntimeException("Unable to find EntityItem attackEntityFrom");
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	public static boolean applyOnNode(MethodNode method, AbstractInsnNode filter) {
		Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

		boolean didAny = false;
		while(iterator.hasNext()) {
			AbstractInsnNode anode = iterator.next();
			if(filter.equals(anode)) {
				didAny = true;
				if(method.equals(anode))
					break;
			}
		}

		return didAny;
	}
}
