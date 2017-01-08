package mod.render360.coretransform.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.EntityRenderer;

public class Hammer extends RenderMethod {

	private final String fragmentShader = "#version 130//\n #define M_PI 3.14159265//\n /* This comes interpolated from the vertex shader */ in vec2 texcoord; /* The 6 textures to be rendered */ uniform sampler2D texFront; uniform sampler2D texBack; uniform sampler2D texLeft; uniform sampler2D texRight; uniform sampler2D texTop; uniform sampler2D texBottom; //fovx\n uniform float fovx; uniform vec4 backgroundColor; uniform vec2 cursorPos; uniform bool drawCursor; out vec4 color; vec3 rotate(vec3 ray, vec2 angle) { //rotate y\n float y = -sin(angle.y)*ray.z; float z = cos(angle.y)*ray.z; ray.y = y; ray.z = z; //rotate x\n float x = -sin(angle.x)*ray.z; z = cos(angle.x)*ray.z; ray.x = x; ray.z = z; return ray; } void main(void) { /* Ray-trace a cube */ //create ray\n vec3 ray = vec3(0, 0, -1); //hammer stuff http://paulbourke.net/geometry/transformationprojection/ \n float x = texcoord.x*2 - 1; float y = texcoord.y*2 - 1; float z = sqrt(1 - x*x/2 - y*y/2); float longitude = 2*atan((sqrt(2)*z*x)/(2*z*z - 1)); float latitude = asin(sqrt(2)*z*y); if (x*x + y*y > 1) { color = backgroundColor; return; } //rotate ray\n ray = rotate(ray, vec2(longitude, latitude)); //find which side to use\n if (abs(ray.x) > abs(ray.y)) { if (abs(ray.x) > abs(ray.z)) { if (ray.x > 0) { //right\n float x = ray.z / ray.x; float y = ray.y / ray.x; color = vec4(texture(texRight, vec2((x+1)/2, (y+1)/2)).rgb, 1); } else { //left\n float x = -ray.z / -ray.x; float y = ray.y / -ray.x; color = vec4(texture(texLeft, vec2((x+1)/2, (y+1)/2)).rgb, 1); } } else { if (ray.z > 0) { //back\n float x = -ray.x / ray.z; float y = ray.y / ray.z; color = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1); } else { //front\n float x = ray.x / -ray.z; float y = ray.y / -ray.z; color = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1); } } } else { if (abs(ray.y) > abs(ray.z)) { if (ray.y > 0) { //top\n float x = ray.x / ray.y; float y = ray.z / ray.y; color = vec4(texture(texTop, vec2((x+1)/2, (y+1)/2)).rgb, 1); } else { //bottom\n float x = ray.x / -ray.y; float y = -ray.z / -ray.y; color = vec4(texture(texBottom, vec2((x+1)/2, (y+1)/2)).rgb, 1); } } else { if (ray.z > 0) { //back\n float x = -ray.x / ray.z; float y = ray.y / ray.z; color = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1); } else { //front\n float x = ray.x / -ray.z; float y = ray.y / -ray.z; color = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1); } } } if (drawCursor) { vec2 normalAngle = cursorPos*2 - 1; float x = ray.x / -ray.z; float y = ray.y / -ray.z; if (x <= normalAngle.x + 0.01 && y <= normalAngle.y + 0.01 && x >= normalAngle.x - 0.01 && y >= normalAngle.y - 0.01 && ray.z < 0) { color = vec4(1, 1, 1, 1); } } }";
	
	private boolean skyBackground = false;
	
	@Override
	public String getName() {
		return "Hammer";
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
