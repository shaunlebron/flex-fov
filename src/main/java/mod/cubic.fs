#version 130//\n

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

uniform vec4 backgroundColor;

uniform vec2 cursorPos;

uniform bool drawCursor;

out vec4 color;

void main(void) {
	//Anti-aliasing
	vec4 colorN[4];
	
	for (int loop = 0; loop < 4; loop++) {
		vec2 coord = texcoord+pixelOffset[loop];
		
		if (coord.y >= 0.333333333 && coord.y < 0.666666666) {
			//Left\n
			if (coord.x < 0.25) {
				colorN[loop] = vec4(texture(texLeft, vec2(coord.x*4, coord.y*3-1)).rgb, 1);
			}
			//Front\n
			else if (coord.x < 0.5) {
				colorN[loop] = vec4(texture(texFront, vec2(coord.x*4-1, coord.y*3-1)).rgb, 1);
			}
			//Right\n
			else if (coord.x < 0.75) {
				colorN[loop] = vec4(texture(texRight, vec2(coord.x*4-2, coord.y*3-1)).rgb, 1);
			}
			//Back\n
			else {
				colorN[loop] = vec4(texture(texBack, vec2(coord.x*4-3, coord.y*3-1)).rgb, 1);
			}
		}
		else if (coord.x < 0.5 && coord.x >= 0.25) {
			//Bottom\n
			if (coord.y < 0.333333333) {
				colorN[loop] = vec4(texture(texBottom, vec2(coord.x*4-1, coord.y*3)).rgb, 1);
			}
			//Top\n
			else {
				colorN[loop] = vec4(texture(texTop, vec2(coord.x*4-1, coord.y*3-2)).rgb, 1);
			}
		}
		else {
			colorN[loop] = backgroundColor;
		}
		
		if (drawCursor) {
			if (coord.x + 0.0015 >= (cursorPos.x+1)/4 && coord.x - 0.0015 < (cursorPos.x+1)/4 &&
				coord.y + 0.002 >= (cursorPos.y+1)/3 && coord.y - 0.002 < (cursorPos.y+1)/3) {
					colorN[loop] = vec4(1, 1, 1, 1);
			}
		}
	}
	
	color = mix(mix(colorN[0], colorN[1], 0.5), mix(colorN[2], colorN[3], 0.5), 0.5);
}