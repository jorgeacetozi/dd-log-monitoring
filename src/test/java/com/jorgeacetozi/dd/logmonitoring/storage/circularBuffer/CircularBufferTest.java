package com.jorgeacetozi.dd.logmonitoring.storage.circularBuffer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class CircularBufferTest {

  CircularBuffer<String> circularBuffer = new CircularBuffer<>(5);

  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenRetrievingItemFromEmptyArray() {
    circularBuffer.removeStart();
  }

  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenInsertingOnAFullArray() {
    circularBuffer.insertEnd("jorge");
    circularBuffer.insertEnd("cobra");
    circularBuffer.insertEnd("xuxa");
    circularBuffer.insertEnd("xuxinha");
    circularBuffer.insertEnd("july");
    circularBuffer.insertEnd("sorry, array is full");
  }

  @Test
  public void shouldInsertIntoCircularArrayWithNoRotations() {
    circularBuffer.insertEnd("jorge");
    circularBuffer.insertEnd("cobra");
    circularBuffer.insertEnd("xuxa");
    circularBuffer.insertEnd("xuxinha");
    circularBuffer.insertEnd("july");
    assertThat(circularBuffer.isFull(), equalTo(true));
    assertThat(circularBuffer.getSize(), equalTo(5));
  }

  @Test
  public void shouldRemoveFromCircularArray() {
    circularBuffer.insertEnd("jorge");
    circularBuffer.insertEnd("cobra");
    circularBuffer.insertEnd("xuxa");
    assertThat(circularBuffer.getSize(), equalTo(3));

    String firstItemInserted = circularBuffer.removeStart();
    assertThat(firstItemInserted, equalTo("jorge"));
    assertThat(circularBuffer.getSize(), equalTo(2));

    String secondItemInserted = circularBuffer.removeStart();
    assertThat(secondItemInserted, equalTo("cobra"));
    assertThat(circularBuffer.getSize(), equalTo(1));

    String thirdItemInserted = circularBuffer.removeStart();
    assertThat(thirdItemInserted, equalTo("xuxa"));
    assertThat(circularBuffer.getSize(), equalTo(0));
  }

  @Test
  public void shouldGetActualEndAfterRotations() {
    circularBuffer.insertEnd("a");
    circularBuffer.insertEnd("b");
    circularBuffer.insertEnd("c");
    circularBuffer.insertEnd("d");
    circularBuffer.insertEnd("e");

    // [a|b|c|d|e], start = end = 0
    assertThat(circularBuffer.getSymmetricIndexOf(0), equalTo(4)); // symmetric of 'a' is 'e'
    assertThat(circularBuffer.getSymmetricIndexOf(1), equalTo(3)); // symmetric of 'b' is 'd'
    assertThat(circularBuffer.getSymmetricIndexOf(2), equalTo(2)); // symmetric of 'c' is 'c'
    assertThat(circularBuffer.getSymmetricIndexOf(3), equalTo(1)); // symmetric of 'd' is 'b'
    assertThat(circularBuffer.getSymmetricIndexOf(4), equalTo(0)); // symmetric of 'e' is 'a'

    // Move the positions of start and end by 1 unit ahead
    // Now, the position of the first item inserted is 1
    // And the position of last item inserted is 0
    circularBuffer.removeStart();
    circularBuffer.insertEnd("f");

    // So, the symmetric index of 0 is no longer 4 as before, because the start and end were moved
    // to position 1: [f|a|b|c|d], start = end = 1
    assertThat(circularBuffer.getSymmetricIndexOf(0), equalTo(0)); // symmetric of 'a' is 'f'
    assertThat(circularBuffer.getSymmetricIndexOf(1), equalTo(4)); // symmetric of 'b' is 'd'
    assertThat(circularBuffer.getSymmetricIndexOf(2), equalTo(3)); // symmetric of 'c' is 'c'
    assertThat(circularBuffer.getSymmetricIndexOf(3), equalTo(2)); // symmetric of 'd' is 'b'
    assertThat(circularBuffer.getSymmetricIndexOf(4), equalTo(1)); // symmetric of 'f' is 'a'
  }
}
