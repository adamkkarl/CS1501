/******************************************************************************
 *  Compilation:  javac CarIndexMinPQ.java
 *  Execution:    java CarIndexMinPQ
 *  Dependencies: StdOut.java
 *
 *  Minimum-oriented indexed PQ implementation using a binary heap.
 *
 ******************************************************************************/
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.*;
import java.io.File;

/**
 *  Heavily modified version of IndexMinPQ.java from the textbook
 */
public class CarIndexMinPQ { ////<Car extends Comparable<Car>> implements Iterable<Integer>
    private int maxN;        // maximum number of elements on PQ
    private int n;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Car[] cars;      // cars[i] = priority of i

    String type; //either "price" or "mileage"

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     * @param  maxN the cars on this priority queue are index from {@code 0}
     *         {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public CarIndexMinPQ(int maxN, String type) {
        if (maxN < 0) throw new IllegalArgumentException();
        this.maxN = maxN;
        if (!type.equals("mileage") && !type.equals("price")) throw new IllegalArgumentException();
        this.type = type;
        n = 0;
        cars = new Car[maxN + 1];
        pq   = new int[maxN + 1];
        qp   = new int[maxN + 1];
        for (int i = 0; i <= maxN; i++)
            qp[i] = -1;
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param  i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(int i) {
        validateIndex(i);
        return qp[i] != -1;
    }

    /**
     * Returns the number of cars on this priority queue.
     *
     * @return the number of cars on this priority queue
     */
    public int size() {
        return n;
    }

    /**
     * Associates car with index {@code i}.
     *
     * @param  i an index
     * @param  car the car to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *         with index {@code i}
     */
    public void insert(int i, Car car) {
        validateIndex(i);
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        n++;
        qp[i] = n;
        pq[n] = i;
        cars[i] = car;
        swim(n);
    }

    /**
     * Returns an index associated with a minimum car.
     *
     * @return an index associated with a minimum car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int minIndex() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    /**
     * Returns a minimum car.
     *
     * @return a minimum car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Car minCar() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        return cars[pq[1]];
    }

    /**
     * Removes a minimum car and returns its associated index.
     * @return an index associated with a minimum car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int delMin() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        int min = pq[1];
        exch(1, n--);
        sink(1);
        assert min == pq[n+1];
        qp[min] = -1;        // delete
        cars[min] = null;    // to help with garbage collection
        pq[n+1] = -1;        // not needed
        return min;
    }

    public void delCar(String vin) {
      for (int i=0; i<maxN; i++) {
        if (cars[i].getVIN().equals(vin)) {
          delete(i);
          return;
        }
      }
    }

    /**
     * Returns the car associated with index {@code i}.
     *
     * @param  i the index of the car to return
     * @return the car associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public Car carOf(int i) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        else return cars[i];
    }

    /**
     * Change the car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the car to change
     * @param  car change the car associated with index {@code i} to this car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public void changeCar(int i, Car car) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        cars[i] = car;
        swim(qp[i]);
        sink(qp[i]);
    }

    /**
     * Change the car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the car to change
     * @param  car change the car associated with index {@code i} to this car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @deprecated Replaced by {@code changeCar(int, Car)}.
     */
    @Deprecated
    public void change(int i, Car car) {
        changeCar(i, car);
    }

    /**
     * Remove the car associated with index {@code i}.
     *
     * @param  i the index of the car to remove
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public void delete(int i) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = qp[i];
        exch(index, n--);
        swim(index);
        sink(index);
        cars[i] = null;
        qp[i] = -1;
    }

    // throw an IllegalArgumentException if i is an invalid index
    private void validateIndex(int i) {
        if (i < 0) throw new IllegalArgumentException("index is negative: " + i);
        if (i >= maxN) throw new IllegalArgumentException("index >= capacity: " + i);
    }

   /***************************************************************************
    * General helper functions.
    ***************************************************************************/
    private boolean greater(int i, int j) {
        if (this.type == "price") {
          return cars[pq[i]].comparePriceTo(cars[pq[j]]) > 0;
        }
        return cars[pq[i]].compareMileageTo(cars[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }


   /***************************************************************************
    * Heap helper functions.
    ***************************************************************************/
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    /**
     * Unit tests the {@code CarIndexMinPQ} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
      CarIndexMinPQ pq = new CarIndexMinPQ(100, "price");
      try {
        File f = new File("cars.txt");
        Scanner sc = new Scanner(f);
        while(sc.hasNext()) {
          String line = sc.nextLine();
          if (line.charAt(0) != '#') { //don't read in comment lines
            Car c = new Car(line);
            pq.insert(pq.size(), c);
          }
        }
        sc.close();
      } catch (Exception e) {
        System.out.println("Issue reading cars.txt");
      }

    while (!pq.isEmpty()) {
      System.out.println(pq.minCar().toString() + "\n");
      pq.delMin();

    }
  }
}
