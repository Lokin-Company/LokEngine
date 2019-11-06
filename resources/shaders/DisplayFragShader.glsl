#version 330 core

in vec2 uvposition;
layout(location = 0) out vec4 color;
uniform sampler2D frame;

void main() {
	color = texture(frame, uvposition);
}