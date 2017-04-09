package mod.render360.coretransform.classtransformers.particle;

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
	public String getObfuscatedClassName() {
		return "bnx";
	}

	@Override
	public String getClassName() {
		return "net.minecraft.client.particle.Barrier";
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
					if (instruction.getOpcode() == ISHR) {
						CLTLog.info("Found ISHR in method " + getMethodName());
						
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
