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

  private int count;
  private float[] fovs;
  private FloatBuffer[] coordFrames;

  @Override
  public int getCount() {
    return count;
  }

  @Override
  public float getFov(int i) {
    return fovs[i];
  }

  @Override
  public FloatBuffer getCoordFrame(int i) {
    return coordFrames[i];
  }

  public Cube() {
    count = 6;
    fovs = new float[] {90,90,90,90,90,90};
    coordFrames = new FloatBuffer[] {
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
}
