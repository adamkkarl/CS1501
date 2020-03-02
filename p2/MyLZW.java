/*************************************************************************
 *  Compilation:  javac MyLZW.java
 *  Execution:    java MyLZW - < input.txt   (compress)
 *  Execution:    java MyLZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

 /**
  * Modified by: Adam Karl
  * akk67@pitt.edu
  * 1 March 2020
  * CS 1501
  */

public class MyLZW {
    private static final int R = 256; // number of input chars
    private static int L = 512;       // initial number of codewords = 2^W; W=9
    private static int W = 9;         // initial codeword width

    public static void compress() {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        BinaryStdOut.write('n');      // Add 'n' to indicate compression type

        //=================================================
        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            if (code == L && W < 16) { //check if out of codewords of length W
              L*=2;
              W++;
            }
        //=================================================
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }


        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }

    public static void reset_compress() {
      String input = BinaryStdIn.readString();
      TST<Integer> st = new TST<Integer>();
      for (int i = 0; i < R; i++)
          st.put("" + (char) i, i);
      int code = R+1;  // R is codeword for EOF

      BinaryStdOut.write('r');      // Add 'r' to indicate compression type

      while (input.length() > 0) {
        String s = st.longestPrefixOf(input);  // Find max prefix match s.
        BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
        int t = s.length();
        if (code == L && W < 16) { //check if out of codewords of length W
          L*=2;
          W++;
        }
        if (t < input.length() && code < L) {   // Add s to symbol table.
            st.put(input.substring(0, t + 1), code++);
        }

        if (code == L && W == 16) { //check if entire codebook needs reset
          L = 512;
          W = 9;
          st = new TST<Integer>();
          for (int i = 0; i < R; i++)
              st.put("" + (char) i, i);
          code = R+1;  // R is codeword for EOF
        }
        input = input.substring(t);            // Scan past s in input.
      }
      BinaryStdOut.write(R, W);
      BinaryStdOut.close();
    }

    public static void monitor_compress() {
      String input = BinaryStdIn.readString();
      TST<Integer> st = new TST<Integer>();
      for (int i = 0; i < R; i++)
          st.put("" + (char) i, i);
      int code = R + 1;  // R is codeword for EOF

      int added = 0; //uncompressed characters processed (for debugging purposes only)

      int uncompressedBits = 0;
      int compressedBits = 0;
      double oldRatio = 0.0;

      BinaryStdOut.write('m');      // Add 'm' to indicate compression type

      while (input.length() > 0) {
        String s = st.longestPrefixOf(input);  // Find max prefix match s.
        BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
        int t = s.length();
        compressedBits += W;
        uncompressedBits += t * 8;
        added += t;
        if (t < input.length() && code < L) {   // Add s to symbol table.
            st.put(input.substring(0, t + 1), code++);

            //will stop updating automatically when i>=L and W==16
            oldRatio = (double) uncompressedBits / (double) compressedBits;
        } else if (t < input.length() && code == L && W < 16) {
          W++;
          L *= 2;
          st.put(input.substring(0, t + 1), code++);
        } else if (t < input.length() && code == L && W == 16) { //check if entire codebook needs reset
          double currRatio = (double) uncompressedBits / (double) compressedBits;
          if (oldRatio / currRatio > 1.1) {
            //reset the codebook
            System.err.println("resetting codebook at uncompressed character " + added);
            L = 512;
            W = 9;
            oldRatio = 0.0;
            st = new TST<Integer>();
            for (int i = 0; i < R; i++)
                st.put("" + (char) i, i);
            code = R+1;  // R is codeword for EOF
            st.put(input.substring(0, t + 1), code++);

            //reset heuristic
            compressedBits = 0;
            uncompressedBits = 0;
          }
        }
        input = input.substring(t);            // Scan past s in input.
      }
      BinaryStdOut.write(R, W);
      BinaryStdOut.close();
    }

    public static void expand() {
        String[] st = new String[65536]; //entire codebook through 2^16
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
          BinaryStdOut.write(val);

          codeword = BinaryStdIn.readInt(W);
          if (codeword == R) break;
          String s = st[codeword];
          if (i == codeword) s = val + val.charAt(0);   // special case hack

          if (i == L-1 && W < 16) { //out of W-length codewords
            W++;
            L *= 2;
          }
          if (i < L) {
            st[i++] = val + s.charAt(0);
          }
          val = s;
        }
        BinaryStdOut.close();
    }

    public static void reset_expand() {
      String[] st = new String[65536]; //entire codebook through 2^16
      int i; // next available codeword value

      // initialize symbol table with all 1-character strings
      for (i = 0; i < R; i++)
          st[i] = "" + (char) i;
      st[i++] = "";                        // (unused) lookahead for EOF

      int codeword = BinaryStdIn.readInt(W);
      if (codeword == R) return;           // expanded message is empty string
      String val = st[codeword];

      while (true) {
        BinaryStdOut.write(val);

        if (i>=L-1 && W==16) { //check if entire codebook is full
          W=9;
          L=512;
          i=R+1;
          codeword = BinaryStdIn.readInt(W);
          if (codeword == R) break;           // expanded message is empty string
          val = st[codeword];
          BinaryStdOut.write(val);
        }

        codeword = BinaryStdIn.readInt(W);
        if (codeword == R) break;
        String s = st[codeword];
        if (i == codeword) s = val + val.charAt(0);   // special case hack
        if (i == L-1 && W < 16) { //out of W-length codewords
          W++;
          L *= 2;
        }
        if (i < L) {
          st[i++] = val + s.charAt(0);
        }
        val = s;
      }
      BinaryStdOut.close();
    }

    public static void monitor_expand() {
      String[] st = new String[65536]; //entire codebook through 2^16
      int i; // next available codeword value
      int compressedBits = 0;
      int uncompressedBits = 0;
      double oldRatio = 0.0;

      int added = 0;

      // initialize symbol table with all 1-character strings
      for (i = 0; i < R; i++)
          st[i] = "" + (char) i;
      st[i++] = "";                        // (unused) lookahead for EOF

      int codeword = BinaryStdIn.readInt(W);
      if (codeword == R) return;           // expanded message is empty string
      String val = st[codeword];

      while (true) {
        BinaryStdOut.write(val);
        compressedBits += W;
        uncompressedBits += val.length() * 8; //numBits = numChars * 8
        added += val.length();
        if (i >= L && W < 16) {
          W++;
          L *= 2;

          //will stop updating automatically when i>=L and W==16
          oldRatio = (double) uncompressedBits / (double) compressedBits;
        } else if (i >= L && W == 16) { //check if entire codebook is full
          double currRatio = (double) uncompressedBits / (double) compressedBits;
          if (oldRatio / currRatio > 1.1) {
            System.err.println("Reset codebook at uncompressed char " + added);
            st = new String[65536];
            // initialize symbol table with all 1-character strings
            for (i = 0; i < R; i++)
                st[i] = "" + (char) i;
            st[i++] = "";                        // (unused) lookahead for EOF
            W = 9;
            L = 512;
            i = R + 1;
            oldRatio = 0.0;

            //reset heuristic
            compressedBits = 0;
            uncompressedBits = 0;
          }
        }

        codeword = BinaryStdIn.readInt(W);
        if (codeword == R) break;
        String s = st[codeword];
        if (i == codeword) s = val + val.charAt(0);   // special case hack

        if (i < L) {
          st[i++] = val + s.charAt(0);

          //will stop updating automatically when i>=L and W==16
          oldRatio = (double) uncompressedBits/ (double) compressedBits;
        }
        val = s;
      }
      BinaryStdOut.close();
    }

    public static void chooseExpand() {
      char c = BinaryStdIn.readChar();
      if (c == 'n') {
        expand();
      } else if (c == 'r') {
        reset_expand();
      } else if (c == 'm') {
        monitor_expand();
      } else throw new IllegalArgumentException("Cannot read expand type");
    }

    public static void main(String[] args) {
      if (args.length < 1) {
        throw new IllegalArgumentException("No compression/expansion operator specified");
      } else if(args[0].equals("-")) { //compress
          if (args.length < 2) {
            throw new IllegalArgumentException("No compression type specified");
          } else if (args[1].equals("n")) {
            compress();
          } else if (args[1].equals("r")) {
            reset_compress();
          } else if (args[1].equals("m")) {
            monitor_compress();
          } else throw new IllegalArgumentException("Unknown compression type");
        } else if(args[0].equals("+")) { //expand
          chooseExpand();
        } else throw new IllegalArgumentException("Illegal command line argument");
    }

}
