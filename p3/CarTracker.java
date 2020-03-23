// VIN:Make:Model:Price:Mileage:Color
import java.util.*;
import java.io.File;

/*
 * Adam Karl
 * akk67
 * CS 1501
 * Project 3
 */

public class CarTracker {
  private static class Heap {
    List<Car> carsList;

    private void addCarFromFile(String info) {
      Car car = new Car(info);
    }

    Heap() {
      carsList = new ArrayList<Car>();
    }

    private static class Car {
      String VIN, make, model, color;
      int price, mileage;

      Car (String info) {
        String[] i = info.split(":");
        VIN = i[0];
        make = i[1];
        model = i[2];
        price = Integer.parseInt(i[3]);
        mileage = Integer.parseInt(i[4]);
        color = i[5];
      }
    }
  }

  private static void readFile(Heap cars) {
  /*
   * Given a Heap object, read the file "cars.txt" into the heap
   */
    try {
      File f = new File("cars.txt");
      Scanner sc = new Scanner(f);
      while(sc.hasNext()) {
        String line = sc.nextLine();
        if (line.charAt(0) != '#') { //don't read in comment lines
          cars.addCarFromFile(line);
        }
      }
      sc.close();
    } catch (Exception e) {
      System.out.println("Issue reading cars.txt");
    }
  }

  public static void main(String[] args) {
    Heap cars = new Heap();
    readFile(cars);

  }
}
