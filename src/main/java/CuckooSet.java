import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import org.apache.commons.codec.digest.MurmurHash3;

public class CuckooSet<T> {
  public static final int BUCKET_SIZE = 1;
  T[][] slots;
  // TODO how to represent int -> long?
  List<IntFunction<Long>> hashes;
  private final static int MAX_COLLISIONS = 50;

  public CuckooSet(int capacity) {
    //noinspection unchecked
    slots = (T[][])new Object[capacity][BUCKET_SIZE];
    hashes = new ArrayList<>();
    hashes.add(MurmurHash3::hash64);
    hashes.add(hashCode -> (long)hashCode);
  }

  boolean insert(T object) {
    int hc = object.hashCode();
    for (IntFunction<Long> hash : hashes) {
      int idx = project(hash.apply(hc), slots.length);
      T[] bucket = slots[idx];
      for (int i = 0; i < BUCKET_SIZE; i++) {
        // TODO what if it's already here
        if (bucket[i] == null) {
          bucket[i] = object;
        }
      }
    }
    // TODO resolve collisions
  }

  boolean contains(T object) {
    int hc = object.hashCode();
    for (IntFunction<Long> hash : hashes) {
      int idx = project(hash.apply(hc), slots.length);
      T[] bucket = slots[idx];
      for (int i = 0; i < BUCKET_SIZE; i++) {
        if (object.equals(bucket[i])) {
          return true;
        }
      }
    }
    return false;
  }

  boolean remove(T object) {
    // TODO dedup remove and contains
    int hc = object.hashCode();
    for (IntFunction<Long> hash : hashes) {
      int idx = project(hash.apply(hc), slots.length);
      T[] bucket = slots[idx];
      for (int i = 0; i < BUCKET_SIZE; i++) {
        if (object.equals(bucket[i])) {
          bucket[i] = null;
          return true;
        }
      }
    }
    return false;
  }

  // map num between 0 and capacity
  int project(long hash, int capacity) {
    // TODO what's the name for this?
    // TODO be fancier
    return (int)(hash % capacity);
  }
}
