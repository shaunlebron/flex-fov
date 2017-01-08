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

	private final String fragmentShader = "#version 130//\n /* This comes interpolated from the vertex shader */ in vec2 texcoord; /* The 6 textures to be rendered */ uniform sampler2D texFront; uniform sampler2D texBack; uniform sampler2D texLeft; uniform sampler2D texRight; uniform sampler2D texTop; uniform sampler2D texBottom; //fovx\n uniform float fovx; uniform vec4 backgroundColor; uniform vec2 cursorPos; uniform bool drawCursor; out vec4 color; void main(void) { if (texcoord.y >= 0.333333333 && texcoord.y < 0.666666666) { //Left\n if (texcoord.x < 0.25) { color = vec4(texture(texLeft, vec2(texcoord.x*4, texcoord.y*3-1)).rgb, 1); } //Front\n else if (texcoord.x < 0.5) { color = vec4(texture(texFront, vec2(texcoord.x*4-1, texcoord.y*3-1)).rgb, 1); } //Right\n else if (texcoord.x < 0.75) { color = vec4(texture(texRight, vec2(texcoord.x*4-2, texcoord.y*3-1)).rgb, 1); } //Back\n else { color = vec4(texture(texBack, vec2(texcoord.x*4-3, texcoord.y*3-1)).rgb, 1); } } else if (texcoord.x < 0.5 && texcoord.x >= 0.25) { //Bottom\n if (texcoord.y < 0.333333333) { color = vec4(texture(texBottom, vec2(texcoord.x*4-1, texcoord.y*3)).rgb, 1); } //Top\n else { color = vec4(texture(texTop, vec2(texcoord.x*4-1, texcoord.y*3-2)).rgb, 1); } } else { color = backgroundColor; } if (drawCursor) { if (texcoord.x + 0.0015 >= (cursorPos.x+1)/4 && texcoord.x - 0.0015 < (cursorPos.x+1)/4 && texcoord.y + 0.002 >= (cursorPos.y+1)/3 && texcoord.y - 0.002 < (cursorPos.y+1)/3) { color = vec4(1, 1, 1, 1); } } }";
	
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
		buttonList.add(new GuiButton(18104, width / 2 - 155, height / 6 + 48, 150, 20, "Background Color: " + (skyBackground ? "Sky" : "Black")));
	}
	
	@Override
	public void onButtonPress(GuiButton button) {
		super.onButtonPress(button);
		//Background Color
		if (button.id == 18104) {
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
}
