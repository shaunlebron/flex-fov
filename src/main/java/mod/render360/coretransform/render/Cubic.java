package mod.render360.coretransform.render;

import java.util.List;

import org.lwjgl.opengl.GL20;

import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.Shader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;

public class Cubic extends RenderMethod {

	private final String fragmentShader = "#version 130//\n\n\n/* This comes interpolated from the vertex shader */\nin vec2 texcoord;\n\n/* The 6 textures to be rendered */\nuniform sampler2D texFront;\nuniform sampler2D texBack;\nuniform sampler2D texLeft;\nuniform sampler2D texRight;\nuniform sampler2D texTop;\nuniform sampler2D texBottom;\n\nuniform vec2 pixelOffset[4];\n\nuniform vec4 backgroundColor;\n\nuniform vec2 cursorPos;\n\nuniform bool drawCursor;\n\nout vec4 color;\n\nvoid main(void) {\n	//Anti-aliasing\n	vec4 colorN[4];\n	\n	for (int loop = 0; loop < 4; loop++) {\n		vec2 coord = texcoord+pixelOffset[loop];\n		\n		if (coord.y >= 0.333333333 && coord.y < 0.666666666) {\n			//Left\n\n			if (coord.x < 0.25) {\n				colorN[loop] = vec4(texture(texLeft, vec2(coord.x*4, coord.y*3-1)).rgb, 1);\n			}\n			//Front\n\n			else if (coord.x < 0.5) {\n				colorN[loop] = vec4(texture(texFront, vec2(coord.x*4-1, coord.y*3-1)).rgb, 1);\n			}\n			//Right\n\n			else if (coord.x < 0.75) {\n				colorN[loop] = vec4(texture(texRight, vec2(coord.x*4-2, coord.y*3-1)).rgb, 1);\n			}\n			//Back\n\n			else {\n				colorN[loop] = vec4(texture(texBack, vec2(coord.x*4-3, coord.y*3-1)).rgb, 1);\n			}\n		}\n		else if (coord.x < 0.5 && coord.x >= 0.25) {\n			//Bottom\n\n			if (coord.y < 0.333333333) {\n				colorN[loop] = vec4(texture(texBottom, vec2(coord.x*4-1, coord.y*3)).rgb, 1);\n			}\n			//Top\n\n			else {\n				colorN[loop] = vec4(texture(texTop, vec2(coord.x*4-1, coord.y*3-2)).rgb, 1);\n			}\n		}\n		else {\n			colorN[loop] = backgroundColor;\n		}\n		\n		if (drawCursor) {\n			if (coord.x + 0.0015 >= (cursorPos.x+1)/4 && coord.x - 0.0015 < (cursorPos.x+1)/4 &&\n				coord.y + 0.002 >= (cursorPos.y+1)/3 && coord.y - 0.002 < (cursorPos.y+1)/3) {\n					colorN[loop] = vec4(1, 1, 1, 1);\n			}\n		}\n	}\n	\n	color = mix(mix(colorN[0], colorN[1], 0.5), mix(colorN[2], colorN[3], 0.5), 0.5);\n}";
	
	private boolean skyBackground = false;
	
	@Override
	public String getName() {
		return "Cubic";
	}
	
	@Override
	public String getFragmentShader() {
		return this.fragmentShader;
	}
	
	@Override
	public void addButtonsToGui(List<GuiButton> buttonList, int width, int height) {
		super.addButtonsToGui(buttonList, width, height);
		buttonList.add(new GuiButton(18103, width / 2 - 155, height / 6 + 72, 150, 20, "Background Color: " + (skyBackground ? "Sky" : "Black")));
	}
	
	@Override
	public void onButtonPress(GuiButton button) {
		super.onButtonPress(button);
		//Background Color
		if (button.id == 18103) {
			skyBackground = !skyBackground;
			button.displayString = "Background Color: " + (skyBackground ? "Sky" : "Black");
		}
	}
	
	@Override
	public float[] getBackgroundColor() {
		if (skyBackground) {
			EntityRenderer er = Minecraft.getMinecraft().entityRenderer;
			return new float[] {er.fogColorRed, er.fogColorGreen, er.fogColorBlue};
		} else {
			return null;
		}
	}
	
	@Override
	public boolean replaceLoadingScreen() {
		return true;
	}
}
