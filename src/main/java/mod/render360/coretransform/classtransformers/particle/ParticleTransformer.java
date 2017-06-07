package mod.render360.coretransform.classtransformers.particle;

import mod.render360.coretransform.classtransformers.name.ClassName;
import mod.render360.coretransform.classtransformers.name.MethodName;
import mod.render360.coretransform.classtransformers.name.Names;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mod.render360.coretransform.CLTLog;
import mod.render360.coretransform.CoreLoader;
import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.classtransformers.ClassTransformer;
import mod.render360.coretransform.classtransformers.ClassTransformer.MethodTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;

import static org.objectweb.asm.Opcodes.*;

public class ParticleTransformer extends ClassTransformer {
	
	private static ClassTransformer[] transformers;
	
	static {
		transformers = new ClassTransformer[] {new ParticleDiggingTransformer(), new ParticleBreakingTransformer(), new BarrierTransformer(), new ParticleExplosionLargeTransformer(), new ParticleSweepAttackTransformer()};
	}

	@Override
	public ClassName getName() {
		return Names.Particle;
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformRenderParticle = new MethodTransformer() {

			@Override
			public MethodName getName() {
				return Names.Particle_renderParticle;
			}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == ANEWARRAY) {
						CLTLog.info("Found ANEWARRAY in method " + getName().all());
						
						instruction = instruction.getPrevious();
						
						transformParticle(classNode, method, instruction, 14);
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformRenderParticle};
	}
	
	protected void transformParticle(ClassNode classNode, MethodNode method, AbstractInsnNode instruction, int firstInsn) {
		InsnList toInsert = new InsnList();
		
		toInsert.add(new VarInsnNode(FLOAD, 4));
		toInsert.add(new VarInsnNode(FLOAD, 5));
		toInsert.add(new VarInsnNode(FLOAD, 6));
		toInsert.add(new VarInsnNode(FLOAD, 7));
		toInsert.add(new VarInsnNode(FLOAD, 8));
		toInsert.add(new VarInsnNode(FLOAD, firstInsn));
		toInsert.add(new VarInsnNode(FLOAD, firstInsn+1));
		toInsert.add(new VarInsnNode(FLOAD, firstInsn+2));
		toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(ParticleTransformer.class),
				"rotateParticle", "(FFFFFFFF)V", false));
		
		toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(ParticleTransformer.class),
				"rotX", "F"));
		toInsert.add(new VarInsnNode(FSTORE, 4));
		toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(ParticleTransformer.class),
				"rotZ", "F"));
		toInsert.add(new VarInsnNode(FSTORE, 5));
		toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(ParticleTransformer.class),
				"rotYZ", "F"));
		toInsert.add(new VarInsnNode(FSTORE, 6));
		toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(ParticleTransformer.class),
				"rotXY", "F"));
		toInsert.add(new VarInsnNode(FSTORE, 7));
		toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(ParticleTransformer.class),
				"rotXZ", "F"));
		toInsert.add(new VarInsnNode(FSTORE, 8));
		
		method.instructions.insertBefore(instruction, toInsert);
	}
	
	public static float rotX;
	public static float rotZ;
	public static float rotYZ;
	public static float rotXY;
	public static float rotXZ;
	public static void rotateParticle(float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ, float posX, float posY, float posZ) {
		posY -= Minecraft.getMinecraft().thePlayer.eyeHeight;
		
		float hDist = (float) (Math.sqrt(posZ*posZ + posX*posX));
		float dist = (float) (Math.sqrt(posZ*posZ + posY*posY + posX*posX));
		
		rotX = posZ/hDist;
		rotZ = 1-Math.abs(posY/dist);
		rotYZ = -posX/hDist;
		rotXY = -posY/dist * posX/hDist;
		rotXZ = -posY/dist * posZ/hDist;
	}
	
	public static ClassTransformer[] getParticleTransformers() {
		return transformers;
	}
}
