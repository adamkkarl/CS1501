//import java.math.BigInteger; //debugging purposes only

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Construct the HeftyInteger from a given byte array and an int
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b, int trailingZeroBytes) {
		val = new byte[b.length + trailingZeroBytes];
		for (int i=0; i<b.length; i++) {
			val[i] = b[i];
		}
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	//input: 2 unsigned bytes to multiply
	//output: byte array of product (w/ at least 1 leading 0)
	private byte[] prodBytes(byte m1, byte m2) {
		int mult = ((int)m1)&0xff;
		int mcand = ((int)m2)&0xff;
//		System.out.print(mult + " * " + mcand + " = ");
		int prod = mult * mcand;
		byte[] ret = new byte[2];
		ret[1] = (byte)(prod & 0xff);
		ret[0] = (byte)((prod & 0xff00)>>8);
		if(ret[0] < 0) { //if leading 1, add 0x0 byte at front
			byte[] nret = new byte[3];
			nret[0] = 0x00;
			nret[1] = ret[0];
			nret[2] = ret[1];
			return nret;
		}
//		System.out.println(ret[0] + ", " + ret[1]);
		return ret;
	}

	private HeftyInteger helperMultiply(HeftyInteger other) {
		byte[] z = new byte[1];
		z[0] = 0;
		HeftyInteger ans = new HeftyInteger(z);
		int thisLen = this.getVal().length;
		int otherLen = other.getVal().length;

		for (int i=0; i<thisLen; i++) {
			int thisShifts = thisLen-1-i; //trailing zero bytes due to multiplicand
			for (int j=0; j<otherLen; j++) {
				int otherShifts = otherLen-1-j; //trailing zero bytes due to multiplier
				int totalShifts = thisShifts + otherShifts;
				byte[] prod = prodBytes(this.getVal()[i], other.getVal()[j]);
				HeftyInteger partialProd = new HeftyInteger(prod, totalShifts);
				ans = ans.add(partialProd);
			}
		}
		return ans;
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		HeftyInteger ans;
		boolean negProd = false;

		if (other.isNegative()) {
			negProd = !negProd;
			other = other.negate();
		}


		if (this.isNegative()) {
			negProd = !negProd;
			ans = this.negate().helperMultiply(other);
		} else {
			ans = this.helperMultiply(other);
		}

		if (negProd) {
			return ans.negate();
		} else {
			return ans;
		}
	}


	public boolean isGreaterThanOrEqualTo(HeftyInteger other) {
		if (this.subtract(other).isNegative()) {
			return false;
		}
		return true;
	}

	public boolean isZero() {
		for (int i=0; i<this.length(); i++) {
			if (val[i] != 0) {
				return false;
			}
		}
		return true;
	}


	public HeftyInteger[] divide(HeftyInteger quotient, HeftyInteger divisor) {
		byte[] bs = {0};
		HeftyInteger divs = new HeftyInteger(bs);

		byte[] Bs = {1};
		HeftyInteger one = new HeftyInteger(Bs);


		while (quotient.isGreaterThanOrEqualTo(divisor)) {
			quotient = quotient.subtract(divisor);
			divs = divs.add(one);
		}

		HeftyInteger[] ret = new HeftyInteger[2];
		ret[0] = divs;
		ret[1] = quotient;
		return ret;
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public HeftyInteger[] XGCD(HeftyInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		//returns [gcd, s, t] such that gcd = this*s + other*t
		boolean flip = false; //true if other > this

		HeftyInteger a, b, gcd;

		if (this.isGreaterThanOrEqualTo(other)) {
			a = new HeftyInteger(this.getVal());
			b = other;
		} else {
			b = new HeftyInteger(this.getVal());
			a = other;
			flip = true;
		}

		HeftyInteger[] ans = new HeftyInteger[2];
		ans[0] = a;
		ans[1] = b;


		HeftyInteger[] quotients = new HeftyInteger[1000]; //stores 1000 quotients (probably overkill)
		int numQuotients = 0;
		while(!ans[1].isZero()) {
			HeftyInteger[] temp = divide(ans[0], ans[1]);
			ans[0] = ans[1];
			ans[1] = temp[1];
			quotients[numQuotients] = temp[0];
			numQuotients++;
		}
		gcd = ans[0];
		numQuotients--;

		HeftyInteger[] st = new HeftyInteger[2];
		byte[] bs = {1};
		st[0] = new HeftyInteger(bs);
		byte[] Bs = {0};
		st[1] = new HeftyInteger(Bs);
		while(numQuotients >= 0) {

			HeftyInteger newS = st[1];
			HeftyInteger newT = st[0].subtract(quotients[numQuotients].multiply(st[1]));
			st[0] = newS;
			st[1] = newT;
			numQuotients--;
		}

		HeftyInteger[] ret = new HeftyInteger[3];
		ret[0] = gcd;
		if (flip) {
			ret[1] = st[1];
			ret[2] = st[0];
		} else {
			ret[1] = st[0];
			ret[2] = st[1];
		}
		return ret;
	 }

	 //for debugging purposes ONLY
	 // public String toString() {
		//  return new BigInteger(val).toString();
	 // }

	 public static void main(String[] args) {
		 byte[] bs = {1,1,1};
		 HeftyInteger h = new HeftyInteger(bs);
		 System.out.println(h.toString());
	 }
}
