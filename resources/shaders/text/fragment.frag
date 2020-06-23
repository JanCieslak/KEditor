#version 400 core

out vec4 out_color;

in vec2 pass_texCoord;

uniform sampler2D textureSampler;

vec3 color = vec3(0.78, 0.57, 0.91);

void main() {
    out_color = vec4(color, 1.0) * texture(textureSampler, pass_texCoord);
}