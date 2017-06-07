package mod.render360.coretransform.classtransformers.particle;

import mod.render360.coretransform.classtransformers.name.ClassName;
import mod.render360.coretransform.classtransformers.name.MethodName;
import mod.render360.coretransform.classtransformers.name.Names;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mod.render360.coretransform.CLTLog;
import mod.render360.coretransform.CoreLoader;
import mod.render360.coretransform.classtransformers.ClassTransformer;
import mod.render360.coretransform.classtransformers.ClassTransformer.MethodTransformer;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;

import static org.objectweb.asm.Opcodes.*;

public class BarrierTransformer extends ParticleTransformer {

	@Override
	public ClassName getName() {
		return Names.Barrier;
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
					if (instruction.getOpcode() == ISHR) {
						CLTLog.info("Found ISHR in method " + getName().all());
						
						for (int i = 0; i < 12; i++) {
							instruction = instruction.getNext();
						}
						
						transformParticle(classNode, method, instruction, 14);
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformRenderParticle};
	}

}
