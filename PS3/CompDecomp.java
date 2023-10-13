import java.io.*;
import java.util.*;

/**
 * Allows for compression and decompression of a given file.
 * @author Cha Krupka, Paige Harris, Dartmouth CS10, Spring 2022
 */

public class CompDecomp {
    private final String inputFile;                     // input file
    private static final String pathName = "inputs/";   // pathname to be combined with inputFile
    private boolean exists = true;                      // does the file exist? for later method
    private Map<Character, Integer> freqMap;            // Map of characters and their frequencies
    private PriorityQueue<BinaryTree<KeyFreq>> pQueue;  // priority queue of KeyFreq BinaryTrees
    private BinaryTree<KeyFreq> huffTree;               // Huffman tree of KeyFreq BinaryTrees
    private Map<String, Character> bitMap;              // Map of chars and corresponding bit codes


    /**
     * Constructs and organizes all data necessary to compress the given fill
     * @param inFile name of the input file, without the pathname.
     */
    public CompDecomp(String inFile) {
        inputFile = pathName+inFile;
    }

    /**
     * KeyFreq holds one or two instance variable to keep track of a char and its frequency, or just a frequency.
     */
    private static class KeyFreq {
        private char key;               //
        private final int frequency;    //

        /**
         * Constructor for both key and frequency
         * @param key character represented
         * @param frequency frequency of char in file
         */
        public KeyFreq(char key, int frequency) {
            this.key = key; this.frequency = frequency;
        }

        /**
         * Constructor for just frequency
         * @param frequency frequency of char in file
         */
        public KeyFreq(int frequency) {
            this.frequency = frequency;
        }

        public int getFreq() {
            return frequency;
        }

        public void setKey(char key) {
            this.key = key;
        }

        public char getKey() {
            return key;
        }
    }

    /**
     * Reads through the input file and processes each character, either creating a new entry in a Map or increasing
     * the frequency of the current entry.
     * @throws IOException
     */
    public void makeMap() throws IOException {
        freqMap = new TreeMap<Character, Integer>();
        BufferedReader input = null;

        // Try to open file
        try {
            input = new BufferedReader(new FileReader(inputFile));

            // Read the file and process data
            for (int currChar = input.read(); currChar != -1; currChar = input.read()) {
                if (freqMap.containsKey((char) currChar)) {
                    freqMap.put((char) currChar, freqMap.get((char) currChar) + 1);
                } else freqMap.put((char) currChar, 1);
            }

            input.close();
        }

        // Can't open file
        catch (FileNotFoundException e) {
            System.err.println("Can't open file.\n" + e.getMessage());
            exists = false;
        }
    }

    /**
     * Converts data from Map into BinaryTrees, inserted them into a Priority Queue
     */
    public void makePriorityTree() {
        pQueue = new PriorityQueue<>((BinaryTree<KeyFreq> kf1, BinaryTree<KeyFreq> kf2) ->
                kf1.getData().getFreq() - kf2.getData().getFreq());
        for (Map.Entry<Character, Integer> key : freqMap.entrySet()) {
            pQueue.add(new BinaryTree<KeyFreq>((new KeyFreq(key.getKey(), key.getValue()))));
        }
    }

    /**
     * Takes individual trees from the priority queue and combines them, eventually into a single Huffman tree.
     */
    public void makeHuffTree() {
        huffTree = null;

        if (pQueue.size() == 1) {        // if the map is only one character
            huffTree = pQueue.remove();
        }

        while (pQueue.size() > 1) {
            BinaryTree<KeyFreq> t1 = pQueue.remove();   // first tree
            BinaryTree<KeyFreq> t2 = pQueue.remove();   // second tree
            int sumFreq = t1.getData().getFreq() + t2.getData().getFreq();  // sum of two trees frequencies
            huffTree = new BinaryTree<KeyFreq>(new KeyFreq(sumFreq), t1, t2); // combine the two nodes
            pQueue.add(huffTree);   // add combined node back into priority queue
        }
    }

    /**
     * Creates a Map holding information on char and their corresponding bitcodes
     * @throws IOException if the Huffman tree is null
     */
    public void makeBitMap() throws IOException {
        bitMap = new TreeMap<String, Character>();

        // try and add data from Huffman Tree
        try {
            if (!huffTree.hasLeft() && !huffTree.hasRight()) bitMap.put("0", huffTree.getData().getKey());
            else makeBitMapHelper(huffTree, bitMap, "");
        }

        // if tree is empty, catch and print to console
        catch (NullPointerException e) {
            if (!exists) {
                System.err.println("Can't create bitmap: No input data");
            }
        }
    }

    /**
     * Helper function for makeBitMap() that allows for recursive search of the huffman tree to sequence
     * chars into bits
     * @param inTree Huffman tree node
     * @param bMap bitmap
     * @param bit sequence of bit code
     */
    public void makeBitMapHelper(BinaryTree<KeyFreq> inTree, Map<String, Character> bMap, String bit) {
        if (!inTree.hasLeft() && !inTree.hasRight()) bMap.put(bit, inTree.getData().getKey()); // hit a leaf, add data

        if (inTree.hasLeft()) makeBitMapHelper(inTree.getLeft(), bMap, bit + "0"); // go left, add 0 to bit code
        if (inTree.hasRight()) makeBitMapHelper(inTree.getRight(), bMap, bit + "1");  // go right, add 1
    }

    /**
     * Compresses the instanced file
     * @throws IOException if the file is not found
     */
    public void compressFile() throws IOException {
        // try to process data from input file
        try {

            // initialize and process data from file into a bitmap
            makeMap();
            System.out.println(freqMap);
            makePriorityTree();
            System.out.println(pQueue);
            makeHuffTree();
            System.out.println(huffTree);
            makeBitMap();
            System.out.println(bitMap);

            BufferedReader input = new BufferedReader(new FileReader(inputFile));
            BufferedBitWriter bitOutput = new BufferedBitWriter(inputFile + "_compressed");

            for (int currChar = input.read(); currChar != -1; currChar = input.read()) { // read through file
                for (Map.Entry<String, Character> entry : bitMap.entrySet()) {   // find the current char's bitcode
                    if (currChar == entry.getValue()) {
                        char[] cBit = entry.getKey().toCharArray(); // write the char's bitcode as bits
                        for (char c : cBit) {
                            bitOutput.writeBit(c != '0');
                        }
                    }
                }
            }
            input.close();
            bitOutput.close();
        }

        // if file not found
        catch (FileNotFoundException e) {
            System.err.println("Can't compress file: File not found.");
        }
    }

    /**
     * Decompresses the file compressed by compressFile()
     * @throws IOException if compressed file is not found
     */
    public void decompressFile() throws IOException {
        // try to decompress file, if the compressed file already exists
        try {
            BufferedBitReader bitInput = new BufferedBitReader(inputFile + "_compressed");
            BufferedWriter output = new BufferedWriter(new FileWriter(inputFile + "_decompressed"));

            String bitString = ""; // initial bit sequence

            while (bitInput.hasNext()) {                // read through bits
                boolean bit = bitInput.readBit();
                if (!bit) {
                    bitString = bitString + "0";        // if false, add 0 to bit sequence
                } else {
                    bitString = bitString + "1";        // if true, add 1
                }
                for (Map.Entry<String, Character> entry : bitMap.entrySet()) {  // check and see if bitcode corresponds
                    if (bitString.equals(entry.getKey())) {                     // to a char in the bitmap
                        output.write(entry.getValue());
                        bitString = "";         // reset bit sequence
                    }
                }
            }

            bitInput.close();
            output.close();
        }

        // if the file has not been compressed before
        catch (FileNotFoundException e) {
            System.err.println("Can't decompress file: No compressed file exists.");
            }
    }

    public static void main(String[] args) throws IOException {
        CompDecomp test = new CompDecomp("cciaooo");
        test.compressFile();
    }
}
