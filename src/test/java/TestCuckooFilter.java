import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCuckooFilter {
  CuckooFilter filter = new CuckooFilter(1024);
  ThreadLocalRandom rand = ThreadLocalRandom.current();

  @Test
  public void sanityCheckInsert() {
    int n = 0;
    while (filter.insert(n)) {
      assertTrue(filter.contains(n));
      n++;
    }
  }

  @Test
  public void sanityCheckRemove() {
    for (int i = 0; i < 2048; i++) {
      long n = rand.nextLong();
      assertTrue(filter.insert(n));
      assertTrue(filter.remove(n));
      assertFalse(filter.contains(n));
    }
  }
}

