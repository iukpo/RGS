/*
Reference: OpenGL 4.0 Shading Language Cookbook
*/

#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

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

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;
const int levels = 3;
const float scaleFactor = 1.0 / levels;

/*NB: to be able to apply calculations across all lights, change the calcLight (here, renamed calcToonShade) method
to include new data/formulae. Leave the other calculation functions alone-they all feed in here.*/

vec4 calcToonShade(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
  vec4 diffuseColour = vec4(0, 0, 0, 0);
  
   // Diffuse Light
   vec3 s = normalize( to_light_dir );
   
   //Calculate cosine
   float cosine = max( 0.0, dot( s, normal ) );
   float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
   
   //Using cosine, calculate the diffuseColour.
   diffuseColour = vec4(light_colour, 1.0) * floor(cosine*levels) * diffuseFactor;
   
   return diffuseColour;
}

vec4 calcPointLightToon(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_colour = calcToonShade(light.colour, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}

vec4 calcDirectionalLightToon(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcToonShade(light.colour, light.intensity, position, normalize(light.direction), normal);
}

void main()
{
    vec4 baseColour; 
    if ( material.useColour == 1 )
    {
        baseColour = vec4(material.colour, 1);
    }
    else
    {
        baseColour = texture(texture_sampler, outTexCoord);
    }
    vec4 totalLight = vec4(ambientLight, 1.0);
    totalLight += calcDirectionalLightToon(directionalLight, mvVertexPos, mvVertexNormal);
    totalLight += calcPointLightToon(pointLight, mvVertexPos, mvVertexNormal); 
	fragColor = baseColour * totalLight;
}