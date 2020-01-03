import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCuckooSet {
  public static final int INIT_CAPACITY = 64;
  Random rand = ThreadLocalRandom.current();
  CuckooSet<Integer> set = new CuckooSet<>(INIT_CAPACITY);

  @Test
  public void sanityCheckInsert() {
    for (int i = 0; i < 8 * INIT_CAPACITY + 1; i++) {
      assertTrue(set.insert(i));
      assertTrue("does not contain " + i, set.contains(i));
    }
    for (int i = 0; i < 8 * INIT_CAPACITY + 1; i++) {
      assertTrue("set does not contain " + i, set.contains(i));
      assertTrue("set does not contain " + i, set.contains(i));
    }
    for (int i = 8 * INIT_CAPACITY + 1; i < 10000; i++) {
      assertFalse(set.contains(i));
    }
  }

  @Test
  public void randomInsert() {
    Set<Integer> inserted = new HashSet<>();
    for (int i = 0; i < 10000; i++) {
      int n = rand.nextInt();
      assertEquals(
          inserted.add(n),
          set.insert(n)
      );
    }
    for (Integer n : inserted) {
      assertTrue(set.contains(n));
    }
    for (int i = 0; i < 10000; i++) {
      int n = rand.nextInt();
      assertEquals(inserted.contains(n), set.contains(n));
    }
  }

  @Test
  public void sanityCheckRemove() {
    for (int i = 0; i < 8 * INIT_CAPACITY + 1; i++) {
      assertTrue(set.insert(i));
      assertTrue(set.remove(i));
      assertFalse(set.contains(i));
    }
  }

  @Test
  public void randomRemove() {
    int bound = 1000000;
    Set<Integer> inserted = new HashSet<>();
    for (int i = 0; i < bound / 2; i++) {
      int n = rand.nextInt(bound);
      set.insert(n);
      inserted.add(n);
    }
    for (int i = 0; i < bound * 5; i++) {
      int n = rand.nextInt(bound);
      assertEquals(
          inserted.remove(n),
          set.remove(n)
      );
      assertFalse(set.contains(n));
    }
  }
}
