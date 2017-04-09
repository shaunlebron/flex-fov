package mod.render360.coretransform.classtransformers.particle;

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

public class ParticleSweepAttackTransformer extends ParticleTransformer {

	@Override
	public String getObfuscatedClassName() {
		return "bnw";
	}

	@Override
	public String getClassName() {
		return "net.minecraft.client.particle.ParticleSweepAttack";
	}
	
	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformRenderParticle = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "renderParticle";}
			public String getDescName() {
				if (CoreLoader.isObfuscated) {
					return "(Lbpw;Lsm;FFFFFF)V";
				} else {
					return "(L" + Type.getInternalName(VertexBuffer.class) +
							";L" + Type.getInternalName(Entity.class) + ";FFFFFF)V";
				}
			}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == BIPUSH &&
							instruction.getNext().getOpcode() == GETSTATIC) {
						CLTLog.info("Found BIPUSH in method " + getMethodName());
						
						instruction = instruction.getPrevious();
						
						transformParticle(classNode, method, instruction, 15);
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformRenderParticle};
	}
}
