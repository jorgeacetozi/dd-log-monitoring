package com.jorgeacetozi.dd.logmonitoring.storage;

import java.util.List;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.storage.circularBuffer.CircularBuffer;

public class InMemoryStorage implements Storage {

  private final CircularBuffer<DataPoint> circularBuffer;
  private final int capacity;

  public InMemoryStorage(int capacity) {
    circularBuffer = new CircularBuffer<>(capacity);
    insertEmptyDataPoints(capacity);
    this.capacity = capacity;
  }
  
  @Override
  public void insert(DataPoint dataPoint) {
    if (circularBuffer.isFull()) {
      circularBuffer.removeStart();
    }
    circularBuffer.insertEnd(dataPoint);
  }
  
  @Override
  public void insert(int index, DataPoint newDataPoint) {
    index = circularBuffer.getSymmetricIndexOf(index);
    if (circularBuffer.get(index).isPresent()) {
      DataPoint existingDataPoint = circularBuffer.get(index).get();
      existingDataPoint.add(newDataPoint.getRequests());
    }
  }

  @Override
  public void insertEmptyDataPoints(int numberOfSeconds) {
    for (int i = 0; i < numberOfSeconds; i++) {
      insert(new DataPoint());
    }
  }

  @Override
  public List<DataPoint> getDataPoints(int pastSeconds) {
    return circularBuffer.getLastItems(pastSeconds);
  }
  
  @Override
  public boolean isFull() {
    return circularBuffer.isFull();
  }

  @Override
  public int getCapacity() {
    return this.capacity;
  }

  @Override
  public void print() {
    circularBuffer.print();
  }
}
