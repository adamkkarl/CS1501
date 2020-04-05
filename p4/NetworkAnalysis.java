import java.util.*;
import java.io.*;

public class NetworkAnalysis {
  private static ArrayList<ArrayList<MyEdge>> adjList;
  private static int vertices;

  private static void readFile(String fileName) {
    try {
      File f = new File(fileName);
      Scanner sc = new Scanner(f);
      vertices = Integer.parseInt(sc.nextLine());
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

    Scanner sc = new Scanner(System.in);
    int option = -1;
    while (option != 4) {
      System.out.print("1. Find lowest latency path between 2 points (return bandwidth)\n"
      + "2. Is the graph copper-only connected?\n"
      + "3. Would the graph remain connected if any 2 vertces fail?\n"
      + "4. Quit\nEnter choice: ");
      option = sc.nextInt();

      if (option == 1) {
        System.out.println("Vertices are labeled 0 through " + (vertices-1));
        System.out.print("Enter first vertex: ");
        int v = sc.nextInt();
        System.out.print("Enter second vertex: ");
        int w = sc.nextInt();

        EdgeWeightedDigraph G = new EdgeWeightedDigraph(vertices);
        for (ArrayList<MyEdge> list : adjList) {
          for (MyEdge myEdge : list) {
            DirectedEdge e = new DirectedEdge(myEdge.from(), myEdge.to(), myEdge.getLatency());
            G.addEdge(e);
          }
        }

        DijkstraAllPairsSP allPairs = new DijkstraAllPairsSP(G);
        Iterable<DirectedEdge> path = allPairs.path(v,w);
        System.out.println(path);
        int bandwidth = 0;
        for (DirectedEdge p : path) {
          int myV = p.from();
          int myW = p.to();
          double minLatency = -1;
          int minLatencyBandwidth = 0;
          for (MyEdge e : adjList.get(myV)) { //match path to its bandwidth
            if (e.from() == myV && e.to() == myW) {
              if (minLatency == -1) {
                minLatency = e.getLatency();
                minLatencyBandwidth = e.getBandwidth();
              } else if (e.getLatency() < minLatency) {
                minLatency = e.getLatency();
                minLatencyBandwidth = e.getBandwidth();
              }
            }
          }
          bandwidth += minLatencyBandwidth;
        }
        System.out.println("\nBandwidth: " + bandwidth + "b/s\n");

      } else if (option == 2) {
        EdgeWeightedDigraph copperOnlyG = new EdgeWeightedDigraph(vertices);
        for (ArrayList<MyEdge> list : adjList) {
          for (MyEdge myEdge : list) {
            if (myEdge.getType().equals("copper")) {
              DirectedEdge e = new DirectedEdge(myEdge.from(), myEdge.to(), myEdge.getLatency());
              copperOnlyG.addEdge(e);
            }
          }
        }
        DijkstraAllPairsSP allPairs = new DijkstraAllPairsSP(copperOnlyG);
        boolean connected = true;
        for (int myV=0; myV<vertices-1; myV++) {
          for (int myW=myV+1; myW<vertices; myW++) {
            if (connected && !allPairs.hasPath(myV, myW)) {
              connected = false;
            }
          }
        }
        System.out.println();
        if (!connected) {
          System.out.print("NOT ");
        }
        System.out.println("copper connected\n");

      } else if (option == 3) {

      }
    }


  }
}
