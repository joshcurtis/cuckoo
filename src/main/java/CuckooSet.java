import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.apache.commons.codec.digest.MurmurHash3;

public class CuckooSet<T> {
  private T[][] tables;
  List<IntFunction<Long>> hashes;

  public CuckooSet(int capacity) {
    hashes = new ArrayList<>();
    hashes.add(MurmurHash3::hash64);
    hashes.add(i -> Long.valueOf(i));
    tables = (T[][])new Object[hashes.size()][capacity];
  }
  boolean insert(T object) {
    if (contains(object)) {
      return false;
    } else {
      T res = insertElement(object, tables);
      if (res == null) {
        return true;
      } else {
        tables = rehash(tables);
        return insert(res);
      }
    }
  }

  private T[][] rehash(T[][] tables) {
    T[][] tmp = (T[][])new Object[tables.length][2 * tables[0].length];
    for (T[] table : tables) {
      // TODO use buckets
      for (T elem : table) {
        if (elem != null) {
          T res = insertElement(elem, tmp);
          assert res == null;
        }
      }
    }
    return tmp;
  }

  /*
    preconditions: none
    postconditions:
      - returnVal == null && contains(elem) ||
        returnVal != null && !contains(returnVal)
   */
  private T insertElement(T elem, T[][] tables) {
    T prev = null;
    // TODO what's a better way to determine the upper bound on insertions?
    // We can't make more insertions than the total number of slots and succeed
    for (int n = 0; n < tables[0].length * tables.length; n++) {
      for (int t = 0; t < hashes.size(); t++) {
        int idx = project(hashes.get(t).apply(elem.hashCode()), tables[0].length);
        // TODO use buckets
        T[] table = tables[t];
        prev = table[idx];
        table[idx] = elem;
        if (prev == null) {
          assert contains(elem);
          return prev;
        } else {
          elem = prev;
        }
      }
    }
    assert !contains(prev);
    return prev;
  }

  public boolean contains(T object) {
    return find(object, false);
  }

  public boolean remove(T object) {
    return find(object, true);
  }

  private boolean find(T object, boolean delete) {
    for (int t = 0; t < hashes.size(); t++) {
      T[] table = tables[t];
      int idx = project(hashes.get(t).apply(object.hashCode()), table.length);
      T elem = table[idx];
      if (object.equals(elem)) {
        // TODO is there a nicer way to handle deletion?
        if(delete) {
          table[idx] = null;
        }
        return true;
      }
    }
    return false;
  }

  // map num between 0 and capacity
  // TODO: what's this called?
  int project(long hash, int capacity) {
    // TODO be fancier
    int remainder = (int)(hash % capacity);
    return remainder < 0 ? remainder + capacity : remainder;
  }
}
