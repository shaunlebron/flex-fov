package mod.render360.coretransform.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.Shader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;

public class EquirectangularStatic extends RenderMethod {

	private final String fragmentShader = "#version 130//\n\n\n#define M_PI 3.14159265//\n\n\n/* This comes interpolated from the vertex shader */\nin vec2 texcoord;\n\n/* The 6 textures to be rendered */\nuniform sampler2D texFront;\nuniform sampler2D texBack;\nuniform sampler2D texLeft;\nuniform sampler2D texRight;\nuniform sampler2D texTop;\nuniform sampler2D texBottom;\n\nuniform vec2 pixelOffset[4];\n\nuniform vec2 cursorPos;\n\nuniform bool drawCursor;\n\nuniform bool drawCircle;\n\nuniform vec2 rotation;\n\nout vec4 color;\n\nvec3 rotate(vec3 ray, vec2 angle) {\n  \n  //rotate y\n\n  float y = -sin(angle.y)*ray.z;\n  float z = cos(angle.y)*ray.z;\n  ray.y = y;\n  ray.z = z;\n  \n  //rotate x\n\n  float x = -sin(angle.x)*ray.z;\n  z = cos(angle.x)*ray.z;\n  ray.x = x;\n  ray.z = z;\n  \n  return ray;\n}\n\nvec3 rotate2(vec3 ray, vec2 angle) {\n	//rotate x\n\n	float x = cos(angle.x)*ray.x - sin(angle.x)*ray.z;\n	float z = cos(angle.x)*ray.z + sin(angle.x)*ray.x;\n	ray.x = x;\n	ray.z = z;\n	\n	//rotate y\n\n	float y = cos(angle.y)*ray.y - sin(angle.y)*ray.z;\n	z = cos(angle.y)*ray.z + sin(angle.y)*ray.y;\n	ray.y = y;\n	ray.z = z;\n	\n	return ray;\n}\n\nvoid main(void) {\n  /* Ray-trace a cube */\n	\n	//Anti-aliasing\n	vec4 colorN[4];\n	\n	for (int loop = 0; loop < 4; loop++) {\n		\n		//create ray\n\n		vec3 ray = vec3(0, 0, -1);\n		\n		//rotate ray\n\n		ray = rotate(ray, vec2((texcoord.x+pixelOffset[loop].x-0.5)*2*M_PI, (texcoord.y+pixelOffset[loop].y-0.5)*M_PI)); //x (-pi to pi), y (-pi/2 to pi/2\n\n		ray = rotate2(ray, vec2(-rotation.x*M_PI/180, rotation.y*M_PI/180));\n		\n		//find which side to use\n\n		if (abs(ray.x) > abs(ray.y)) {\n			if (abs(ray.x) > abs(ray.z)) {\n				if (ray.x > 0) {\n					//right\n\n					float x = ray.z / ray.x;\n					float y = ray.y / ray.x;\n					colorN[loop] = vec4(texture(texRight, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//left\n\n					float x = -ray.z / -ray.x;\n					float y = ray.y / -ray.x;\n					colorN[loop] = vec4(texture(texLeft, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			} else {\n				if (ray.z > 0) {\n					//back\n\n					float x = -ray.x / ray.z;\n					float y = ray.y / ray.z;\n					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//front\n\n					float x = ray.x / -ray.z;\n					float y = ray.y / -ray.z;\n					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			}\n		} else {\n			if (abs(ray.y) > abs(ray.z)) {\n				if (ray.y > 0) {\n					//top\n\n					float x = ray.x / ray.y;\n					float y = ray.z / ray.y;\n					colorN[loop] = vec4(texture(texTop, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//bottom\n\n					float x = ray.x / -ray.y;\n					float y = -ray.z / -ray.y;\n					colorN[loop] = vec4(texture(texBottom, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			} else {\n				if (ray.z > 0) {\n					//back\n\n					float x = -ray.x / ray.z;\n					float y = ray.y / ray.z;\n					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				} else {\n					//front\n\n					float x = ray.x / -ray.z;\n					float y = ray.y / -ray.z;\n					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);\n				}\n			}\n		}\n		\n		if (drawCursor) {\n			vec2 normalAngle = cursorPos*2 - 1;\n			float x = ray.x / -ray.z;\n			float y = ray.y / -ray.z;\n			if (x <= normalAngle.x + 0.01 && y <= normalAngle.y + 0.01 &&\n				x >= normalAngle.x - 0.01 && y >= normalAngle.y - 0.01 &&\n				ray.z < 0) {\n				colorN[loop] = vec4(1, 1, 1, 1);\n			}\n		} else if (drawCircle) {\n			float phi = (texcoord.y+pixelOffset[loop].y-0.5)*M_PI;\n			float lambda = (texcoord.x+pixelOffset[loop].x-0.5-rotation.x/360)*2*M_PI;\n			float z = cos(phi)*cos(lambda);\n			float y = sin(phi)*cos(rotation.y*M_PI/180+M_PI/2) + z*sin(rotation.y*M_PI/180+M_PI/2);\n			float radius = asin(1-y);\n			if (radius < 0.0013 && radius > 0.0007) {\n				colorN[loop] = vec4(0, 0, 0, 1);\n			}\n		}\n	}\n	\n	color = mix(mix(colorN[0], colorN[1], 0.5), mix(colorN[2], colorN[3], 0.5), 0.5);\n}\n";
	
	private boolean drawCircle = true;
	
	@Override
	public String getName() {
		return "Static";
	}

	@Override
	public String getFragmentShader() {
		return fragmentShader;
	}
	
	@Override
	public void runShader(EntityRenderer er, Minecraft mc, Framebuffer framebuffer,
			Shader shader, int[] framebufferTextures) {
		//Use shader
		GL20.glUseProgram(shader.getShaderProgram());

		GL20.glUseProgram(shader.getShaderProgram());
		int circleUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "drawCircle");
		GL20.glUniform1i(circleUniform, drawCircle ? 1 : 0);
		
		if (!getResizeGui() || mc.gameSettings.hideGUI) {
			int cursorUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "drawCursor");
			GL20.glUniform1i(cursorUniform, 0);
		}
		
		int angleUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "rotation");
		float rotX = mc.getRenderViewEntity().rotationYaw;
		while (rotX < 0) rotX += 360;
		while (rotX > 360) rotX -= 360;
		GL20.glUniform2f(angleUniform, rotX, mc.getRenderViewEntity().rotationPitch);
		
		super.runShader(er, mc, framebuffer, shader, framebufferTextures);
	}
	
	@Override
	public boolean replaceLoadingScreen() {
		return true;
	}
	
	@Override
	public void addButtonsToGui(List<GuiButton> buttonList, int width, int height) {
		super.addButtonsToGui(buttonList, width, height);
		buttonList.add(new GuiButton(18106, width / 2 - 155, height / 6 + 96, 150, 20, "Draw Circle: " + (drawCircle ? "ON" : "OFF")));
	}
	
	@Override
	public void onButtonPress(GuiButton button) {
		super.onButtonPress(button);
		//Draw circle
		if (button.id == 18106) {
			drawCircle = !drawCircle;
			button.displayString = "Draw Circle: " + (drawCircle ? "ON" : "OFF");
		}
	}
}
