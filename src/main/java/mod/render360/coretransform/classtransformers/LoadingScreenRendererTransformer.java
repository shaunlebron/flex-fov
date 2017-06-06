package mod.render360.coretransform.classtransformers;

import mod.render360.coretransform.classtransformers.name.ClassName;
import mod.render360.coretransform.classtransformers.name.MethodName;
import mod.render360.coretransform.classtransformers.name.Names;
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
	public ClassName getName() {
		return Names.LoadingScreenRenderer;
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformSetProgressAndMessage = new MethodTransformer() {

			@Override
			public MethodName getName() {
				return Names.LoadingScreenRenderer_setLoadingProgress;
			}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == FSTORE) {
						CLTLog.info("Found FSTORE in method " + getName().all());
						
						
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
								Names.LoadingScreenRenderer_mc.getFullName(obfuscated),
								Names.LoadingScreenRenderer_mc.getDesc(obfuscated))); //mc
						toInsert.add(new FieldInsnNode(GETFIELD, Type.getInternalName(Minecraft.class),
								Names.Minecraft_currentScreen.getFullName(obfuscated),
								Names.Minecraft_currentScreen.getDesc(obfuscated))); //currentScreen
						
						toInsert.add(new VarInsnNode(ALOAD, 0)); //this
						toInsert.add(new FieldInsnNode(GETFIELD, classNode.name,
								Names.LoadingScreenRenderer_framebuffer.getFullName(obfuscated),
								Names.LoadingScreenRenderer_framebuffer.getDesc(obfuscated))); //framebuffer
						toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(RenderMethod.class),
								"renderLoadingScreen", "(L" + Type.getInternalName(GuiScreen.class) +
								";L" + Type.getInternalName(Framebuffer.class) + ";)V", false));
						
						//else
						toInsert.add(new JumpInsnNode(GOTO, label2));
						toInsert.add(label1);

						//this.mc.guiScreen.drawBackground();
						toInsert.add(new VarInsnNode(ALOAD, 0)); //this
						toInsert.add(new FieldInsnNode(GETFIELD, classNode.name,
								Names.LoadingScreenRenderer_mc.getFullName(obfuscated),
								Names.LoadingScreenRenderer_mc.getDesc(obfuscated))); //mc
						toInsert.add(new FieldInsnNode(GETFIELD, Type.getInternalName(Minecraft.class),
								Names.Minecraft_currentScreen.getFullName(obfuscated),
								Names.Minecraft_currentScreen.getDesc(obfuscated))); //currentScreen
						toInsert.add(new InsnNode(ICONST_0)); //0
						toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(GuiScreen.class),
								Names.GuiScreen_drawBackground.getFullName(obfuscated),
								Names.GuiScreen_drawBackground.getDesc(obfuscated), false)); //drawBackground
						
						toInsert.add(label2);

						method.instructions.insert(instruction, toInsert);
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformSetProgressAndMessage};
	}

}
