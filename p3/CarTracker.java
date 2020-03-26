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
  private static CarHeaps cars = new CarHeaps(1000);

  private static void readFile() {
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
    readFile();

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
        String info = "";
        System.out.print("Enter VIN: ");
        info += sc.next() + ":";
        System.out.print("Enter make: ");
        info += sc.next() + ":";
        System.out.print("Enter model: ");
        info += sc.next() + ":";
        System.out.print("Enter price: ");
        info += sc.next() + ":";
        System.out.print("Enter mileage: ");
        info += sc.next() + ":";
        System.out.print("Enter color: ");
        info += sc.next();
        Car c = new Car(info);
        cars.add(c);
      } else if (choice == 2) {
        System.out.println("Update Car\n");
        System.out.print("Enter VIN of car to update: ");
        String VIN = sc.next();
        System.out.println("Finding car...");
        int i = cars.indexOfCar(VIN);
        if (i == -1) {
          System.out.println("\nERROR: CAR NOT FOUND\n");
        } else {
          System.out.println("Car found!");
          System.out.println("1. Update Price\n2. Update Mileage\n3. Update Color");
          System.out.print("Enter choice: ");
          int ch = sc.nextInt();
          if (ch == 1) {
            System.out.print("Enter New Price: ");
            int p = sc.nextInt();
            cars.updatePrice(i, p);
          } else if (ch == 2) {
            System.out.print("Enter New Mileage: ");
            int m = sc.nextInt();
            cars.updateMileage(i, m);
          } else if (ch == 3) {
            System.out.print("Enter Color: ");
            String c = sc.next();
            cars.updateColor(i, c);
          } else {
            System.out.println("\nERROR: invalid choice\n");
          }
        }
      } else if (choice == 3) {
        System.out.println("Remove Car\n");
        System.out.print("Enter VIN of car to remove: ");
        String VIN = sc.next();
        cars.removeByVIN(VIN);
        System.out.println("Car removed!");
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
        System.out.print("Enter Make: ");
        String make = sc.next();
        System.out.print("Enter Model: ");
        String model = sc.next();
        Car c = cars.getMinPrice(make, model);
        System.out.println(c.toString());
      } else if (choice == 7) {
        System.out.println("Get Lowest Mileage by Model\n");
        System.out.print("Enter Make: ");
        String make = sc.next();
        System.out.print("Enter Model: ");
        String model = sc.next();
        Car c = cars.getMinMileage(make, model);
        System.out.println(c.toString());
      } else if (choice == 8) {
        System.out.println("Exit\n");
        sc.close();
        break;
      } else {
        System.out.println("Bad input, try again\n");
      }

    }

  }
}
