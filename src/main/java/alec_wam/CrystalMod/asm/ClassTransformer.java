package alec_wam.CrystalMod.asm;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;

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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import alec_wam.CrystalMod.client.model.dynamic.CustomItemRendererHandler;

import com.google.common.collect.Sets;

@SuppressWarnings("unused")
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
			boolean patched2 = false;
			
			LabelNode l = new LabelNode(new Label());
			InsnList toInsert = new InsnList();

			toInsert.add(new FieldInsnNode(GETSTATIC, "alec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler", "instance", "Lalec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler;"));
			toInsert.add(new VarInsnNode(ALOAD, 1));
			toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, "alec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler", "overrideRender", "(Lnet/minecraft/item/ItemStack;)Z", false));
			toInsert.add(new JumpInsnNode(IFGT, l));
			toInsert.add(new InsnNode(RETURN));
			toInsert.add(l);
			patched = true;

			renderItem.instructions.insert(toInsert);
			

			
			/*for (int i = 0; i < renderItem.instructions.size(); i++)
			{
				AbstractInsnNode ain = renderItem.instructions.get(i);
				if (ain instanceof MethodInsnNode)
				{
					MethodInsnNode min = (MethodInsnNode) ain;

					if (Sets.newHashSet(ObfuscatedNames.RenderItem_renderItem_renderByItem).contains(min.name))
					{
						LabelNode l1 = new LabelNode(new Label());
						LabelNode l2 = new LabelNode(new Label());
						logger.log(Level.INFO, "- Inserting Custom Item Renderer");
						InsnList insertBefore = new InsnList();

						insertBefore.add(new FieldInsnNode(GETSTATIC, "alec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler", "instance", "Lalec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler;"));
						insertBefore.add(new VarInsnNode(ALOAD, 1));
						insertBefore.add(new MethodInsnNode(INVOKEVIRTUAL, "alec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler", "renderByItem", "(Lnet/minecraft/item/ItemStack;)Z", false));
						insertBefore.add(new JumpInsnNode(IFEQ, l2));
						insertBefore.add(new InsnNode(POP));
						insertBefore.add(new InsnNode(POP));
						insertBefore.add(new JumpInsnNode(GOTO, l1));
						insertBefore.add(l2);

						InsnList insertAfter = new InsnList();
						insertAfter.add(l1);

						renderItem.instructions.insertBefore(min, insertBefore);
						renderItem.instructions.insert(min, insertAfter);

						i += 8;
						patched2 = true;
					}
				}
			}*/
			
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

			toInsert.add(new FieldInsnNode(GETSTATIC, "alec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler", "instance", "Lalec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler;"));
			toInsert.add(new VarInsnNode(ALOAD, 3));
			toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, "alec_wam/CrystalMod/client/model/dynamic/CustomItemRendererHandler", "overrideRender", "(Lnet/minecraft/item/ItemStack;)Z", false));
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

	private byte[] patchDummyClass(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found Dummy Class: " + classNode.name);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
}
