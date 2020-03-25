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

  private static void readFile(CarHeaps cars) {
  /*
   * Given a CarHeaps object, read the file "cars.txt" into it
   */
    try {
      File f = new File("cars.txt");
      Scanner sc = new Scanner(f);
      while(sc.hasNext()) {
        String line = sc.nextLine();
        if (line.charAt(0) != '#') { //don't read in comment lines
          Car c = new Car(line);
          cars.add(c);
        }
      }
      sc.close();
    } catch (Exception e) {
      System.out.println("Issue reading cars.txt");
    }
  }

  public static void main(String[] args) {
    CarHeaps cars = new CarHeaps(100);
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
        System.out.println("Get Lowest Price\n");
        Car c = cars.getMinPrice();
        System.out.println(c.toString());
      } else if (choice == 5) {
        System.out.println("Get Lowest Mileage\n");
        Car c = cars.getMinMileage();
        System.out.println(c.toString());
      } else if (choice == 6) {
        System.out.println("Get Lowest Price by Model\n");
      } else if (choice == 7) {
        System.out.println("Get Lowest Mileage by Model\n");
      } else if (choice == 8) {
        System.out.println("Exit\n");
        break;
      } else {
        System.out.println("Bad input, try again\n");
      }

    }

  }
}
