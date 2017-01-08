package mod.render360.coretransform;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import mod.render360.coretransform.render.RenderMethod;

public class Shader {

	public float fovx;
	
	private int shaderProgram;
	private int vshader;
	private int fshader;
	private int vbo;
	
	private static final String vertexShader = "#version 130//\n /* The position of the vertex as two-dimensional vector */ in vec2 vertex; /* Write interpolated texture coordinate to fragment shader */ out vec2 texcoord; void main(void) { gl_Position = vec4(vertex, 0.0, 1.0); /* * Compute texture coordinate by simply * interval-mapping from [-1..+1] to [0..1] */ texcoord = vertex * 0.5 + vec2(0.5, 0.5); } ";
	
	public void createShaderProgram(RenderMethod renderMethod) throws IOException {
		shaderProgram = GL20.glCreateProgram();
		vshader = createShader(vertexShader, GL20.GL_VERTEX_SHADER);
		fshader = createShader(renderMethod.getFragmentShader(), GL20.GL_FRAGMENT_SHADER);
		GL20.glAttachShader(shaderProgram, vshader);
		GL20.glAttachShader(shaderProgram, fshader);
		GL20.glBindAttribLocation(shaderProgram, 0, "vertex");
		GL30.glBindFragDataLocation(shaderProgram, 0, "color");
		GL20.glLinkProgram(shaderProgram);
		int linked = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
		String programLog = GL20.glGetProgramInfoLog(shaderProgram, GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH));
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		
		//init vbo
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		ByteBuffer bb = BufferUtils.createByteBuffer(2 * 6);
		bb.put((byte) -1).put((byte) -1);
		bb.put((byte) 1).put((byte) -1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) -1).put((byte) 1);
		bb.put((byte) -1).put((byte) -1);
		bb.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bb, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private int createShader(String resource, int type) throws IOException {
		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, resource);
		GL20.glCompileShader(shader);
		int compiled = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
		String shaderLog = GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
		if (shaderLog.trim().length() > 0) {
			System.err.println(shaderLog);
		}
		if (compiled == 0) {
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}
	
	public void deleteShaderProgram() {
		GL15.glDeleteBuffers(vbo);
		GL20.glDetachShader(shaderProgram, vshader);
		GL20.glDetachShader(shaderProgram, fshader);
		GL20.glDeleteShader(vshader);
		vshader = 0;
		GL20.glDeleteShader(fshader);
		fshader = 0;
		GL20.glDeleteProgram(shaderProgram);
		shaderProgram = 0;
	}
	
	/**
	 * Get shader from file.
	 */
	public CharSequence getCode(InputStream is) throws IOException {
		final DataInputStream dataStream = new DataInputStream(is);
		byte[] shaderCode = new byte[dataStream.available()];
		dataStream.readFully(shaderCode);
		return new String(shaderCode);
	}
	
	public int getShaderProgram() {
		return shaderProgram;
	}
	
	public int getVbo() {
		return vbo;
	}
}
