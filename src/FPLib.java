/*

FPLib version 3.2

Copyright (c) 2004-2010 Andre de Leiradella <andre@leiradella.com>

This program is licensed under the Artistic License.

See http://www.opensource.org/licenses/artistic-license.html for details.

Uses parts or ideas from FPMath. FPMath is copyright (c) 2001 Beartronics and
is authored by Henry Minsky.
http://bearlib.sourceforge.net/

Uses parts or ideas from oMathFP. oMathFP is copyright (c) 2004 Dan Carter.
http://orbisstudios.com/

 */

public final class FPLib {
  public static final int HALF = 32768;
  public static final int ONE = 65536;
  public static final int TWO = 131072;
  public static final int PI = 205887;
  public static final int E = 178145;
  private static byte[] gP = new byte[21];
  private static byte[] gA = new byte[21];
  private static StringBuffer gB = new StringBuffer(23);

  private FPLib() {
  }

  public static final int fpToInt(int f) {
    return f >> 16;
  }

  public static final int intToFp(int i) {
    return i << 16;
  }

  public static final int add(int f1, int f2) {
    return f1 + f2;
  }

  public static final int sub(int f1, int f2) {
    return f1 - f2;
  }

  public static final int mul(int f1, int f2) {
    return ((int) ((((long) f1) * ((long) f2)) >> 16));
  }

  public static final int div(int f1, int f2) {
    return ((int) (((((long) f1) << 32) / f2) >> 16));
  }

  public static final int mod(int f1, int f2) {
    return f1 % f2;
  }

  public static final int min(int f1, int f2) {
    return f1 < f2 ? f1 : f2;
  }

  public static final int max(int f1, int f2) {
    return f1 > f2 ? f1 : f2;
  }

  public static final int sqrt(int f) {
    int i, g;
    g = ((f + 65536) >> 1);
    i = 8;
    do
      g = ((int) ((g + (((((long) f) << 32) / g) >> 16)) >> 1));
    while (--i != 0);
    return g;
  }

  public static final int round(int f) {
    return f < 0 ? -((-f + 32768) & ~0xFFFF) : (f + 32768) & ~0xFFFF;
  }

  public static final int ceil(int f) {
    if ((f & 0xFFFF) == 0)
      return f;
    return (f & ~0xFFFF) + (f < 0 ? 0 : 65536);
  }

  public static final int floor(int f) {
    if ((f & 0xFFFF) == 0)
      return f;
    return (f & ~0xFFFF) + (f < 0 ? 65536 : 0);
  }

  public static final int trunc(int f) {
    return f < 0 ? -(-f & ~0xFFFF) : f & ~0xFFFF;
  }

  public static final int frac(int f) {
    return (f < 0 ? -f : f) & 0xFFFF;
  }

  public static final int sin(int f) {
    boolean neg;
    f = (f % 411774);
    if (neg = f < 0)
      f = -f;
    if (f < 102943) {
      ;
    } else if (f < 205887) {
      f = (205887 - f);
    } else if (f < 308830) {
      f = (f - 205887);
      neg = !neg;
    } else {
      f = (411774 - f);
      neg = !neg;
    }
    int g = ((int) ((((long) f) * ((long) f)) >> 16));
    g = ((int) ((((long) f) * (65536 + ((((long) g) * (((((long) g) * ((long) 498)) >> 16) - 10881)) >> 16))) >> 16));
    return neg ? -g : g;
  }

  public static final int cos(int f) {
    boolean neg;
    f = ((f + 102943) % 411774);
    if (neg = f < 0)
      f = -f;
    if (f < 102943) {
      ;
    } else if (f < 205887) {
      f = (205887 - f);
    } else if (f < 308830) {
      f = (f - 205887);
      neg = !neg;
    } else {
      f = (411774 - f);
      neg = !neg;
    }
    int g = ((int) ((((long) f) * ((long) f)) >> 16));
    g = ((int) ((((long) f) * (65536 + ((((long) g) * (((((long) g) * ((long) 498)) >> 16) - 10881)) >> 16))) >> 16));
    return neg ? -g : g;
  }

  public static final int tan(int f) {
    int s, c;
    s = sin(f);
    c = cos(f);
    if (c != 0)
      return ((int) (((((long) s) << 32) / c) >> 16));
    return s < 0 ? -2147483648 : 2147483647;
  }

  public static final int asin(int f) {
    boolean neg;
    if (neg = f < 0)
      f = -f;
    int g = ((int) (102943 - ((((long) sqrt((65536 - f))) * (((((long) f) * (((((long) f) * (((((long) f) * ((long) -1228)) >> 16) + 4866)) >> 16) - 13900)) >> 16) + 102939)) >> 16)));
    return neg ? -g : g;
  }

  public static final int acos(int f) {
    return (102943 - asin(f));
  }

  public static final int atan(int f) {
    return asin(((int) (((((long) f) << 32) / sqrt(((int) (65536 + ((((long) f) * ((long) f)) >> 16))))) >> 16)));
  }

  public static final int atan2(int y, int x) {
    if (y == 0)
      return x < 0 ? 205887 : 0;
    if (x == 0)
      return y > 0 ? 102943 : -102943;
    int z = ((int) (((((long) y) << 32) / x) >> 16));
    z = atan(z < 0 ? -z : z);
    if (x > 0)
      return y > 0 ? z : -z;
    return y > 0 ? (205887 - z) : (z - 205887);
  }

  public static final int exp(int f) {
    if (f == 0)
      return 65536;
    int k = ((int) (((((long) (f < 0 ? -f : f)) * ((long) 94547)) >> 16) + 32768)) & ~0xFFFF;
    if (f < 0)
      k = -k;
    f = ((int) (f - ((((long) k) * ((long) 45425)) >> 16)));
    int z = ((int) ((((long) f) * ((long) f)) >> 16));
    int r = ((int) (131072 + ((((long) z) * (((((long) z) * ((long) ((z >> 14) - 182))) >> 16) + 10921)) >> 16)));
    k = k < 0 ? 65536 >> (-k >> 16) : 65536 << (k >> 16);
    return ((int) ((((long) k) * (65536 + (((((long) (f << 1)) << 32) / (r - f)) >> 16))) >> 16));
  }

  public static final int ln(int f) {
    if (f < 0)
      return 0;
    if (f == 0)
      return -2147483648;
    int log2 = 0, g = f;
    while (g >= 131072) {
      g >>= 1;
      log2++;
    }
    g -= 65536;
    int s = ((int) (((((long) g) << 32) / (131072 + g)) >> 16));
    int z = ((int) ((((long) s) * ((long) s)) >> 16));
    int w = ((int) ((((long) z) * ((long) z)) >> 16));
    int r = ((int) (((((long) w) * (((((long) w) * (((((long) w) * ((long) 10036)) >> 16) + 14563)) >> 16) + 26214)) >> 16) + (((((((long) w) * (((((long) w) * (((((long) w) * ((long) 9697)) >> 16) + 11916)) >> 16) + 18724)) >> 16) + 43689) * ((long) z)) >> 16)));
    return ((int) (((45425 * log2) + g) - ((((long) s) * ((long) (g - r))) >> 16)));
  }

  public static final int pow(int f, int n) {
    if (n == 0)
      return 65536;
    return exp(((int) ((((long) ln(f)) * ((long) n)) >> 16)));
  }

  public static final String fpToStr(int f) {
    byte[] pow = gP;
    byte[] acc = gA;
    int digit, carry;
    boolean neg;
    StringBuffer sb = gB;
    digit = 0;
    do
      pow[digit] = acc[digit] = 0;
    while (++digit < 21);
    pow[9] = 1;
    pow[10] = 5;
    pow[11] = 2;
    pow[12] = 5;
    pow[13] = 8;
    pow[14] = 7;
    pow[15] = 8;
    pow[16] = 9;
    pow[17] = 0;
    pow[18] = 6;
    pow[19] = 2;
    pow[20] = 5;
    if (neg = f < 0)
      f = -f;
    while (f != 0) {
      if ((f & 1) != 0) {
        digit = 20;
        carry = 0;
        do {
          acc[digit] = (byte) (acc[digit] + pow[digit] + carry);
          if (acc[digit] > 9) {
            acc[digit] -= 10;
            carry = 1;
          } else
            carry = 0;
        } while (--digit >= 0);
      }
      digit = 20;
      carry = 0;
      do {
        pow[digit] = (byte) (pow[digit] + pow[digit] + carry);
        if (pow[digit] > 9) {
          pow[digit] -= 10;
          carry = 1;
        } else
          carry = 0;
      } while (--digit >= 0);
      f >>>= 1;
    }
    sb.setLength(0);
    if (neg)
      sb.append('-');
    for (f = 0; f < 21 && acc[f] == 0; f++)
      ;
    if (f > 4)
      sb.append('0');
    for (digit = f; digit < 5; digit++)
      sb.append(acc[digit]);
    for (f = 20; f >= 0 && acc[f] == 0; f--)
      ;
    if (f > 4)
      sb.append('.');
    for (digit = 5; digit <= f; digit++)
      sb.append(acc[digit]);
    return sb.toString();
  }
}
