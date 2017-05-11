package mod.render360.coretransform.render;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Trism implements Globe {
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
  public String getName() {
    return "Trism";
  }

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

  public Trism() {
    count = 5;
    fovs = new float[] {120,120,120,128,128};

    float c = (float)Math.cos((float)Math.PI / 6);
    float s = (float)Math.sin((float)Math.PI / 6);
    coordFrames = new FloatBuffer[] {
      // left
      createFloatBuffer(new float[] {
        s, 0,-c, 0,
        0, 1, 0, 0,
        c, 0, s, 0,
        0, 0, 0, 1
      }),
      // right
      createFloatBuffer(new float[] {
        s, 0, c, 0,
        0, 1, 0, 0,
       -c, 0, s, 0,
        0, 0, 0, 1
      }),
      // back
      createFloatBuffer(new float[] {
       -1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0,-1, 0,
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
