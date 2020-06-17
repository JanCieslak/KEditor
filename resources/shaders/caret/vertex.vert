#version 400 core

layout (location = 0) in vec2 position;

uniform mat4 model = mat4(1.0);
// todo uniform mat4 view
uniform mat4 projection = mat4(1.0);

void main() {
    gl_Position = projection * model * vec4(position.x, position.y, 0.0, 1.0);
}