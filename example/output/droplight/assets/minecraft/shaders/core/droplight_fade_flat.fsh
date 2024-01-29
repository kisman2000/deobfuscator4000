#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

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

	vec2 scaledFragCoord = (gl_FragCoord.xy - 0.5) / (textureSize(Sampler1, 0) - 1.0);
	float sampledDepth = texture(Sampler1, scaledFragCoord).r;

	float linearFragmentDepth = -ProjMat[3][2] / (ProjMat[2][2] + gl_FragCoord.z * 2.0 - 1.0);
	float linearSampledDepth  = -ProjMat[3][2] / (ProjMat[2][2] + sampledDepth   * 2.0 - 1.0);
	float depthDifference = abs(linearSampledDepth - linearFragmentDepth);

	if (depthDifference < 0.125)
	{
		fragColor.a *= depthDifference * 8.0;
	}
}
