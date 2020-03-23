import java.io.File;
import java.io.FileWriter;
import java.util.*;

/*
 * Adam Karl
 * CS 1501
 * 4 Feb 2020
 *
 * TO RUN:
 * javac ac_test.java
 * java ac_test
 *
 * Enter 1 char at a time, use 1-5 to select autocomplete word,
 * $ to finish word, or ! to exit program
 */

public class ac_test {
  private static class DLB {
    Node head;

    private void add_word(String letters) {
    /*
     * Add a word to the DLB
     */
      if (head == null) {
        head = new Node(letters);
      } else {
        head.insert(letters);
      }
    }

    private List<String> getStrings(String requiredLetters, int numWords) {
      /*
       * Given the beginning of a word and number of words to return,
       * returns (in alphabetical order) an array of words
       */
      if (head != null) {
        return head.getStrings(requiredLetters, numWords, "");
      } else {
        return new ArrayList<String>();
      }
    }

    private static class Node {
      Node sibling;
      Node child;
      char c; //uppercase/lowercase letter, or apostrophe node
      boolean isWord; //marks the end of a valid word

      private void insert(String letters) {
        char my_char = letters.charAt(0);
        String my_str = letters.substring(1, letters.length());
        if(my_char == c) { //found node for leading char
          if(my_str.isEmpty()) { //end of word, stop
            isWord = true;
          } else if (child != null) { //continue to child
            child.insert(my_str);
          } else { //no child found, must create one
            child = new Node(my_str);

          }
        }
        else if (sibling != null) { //continue to sibling
          sibling.insert(letters);
        } else { //no sibling found, must create one
          sibling = new Node(letters);
        }
      }

      private List<String> getStrings(String requiredLetters, int numWords, String lettersSoFar){
        List<String> emptyList = new ArrayList<String>();
        if (numWords < 1){
          return emptyList;
        } else if (!requiredLetters.isEmpty()) { //haven't fulfilled requirement letters yet
          if (requiredLetters.charAt(0) == c) { //matched char!
            String newRequiredLetters = requiredLetters.substring(1,requiredLetters.length());
            if (newRequiredLetters.isEmpty()) { //finished with required letters
              List<String> myList = new ArrayList<String>();
              if (isWord) {
                myList.add(lettersSoFar + c);
                numWords--;
              }

              if (child != null) { //check downwards first (depth first search)
                List<String> childList = child.getStrings("", numWords, lettersSoFar + c);
                myList.addAll(childList);
              }
              return myList;
            } else if (child != null) { //not finished with required letters
              return child.getStrings(newRequiredLetters, numWords, lettersSoFar + c);
            }

          } else if (sibling != null) { //doesn't match node char, go to sibling if possible
            return sibling.getStrings(requiredLetters, numWords, lettersSoFar);
          } else { //no sibling = dead end
            return emptyList;
          }
        } else { //required letters already fulfilled, just find words
          List<String> myList = emptyList;
          if (isWord) {
            myList.add(lettersSoFar + c);
            numWords--;
          }

          if (child != null) { //check downwards first (depth first search)
            List<String> childList = child.getStrings("", numWords, lettersSoFar + c);
            numWords -= childList.size();
            myList.addAll(childList);
          }

          if (sibling != null) { //then check breadthwise
            List<String> siblingList = sibling.getStrings("", numWords, lettersSoFar);
            //numWords -= siblingList.size();
            myList.addAll(siblingList);
          }

          return myList;
        }

        return emptyList;
      }


      Node(String letters) { //Constructor
        sibling = null;
        child = null;
        c = letters.charAt(0);
        isWord = false;
        insert(letters);
      }
    }
  }

  private static void readFile(DLB dlb) {
  /*
   * Given a DLB onject, read the file "dictionary.txt" into the DLB
   */
    try {
      File f = new File("dictionary.txt");
      Scanner sc = new Scanner(f);
      String word;
      while(sc.hasNext()){
        word = sc.next();
        dlb.add_word(word);
      }
      sc.close();
    } catch (Exception e) {
      System.out.println("Issue reading dictionary.txt");
    }
  }

  private static void readHistoryFile(Map <String, Integer> history) {
  /*
   * Given a Map <String, Integer>, read the file "user_history.txt" into the map.
   * If "user_history.txt" doesn't exist, just initialize an empty map
   *
   * Note: each line of user_history.txt reads as (string) (frequency)
   */
    try {
      File f = new File("user_history.txt");
      Scanner sc = new Scanner(f);
      String word;
      while(sc.hasNextLine()){
        String[] entry = sc.nextLine().split(" ");
        addToHistory(history, entry[0], Integer.parseInt(entry[1]));
      }
      sc.close();
    } catch (Exception e) {
//      System.out.println("Issue reading user_history.txt");
    }
  }

  private static void writeHistoryFile(Map <String, Integer> historyMap) {
    /*
     * Given a Map <String, Integer>, write the file "user_history.txt" from the map.
     * If user_history.txt exists, it is completely overwritten
     *
     * Note: each line of user_history.txt reads as (string) (frequency)
     */
     try (FileWriter fw = new FileWriter("user_history.txt", false)) {
       String line;

       for (Map.Entry<String, Integer> entry : historyMap.entrySet()) {
           fw.write(entry.getKey() + " " + entry.getValue() + "\n");
       }

       fw.close();
     } catch (Exception e) {
       System.out.println("Issue writing user_history.txt");
     }
  }


  // private static void addToHistory(Map <String, Integer> history, String word) {
  //   //add 1 occurance of the given word to the history map
  //   if (history.isEmpty() || !history.containsKey(word)) {
  //     //first occurance of word, add new key/value pair
  //     history.put(word, 1);
  //   } else {
  //     //word has occurred before, add 1 to frequency
  //     history.replace(word, history.get(word) + 1);
  //   }
  // }

  private static void addToHistory(Map <String, Integer> history, String word, Integer freq) {
    //add a number of occurances of the given word to the history map
    if (history.isEmpty() || !history.containsKey(word)) {
      //first occurance of word, add new key/value pair
      history.put(word, freq);
    } else {
      //word has occurred before, add to frequency
      history.replace(word, history.get(word) + freq);
    }
  }

  private static List<String> getHistoryPredictions(Map <String, Integer> history, String inputLetters) {
    /* Return (up to 5) predictions from user history map
     * They are sorted in descending order of frequency
     * Achieved using parallel arrays of the top 5 most frequently used words
     */
    List<String> predictions = new ArrayList<String>();
    List<Integer> freqs = new ArrayList<Integer>();
    int minFreq = 0;
    for (Map.Entry<String, Integer> entry : history.entrySet()) {
      if (entry.getValue() > minFreq && entry.getKey().startsWith(inputLetters)) {
        //word both starts with correct prefix AND has a high enough frequency
        boolean inserted = false;
        for (int i=0; i<predictions.size(); i++) { //find spot to insert word
          if (!inserted && entry.getValue() > freqs.get(i)) {
            //found entry position
            predictions.add(i, entry.getKey());
            freqs.add(i, entry.getValue());
            inserted = true;
          }
        }
        if (!inserted) {
          //insert at end of lists
          predictions.add(entry.getKey());
          freqs.add(entry.getValue());
        }
        while (predictions.size() > 5) {
          predictions.remove(5);
          freqs.remove(5);
          minFreq = freqs.get(4);
        }
      }
    }
    return predictions;
  }

  private static List<String> getPredictions(DLB dict, Map <String, Integer> history, String inputLetters) {
    /* First using the user history, then (if necessary) using the dictionary,
     * return (up to) 5 predictions starting with the given string
     */
    List<String> predictions = getHistoryPredictions(history, inputLetters);
    if (predictions.size() < 5) {
      // if <5 predictions from the history, add dictionary predictions until full (or we run out)
      List<String> dictionaryPredictions = dict.getStrings(inputLetters, 5);
      for (int i=0; i< dictionaryPredictions.size(); i++) {
        if (predictions.size() < 5) {
          String potentialPrediction = dictionaryPredictions.get(i);
          boolean alreadyInPredictions = false;
          for (int j=0; j<predictions.size(); j++) {
            //check to make sure dict word isn't in predictions already
            if (predictions.get(j).equals(potentialPrediction)) {
              alreadyInPredictions = true;
            }
          }
          if (!alreadyInPredictions) {
            predictions.add(potentialPrediction);
          }
        }
      }
    }
    return predictions;
  }

  public static void main(String[] args) {
    DLB dlb = new DLB();
    dlb.head = null;
    readFile(dlb);

    Map <String, Integer> historyMap = new HashMap<String, Integer>();
    readHistoryFile(historyMap);

    Scanner input = new Scanner(System.in);
    List<String> predictions = dlb.getStrings("", 5);
    char inputChar;
    String inputLetters = "";
    int numTimings = 0;
    long totalTimeNanoseconds = 0;
    boolean firstWord = true;

    do {
      if (inputLetters.isEmpty() && firstWord) {
        //only executes for first word
        System.out.print("Enter your first character: ");
        firstWord = false;
      } else if (inputLetters.isEmpty()) {
        System.out.print("Enter first character of the next word: ");
      } else {
        System.out.print("Enter the next character: ");
      }

      inputChar = input.nextLine().charAt(0);

      if (Character.isDigit(inputChar)) {
        //char is a digit => user chose an autocomplete option
        String wordSelected = predictions.get(Integer.valueOf(Character.toString(inputChar)) - 1);
        System.out.println("\n\nWORD COMPLETED: " + wordSelected + "\n\n");
        addToHistory(historyMap, wordSelected, 1);
        inputLetters = "";
      } else if (inputChar - '$' == 0) {
        //terminator character '$' => word is complete
        System.out.println("\n\nWORD COMPLETED: " + inputLetters + "\n\n");
        addToHistory(historyMap, inputLetters, 1);
        inputLetters = "";
      } else if (inputChar - '!' != 0) {
        //char is a letter (or apostrophe)
        inputLetters += inputChar;

        long start = System.nanoTime();
        predictions = getPredictions(dlb, historyMap, inputLetters);
        long end = System.nanoTime();
        long elapsedNanoSeconds = end - start;

        totalTimeNanoseconds += elapsedNanoSeconds;
        numTimings++;

        double elapsedSeconds = (double) elapsedNanoSeconds / 1_000_000_000;
        String timeString = String.format("%.6f", elapsedSeconds);

        System.out.println("(" + timeString + " s)");
        System.out.println("\n\nPredictions:");
        for (int i=0; i<predictions.size(); i++) {
          System.out.print("(" + (i+1) + ") " + predictions.get(i) + "  ");
        }
        System.out.println("\n");
      }
    } while (inputChar != '!');

    double elapsedSeconds = ((double) totalTimeNanoseconds / numTimings) / 1_000_000_000;
    String timeString = String.format("%.6f", elapsedSeconds);
    writeHistoryFile(historyMap);
    System.out.println("\n\n Average time:  " + timeString + " s\nBye!");
  }
}
