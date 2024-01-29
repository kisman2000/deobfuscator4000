#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color0;
in vec4 Color1;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord0;
out vec4 vertexColor0;
out vec4 vertexColor1;

void main()
{
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

	texCoord0 = UV0;
	vertexColor0 = Color0;
	vertexColor1 = Color1;
}
