package com.jorgeacetozi.dd.logmonitoring.storage;

import java.util.List;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;

public interface Storage {

  void insert(DataPoint dataPoint);

  void insert(int index, DataPoint dataPoint);

  void insertEmptyDataPoints(int numberOfSeconds);

  List<DataPoint> getDataPoints(int pastSeconds);

  boolean isFull();

  int getCapacity();

  void print();

}
