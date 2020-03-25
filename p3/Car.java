/*
 * Adam Karl
 * akk67
 * CS 1501
 * Project 3
 */

class Car {
  String VIN, make, model, color;
  int price, mileage;

  public String toString() {
    return "VIN: " + VIN + "\nMake: " + make + "\nModel: " + model +
    "\nPrice: $" + price + "\nMileage: " + mileage + "\nColor:" + color;
  }

  public String getVIN() {
    return this.VIN;
  }

  public int getPrice() {
    return this.price;
  }

  public int getMileage() {
    return this.mileage;
  }

  public int comparePriceTo(Car c) {
    return Integer.compare(this.getPrice(), c.getPrice());
  }

  public int compareMileageTo(Car c) {
    return Integer.compare(this.getMileage(), c.getMileage());
  }

  public boolean equals(Car c) {
    if (this.getVIN().equals(c.getVIN())) {
      return true;
    } else {
      return false;
    }
  }

  public Car (String info) {
    String[] i = info.split(":");
    this.VIN = i[0];
    this.make = i[1];
    this.model = i[2];
    this.price = Integer.parseInt(i[3]);
    this.mileage = Integer.parseInt(i[4]);
    this.color = i[5];
  }
}
