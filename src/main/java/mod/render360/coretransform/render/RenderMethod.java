package mod.render360.coretransform.render;

import java.io.IOException;
import java.util.List;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;
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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
	protected static boolean rubix = false;
	protected static boolean split = false;

	private float yaw; //TODO remove
	private float pitch;
	private float prevYaw;
	private float prevPitch;

	public FloatBuffer coordFrame;

	public Entity player;
	public float playerDeltaYaw;
	public float playerDeltaPitch;

	static {
		//Put all of the render methods here
		renderMethods = new RenderMethod[] {
			new Standard(),
			new Flex(),
		};
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
	 * Called from {@link net.minecraft.client.gui.GuiScreen#drawWorldBackground(int) drawWorldBackground()}
	 * @param guiScreen
	 */
	public void renderLoadingScreen(GuiScreen guiScreen) {
		renderLoadingScreen(guiScreen, Minecraft.getMinecraft().getFramebuffer());
	}

	public void renderLoadingScreen(GuiScreen guiScreen, Framebuffer framebufferIn) {
		//Prevents null pointer exception when moving between dimensions
		if (guiScreen == null) {
			guiScreen = new GuiScreen(){};
			guiScreen.width = framebufferIn.framebufferTextureWidth;
			guiScreen.height = framebufferIn.framebufferTextureHeight;
		}
		Minecraft mc = Minecraft.getMinecraft();
		Framebuffer framebuffer = new Framebuffer((int)(Display.getHeight()*getQuality()), (int)(Display.getHeight()*getQuality()), true);

		framebuffer.bindFramebuffer(false);
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture, 0);
		GlStateManager.bindTexture(0);

		//replacement for guiScreen.drawBackground(0);
		GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        mc.getTextureManager().bindTexture(guiScreen.OPTIONS_BACKGROUND);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        vertexbuffer.pos(0.0D, (double)guiScreen.height, 0.0D).tex(0.0D, (double)((float)guiScreen.height / 32.0F)).color(64, 64, 64, 255).endVertex();
        vertexbuffer.pos((double)guiScreen.width, (double)guiScreen.height, 0.0D).tex((double)((float)guiScreen.width / 32.0F), (double)((float)guiScreen.height / 32.0F)).color(64, 64, 64, 255).endVertex();
        vertexbuffer.pos((double)guiScreen.width, 0.0D, 0.0D).tex((double)((float)guiScreen.width / 32.0F), (double)0).color(64, 64, 64, 255).endVertex();
        vertexbuffer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
        //

		framebufferIn.bindFramebuffer(false);
		GlStateManager.viewport(0, 0, framebufferIn.framebufferTextureWidth, framebufferIn.framebufferTextureHeight);

		Shader shader = new Shader();
		shader.createShaderProgram(this);

		// clone frame buffers to simulate 3d view in loading screen?
		int[] frameBufferTextures = new int[] {
			framebuffer.framebufferTexture,
			framebuffer.framebufferTexture,
			framebuffer.framebufferTexture,
			framebuffer.framebufferTexture,
			framebuffer.framebufferTexture,
			framebuffer.framebufferTexture
		};
		runShader(mc, shader, frameBufferTextures);

		//Unbind shader
		GL20.glUseProgram(0);
		shader.deleteShaderProgram();
		framebuffer.deleteFramebuffer();
		framebufferIn.bindFramebuffer(false);
	}

	/**
	 * Render the world.
	 * Called between {@link net.minecraft.client.renderer.EntityRenderer#renderWorld(float, long) renderWorld}
	 * and {@link net.minecraft.client.renderer.EntityRenderer#renderWorldPass(int, float, long) renderWorldPass}
	 */
	public void renderWorld(EntityRenderer er, Minecraft mc, Framebuffer framebuffer, Shader shader,
			int[] framebufferTextures, float partialTicks, long finishTimeNano, int width, int height, float sizeIncrease) {
		//save the players state
		player = mc.getRenderViewEntity();
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

		for (int i=0; i<6; i++) {
			// set camera/player rotation state (read by hooks at the correct moment)
			coordFrame = Cube.coordFrames[i];

			// the forward vector is the opposite direction of the z-axis
			float x = -coordFrame.get(2);
			float y = -coordFrame.get(6);
			float z = -coordFrame.get(10);

			// calculate the delta yaw/pitch to point the player
			float lon = (float)Math.atan2(x, -z);
			float lat = (float)Math.atan2(y, Math.sqrt(x*x+z*z));
			playerDeltaYaw = lon;
			playerDeltaPitch = lat;

			// render to texture
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, framebufferTextures[i], 0);
			GlStateManager.bindTexture(0);
			er.renderWorldPass(2, partialTicks, finishTimeNano);

			// reset player view
			player.rotationYaw = yaw;
			player.rotationPitch = pitch;
			player.prevRotationYaw = prevYaw;
			player.prevRotationPitch = prevPitch;
		}

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
			runShader(mc, shader, framebufferTextures);
		}
	}

	public void runShader(Minecraft mc, Shader shader, int[] framebufferTextures) {
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

		//Anti-aliasing
		int pixelOffsetUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "pixelOffset[0]");
		GL20.glUniform2f(pixelOffsetUniform, -0.25f/mc.displayWidth, -0.25f/mc.displayHeight);
		pixelOffsetUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "pixelOffset[1]");
		GL20.glUniform2f(pixelOffsetUniform, 0.25f/mc.displayWidth, -0.25f/mc.displayHeight);
		pixelOffsetUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "pixelOffset[2]");
		GL20.glUniform2f(pixelOffsetUniform, -0.25f/mc.displayWidth, 0.25f/mc.displayHeight);
		pixelOffsetUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "pixelOffset[3]");
		GL20.glUniform2f(pixelOffsetUniform, 0.25f/mc.displayWidth, 0.25f/mc.displayHeight);

		for (int i=0; i<6; i++) {
			int textureUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "textures["+i+"]");
			GL20.glUniform1i(textureUniform, i);
			int coordFrameUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "coordFrames["+i+"]");
			GL20.glUniformMatrix4(coordFrameUniform, false, Cube.coordFrames[i]);
			int textureFovUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "textureFovs["+i+"]");
			GL20.glUniform1f(textureFovUniform, Cube.fovs[i]);
		}
		int textureCountUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "textureCount");
		GL20.glUniform1i(textureCountUniform, Cube.count);

		int fovUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "fovx");
		GL20.glUniform1f(fovUniform, getFOV());
		int aspectUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "aspect");
		GL20.glUniform1f(aspectUniform, (float)Display.getWidth() / (float)Display.getHeight());
		int pitchUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "pitch");
		GL20.glUniform1f(pitchUniform, pitch);
		int rubixUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "rubix");
		GL20.glUniform1i(rubixUniform, rubix ? 1 : 0);
		int splitUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "split");
		GL20.glUniform1i(splitUniform, split ? 1 : 0);

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
		buttonList.add(new GuiButton(18101, width / 2 - 155, height / 6 + 48, 150, 20, "Resize Gui: " + (resizeGui ? "ON" : "OFF")));
		buttonList.add(new Slider(new Responder(), 18102, width / 2 + 5, height / 6 + 48, 150, 20, "Quality", 0.1f, 5f, quality, 0.1f, null));
		buttonList.add(new GuiButton(18103, width / 2 - 155, height / 6 + 78, 150, 20, "Rubix: " + (rubix ? "ON" : "OFF")));
		buttonList.add(new GuiButton(18105, width / 2 - 155, height / 6 + 108, 150, 20, "Split Compare: " + (split ? "ON" : "OFF")));
	}

	public void onButtonPress(GuiButton button) {
		//Resize Gui
		if (button.id == 18101) {
			resizeGui = !resizeGui;
			button.displayString = "Resize Gui: " + (resizeGui ? "ON" : "OFF");
		} else if (button.id == 18103) {
			rubix = !rubix;
			button.displayString = "Rubix: " + (rubix ? "ON" : "OFF");
		} else if (button.id == 18105) {
			split = !split;
			button.displayString = "Split Compare: " + (split ? "ON" : "OFF");
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

	public float getFOV() {
		return 360;
	}

	public float getQuality() {
		return quality;
	}

	public boolean getResizeGui() {
		return resizeGui;
	}

	public boolean replaceLoadingScreen() {
		return false;
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
			if (id == 18102) {
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
