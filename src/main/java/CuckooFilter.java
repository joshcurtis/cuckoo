import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.CRC32;

import org.apache.commons.codec.digest.MurmurHash3;

public class CuckooFilter {
  // each long represents a bucket of size 4
  private final long[] table;
  public static final long FLAG_BIT = 0x8000L;
  ThreadLocalRandom rand = ThreadLocalRandom.current();

  CuckooFilter(int capacity) {
    table = new long[capacity];
  }

  boolean insert(long n) {
    long fp = fingerprint(n);
    int idx = hash(n);
    assert idx == (idx ^ hash(fp) ^ hash(fp));
    if (contains(n)) {
      return true;
    }
    for (int i = 0; i < 500; i++) {
      assert 0 <= idx && idx < table.length;
      long bucket = table[idx];
      int pos;
      for (pos = 0; pos < 3; pos++) {
        if ((bucket & (FLAG_BIT << (16 * pos))) == 0) {
          break;
        }
      }
      if (pos == 4) {
        pos = rand.nextInt(4);
      }
      int shift = pos * 16;
      long prev = (bucket & 0xffffL << shift) >> shift;
      bucket &= ~(0xffffL << shift);
      bucket |= FLAG_BIT << shift;
      bucket |= fp << shift;
      table[idx] = bucket;
      if ((prev & FLAG_BIT) == 0) {
        return true;
      }
      fp = prev & 0x7fff;
      idx = idx ^ hash(fp);
    }
    return false;
  }

  boolean contains(long n) {
    return find(n, false);
  }

  boolean remove(long n) {
    return find(n, true);
  }

  boolean find(long n, boolean delete) {
    long fp = fingerprint(n);
    int idx = hash(n);
    for (int i = 0; i < 2; i++) {
      long bucket = table[idx];
      for (int p = 0; p < 4; p++) {
        int shift = 16 * p;
        long entry = (bucket & 0xffffL << shift) >> shift;
        if((entry & FLAG_BIT) != 0 && (entry & 0x7fff) == fp) {
          if(delete) {
            table[idx] &= ~(0xffffL << shift);
          }
          return true;
        }
      }
      idx ^= hash(fp);
    }
    return false;
  }

  // return a 15 bit fingerprint
  private int fingerprint(long n) {
    return MurmurHash3.hash32(n) & 0x7fff;
  }

  // get a random index within the table
  int hash(long n) {
    CRC32 crc32 = new CRC32();
    while (n > 0) {
      crc32.update((byte)(n & 0xff));
      n >>= 8;
    }
    int res = (int)(crc32.getValue()) % table.length;
    return res < 0 ? res + table.length : res;
  }
}
