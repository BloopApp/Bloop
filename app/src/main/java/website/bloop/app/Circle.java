package website.bloop.app;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static website.bloop.app.CircleRenderer.loadShader;

/**
 * Drawing circle logic help:
 * http://stackoverflow.com/questions/18140117/how-to-draw-basic-circle-in-opengl-es-2-0-android
 */

public class Circle {
    private int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle;
    private FloatBuffer vertexBuffer;
    private float vertices[] = new float[364 * 3];

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.698039216f, 0.874509804f, 0.858823529f, 1.0f };

    public Circle() {
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for(int i = 1; i < 364; i++) {
            vertices[(i * 3) + 0] = (float) (0.5 * Math.cos((3.14/180) * (float) i));
            vertices[(i * 3) + 1] = (float) (0.5 * Math.sin((3.14/180) * (float) i));
            vertices[(i * 3) + 2] = 0;
        }

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
    }

    public void draw (float[] mvpMatrix){
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the circle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the circle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 364);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
