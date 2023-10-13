import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 * @author Cha Krupka, Paige Harris, Dartmouth CS10, Spring 2022
 * Using data to train, can return predicted parts of speech of a given sentence
 */
public class Trainer {
    private Map<String, Map<String, Double>> toFrom, tagWord; // transition and observation data
    String file;    // file name

    /**
     * Constructs transition and observation probabilities based off of input data
     * @param file file for training and testing
     * @throws IOException
     */
    public Trainer(String file) throws IOException {
        this.file = file;
        String tagFile = "inputs/texts/"+file+"-train-tags.txt";
        String senFile = "inputs/texts/"+file+"-train-sentences.txt";
        trainer(senFile, tagFile);
        makeLogs();
    }

    /**
     * Parses through files of sentences and tags to create maps of appearances and connections
     * @param fileS sentence file
     * @param fileT tag (part-of-speech) file
     * @throws IOException
     */
    public void trainer(String fileS, String fileT) throws IOException {
        BufferedReader inputS = new BufferedReader(new FileReader(fileS)); // open the files
        BufferedReader inputT = new BufferedReader(new FileReader(fileT));
        String lineS;
        String lineT;

        toFrom = new HashMap<>();               // transition data map (which tags correspond)
        toFrom.put("#", new HashMap<>());       // add the start
        tagWord = new HashMap<>();              // map of connections between tags and words

        // parse through the data
        while ((lineS = inputS.readLine()) != null && (lineT = inputT.readLine()) != null) {
            // split the lines into lists of words or tags
            String[] words = lineS.split(" ");
            String[] tags = lineT.split(" ");
            toFrom.get("#").put(tags[0], toFrom.get("#").getOrDefault(tags[0], 0.0)+1.0); // add first to
                                                                                                     // start
            // go through list of words and tags
            for (int i = 0; i < words.length; i++) {
                String lower =  words[i].toLowerCase(); // lowercase
                words[i] = lower;

                // add connection between tag and word
                if (tagWord.containsKey(tags[i]) && !tagWord.get(tags[i]).isEmpty()) {
                    tagWord.get(tags[i]).put(words[i], tagWord.get(tags[i]).getOrDefault(words[i], 0.0)+1.0);
                }
                else {
                    tagWord.put(tags[i], new HashMap<>());
                    tagWord.get(tags[i]).put(words[i], 1.0);
                }

                // add connection between tags
                if (i < (tags.length - 1)) {
                    if (toFrom.containsKey(tags[i]) && !toFrom.get(tags[i]).isEmpty()) {
                        toFrom.get(tags[i]).put(tags[i+1], toFrom.get(tags[i]).getOrDefault(tags[i+1],
                                0.0)+1.0);

                    }
                    else {
                        toFrom.put(tags[i], new HashMap<>());
                        toFrom.get(tags[i]).put(tags[i+1], 1.0);
                    }
                }
                else if (!toFrom.containsKey(tags[i])) toFrom.put(tags[i], new HashMap<>());
            }
        }
        // close the file
        inputS.close();
        inputT.close();
    }

    /**
     * Get log probabilities
     */
    public void makeLogs() {
        makeLogsHelper(toFrom);
        makeLogsHelper(tagWord);
    }

    /**
     * Convert numerical data to logs
     * @param dataMap map of data to alter
     */
    private void makeLogsHelper(Map<String, Map<String, Double>> dataMap) {
        for (String tag : dataMap.keySet()) {                       // go through map of data
            double totalValue = 0;  // total value of appearances or connections
            for (String innerTag : dataMap.get(tag).keySet()) {     // go through inner map
                totalValue += dataMap.get(tag).get(innerTag);       // add data to total
            }
            for (String innerTag : dataMap.get(tag).keySet()) {     // convert to log
                dataMap.get(tag).put(innerTag, Math.log((dataMap.get(tag).get(innerTag))/totalValue));
            }
        }

        // put log back into map
        for (String tag : dataMap.keySet()) {
            for (String innerTag : dataMap.get(tag).keySet()) {
                dataMap.get(tag).put(innerTag, (dataMap.get(tag).get(innerTag)));
            }
        }
    }

    /**
     * Runs the viterbi algorithm on a given sentence to find most probable tags
     * @param sentence sentence to be viterbi-ed
     * @return a sentence of tags
     */
    public String viterbi(String sentence) {
        // split the sentence
        String[] words = sentence.split(" ");
        ArrayList<Map<String, String>> path = new ArrayList<>();
        double u = -100.0; // set the unknown value

        // make a map to hold scores
        Map<String, Double> currScores = new HashMap<>();
        currScores.put("#", 0.0);

        // run through the algorithm
        for (String word : words) {
            Map<String, Double> nextScores = new HashMap<>();   // hold the next scores
            path.add(0, new HashMap<>());                 // add a map of potential next's for finding the path

            for (String state : currScores.keySet()) {          // go through current scores
                if (!toFrom.containsKey(state)) continue;       // check
                for (String nextState : toFrom.get(state).keySet()) {   // go through next states, calculate score
                    double nextScore = currScores.get(state) + toFrom.get(state).get(nextState);

                    nextScore += tagWord.get(nextState).getOrDefault(word, u);

                    if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                        nextScores.put(nextState, nextScore);
                        path.get(0).put(nextState, state); // add to path
                    }
                }
            }
            currScores = nextScores; // update current scores
        }

        // find the lowest (highest) score and associated tag to start with for path
        double best = -100000;
        String bestTag = "";
        for (Map.Entry<String, Double> scrMap : currScores.entrySet()) {
            if (scrMap.getValue() > best) {
                best = scrMap.getValue();
                bestTag = scrMap.getKey();
            }
        }

        // make a list of the best path
        ArrayList<String> pathList = new ArrayList<>();
        for (Map<String, String> obsSection : path) {
            if (!bestTag.equals("#")) {
                pathList.add(0, obsSection.get(bestTag));
                bestTag = obsSection.get(bestTag);
            }
        }

        // get data to create a sentence
        String pathString = "";
        for (int ind = 1; ind < pathList.size(); ind++) {
            pathString += pathList.get(ind) + " ";
            if (ind == pathList.size()-1 ) pathString += ".";
        }
        return pathString;
    }

    /**
     * tester function to test the accuracy of a given sentence compared to real data
     * @throws IOException
     */
    public void tester() throws IOException {
        // open test data
        String testTagFile = "inputs/texts/" + file + "-test-tags.txt";
        String testSenFile = "inputs/texts/" + file + "-test-sentences.txt";
        BufferedReader testInputT = new BufferedReader(new FileReader(testTagFile));
        BufferedReader testInputS = new BufferedReader(new FileReader(testSenFile));
        String testLineT;
        String testLineS;
        int goodTags = 0;
        int badTags = 0;
        int totalTags = 0;

        // go through test data, checking tags against viterbi created tags, adding to totals
        while ((testLineS = testInputS.readLine()) != null && (testLineT = testInputT.readLine()) != null) {
            String[] testTags = testLineT.split(" ");
            String[] trainedTags = viterbi(testLineS).split(" ");
            for (int i = 0; i < trainedTags.length; i++) {
                if (testTags[i].equals(trainedTags[i])) goodTags += 1;
                else {
                    badTags += 1;
                }
                totalTags = badTags + goodTags;
            }
        }
        System.out.println("Good: "+goodTags+", Bad: "+badTags+", Total: "+totalTags);
    }

    /**
     * Allows input of user created sentences
     * @throws IOException
     */
    public void console() throws IOException {
        Scanner in = new Scanner(System.in);                // create scanner to accept console input
        boolean quit = false;

        while (!quit) {
            System.out.print("Enter a sentence: ");
            String line = in.nextLine();
            if (line.equals("q")||line.equals("q ")) {
                quit = true;
                continue;
            }

            String[] tempLine = line.split(" ");
            String[] tagList = viterbi(line).split(" ");
            String joinSentence = "";

            for (int i = 0; i < tempLine.length; i++) {
                joinSentence += tempLine[i]+"/"+tagList[i]+" ";
            }
            System.out.println(joinSentence+"\n");
        }
    }

    public static void main(String[] args) throws IOException {
        Trainer test = new Trainer("brown");
        test.tester();
        test.console();
    }
}
