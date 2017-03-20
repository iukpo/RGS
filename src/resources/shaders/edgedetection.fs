#version 330

in vec3 mvVertexPos;
in vec3 mvVertexNormal;
in vec2 outTexCoord;

// The texture containing the results of the first pass
uniform sampler2D RenderTex;

uniform float EdgeThreshold;  // The squared threshold value
uniform int Width;            // The pixel width
uniform int Height;           // The pixel height

// This subroutine is used for selecting the functionality
// of pass1 and pass2.
subroutine vec4 RenderPassType();
subroutine uniform RenderPassType RenderPass;

// Other uniform variables for the Phong reflection model can
// be placed here...

layout( location = 0 ) out vec4 FragColor;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity;
    Attenuation att;
};

struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec3 colour;
    int useColour;
    float reflectance;
};

uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = light_intensity  * specularFactor * material.reflectance * vec4(light_colour, 1.0);

    return (diffuseColour + specColour);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_colour = calcLightColour(light.colour, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

// Approximates the brightness of a RGB value.
float luma( vec3 color ) {
  return 0.2126 * color.r + 0.7152 * color.g + 
           0.0722 * color.b;
}

// Pass #1
subroutine (RenderPassType)
vec4 pass1()
{
  vec4 totalLight = vec4(ambientLight, 1.0);
    totalLight += calcDirectionalLight(directionalLight, mvVertexPos, mvVertexNormal);
    totalLight += calcPointLight(pointLight, mvVertexPos, mvVertexNormal);
  return totalLight;
}

// Pass #2
subroutine( RenderPassType )
vec4 pass2()
{
  float dx = 1.0 / float(Width);
  float dy = 1.0 / float(Height);

  float s00 = luma(texture( RenderTex, 
                 outTexCoord + vec2(-dx,dy) ).rgb);
  float s10 = luma(texture( RenderTex, 
                   outTexCoord + vec2(-dx,0.0) ).rgb);
  float s20 = luma(texture( RenderTex, 
                   outTexCoord + vec2(-dx,-dy) ).rgb);
  float s01 = luma(texture( RenderTex, 
                   outTexCoord + vec2(0.0,dy) ).rgb);
  float s21 = luma(texture( RenderTex, 
                   outTexCoord + vec2(0.0,-dy) ).rgb);
  float s02 = luma(texture( RenderTex, 
                   outTexCoord + vec2(dx, dy) ).rgb);
  float s12 = luma(texture( RenderTex, 
                   outTexCoord + vec2(dx, 0.0) ).rgb);
  float s22 = luma(texture( RenderTex, 
                   outTexCoord + vec2(dx, -dy) ).rgb);

  float sx = s00 + 2 * s10 + s20 - (s02 + 2 * s12 + s22);
  float sy = s00 + 2 * s01 + s02 - (s20 + 2 * s21 + s22);
 
  float dist = sx * sx + sy * sy;

  if( dist>EdgeThreshold )
  return vec4(1.0);
  else
    return vec4(0.0,0.0,0.0,1.0);
}

void main()
{
	vec4 baseColour; 
    baseColour = vec4(material.colour, 1.0);
    // This will call either pass1() or pass2()
    FragColor = RenderPass();
}