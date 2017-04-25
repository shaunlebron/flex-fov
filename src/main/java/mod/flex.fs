#version 130//\n

#define M_PI 3.14159265//\n

/* This comes interpolated from the vertex shader */
in vec2 texcoord;

/* The 6 textures to be rendered */
uniform sampler2D texFront;
uniform sampler2D texBack;
uniform sampler2D texLeft;
uniform sampler2D texRight;
uniform sampler2D texTop;
uniform sampler2D texBottom;

uniform vec2 pixelOffset[4];

//fovx\n
uniform float fovx;
uniform float aspect;

uniform vec2 cursorPos;

uniform bool drawCursor;

out vec4 color;



void main(void) {
  /* Ray-trace a cube */

	//Anti-aliasing
	vec4 colorN[4];

	for (int loop = 0; loop < 4; loop++) {

		//create ray\n
		vec3 ray = vec3(0, 0, -1);

		//rotate ray\n
		ray = rotate(ray, vec2(
      (texcoord.x+pixelOffset[loop].x-0.5)*2*M_PI*fovx/360,
      (texcoord.y+pixelOffset[loop].y-0.5)*M_PI*fovx/360
    )); //x (-pi to pi), y (-pi/2 to pi/2\n

		//find which side to use\n
		if (abs(ray.x) > abs(ray.y)) {
			if (abs(ray.x) > abs(ray.z)) {
				if (ray.x > 0) {
					//right\n
					float x = ray.z / ray.x;
					float y = ray.y / ray.x;
					colorN[loop] = vec4(texture(texRight, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				} else {
					//left\n
					float x = -ray.z / -ray.x;
					float y = ray.y / -ray.x;
					colorN[loop] = vec4(texture(texLeft, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				}
			} else {
				if (ray.z > 0) {
					//back\n
					float x = -ray.x / ray.z;
					float y = ray.y / ray.z;
					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				} else {
					//front\n
					float x = ray.x / -ray.z;
					float y = ray.y / -ray.z;
					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				}
			}
		} else {
			if (abs(ray.y) > abs(ray.z)) {
				if (ray.y > 0) {
					//top\n
					float x = ray.x / ray.y;
					float y = ray.z / ray.y;
					colorN[loop] = vec4(texture(texTop, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				} else {
					//bottom\n
					float x = ray.x / -ray.y;
					float y = -ray.z / -ray.y;
					colorN[loop] = vec4(texture(texBottom, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				}
			} else {
				if (ray.z > 0) {
					//back\n
					float x = -ray.x / ray.z;
					float y = ray.y / ray.z;
					colorN[loop] = vec4(texture(texBack, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				} else {
					//front\n
					float x = ray.x / -ray.z;
					float y = ray.y / -ray.z;
					colorN[loop] = vec4(texture(texFront, vec2((x+1)/2, (y+1)/2)).rgb, 1);
				}
			}
		}

		if (drawCursor) {
			vec2 normalAngle = cursorPos*2 - 1;
			float x = ray.x / -ray.z;
			float y = ray.y / -ray.z;
			if (x <= normalAngle.x + 0.01 && y <= normalAngle.y + 0.01 &&
				x >= normalAngle.x - 0.01 && y >= normalAngle.y - 0.01 &&
				ray.z < 0) {
				colorN[loop] = vec4(1, 1, 1, 1);
			}
		}
	}

	color = mix(mix(colorN[0], colorN[1], 0.5), mix(colorN[2], colorN[3], 0.5), 0.5);
}
