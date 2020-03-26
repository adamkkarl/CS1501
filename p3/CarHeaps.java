import java.util.*;

class CarHeaps {
  CarIndexMinPQ price;
  CarIndexMinPQ mileage;
  List<Car> carList = new ArrayList<Car>();
  int size = 0;
  int index = 0;
  int max;

  public CarHeaps(int n) {
    max = n;
    price = new CarIndexMinPQ(n, "price");
    mileage = new CarIndexMinPQ(n, "mileage");
  }

  public void add(Car c) {
    if (size >= max) {
      System.out.println("Heap is full, please increase heap size");
      return;
    }
    carList.add(c);
    price.insert(index, c);
    mileage.insert(index, c);
    size++;
    index++;
  }

  public int indexOfCar(String VIN) {
    //return index of car with matching VIN, of -1 if not found
    //TODO hashmap
    for (int i = 0; i<size; i++) {
      if (VIN.equals(carList.get(i).getVIN())) {
        return i;
      }
    }
    return -1;
  }

  public void updateColor (int i, String color){
    Car car = carList.get(i);
		car.setColor(color);
		price.changeCar(i, car);
		mileage.changeCar(i, car);
		carList.set(i, car);
	}

  public void updatePrice(int i, int p){
    Car car = carList.get(i);
		car.setPrice(p);
		price.changeCar(i, car);
		mileage.changeCar(i, car);
		carList.set(i, car);
	}

  public void updateMileage(int i, int m){
		Car car = carList.get(i);
		car.setMileage(m);
		price.changeCar(i, car);
		mileage.changeCar(i, car);
		carList.set(i, car);
	}

  public void removeByVIN(String VIN) {
    price.delCar(VIN);
    mileage.delCar(VIN);
    int i = indexOfCar(VIN);
    carList.set(i, carList.get(size-1)); //put last car in its place
		carList.remove(size-1); //remove car
    size-=1;
  }

  public Car getMinMileage() {
    if (size == 0) return null;
    return mileage.minCar();
  }

  public Car getMinPrice() {
    if (size == 0) return null;
    return price.minCar();
  }

  public Car getMinMileage(String make, String model) {
      CarIndexMinPQ carsOfMake = new CarIndexMinPQ(max, "mileage");
      for (Car c : carList) {
        if (c.getModel().equals(model) && c.getMake().equals(make)) {
          carsOfMake.insert(carsOfMake.size(), c);
        }
      }
      if (carsOfMake.size() == 0){
        System.out.println("No cars of make and model found.");
        return null;
      }
      return carsOfMake.minCar();
  }

  public Car getMinPrice(String make, String model) {
      CarIndexMinPQ carsOfMake = new CarIndexMinPQ(max, "price");
      for (Car c : carList) {
        if (c.getModel().equals(model) && c.getMake().equals(make)) {
          carsOfMake.insert(carsOfMake.size(), c);
        }
      }
      if (carsOfMake.size() == 0){
        System.out.println("No cars of make and model found.");
        return null;
      }
      return carsOfMake.minCar();
  }

}
