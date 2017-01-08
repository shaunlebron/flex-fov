package mod.render360.coretransform.classtransformers;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mod.render360.coretransform.CLTLog;
import mod.render360.coretransform.CoreLoader;
import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.classtransformers.ClassTransformer.MethodTransformer;
import net.minecraft.client.multiplayer.WorldClient;

public class MinecraftTransformer extends ClassTransformer {
	
	protected MinecraftTransformer() {
		//Prevents this class from accidentally being called
	}
	
	@Override
	public String getObfuscatedClassName() {return "beq";}
	
	@Override
	public String getClassName() {return "net.minecraft.client.Minecraft";}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		MethodTransformer loadWorldTransformer = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "loadWorld";}
			public String getDescName() {return "(L" + (CoreLoader.isObfuscated ? "bno" : Type.getInternalName(WorldClient.class)) + ";Ljava/lang/String;)V";}
			
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				CLTLog.info("begining at start of method " + getMethodName());
				
				InsnList toInsert = new InsnList();
				toInsert.add(new VarInsnNode(ALOAD, 1)); //worldClientIn
				toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(RenderUtil.class),
						"onWorldLoad", "(L" + Type.getInternalName(WorldClient.class) + ";)V", false));
				method.instructions.insertBefore(method.instructions.getFirst(), toInsert);
			}
		};
		
		return new MethodTransformer[] {loadWorldTransformer};
	}

}
