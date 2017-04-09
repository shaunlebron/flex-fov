package mod.render360.coretransform.classtransformers;

import org.objectweb.asm.Type;
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

import mod.render360.coretransform.CLTLog;
import mod.render360.coretransform.CoreLoader;
import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.render.RenderMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.shader.Framebuffer;

import static org.objectweb.asm.Opcodes.*;

public class LoadingScreenRendererTransformer extends ClassTransformer {

	@Override
	public String getObfuscatedClassName() {
		return "bet";
	}

	@Override
	public String getClassName() {
		return "net.minecraft.client.LoadingScreenRenderer";
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformResetProgressAndMessage = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "setLoadingProgress";}
			public String getDescName() {return "(I)V";}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == FSTORE) {
						CLTLog.info("Found FSTORE in method " + getMethodName());
						
						
						instruction = instruction.getNext();
						
						//replace float f = 32.0F;
						//...
						//tessellator.draw();
						//with guiScreen.drawBackground()
						for (int i = 0; i < 93; i++) {
							method.instructions.remove(instruction.getNext());
						}
						
						//if (RenderUtil.renderMethod.replaceLoadingScreen()) {
						//RenderUtil.renderMethod.renderLoadingScreen(this.mc.guiScreen, framebuffer);
						//} else {
						//this.mc.guiScreen.drawBackground();
						//}

						InsnList toInsert = new InsnList();
						LabelNode label1 = new LabelNode();
						LabelNode label2 = new LabelNode();
						
						//if (RenderUtil.renderMethod.replaceLoadingScreen())
						toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(RenderUtil.class),
								"renderMethod", "L" + Type.getInternalName(RenderMethod.class) + ";")); //renderMethod
						toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(RenderMethod.class),
								"replaceLoadingScreen", "()Z", false)); //replaceLoadingScreen()
						toInsert.add(new JumpInsnNode(IFEQ, label1));
						
						//RenderUtil.renderMethod.renderLoadingScreen(this.mc.guiScreen, framebuffer);
						toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(RenderUtil.class),
								"renderMethod", "L" + Type.getInternalName(RenderMethod.class) + ";")); //renderMethod
						
						toInsert.add(new VarInsnNode(ALOAD, 0)); //this
						toInsert.add(new FieldInsnNode(GETFIELD, classNode.name,
								obfuscated ? "field_73725_b" : "mc",
										"L" + Type.getInternalName(Minecraft.class) + ";")); //mc
						toInsert.add(new FieldInsnNode(GETFIELD, Type.getInternalName(Minecraft.class),
								obfuscated ? "field_71462_r" : "currentScreen",
										"L" + Type.getInternalName(GuiScreen.class) + ";")); //currentScreen
						
						toInsert.add(new VarInsnNode(ALOAD, 0)); //this
						toInsert.add(new FieldInsnNode(GETFIELD, classNode.name,
								obfuscated ? "field_146588_g" : "framebuffer",
								"L" + Type.getInternalName(Framebuffer.class) + ";")); //framebuffer
						toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(RenderMethod.class),
								"renderLoadingScreen", "(L" + Type.getInternalName(GuiScreen.class) +
								";L" + Type.getInternalName(Framebuffer.class) + ";)V", false));
						
						//else
						toInsert.add(new JumpInsnNode(GOTO, label2));
						toInsert.add(label1);

						//this.mc.guiScreen.drawBackground();
						toInsert.add(new VarInsnNode(ALOAD, 0)); //this
						toInsert.add(new FieldInsnNode(GETFIELD, classNode.name,
								obfuscated ? "field_73725_b" : "mc",
										"L" + Type.getInternalName(Minecraft.class) + ";")); //mc
						toInsert.add(new FieldInsnNode(GETFIELD, Type.getInternalName(Minecraft.class),
								obfuscated ? "field_71462_r" : "currentScreen",
										"L" + Type.getInternalName(GuiScreen.class) + ";")); //currentScreen
						toInsert.add(new InsnNode(ICONST_0)); //0
						toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(GuiScreen.class),
								obfuscated ? "func_146278_c" : "drawBackground", "(I)V", false)); //drawBackground
						
						toInsert.add(label2);

						method.instructions.insert(instruction, toInsert);
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformResetProgressAndMessage};
	}

}
