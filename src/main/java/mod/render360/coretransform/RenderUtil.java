package mod.render360.coretransform;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import mod.render360.coretransform.render.Standard;
import mod.render360.coretransform.render.RenderMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;

public class RenderUtil {

	/**Enables or disables 360 degree rendering.*/
	public static boolean render360 = true;

	public static float fov;

	/**The current render method.*/
	public static RenderMethod renderMethod = new Standard();
	/**The index of the current render method.*/
	public static int index = 0; //TODO remove

	/**Used to check if the screen was resized.*/
	private static int width = 0;
	/**Used to check if the screen was resized.*/
	private static int height = 0;
	/**Used for rendering multiple times*/ //TODO remove
	public static int partialWidth = 0;
	public static int partialHeight = 0;

	public static float rotation = 0;

	/**The 360 degree shader.*/
	private static Shader shader = null;
	/**The secondary framebuffer used to render the world in 360 degrees.*/
	private static Framebuffer framebuffer = null;
	private static int[] framebufferTextures = new int[6];

	/**Reload the framebuffer and shader.*/
	private static boolean forceReload = false;

	/**Reload the framebuffer and shader.*/
	public static void forceReload() {
		forceReload = true;
	}

	/**
	 * Checks if shader exists before creating it.
	 */
	private static void createShader() {
		if (shader == null) {
			shader = new Shader();
			shader.createShaderProgram(renderMethod);
		} else {
			CLTLog.info("Attemped to re-create existing shader");
		}
	}

	/**
	 * Checks to see if the shader exists before deleting it.
	 */
	private static void deleteShader() {
		if (shader != null) {
			shader.deleteShaderProgram();
			shader = null;
		} else {
			CLTLog.info("Attemped to delete non-existent shader");
		}
	}

	/**
	 * Create or delete shader and secondary framebuffer.<br>
	 * Called from asm modified code on world load and unload.
	 * @param worldClient used to detect if the world is being loaded or unloaded.
	 */
	public static void onWorldLoad(WorldClient worldClient) {
		if (worldClient != null) {
			if (framebuffer == null) {
				//The actual numbers don't matter, they are reset later.
				framebuffer = new Framebuffer((int)(Display.getHeight()*renderMethod.getQuality()),
						(int)(Display.getHeight()*renderMethod.getQuality()), true);
				//create 6 new textures
				for (int i = 0; i < framebufferTextures.length; i++) {
					framebufferTextures[i] = TextureUtil.glGenTextures();
					GlStateManager.bindTexture(framebufferTextures[i]);
					GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8,
							framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight,
							0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);
					GlStateManager.bindTexture(framebufferTextures[i]);
		            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				}
				GlStateManager.bindTexture(0);
			} else {
				CLTLog.info("Attempted to recreate existing framebuffer");
			}
			createShader();
		} else {
			deleteShader();
			if (framebuffer != null) {
				//delete textures
				for (int i = 0; i < framebufferTextures.length; i++) {
					TextureUtil.deleteTexture(framebufferTextures[i]);
					framebufferTextures[i] = -1;
				}
				framebuffer.deleteFramebuffer();
				framebuffer = null;
			} else {
				CLTLog.info("Attempted to delete non-existant framebuffer");
			}
		}
	}

	/**
	 * Render the world. Called from asm modified code.
	 * @param er
	 * @param mc
	 * @param partialTicks
	 * @param finishTimeNano
	 */
	public static void setupRenderWorld(EntityRenderer er, Minecraft mc, float partialTicks, long finishTimeNano) {

		//reload the framebuffer and shader
		if (forceReload || width != mc.displayWidth || height != mc.displayHeight) {
			forceReload = false;
			width = mc.displayWidth;
			height = mc.displayHeight;
			//delete textures
			for (int i = 0; i < framebufferTextures.length; i++) {
				TextureUtil.deleteTexture(framebufferTextures[i]);
				framebufferTextures[i] = -1;
			}
			//recreate framebuffer with the new size
			framebuffer.deleteFramebuffer();
			//height is listed twice for an aspect ratio of 1:1
			framebuffer = new Framebuffer((int)(height*renderMethod.getQuality()), (int)(height*renderMethod.getQuality()), true);
			deleteShader();
			createShader();
			//create 6 new textures
			for (int i = 0; i < framebufferTextures.length; i++) {
				framebufferTextures[i] = TextureUtil.glGenTextures();
				GlStateManager.bindTexture(framebufferTextures[i]);
				GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8,
						framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight,
						0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);
				GlStateManager.bindTexture(framebufferTextures[i]);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
	            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
			}
			GlStateManager.bindTexture(0);
		}

		renderMethod.renderWorld(er, mc, framebuffer, shader, framebufferTextures, partialTicks, finishTimeNano, width, height, renderMethod.getQuality());
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#setupCameraTransform(float, int) setupCameraTransform}
	 */
	public static void rotateCamera() {
		if (renderMethod.coordFrame != null) {
			GlStateManager.multMatrix(renderMethod.coordFrame);
		}
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#setupCameraTransform(float, int) setupCameraTransform}
	 */
	public static void rotatePlayer() {
		// to prevent bad culling, we have to do this after orientCamera
		Entity player = renderMethod.player;
		float deltaYaw = renderMethod.playerDeltaYaw;
		float deltaPitch = renderMethod.playerDeltaPitch;
		if (player != null) {
			player.rotationYaw += deltaYaw;
			player.prevRotationYaw += deltaYaw;
			player.rotationPitch += deltaPitch;
			player.prevRotationPitch += deltaPitch;
		}
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#updateCameraAndRender(float, long) updateCameraAndRender}
	 */
	public static void renderGuiStart() {
		if (renderMethod.getResizeGui()) {
			Minecraft mc = Minecraft.getMinecraft();
			framebuffer.bindFramebuffer(false);
			GlStateManager.viewport(0, 0, (int) (mc.displayHeight*renderMethod.getQuality()), (int) (mc.displayHeight*renderMethod.getQuality()));
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTextures[0], 0);
			GlStateManager.bindTexture(0);
		}
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#updateCameraAndRender(float, long) updateCameraAndRender}
	 */
	public static void renderGuiEnd() {
		if (renderMethod.getResizeGui()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (!Mouse.isGrabbed()) {
				GL20.glUseProgram(shader.getShaderProgram());
				int angleUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "cursorPos");
				GL20.glUniform2f(angleUniform, Mouse.getX()/(float)mc.displayWidth, Mouse.getY()/(float)mc.displayHeight);
				int cursorUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "drawCursor");
				GL20.glUniform1i(cursorUniform, 1);
				GL20.glUseProgram(0);
			} else {
				GL20.glUseProgram(shader.getShaderProgram());
				int cursorUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "drawCursor");
				GL20.glUniform1i(cursorUniform, 0);
				GL20.glUseProgram(0);
			}
			mc.getFramebuffer().bindFramebuffer(false);
			GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
			//if not in menu or inventory
			if (mc.currentScreen == null) {
				renderMethod.runShader(mc, shader, framebufferTextures);
			}
		}
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#updateCameraAndRender(float, long) updateCameraAndRender}
	 */
	public static void renderGuiStart2() {
		if (renderMethod.getResizeGui()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.theWorld != null) {
				framebuffer.bindFramebuffer(false);
				GlStateManager.viewport(0, 0, (int) (mc.displayHeight*renderMethod.getQuality()), (int) (mc.displayHeight*renderMethod.getQuality()));
			}
		}
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#updateCameraAndRender(float, long) updateCameraAndRender}
	 */
	public static void renderGuiEnd2() {
		if (renderMethod.getResizeGui()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.theWorld != null) {
				mc.getFramebuffer().bindFramebuffer(false);
				GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
				renderMethod.runShader(mc, shader, framebufferTextures);
			}
		}
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#drawNameplate(float, long) drawNameplate}
	 */
	public static float setViewerYaw(float x, float z) {
		float yaw = (float) -(Math.atan(x/z)*180/Math.PI);
		if (z < 0) {
			yaw += 180;
		}
		return yaw;
	}

	/**
	 * Called from asm modified code
	 * {@link net.minecraft.client.renderer.EntityRenderer#drawNameplate(float, long) drawNameplate}
	 */
	public static float setViewerPitch(float x, float y, float z) {
		float distance = (float) (Math.sqrt(x*x + z*z));
		float pitch = (float) -(Math.atan((y-Minecraft.getMinecraft().getRenderViewEntity().height)/distance)*180/Math.PI);
		return pitch;
	}
}
