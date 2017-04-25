package mod.render360.coretransform.render;

import java.util.List;

import mod.render360.coretransform.gui.Slider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;

public class Flex extends RenderMethod {

	private final String fragmentShader = "#version 130//\n    #define M_PI 3.14159265//\n    /* This comes interpolated from the vertex shader */  in vec2 texcoord;    /* The 6 textures to be rendered */  uniform sampler2D texFront;  uniform sampler2D texBack;  uniform sampler2D texLeft;  uniform sampler2D texRight;  uniform sampler2D texTop;  uniform sampler2D texBottom;    uniform vec2 pixelOffset[4];    //fovx\n  uniform float fovx;  uniform float aspect;    uniform vec2 cursorPos;    uniform bool drawCursor;    out vec4 color;    vec2 tex_to_lens(vec2 tex) {    return (tex - vec2(0.5, 0.5)) * vec2(2, -2/aspect);  }    vec3 latlon_to_ray(float lat, float lon) {    return vec3(      sin(lon)*cos(lat),      sin(lat),      cos(lon)*cos(lat)    );  }    vec3 panini_inverse(vec2 lenscoord) {    float x = lenscoord.x;    float y = lenscoord.y;    float d = 1;    float k = x*x/((d+1)*(d+1));    float dscr = k*k*d*d - (k+1)*(k*d*d-1);    float clon = (-k*d+sqrt(dscr))/(k+1);    float S = (d+1)/(d+clon);    float lon = atan(x,S*clon);    float lat = atan(y,S);    return latlon_to_ray(lat, lon);  }    vec2 panini_forward(float lat, float lon) {    float d = 1;    float S = (d+1)/(d+cos(lon));    float x = S*sin(lon);    float y = S*tan(lat);    return vec2(x,y);  }    void main(void) {    /* Ray-trace a cube */    	//Anti-aliasing\n  	vec4 colorN[4];    	for (int loop = 0; loop < 4; loop++) {    		//create ray\n  		vec2 lenscoord = tex_to_lens(texcoord);      lenscoord *= panini_forward(0, radians(fovx)/2).x;      vec3 ray = panini_inverse(lenscoord);      ray.z *= -1;    		//find which side to use\n  		if (abs(ray.x) > abs(ray.y)) {  			if (abs(ray.x) > abs(ray.z)) {  				if (ray.x > 0) {  					//right\n  					float x = ray.z / ray.x;  					float y = ray.y / ray.x;  					colorN[loop] = vec4(texture(texRight, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				} else {  					//left\n  					float x = -ray.z / -ray.x;  					float y = ray.y / -ray.x;  					colorN[loop] = vec4(texture(texLeft, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				}  			} else {  				if (ray.z > 0) {  					//back\n  					float x = -ray.x / ray.z;  					float y = ray.y / ray.z;  					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				} else {  					//front\n  					float x = ray.x / -ray.z;  					float y = ray.y / -ray.z;  					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				}  			}  		} else {  			if (abs(ray.y) > abs(ray.z)) {  				if (ray.y > 0) {  					//top\n  					float x = ray.x / ray.y;  					float y = ray.z / ray.y;  					colorN[loop] = vec4(texture(texTop, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				} else {  					//bottom\n  					float x = ray.x / -ray.y;  					float y = -ray.z / -ray.y;  					colorN[loop] = vec4(texture(texBottom, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				}  			} else {  				if (ray.z > 0) {  					//back\n  					float x = -ray.x / ray.z;  					float y = ray.y / ray.z;  					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				} else {  					//front\n  					float x = ray.x / -ray.z;  					float y = ray.y / -ray.z;  					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);  				}  			}  		}    		if (drawCursor) {  			vec2 normalAngle = cursorPos*2 - 1;  			float x = ray.x / -ray.z;  			float y = ray.y / -ray.z;  			if (x <= normalAngle.x + 0.01 && y <= normalAngle.y + 0.01 &&  				x >= normalAngle.x - 0.01 && y >= normalAngle.y - 0.01 &&  				ray.z < 0) {  				colorN[loop] = vec4(1, 1, 1, 1);  			}  		}  	}    	color = mix(mix(colorN[0], colorN[1], 0.5), mix(colorN[2], colorN[3], 0.5), 0.5);  }";

	private float fov = 180;

	@Override
	public String getName() {
		return "Flex";
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
		buttonList.add(new Slider(new Responder(), 18104, width / 2 - 180, height / 6 + 24, 360, 20, "FOV", 0f, 360f, fov, 1f, null));
	}

	@Override
	public float getFOV() {
		return fov;
	}

	public class Responder implements GuiResponder {
		@Override
		public void setEntryValue(int id, boolean value) {

		}

		@Override
		public void setEntryValue(int id, float value) {
			//FOV
			if (id == 18104) {
				fov = value;
			}
		}

		@Override
		public void setEntryValue(int id, String value) {

		}
	}
}
