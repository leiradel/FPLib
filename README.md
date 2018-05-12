# FPLib

Everybody knows embedded processors are not very good in floating point arithmetic. Some (most?) of them don't even have a floating-point unit and do all operations in software. To address this issue, I wrote FPLib and FPComp.

FPLib is a Java class that contains methods to perform a bunch of operations on fixed-point (FP) numbers:

* `int fpToInt(int f)`: Converts a FP number to an integer.
* `int intToFp(int i)`: Converts an integer to a FP number.
* `int add(int f1, int f2)`: Adds two FP numbers.
* `int sub(int f1, int f2)`: Subtracts one FP number from another.
* `int mul(int f1, int f2)`: Multiplies two FP numbers.
* `int div(int f1, int f2)`: Divides one FP number by another.
* `int mod(int f1, int f2)`: Returns the rest of the division of one FP number by another.
* `int min(int f1, int f2)`: Returns the lesser of two FP numbers.
* `int max(int f1, int f2)`: Returns the greater of two FP numbers.
* `int sqrt(int f)`: Returns the square root of a FP number.
* `int round(int f)`: Returns the FP number rounded to the nearest integer, away from zero. This function and the next three functions return a FP number with the integer value.
* `int ceil(int f)`: Returns the smallest integer not less than the FP number.
* `int floor(int f)`: Returns the largest integer not greater than the FP number.
* `int trunc(int f)`: Returns the FP number rounded to the nearest integer, towards zero.
* `int frac(int f)`: Returns the fractional component of the FP number.
* `int sin(int f)`: Returns the sine of the FP number (radians.)
* `int cos(int f)`: Returns the cosine of the FP number (radians.)
* `int tan(int f)`: Returns the tangent of the FP number (radians.)
* `int asin(int f)`: Returns the arc sine of the FP number.
* `int acos(int f)`: Returns the arc cosine of the FP number.
* `int atan2(int y, int x)`: Returns the arc tangent of the two FP numbers. This is similar to atan(y/x) but the signs of both values are used to determine the quadrant.
* `int exp(int f)`: Returns the value of the natural number raised to the power of the FP number.
* `int ln(int f)`: Returns the natural logarithm of the FP number.
* `int pow(int f, int n)`: Returns the FP number f raised to the power of the FP number n.
* `String fpToStr(int f)`: Converts the FP number to its string representation.

FPLib also has some common FP values defined:

* `int HALF = 32768;`
* `int ONE = 65536;`
* `int TWO = 131072;`
* `int PI = 205887;`
* `int E = 178145;`

If you're scratching your head looking to **FPLib**'s source code, trying to make sense of the expressions in it, don't. They were created using **FPComp**, an utility written in Java that takes an expression and prints the same expression using fixed-point arithmetic. The objective is to avoid calls from your code to FPLib, saving precious cycles.

I wrote **FPComp** as part of the article "Optimizing Fixed Point (FP) Math with J2ME" that was available at [Devx](http://www.devx.com/Java/Article/21850/0). Since the website doesn't exist any longer, I'm making it available it [here](fplib_devx.pdf).

FPComp usage is very simple, just run it from the command line and copy the result into your code:

```
$ java FPComp "a * b / c"
((int)(((((long)((int)((((long)a)*((long)b))>>16)))<<32)/c)>>16))
```

Enjoy!
