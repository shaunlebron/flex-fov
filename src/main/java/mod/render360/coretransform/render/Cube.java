package mod.render360.coretransform.render;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Cube implements Globe {
  public static FloatBuffer createFloatBuffer(float[] floats) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    buffer.put(floats);
    buffer.rewind();
    return buffer;
  }

  public static int count = 6;
  public static float[] fovs = new float[] {90,90,90,90,90,90};
  public static FloatBuffer[] coordFrames = new FloatBuffer[] {
    // front
    createFloatBuffer(new float[] {
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1
    }),
    // back
    createFloatBuffer(new float[] {
     -1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0,-1, 0,
      0, 0, 0, 1
    }),
    // left
    createFloatBuffer(new float[] {
      0, 0, 1, 0,
      0, 1, 0, 0,
     -1, 0, 0, 0,
      0, 0, 0, 1
    }),
    // right
    createFloatBuffer(new float[] {
      0, 0,-1, 0,
      0, 1, 0, 0,
      1, 0, 0, 0,
      0, 0, 0, 1
    }),
    // up
    createFloatBuffer(new float[] {
      1, 0, 0, 0,
      0, 0,-1, 0,
      0, 1, 0, 0,
      0, 0, 0, 1
    }),
    // down
    createFloatBuffer(new float[] {
      1, 0, 0, 0,
      0, 0, 1, 0,
      0,-1, 0, 0,
      0, 0, 0, 1
    }),
  };
}
