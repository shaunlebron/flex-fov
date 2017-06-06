package mod.render360.coretransform.classtransformers.particle;

import mod.render360.coretransform.classtransformers.name.ClassName;
import mod.render360.coretransform.classtransformers.name.MethodName;
import mod.render360.coretransform.classtransformers.name.Names;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import mod.render360.coretransform.CLTLog;
import mod.render360.coretransform.CoreLoader;
import mod.render360.coretransform.classtransformers.ClassTransformer.MethodTransformer;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;

import static org.objectweb.asm.Opcodes.*;

public class ParticleBreakingTransformer extends ParticleTransformer {

	@Override
	public ClassName getName() {
		return Names.ParticleBreaking;
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
						
						instruction = instruction.getPrevious().getPrevious();
						
						transformParticle(classNode, method, instruction, 14);
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformRenderParticle};
	}
}
