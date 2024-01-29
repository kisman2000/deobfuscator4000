#version 150

uniform sampler2D Sampler0;

uniform mat4 ProjMat;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor0;
in vec4 vertexColor1;

out vec4 fragColor;

void main()
{
	vec4 color = texture(Sampler0, texCoord0);
	color *= ColorModulator;
	color.rgb = (color.a * vertexColor0.rgb) + (1.0 - color.a) * vertexColor1.rgb;
	color.a *= vertexColor0.a * vertexColor1.a;
	fragColor = color * ColorModulator;
}
