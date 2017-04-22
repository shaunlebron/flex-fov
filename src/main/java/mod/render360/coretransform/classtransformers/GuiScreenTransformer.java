package mod.render360.coretransform.classtransformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import mod.render360.coretransform.CLTLog;
import mod.render360.coretransform.CoreLoader;
import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.render.Equirectangular;
import mod.render360.coretransform.render.Panini;
import mod.render360.coretransform.render.RenderMethod;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;

public class GuiScreenTransformer extends ClassTransformer {

	@Override
	public String getObfuscatedClassName() {
		return "bhm";
	}

	@Override
	public String getClassName() {
		return "net.minecraft.client.gui.GuiScreen";
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {

		MethodTransformer transformDrawWorldBackground = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "d_" : "drawWorldBackground";}
			public String getDescName() {return "(I)V";}

			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);

				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == ICONST_0) {
						CLTLog.info("Found ICONST_0 in method " + getMethodName());

						instruction = instruction.getPrevious();

						InsnList toInsert = new InsnList();
						LabelNode label = new LabelNode();

						//if RenderUtil.renderMethod.getResizeGui() {
						//this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
						//}
						toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(RenderUtil.class),
								"renderMethod", "L" + Type.getInternalName(RenderMethod.class) + ";")); //renderMethod
						toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(RenderMethod.class),
								"getResizeGui", "()Z", false)); //getResizeGui()
						toInsert.add(new JumpInsnNode(IFNE, label));

						method.instructions.insertBefore(instruction, toInsert);

						for (int i = 0; i < 10; i++) {
							instruction = instruction.getNext();
						}

						method.instructions.insertBefore(instruction, label);

						break;
					}
				}

			}
		};

		MethodTransformer transformDrawBackground = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "c" : "drawBackground";}
			public String getDescName() {return "(I)V";}

			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);

				InsnList toInsert = new InsnList();
				LabelNode label1 = new LabelNode();
				LabelNode label2 = new LabelNode();

				//if (!RenderUtil.renderMethod.replaceLoadingScreen())
				toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(RenderUtil.class),
						"renderMethod", "L" + Type.getInternalName(RenderMethod.class) + ";")); //renderMethod
				toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(RenderMethod.class),
						"replaceLoadingScreen", "()Z", false)); //replaceLoadingScreen()
				toInsert.add(new JumpInsnNode(IFNE, label1));

				method.instructions.insert(toInsert);

				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == RETURN) {
						CLTLog.info("Found RETURN in method " + getMethodName());

						//else {
						//RenderUtil.renderMethod.renderLoadingScreen(this)
						//}
						toInsert.add(new JumpInsnNode(GOTO, label2));
						toInsert.add(label1);

						toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(RenderUtil.class),
								"renderMethod", "L" + Type.getInternalName(RenderMethod.class) + ";")); //renderMwthod
						toInsert.add(new VarInsnNode(ALOAD, 0)); //this
						toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(RenderMethod.class),
								"renderLoadingScreen", "(L" + classNode.name + ";)V", false)); //renderLoadingScreen()

						toInsert.add(label2);

						method.instructions.insertBefore(instruction, toInsert);

						break;
					}
				}
			}
		};

		return new MethodTransformer[] {transformDrawWorldBackground, transformDrawBackground};
	}

}
