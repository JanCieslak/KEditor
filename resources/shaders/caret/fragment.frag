#version 400 core

uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);

out vec4 out_color;

void main() {
    out_color = color;
}