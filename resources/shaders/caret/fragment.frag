#version 400 core

uniform vec4 color; // set in CaretLayer

out vec4 out_color;

void main() {
    out_color = color;
}