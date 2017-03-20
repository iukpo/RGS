/*

Translated from

https://github.com/ernestosc/comp-graphics/blob/master/shaders/src/gouraud.vert

*/

#version 330

out vec4 fragColor;
in vec4 Intensity, baseColour; /* Color and intensity vertex info. */
void main() {
	/* Interpolate fragment corner colors. This happens whenever color computation is done in vertex shader,
	then output in fragment shader.*/
	fragColor = Intensity;
}