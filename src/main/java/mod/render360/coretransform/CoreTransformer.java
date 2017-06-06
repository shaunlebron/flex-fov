package mod.render360.coretransform;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mod.render360.coretransform.classtransformers.ClassTransformer;
import mod.render360.coretransform.classtransformers.ClassTransformer.MethodTransformer;
import mod.render360.coretransform.classtransformers.EntityRendererTransformer;
import mod.render360.coretransform.classtransformers.MinecraftTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.launchwrapper.IClassTransformer;

import static org.objectweb.asm.Opcodes.*;

import java.io.PrintStream;

public class CoreTransformer implements IClassTransformer {
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		//get a list of all class transformers
		ClassTransformer[] classTransformers = ClassTransformer.getClassTransformers();
		
		//for each class transformer
		for (ClassTransformer classTransformer : classTransformers) {
			
			//if the class transformer should modify this class
			if (name.equals(classTransformer.getName().getName())) {
				
				CLTLog.info(String.format("Class: %s", name));
				boolean obfuscated = CoreLoader.isObfuscated;

				try {
					ClassNode classNode = new ClassNode();
					ClassReader classReader = new ClassReader(basicClass);
					classReader.accept(classNode, 0);

					//get a list of all method transformers for this class
					MethodTransformer[] mts = classTransformer.getMethodTransformers();

					//transform methods
					for (MethodNode method : classNode.methods) {
						for (MethodTransformer mt : mts) {
							if (method.name.equals(mt.getName().getShortName(obfuscated)) && method.desc.equals(mt.getName().getDesc(obfuscated))) {
								mt.transform(classNode, method, obfuscated);
							}
						}
					}

					ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
					classNode.accept(classWriter);
					return classWriter.toByteArray();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				//The class transformer has been matched to the class, no need to check other transformers
				break;
			}
		}
		return basicClass;
	}
}
