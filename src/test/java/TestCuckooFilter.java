import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCuckooFilter {
  public static final int CAPACITY = 1024;
  CuckooFilter filter = new CuckooFilter(CAPACITY);
  ThreadLocalRandom rand = ThreadLocalRandom.current();

  @Test
  public void sanityCheckInsert() {
    int n = 0;
    while (filter.insert(n)) {
      for (int i = 0; i <= n; i++) {
        assertTrue(filter.contains(i));
      }
      n++;
    }
  }

  @Test
  public void sanityCheckRemove() {
    for (int i = 0; i < 2 * CAPACITY; i++) {
      long n = rand.nextLong();
      assertTrue(filter.insert(n));
      assertTrue(filter.remove(n));
      assertFalse(filter.contains(n));
    }
  }

  @Test
  public void testLoadFactor() {
    for (int i = 0; i < 10; i++) {
      filter = new CuckooFilter(CAPACITY);
      int n =0;
      while (filter.insert(rand.nextLong())) {
        n++;
      }
      double loadFactor = 100.0 * n / CAPACITY / 4;
      System.out.println(loadFactor);
      //assertTrue(loadFactor >= 90);
    }
  }
}

