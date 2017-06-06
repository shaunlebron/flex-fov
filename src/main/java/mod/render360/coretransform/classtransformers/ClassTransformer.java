package mod.render360.coretransform.classtransformers;

import mod.render360.coretransform.classtransformers.name.ClassName;
import mod.render360.coretransform.classtransformers.name.MethodName;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import mod.render360.coretransform.classtransformers.particle.BarrierTransformer;
import mod.render360.coretransform.classtransformers.particle.ParticleExplosionLargeTransformer;
import mod.render360.coretransform.classtransformers.particle.ParticleTransformer;

/**
 * Holds all of the class transformers.
 *
 */
public abstract class ClassTransformer {

	private static ClassTransformer[] transformers;
	
	static {
		//Put all of the class transformers here
		ClassTransformer[] classTransformers = new ClassTransformer[] {new MinecraftTransformer(), new GuiScreenTransformer(), new LoadingScreenRendererTransformer(), new EntityRendererTransformer(), new ParticleTransformer(), new GuiOptionsTransformer()};
		
		transformers = ArrayUtils.addAll(classTransformers, ParticleTransformer.getParticleTransformers());
	}
	
	//Template for a method transformer
	public static abstract class MethodTransformer {
		public abstract MethodName getName();
		public abstract void transform(ClassNode classNode, MethodNode method, boolean obfuscated);
	}

	/**
	 * @return the name of the class
	 */
	public abstract ClassName getName();
	
	/**
	 * @return an array containing all method transformers for this class transformer
	 */
	public abstract MethodTransformer[] getMethodTransformers();
	
	/**
	 * @return an array containing all class transformers
	 */
	public static ClassTransformer[] getClassTransformers() {
		return transformers;
	}
}
