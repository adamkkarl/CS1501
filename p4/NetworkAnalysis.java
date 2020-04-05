import java.util.*;
import java.io.*;

public class NetworkAnalysis {
  private static ArrayList<ArrayList<MyEdge>> adjList;

  private static void readFile(String fileName) {
    try {
      File f = new File(fileName);
      Scanner sc = new Scanner(f);
      int vertices = Integer.parseInt(sc.nextLine());
      adjList = new ArrayList<ArrayList<MyEdge>>(); //first line
      for(int i=0; i<vertices; i++) {
        adjList.add(new ArrayList<MyEdge>());
      }
      while(sc.hasNext()) {
        String line = sc.nextLine();

        if (!line.isBlank()) { //don't read in comment lines
          String[] info = line.split(" ");
          int v = Integer.parseInt(info[0]);
          int w = Integer.parseInt(info[1]);
          String type = info[2];
          int bandwidth = Integer.parseInt(info[3]);
          int length = Integer.parseInt(info[4]);
          adjList.get(v).add(new MyEdge(v, w, type, bandwidth, length));
          adjList.get(w).add(new MyEdge(w, v, type, bandwidth, length));
        }
      }
      sc.close();
    } catch (Exception e) {
      System.out.println("Issue reading " + fileName + "\n" + e);
    }
  }

  public static void main(String[] args) {
    readFile(args[0]);
//    EdgeWeightedDigraph G = new EdgeWeightedDigraph(5);
  }
}
