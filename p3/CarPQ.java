


class CarPQ {
  IndexMinPQ<Car> price;
  IndexMinPQ<Car> mileage;
  List<Car> carsList = new ArrayList<Car>();
  int size = 0;
  int max;

  public CarPQ(int n) {
    max = n;
    prices = new IndexMinPQ(n, "price");
    mileage = new IndexMinPQ(n, "mileage");
  }

  public void add(Car c) {
    if (size >= max) {
      System.out.println("Heap is full, please increase heap size");
      return;
    }
    carsList.add(c);
    prices.insert(size, c);
    mileage.insert(size, c);
    size++
  }

  public car getMinPrice() {
    if (size == 0) return null;
    return carsList.get(price.minIndex())
  }


}
