// NOTE: run update-shader.sh to dump this file into the appropriate Java string
// for compilation.

#version 130

#define M_PI 3.14159265

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

//fovx
uniform float fovx;
uniform float aspect;

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

vec4 rubix_color(vec2 coord, vec3 hue) {
  int numCells = 10;
  int cellSize = 4;
  int padSize = 1;

  int blockSize = padSize + cellSize;
  int numUnits = numCells * blockSize + padSize;

  bool onGrid = (
    mod(coord.x * numUnits, blockSize) < padSize ||
    mod(coord.y * numUnits, blockSize) < padSize
  );

  return onGrid ? vec4(0,0,0,0) : vec4(hue, 0.3);
}

vec4 texcoord_color(sampler2D tex, vec3 hue, vec2 coord) {
  coord = (coord + vec2(1,1)) / 2;
  vec4 color = texture(tex, coord);
  vec4 rubix = rubix_color(coord, hue);
  float a = rubix.a;
  return vec4((1-a)*color.rgb + a*rubix.rgb, 1);
}

vec4 ray_to_color(vec3 ray) {
  //find which side to use
  if (abs(ray.x) > abs(ray.y)) {
    if (abs(ray.x) > abs(ray.z)) {
      if (ray.x > 0) {
        return texcoord_color(texRight, vec3(0,0,1), vec2(ray.z/ray.x, ray.y/ray.x));
      } else {
        return texcoord_color(texLeft, vec3(1,0,0), vec2(ray.z/ray.x, -ray.y/ray.x));
      }
    } else {
      if (ray.z > 0) {
        return texcoord_color(texBack, vec3(1,1,0), vec2(-ray.x/ray.z, ray.y/ray.z));
      } else {
        return texcoord_color(texFront, vec3(1,1,1), vec2(-ray.x/ray.z, -ray.y/ray.z));
      }
    }
  } else {
    if (abs(ray.y) > abs(ray.z)) {
      if (ray.y > 0) {
        return texcoord_color(texTop, vec3(1,0,1), vec2(ray.x/ray.y, ray.z/ray.y));
      } else {
        return texcoord_color(texBottom, vec3(0,1,1), vec2(-ray.x/ray.y, ray.z/ray.y));
      }
    } else {
      if (ray.z > 0) {
        return texcoord_color(texBack, vec3(1,1,0), vec2(-ray.x/ray.z, ray.y/ray.z));
      } else {
        return texcoord_color(texFront, vec3(1,1,1), vec2(-ray.x/ray.z, -ray.y/ray.z));
      }
    }
  }
}

vec2 tex_to_lens(vec2 tex) {
  return (tex - vec2(0.5, 0.5)) * vec2(2,2/aspect);
}

vec3 tex_to_ray(vec2 texcoord) {
  vec3 ray;
  vec2 c = tex_to_lens(texcoord);
  if (fovx < 120) {
    ray = standard_ray(c);
  } else if (fovx < 140) {
    ray = mix(standard_ray(c), stereographic_ray(c), (fovx - 120)/ 20.0);
  } else if (fovx < 200) {
    ray = stereographic_ray(c);
  } else if (fovx < 220) {
    ray = mix(stereographic_ray(c), mercator_ray(c), (fovx - 200)/ 20.0);
  } else if (fovx < 340) {
    ray = mercator_ray(c);
  } else if (fovx < 360) {
    ray = mix(mercator_ray(c), equirect_ray(c), (fovx - 340)/ 20.0);
    float len = length(ray);
    // alpha = clamp(len*2, 0, 1);
  } else if (fovx == 360) {
    ray = equirect_ray(c);
  } else {
    ray = vec3(0,0,0);
  }
  ray.z *= -1;
  return ray;
}

bool isRayOnCursor(vec3 ray) {
  vec2 normalAngle = cursorPos*2 - 1;
  float x = ray.x / -ray.z;
  float y = ray.y / -ray.z;
  return (
    x <= normalAngle.x + 0.01 && y <= normalAngle.y + 0.01 &&
    x >= normalAngle.x - 0.01 && y >= normalAngle.y - 0.01 &&
    ray.z < 0
  );
}

vec4 tex_color(vec2 texcoord) {
  vec3 ray = tex_to_ray(texcoord);
  if (length(ray) == 0) {
    return backgroundColor;
  }
  vec4 c = ray_to_color(ray);
  if (drawCursor && isRayOnCursor(ray)) {
    c += vec4(1, 1, 1, 1);
  }
  return c;
}

vec4 antialias_color(vec2 texcoord) {
  vec4 c = vec4(0,0,0,0);
  for (int i = 0; i < 4; i++) {
    c += tex_color(texcoord + pixelOffset[i]);
  }
  return c / 4;
}

void main(void) {
  color = tex_color(texcoord);

  // Uncomment this to use anti-aliasing instead.
  // color = antialias_color(texcoord);
}
