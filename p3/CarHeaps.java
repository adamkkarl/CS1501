import java.util.*;

class CarHeaps {
  CarIndexMinPQ price;
  CarIndexMinPQ mileage;
  List<Car> carList = new ArrayList<Car>();
  int size = 0;
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
    price.insert(size, c);
    mileage.insert(size, c);
    size++;
  }

  public int indexOfCar(String VIN) {
    //return index of car with matching VIN, of -1 if not found
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

  public void delete(int i) {
    price.delete(i);
    mileage.delete(i);
    carList.set(i, carList.get(size-1)); //put last car in its place
		carList.remove(size-1); //remove car
    size-=1;
  }

  public Car getMinMileage() {
    if (size == 0) return null;
    return carList.get(mileage.minIndex());
  }

  public Car getMinPrice() {
    if (size == 0) return null;
    return carList.get(price.minIndex());
  }

}
