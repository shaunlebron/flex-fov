package mod.render360.coretransform.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.Shader;
import mod.render360.coretransform.gui.Slider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;

public abstract class RenderMethod {

	private final String vertexShader = "#version 130//\n /* The position of the vertex as two-dimensional vector */ in vec2 vertex; /* Write interpolated texture coordinate to fragment shader */ out vec2 texcoord; void main(void) { gl_Position = vec4(vertex, 0.0, 1.0); /* * Compute texture coordinate by simply * interval-mapping from [-1..+1] to [0..1] */ texcoord = vertex * 0.5 + vec2(0.5, 0.5); } ";
	
	/**
	 * Contains all render methods
	 */
	private static final RenderMethod[] renderMethods;
	
	protected static float quality = 1;
	protected static boolean resizeGui = false;
	
	private float yaw; //TODO remove
	private float pitch;
	private float prevYaw;
	private float prevPitch;
	
	private float rotateX;
	private float rotateY;
	public static float testZ;
	
	static {
		//Put all of the render methods here
		renderMethods = new RenderMethod[] {new Standard(), new Cubic(), new Hammer(), new Equirectangular(), new EquirectangularStatic()};
	}
	
	/**
	 * Cycles through all of the render methods
	 * @param index the current index
	 * @return the new index
	 */
	public static int getNextIndex(int index) {
		if (index >= renderMethods.length-1) {
			return 0;
		} else {
			return index + 1;
		}
	}
	
	/**
	 * Returns a render method from the given index
	 * @param index
	 * @return the render method
	 */
	public static RenderMethod getRenderMethod(int index) {
		return renderMethods[index];
	}
	
	/**
	 * @return the name to be displayed on the menu button
	 */
	public abstract String getName();
	
	public String getVertexShader() {
		return vertexShader;
	}
	
	public abstract String getFragmentShader();
	
	/**
	 * Render the world.
	 * Called between {@link net.minecraft.client.renderer.EntityRenderer#renderWorld(float, long) renderWorld}
	 * and {@link net.minecraft.client.renderer.EntityRenderer#renderWorldPass(int, float, long) renderWorldPass}
	 */
	public void renderWorld(EntityRenderer er, Minecraft mc, Framebuffer framebuffer, Shader shader,
			int[] framebufferTextures, float partialTicks, long finishTimeNano, int width, int height, float sizeIncrease) {
		//save the players state
		Entity player = mc.getRenderViewEntity();
		yaw = player.rotationYaw;
		pitch = player.rotationPitch;
		prevYaw = player.prevRotationYaw;
		prevPitch = player.prevRotationPitch;

		//clear the primary framebuffer
		mc.getFramebuffer().framebufferClear();
		//clear the secondary framebuffer
		framebuffer.framebufferClear();
		//bind the secondary framebuffer
		framebuffer.bindFramebuffer(false);

		//displayWidth and displayHeight are called during world rendering
		//set them to the secondary framebuffer dimensions
		mc.displayWidth = (int)(height*sizeIncrease);
		mc.displayHeight = (int)(height*sizeIncrease); //Must be square
		
		RenderUtil.partialWidth = mc.displayWidth; //TODO is this even needed?
		RenderUtil.partialHeight = mc.displayHeight; //TODO remove
		
		RenderUtil.render360 = true;

		renderFront(er, mc, partialTicks, finishTimeNano, player, framebufferTextures[0], yaw, pitch, prevYaw, prevPitch);
		if (getFOV() >= 90) {
			renderLeft(er, mc, partialTicks, finishTimeNano, player, framebufferTextures[2], yaw, pitch, prevYaw, prevPitch);
			renderRight(er, mc, partialTicks, finishTimeNano, player, framebufferTextures[3], yaw, pitch, prevYaw, prevPitch);
			renderTop(er, mc, partialTicks, finishTimeNano, player, framebufferTextures[4], yaw, pitch, prevYaw, prevPitch);
			renderBottom(er, mc, partialTicks, finishTimeNano, player, framebufferTextures[5], yaw, pitch, prevYaw, prevPitch);
			if (getFOV() >= 270) {
				renderBack(er, mc, partialTicks, finishTimeNano, player, framebufferTextures[1], yaw, pitch, prevYaw, prevPitch);
			}
		}
		
		//reset the players state
		player.rotationYaw = yaw;
		player.rotationPitch = pitch;
		player.prevRotationYaw = prevYaw;
		player.prevRotationPitch = prevPitch;
		
		//reset displayWidth and displayHeight to the primary framebuffer dimensions
		mc.displayWidth = width;
		mc.displayHeight = height;
		
		//reset viewport to full screen
		GlStateManager.viewport(0, 0, width, height);
		//bind primary framebuffer
		mc.getFramebuffer().bindFramebuffer(false);
		
		if (!getResizeGui() || mc.gameSettings.hideGUI) {
			GL20.glUseProgram(shader.getShaderProgram());
			int cursorUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "drawCursor");
			GL20.glUniform1i(cursorUniform, 0);
			runShader(er, mc, framebuffer, shader, framebufferTextures);
		}
	}
	
	protected void renderFront(EntityRenderer er, Minecraft mc, float partialTicks, long finishTimeNano,
			Entity player, int framebufferTexture, float yaw, float pitch, float prevYaw, float prevPitch) {
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTexture, 0);
		GlStateManager.bindTexture(0);
		//rotate the player and render
		er.renderWorldPass(2, partialTicks, finishTimeNano);
	}
	
	protected void renderLeft(EntityRenderer er, Minecraft mc, float partialTicks, long finishTimeNano,
			Entity player, int framebufferTexture, float yaw, float pitch, float prevYaw, float prevPitch) {
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTexture, 0);
		GlStateManager.bindTexture(0);
		player.rotationYaw = yaw - 90;
		player.prevRotationYaw = prevYaw - 90;
		player.rotationPitch = 0;
		player.prevRotationPitch = 0;
		RenderUtil.rotation = pitch;
		er.renderWorldPass(2, partialTicks, finishTimeNano);
		RenderUtil.rotation = 0;
	}
	
	protected void renderRight(EntityRenderer er, Minecraft mc, float partialTicks, long finishTimeNano,
			Entity player, int framebufferTexture, float yaw, float pitch, float prevYaw, float prevPitch) {
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTexture, 0);
		GlStateManager.bindTexture(0);
		player.rotationYaw = yaw + 90;
		player.prevRotationYaw = prevYaw + 90;
		player.rotationPitch = 0;
		player.prevRotationPitch = 0;
		RenderUtil.rotation = -pitch;
		er.renderWorldPass(2, partialTicks, finishTimeNano);
		RenderUtil.rotation = 0;
	}
	
	protected void renderTop(EntityRenderer er, Minecraft mc, float partialTicks, long finishTimeNano,
			Entity player, int framebufferTexture, float yaw, float pitch, float prevYaw, float prevPitch) {
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTexture, 0);
		GlStateManager.bindTexture(0);
		player.rotationYaw = yaw;
		player.prevRotationYaw = prevYaw;
		player.rotationPitch = pitch - 90;
		player.prevRotationPitch = prevPitch - 90;
		er.renderWorldPass(2, partialTicks, finishTimeNano);
	}
	
	protected void renderBottom(EntityRenderer er, Minecraft mc, float partialTicks, long finishTimeNano,
			Entity player, int framebufferTexture, float yaw, float pitch, float prevYaw, float prevPitch) {
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTexture, 0);
		GlStateManager.bindTexture(0);
		player.rotationYaw = yaw;
		player.prevRotationYaw = prevYaw;
		player.rotationPitch = pitch + 90;
		player.prevRotationPitch = prevPitch + 90;
		er.renderWorldPass(2, partialTicks, finishTimeNano);
	}
	
	protected void renderBack(EntityRenderer er, Minecraft mc, float partialTicks, long finishTimeNano,
			Entity player, int framebufferTexture, float yaw, float pitch, float prevYaw, float prevPitch) {
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTexture, 0);
		GlStateManager.bindTexture(0);
		player.rotationYaw = yaw + 180;
		player.prevRotationYaw = prevYaw + 180;
		player.rotationPitch = -pitch;
		player.prevRotationPitch = -prevPitch;
		er.renderWorldPass(2, partialTicks, finishTimeNano);
	}
	
	public void runShader(EntityRenderer er, Minecraft mc, Framebuffer framebuffer,
			Shader shader, int[] framebufferTextures) {
		//Use shader
		GL20.glUseProgram(shader.getShaderProgram());

		//Setup view
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(-1, 1, -1, 1, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		int texFrontUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "texFront");
		GL20.glUniform1i(texFrontUniform, 0);
		int texBackUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "texBack");
		GL20.glUniform1i(texBackUniform, 1);
		int texLeftUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "texLeft");
		GL20.glUniform1i(texLeftUniform, 2);
		int texRightUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "texRight");
		GL20.glUniform1i(texRightUniform, 3);
		int texTopUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "texTop");
		GL20.glUniform1i(texTopUniform, 4);
		int texBottomUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "texBottom");
		GL20.glUniform1i(texBottomUniform, 5);
		int fovUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "fovx");
		GL20.glUniform1f(fovUniform, getFOV());
		
		int backgroundUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "backgroundColor");
		float backgroundColor[] = getBackgroundColor();
		if (backgroundColor != null) {
			GL20.glUniform4f(backgroundUniform, backgroundColor[0], backgroundColor[1], backgroundColor[2], 1);
		} else {
			GL20.glUniform4f(backgroundUniform, 0, 0, 0, 1);
		}

		//Render from the secondary framebuffer to the primary framebuffer using the shader.
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, shader.getVbo());
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_BYTE, false, 0, 0L);
		for (int i = 0; i < framebufferTextures.length; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTextures[i]);
		}
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		//Reset view
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();

		//unbind textures
		for (int i = framebufferTextures.length-1; i >= 0; i--) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
		
		//Unbind shader
		GL20.glUseProgram(0);
	}
	
	public void addButtonsToGui(List<GuiButton> buttonList, int width, int height) {
		buttonList.add(new GuiButton(18105, width / 2 - 155, height / 6 + 24, 150, 20, "Resize Gui: " + (resizeGui ? "ON" : "OFF")));
		buttonList.add(new Slider(new Responder(), 18103, width / 2 + 5, height / 6 + 24, 150, 20, "Quality", 0.1f, 10f, quality, 0.1f, null));
	}
	
	public void onButtonPress(GuiButton button) {
		//Resize Gui
		if (button.id == 18105) {
			resizeGui = !resizeGui;
			button.displayString = "Resize Gui: " + (resizeGui ? "ON" : "OFF");
		}
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getPrevYaw() {
		return prevYaw;
	}
	
	public float getPrevPitch() {
		return prevPitch;
	}
	
	public float getRotateX() {
		return rotateX;
	}
	
	public float getRotateY() {
		return rotateY;
	}
	
	public float getFOV() {
		return 360;
	}
	
	public float getQuality() {
		return quality;
	}
	
	public boolean getResizeGui() {
		return resizeGui;
	}
	
	public float[] getBackgroundColor() {
		return null;
	}
	
	public class Responder implements GuiResponder {
		@Override
		public void setEntryValue(int id, boolean value) {
			
		}

		@Override
		public void setEntryValue(int id, float value) {
			//Quality
			if (id == 18103) {
				if (quality != value) {
					quality = value;
					RenderUtil.forceReload();
				}
			}
		}

		@Override
		public void setEntryValue(int id, String value) {
			
		}
	}
}
