package mod.render360.coretransform.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.EntityRenderer;

public class Hammer extends RenderMethod {

	private final String fragmentShader = "#version 130//\n\n\n#define M_PI 3.14159265//\n\n\n/* This comes interpolated from the vertex shader */\nin vec2 texcoord;\n\n/* The 6 textures to be rendered */\nuniform sampler2D texFront;\nuniform sampler2D texBack;\nuniform sampler2D texLeft;\nuniform sampler2D texRight;\nuniform sampler2D texTop;\nuniform sampler2D texBottom;\n\nuniform vec2 pixelOffset[4];\n\nuniform vec4 backgroundColor;\n\nuniform vec2 cursorPos;\n\nuniform bool drawCursor;\n\nout vec4 color;\n\nvec3 rotate(vec3 ray, vec2 angle) {\n  \n  //rotate y\n\n  float y = -sin(angle.y)*ray.z;\n  float z = cos(angle.y)*ray.z;\n  ray.y = y;\n  ray.z = z;\n  \n  //rotate x\n\n  float x = -sin(angle.x)*ray.z;\n  z = cos(angle.x)*ray.z;\n  ray.x = x;\n  ray.z = z;\n  \n  return ray;\n}\n\nvoid main(void) {\n  /* Ray-trace a cube */\n	\n	//Anti-aliasing\n	vec4 colorN[4];\n	\n	for (int loop = 0; loop < 4; loop++) {\n		\n		//create ray\n\n		vec3 ray = vec3(0, 0, -1);\n		\n		//hammer stuff http://paulbourke.net/geometry/transformationprojection/ \n\n		float x = (texcoord.x+pixelOffset[loop].x)*2 - 1;\n		float y = (texcoord.y+pixelOffset[loop].y)*2 - 1;\n		float z = sqrt(1 - x*x/2 - y*y/2);\n		float longitude = 2*atan((sqrt(2)*z*x)/(2*z*z - 1));\n		float latitude = asin(sqrt(2)*z*y);\n		\n		if (x*x + y*y > 1) {\n			color = backgroundColor;\n			return;\n		}\n		\n		//rotate ray\n\n		ray = rotate(ray, vec2(longitude, latitude));\n		\n		//find which side to use\n\n		if (abs(ray.x) > abs(ray.y)) {\n			if (abs(ray.x) > abs(ray.z)) {\n				if (ray.x > 0) {\n					//right\n\n					float x = ray.z / ray.x;\n					float y = ray.y / ray.x;\n					colorN[loop] = vec4(texture(texRight, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//left\n\n					float x = -ray.z / -ray.x;\n					float y = ray.y / -ray.x;\n					colorN[loop] = vec4(texture(texLeft, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			} else {\n				if (ray.z > 0) {\n					//back\n\n					float x = -ray.x / ray.z;\n					float y = ray.y / ray.z;\n					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//front\n\n					float x = ray.x / -ray.z;\n					float y = ray.y / -ray.z;\n					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			}\n		} else {\n			if (abs(ray.y) > abs(ray.z)) {\n				if (ray.y > 0) {\n					//top\n\n					float x = ray.x / ray.y;\n					float y = ray.z / ray.y;\n					colorN[loop] = vec4(texture(texTop, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//bottom\n\n					float x = ray.x / -ray.y;\n					float y = -ray.z / -ray.y;\n					colorN[loop] = vec4(texture(texBottom, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			} else {\n				if (ray.z > 0) {\n					//back\n\n					float x = -ray.x / ray.z;\n					float y = ray.y / ray.z;\n					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//front\n\n					float x = ray.x / -ray.z;\n					float y = ray.y / -ray.z;\n					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			}\n		}\n		\n		if (drawCursor) {\n			vec2 normalAngle = cursorPos*2 - 1;\n			float x = ray.x / -ray.z;\n			float y = ray.y / -ray.z;\n			if (x <= normalAngle.x + 0.01 && y <= normalAngle.y + 0.01 &&\n				x >= normalAngle.x - 0.01 && y >= normalAngle.y - 0.01 &&\n				ray.z < 0) {\n				colorN[loop] = vec4(1, 1, 1, 1);\n			}\n		}\n	}\n	\n	color = mix(mix(colorN[0], colorN[1], 0.5), mix(colorN[2], colorN[3], 0.5), 0.5);\n}\n";
	
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
	public boolean replaceLoadingScreen() {
		return true;
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
}
