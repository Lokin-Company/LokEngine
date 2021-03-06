#version 330 core

layout(location = 0) in vec2 vertexPosition;
layout(location = 1) in vec2 UVPosition;
uniform mat4 Projection;
out vec2 uvposition;
void main() {
    uvposition = UVPosition;
    gl_Position = Projection * vec4(vertexPosition.x, vertexPosition.y, 0, 1);
}