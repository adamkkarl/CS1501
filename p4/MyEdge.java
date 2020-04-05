

public class MyEdge {
  private final int v;
  private final int w;
  private final String type;
  private final int bandwidth;
  private final int length;
  private final float latency;

  public MyEdge(int v, int w, String type, int bandwidth, int length) {
    this.v = v;
    this.w = w;
    this.type = type;
    this.bandwidth = bandwidth;
    this.length = length;
    if (type.equals("copper")){
      this.latency = length / (float) 230000000;
    } else if (type.equals("optical")) {
      this.latency = length / (float) 200000000;
    } else {
      throw new IllegalArgumentException("Type must be copper or optical");
    }
  }

  public int from() {
    return this.v;
  }

  public int to() {
    return this.w;
  }

  public float getLatency() {
    return this.latency;
  }
}
