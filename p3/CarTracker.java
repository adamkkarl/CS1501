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
      carsList.add(car);
    }

    private void printCars() {
      int numCars = carsList.size();
      for (int i=0; i<numCars; i++) {
        System.out.println(carsList.get(i).toString());
      }
    }

    Heap() {
      carsList = new ArrayList<Car>();
    }

    private static class Car {
      String VIN, make, model, color;
      int price, mileage;

    public String toString() {
      return VIN + " " + make + " " + model + " " + price + " " + mileage + " " + color;
    }

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
    cars.printCars();
  }

  public static void main(String[] args) {
    Heap cars = new Heap();
    readFile(cars);

    Scanner sc = new Scanner(System.in);
    while(true) {
      System.out.print("1. Add a car\n" +
      "2. Update a car\n" +
      "3. Remove a specific car from consideration\n" +
      "4. Retrieve the lowest price car\n" +
      "5. Retrieve the lowest mileage car\n" +
      "6. Retrieve the lowest price car by make and model\n" +
      "7. Retrieve the lowest mileage car by make and model\n" +
      "8. Exit\n" +
      "Enter the number of the chosen operation: ");
      int choice = sc.nextInt();
      System.out.println();
      if (choice == 1) {
        System.out.println("Add Car\n");
      } else if (choice == 2) {
        System.out.println("Update Car\n");
      } else if (choice == 3) {
        System.out.println("Remove Car\n");
      } else if (choice == 4) {
        System.out.println("Lowest Price\n");
      } else if (choice == 5) {
        System.out.println("Add Mileage\n");
      } else if (choice == 6) {
        System.out.println("Add Price by Model\n");
      } else if (choice == 7) {
        System.out.println("Add Mileage by Model\n");
      } else if (choice == 8) {
        System.out.println("Exit\n");
        break;
      } else {
        System.out.println("Bad input, try again\n");
      }

    }

  }
}
