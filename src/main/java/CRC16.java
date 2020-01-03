// https://github.com/frohoff/jdk8u-jdk/blob/master/src/share/classes/sun/misc/CRC16.java
public class CRC16 {
  /** value contains the currently computed CRC, set it to 0 initally */
  public int value;

  public CRC16() {
    value = 0;
  }

  static int hash(long l) {
    CRC16 crc16 = new CRC16();
    while(l > 0) {
      crc16.update((byte)(l & 0xff));
      l >>= 8;
    }
    return crc16.value;
  }

  /** update CRC with byte b */
  public void update(byte aByte) {
    int a, b;

    a = (int) aByte;
    for (int count = 7; count >=0; count--) {
      a = a << 1;
      b = (a >>> 8) & 1;
      if ((value & 0x8000) != 0) {
        value = ((value << 1) + b) ^ 0x1021;
      } else {
        value = (value << 1) + b;
      }
    }
    value = value & 0xffff;
    return;
  }

  /** reset CRC value to 0 */
  public void reset() {
    value = 0;
  }
}
