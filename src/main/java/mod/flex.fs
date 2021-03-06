// NOTE: run update-shader.sh to dump this file into the appropriate Java string
// for compilation.

#version 130

#define M_PI 3.14159265

/* This comes interpolated from the vertex shader */
in vec2 texcoord;

/* The 6 textures to be rendered */
uniform int textureCount;
uniform sampler2D textures[6];
uniform mat4 coordFrames[6];
uniform float textureFovs[6];

uniform vec2 pixelOffset[4];

//fovx
uniform float fovx;
uniform float aspect;
uniform float pitch;

// boolean uniforms have to be ints (0=false, 1=true)
uniform int rubix;
uniform int split;

uniform vec2 cursorPos;

uniform bool drawCursor;

uniform vec4 backgroundColor;

out vec4 color;

vec3 latlon_to_ray(float lat, float lon) {
  return vec3(
    sin(lon)*cos(lat),
    sin(lat),
    cos(lon)*cos(lat)
  );
}

vec3 standard_inverse(vec2 lenscoord) {
  float x = lenscoord.x;
  float y = lenscoord.y;
  float r = length(lenscoord);
  float theta = atan(r);
  float s = sin(theta);
  return vec3(x/r*s, y/r*s, cos(theta));
}
vec2 standard_forward(float lat, float lon) {
  vec3 ray = latlon_to_ray(lat, lon);
  float x = ray.x;
  float y = ray.y;
  float z = ray.z;
  float theta = acos(z);
  float r = tan(theta);
  float c = r/length(ray.xy);
  return vec2(x*c, y*c);
}
vec3 standard_ray(vec2 lenscoord) {
  float scale = standard_forward(0, radians(fovx)/2).x;
  return standard_inverse(lenscoord * scale);
}

vec3 panini_inverse(vec2 lenscoord) {
  float x = lenscoord.x;
  float y = lenscoord.y;
  float d = 1;
  float k = x*x/((d+1)*(d+1));
  float dscr = k*k*d*d - (k+1)*(k*d*d-1);
  float clon = (-k*d+sqrt(dscr))/(k+1);
  float S = (d+1)/(d+clon);
  float lon = atan(x,S*clon);
  float lat = atan(y,S);
  return latlon_to_ray(lat, lon);
}
vec2 panini_forward(float lat, float lon) {
  float d = 1;
  float S = (d+1)/(d+cos(lon));
  float x = S*sin(lon);
  float y = S*tan(lat);
  return vec2(x,y);
}
vec3 panini_ray(vec2 lenscoord) {
  float scale = panini_forward(0, radians(fovx)/2).x;
  return panini_inverse(lenscoord * scale);
}

vec3 mercator_inverse(vec2 lenscoord) {
  float lon = lenscoord.x;
  float lat = atan(sinh(lenscoord.y));
  return latlon_to_ray(lat, lon);
}
vec2 mercator_forward(float lat, float lon) {
  float x = lon;
  float y = log(tan(M_PI*0.25+lat*0.5));
  return vec2(x,y);
}
vec3 mercator_ray(vec2 lenscoord) {
  float scale = mercator_forward(0, radians(fovx)/2).x;
  return mercator_inverse(lenscoord * scale);
}

vec3 equirect_inverse(vec2 lenscoord) {
  if (abs(lenscoord.x) > M_PI || abs(lenscoord.y) > M_PI/2) {
    return vec3(0,0,0);
  }
  float lon = lenscoord.x;
  float lat = lenscoord.y;
  return latlon_to_ray(lat, lon);
}
vec2 equirect_forward(float lat, float lon) {
  float x = lon;
  float y = lat;
  return vec2(x,y);
}
vec3 equirect_ray(vec2 lenscoord) {
  float scale = equirect_forward(0, radians(fovx)/2).x;
  return equirect_inverse(lenscoord * scale);
}

vec3 stereographic_inverse(vec2 lenscoord) {
  float x = lenscoord.x;
  float y = lenscoord.y;
  float angleScale = 0.5;
  float r = length(lenscoord);
  float theta = atan(r)/angleScale;
  float s = sin(theta);
  return vec3(x/r*s, y/r*s, cos(theta));
}
vec2 stereographic_forward(float lat, float lon) {
  vec3 ray = latlon_to_ray(lat, lon);
  float theta = acos(ray.z);
  float angleScale = 0.5;
  float r = tan(theta*angleScale);
  float c = r/length(ray.xy);
  return vec2(ray.x*c, ray.y*c);
}
vec3 stereographic_ray(vec2 lenscoord) {
  float scale = stereographic_forward(0, radians(fovx)/2).x;
  return stereographic_inverse(lenscoord * scale);
}


// Use panini when looking straight, and stereographic when looking down
// NOTE: `i` represents which split screen we are on so we can compare the projections
// we are trying to fuse.
vec3 hybrid_stereo_ray(vec2 c, int i) {
  if (i == 0) {
    return mix(panini_ray(c), stereographic_ray(c), abs(pitch) / 90);
  } else if (i == 1) {
    return panini_ray(c);
  } else if (i == 2) {
    return stereographic_ray(c);
  } else {
    return vec3(0,0,0);
  }
}

vec4 rubix_color(vec2 uv, int i) {
  if (rubix == 0) {
    return vec4(0,0,0,0);
  }
  int numCells = 10;
  int cellSize = 4;
  int padSize = 1;

  int blockSize = padSize + cellSize;
  int numUnits = numCells * blockSize + padSize;

  bool onGrid = (
    mod(uv.x * numUnits, blockSize) < padSize ||
    mod(uv.y * numUnits, blockSize) < padSize
  );

  vec3 hue;
  switch (i) {
    case 0: hue = vec3(1,1,1); break;
    case 1: hue = vec3(0,0,1); break;
    case 2: hue = vec3(1,0,0); break;
    case 3: hue = vec3(0,1,0); break;
    case 4: hue = vec3(1,1,0); break;
    case 5: hue = vec3(0,1,1); break;
  }
  return onGrid ? vec4(0,0,0,0) : vec4(hue, 0.3);
}

vec4 texuv_color(int i, vec2 uv) {
  vec4 color;
  switch (i) {
    case 0: color = texture(textures[0], uv); break;
    case 1: color = texture(textures[1], uv); break;
    case 2: color = texture(textures[2], uv); break;
    case 3: color = texture(textures[3], uv); break;
    case 4: color = texture(textures[4], uv); break;
    case 5: color = texture(textures[5], uv); break;
  }
  vec4 rubix = rubix_color(uv, i);
  float a = rubix.a;
  return vec4((1-a)*color.rgb + a*rubix.rgb, 1);
}

vec3 frame_forward(mat3 coordFrame) {
  return vec3(
    -coordFrame[0].z,
    -coordFrame[1].z,
    -coordFrame[2].z
  );
}

int ray_to_texture_index(vec3 ray) {
  int index=0;
  float maxd=-2;
  for (int i=0; i<textureCount; i++) {
    float d = dot(ray, frame_forward(mat3(coordFrames[i])));
    if (d > maxd) {
      maxd = d;
      index = i;
    }
  }
  return index;
}

vec4 ray_to_color(vec3 ray) {
  //find which side to use
  int i = ray_to_texture_index(ray);
  vec3 ray2 = mat3(coordFrames[i]) * ray;
  float d = 0.5 / tan(radians(textureFovs[i]/2.0));
  vec2 uv = vec2(
    -ray2.x / ray2.z * d + 0.5,
    -ray2.y / ray2.z * d + 0.5
  );
  return texuv_color(i, uv);
}

vec2 tex_to_screen(vec2 tex, float aspect) {
  return (tex - vec2(0.5, 0.5)) * vec2(2,2/aspect);
}

vec2 tex_to_splitscreen(vec2 tex, out int i) {
  if (tex.y > 0.5) {
    if (tex.x < 0.5) {
      i = 1;
      return tex_to_screen((tex - vec2(0, 0.5)) * vec2(2, 2), aspect);
    } else {
      i = 2;
      return tex_to_screen((tex - vec2(0.5, 0.5)) * vec2(2, 2), aspect);
    }
  } else {
    i = 0;
    // oldx -> newx
    // 0    -> -0.5
    // 0.25 -> 0
    // 0.75 -> 1
    // 1    -> 1.5
    return tex_to_screen((tex * vec2(2, 2)) - vec2(0.5, 0), aspect);
  }
}

// NOTE: `c` represents the lens coordinate, and `i` represents the split screen
// number (0-2) that we use to compare projections.
vec3 screen_to_ray(vec2 c, int i) {
  vec3 ray;
  if (fovx < 90) {
    ray = standard_ray(c);
  } else if (fovx < 160) {
		float linear = (fovx - 90)/ 70.0;
		float parabola = 1-(linear-1)*(linear-1);
    ray = mix(standard_ray(c), hybrid_stereo_ray(c, i), parabola);
  } else if (fovx < 300) {
		float linear = (fovx - 160)/ 140.0;
		float parabola = 1-(linear-1)*(linear-1);
    ray = mix(hybrid_stereo_ray(c, i), mercator_ray(c), parabola);
  } else if (fovx <= 360) {
    ray = mercator_ray(c);
  //} else if (fovx < 360) {
    //ray = mix(mercator_ray(c), equirect_ray(c), (fovx - 340)/ 20.0);
    //float len = length(ray);
    // alpha = clamp(len*2, 0, 1);
  //} else if (fovx == 360) {
    //ray = equirect_ray(c);
  } else {
    ray = vec3(0,0,0);
  }
  ray.z *= -1;
  return ray;
}

bool isScreenCursor(vec2 screen) {
  if (split == 0) {
    return false;
  }
  float thick = 0.0025;
  float w = 0.025;
  if (abs(screen.x) < w && abs(screen.y) < w) {
    return abs(screen.y) < thick || abs(screen.x) < thick;
  }
  return false;
}

vec4 screen_color(vec2 screen, int i) {
  vec3 ray = screen_to_ray(screen, i);
  if (length(ray) == 0) {
    return backgroundColor;
  }
  vec4 c = ray_to_color(ray);
  if (isScreenCursor(screen)) {
    c += vec4(1, 1, 1, 1);
  }
  return c;
}

vec4 screen_color_antialias(vec2 screen, int i) {
  vec4 c = vec4(0,0,0,0);
  for (int j = 0; j < 4; j++) {
    c += screen_color(screen + pixelOffset[j], i);
  }
  return c / 4;
}

void main(void) {
  int i;
  vec2 screen;

  if (split == 0) {
    i = 0;
    screen = tex_to_screen(texcoord, aspect);
  } else {
    screen = tex_to_splitscreen(texcoord, i);
  }

  if (abs(screen.x) > 1) {
    color = backgroundColor;
  } else {
    color = screen_color_antialias(screen, i);
  }
}
