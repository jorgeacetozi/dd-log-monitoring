package com.jorgeacetozi.dd.logmonitoring.storage.circularBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CircularBuffer<T> {

  private T[] array;
  private int start, end;
  private int size, capacity;

  @SuppressWarnings("unchecked")
  public CircularBuffer(int capacity) {
    array = (T[]) new Object[capacity];
    this.capacity = capacity;
  }

  // O(1) time complexity
  public void insertEnd(T item) {
    if (isFull()) {
      throw new RuntimeException("The array is full");
    }
    array[end] = item;
    end = (end + 1) % capacity;
    size++;
  }

  // O(1) time complexity
  public T removeStart() {
    if (isEmpty()) {
      throw new RuntimeException("The array is empty");
    }
    T temp = array[start];
    array[start] = null;
    start = (start + 1) % capacity;
    size--;
    return temp;
  }

  // O(1) time complexity
  public Optional<T> get(int i) {
    if (array[i] == null) {
      return Optional.empty();
    } else {
      return Optional.of(array[i]);
    }
  }

  public int getSize() {
    return size;
  }

  public boolean isFull() {
    return size == capacity ? true : false;
  }

  public boolean isEmpty() {
    return size == 0 ? true : false;
  }

  public int getSymmetricIndexOf(int index) {
    int symmetricIndex = (end - 1) - index;
    if (symmetricIndex < 0) {
      symmetricIndex = capacity + symmetricIndex;
    }
    return symmetricIndex;
  }

  public List<T> getLastItems(int pastSeconds) {
    int actualStart = end - pastSeconds;
    if (actualStart < 0) {
      actualStart = capacity + actualStart;
    }

    int actualEnd = end - 1;
    if (actualEnd < 0) {
      actualEnd = actualEnd + capacity;
    }

    List<T> subList = new ArrayList<>();
    while (actualStart != actualEnd) {
      subList.add(get(actualStart).get());
      actualStart = (actualStart + 1) % capacity;
    }
    return subList;
  }

  public void print() {
    int startCopy = start;
    int endCopy = end;

    if (isFull()) {
      do {
        System.out.print(get(startCopy).get());
        startCopy = (startCopy + 1) % capacity;
      } while (startCopy != endCopy);
    }
    System.out.println();
    System.out.println("-----");
  }

}
