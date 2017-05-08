package mod.render360.coretransform.render;

public class Cube implements Globe {
  public static int count = 6;
  public static float fov = 90;
  public static float[][] coordFrames = new float[][] {
    // front
    new float[] {
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1
    },
    // back
    new float[] {
     -1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0,-1, 0,
      0, 0, 0, 1
    },
    // left
    new float[] {
      0, 0, 1, 0,
      0, 1, 0, 0,
     -1, 0, 0, 0,
      0, 0, 0, 1
    },
    // right
    new float[] {
      0, 0,-1, 0,
      0, 1, 0, 0,
      1, 0, 0, 0,
      0, 0, 0, 1
    },
    // up
    new float[] {
      1, 0, 0, 0,
      0, 0,-1, 0,
      0, 1, 0, 0,
      0, 0, 0, 1
    },
    // down
    new float[] {
      1, 0, 0, 0,
      0, 0, 1, 0,
      0,-1, 0, 0,
      0, 0, 0, 1
    },
  };
}
