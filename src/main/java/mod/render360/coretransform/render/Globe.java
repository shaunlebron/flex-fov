package mod.render360.coretransform.render;
import java.nio.FloatBuffer;

public interface Globe {
  public int getCount();
  public float getFov(int i);
  public FloatBuffer getCoordFrame(int i);
}
