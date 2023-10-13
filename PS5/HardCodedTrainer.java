import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cha Krupka, Paige Harris, Dartmouth CS10, Spring 2022
 * Hard-coded example of a graph for testing. For documentation see Trainer.java, as classes are nearly identical.
 */
public class HardCodedTrainer {
    private Map<String, Map<String, Double>> toFrom, tagWord;

    public HardCodedTrainer() {
        trainer();
        makeLogs();
        System.out.println(toFrom);
        System.out.println(tagWord);
    }

    /**
     * hard-coded graph
     */
    public void trainer() {
        toFrom = new HashMap<>();
        toFrom.put("#", new HashMap<>());
        tagWord = new HashMap<>();

        toFrom.get("#").put("N", 5.0);
        toFrom.get("#").put("NP", 2.0);
        toFrom.put("CNJ", new HashMap<>());
        toFrom.get("CNJ").put("N", 1.0);
        toFrom.get("CNJ").put("NP", 1.0);
        toFrom.get("CNJ").put("V", 1.0);
        toFrom.put("N", new HashMap<>());
        toFrom.get("N").put("CNJ", 2.0);
        toFrom.get("N").put("V", 6.0);
        toFrom.put("NP", new HashMap<>());
        toFrom.get("NP").put("V", 2.0);
        toFrom.put("V", new HashMap<>());
        toFrom.get("V").put("CNJ", 1.0);
        toFrom.get("V").put("N", 6.0);
        toFrom.get("V").put("NP", 2.0);

        tagWord.put("CNJ", new HashMap<>());
        tagWord.get("CNJ").put("and", 3.0);
        tagWord.put("N", new HashMap<>());
        tagWord.get("N").put("cat", 5.0);
        tagWord.get("N").put("dog", 5.0);
        tagWord.get("N").put("watch", 2.0);
        tagWord.put("NP", new HashMap<>());
        tagWord.get("NP").put("chase", 5.0);
        tagWord.put("V", new HashMap<>());
        tagWord.get("V").put("chase", 2.0);
        tagWord.get("V").put("get", 1.0);
        tagWord.get("V").put("watch", 6.0);
    }

    public void makeLogs() {
        makeLogsHelper(toFrom);
        makeLogsHelper(tagWord);
    }

    private void makeLogsHelper(Map<String, Map<String, Double>> dataMap) {
        for (String tag : dataMap.keySet()) {
            double totalValue = 0;
            for (String innerTag : dataMap.get(tag).keySet()) {
                totalValue += dataMap.get(tag).get(innerTag);
            }
            for (String innerTag : dataMap.get(tag).keySet()) {
                dataMap.get(tag).put(innerTag, Math.log((dataMap.get(tag).get(innerTag))/totalValue));
            }
        }

        for (String tag : dataMap.keySet()) {
            for (String innerTag : dataMap.get(tag).keySet()) {
                dataMap.get(tag).put(innerTag, (dataMap.get(tag).get(innerTag)));
            }
        }
    }

    public String viterbi(String sentence) {
        String[] words = sentence.split(" ");
        ArrayList<Map<String, String>> path = new ArrayList<>();
        int u = -100;

        Map<String, Double> currScores = new HashMap<>();
        currScores.put("#", 0.0);

        for (int i = 0; i < words.length; i++) {
            Map<String, Double> nextScores = new HashMap<>();
            path.add(0, new HashMap<>());

            for (String state : currScores.keySet()) {
                if (!toFrom.containsKey(state)) continue;
                for (String nextState : toFrom.get(state).keySet()) {
                    double nextScore = currScores.get(state) + toFrom.get(state).get(nextState);

                    if (tagWord.get(nextState).containsKey(words[i])) {
                        nextScore += tagWord.get(nextState).get(words[i]);
                    }
                    else nextScore += u;

                    if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                        nextScores.put(nextState, nextScore);
                        path.get(0).put(nextState, state);
                    }
                }
            }
            currScores = nextScores;
        }

        double best = -100000;
        String bestTag = "";
        for (Map.Entry<String, Double> scrMap : currScores.entrySet()) {
            if (scrMap.getValue() > best) {
                best = scrMap.getValue();
                bestTag = scrMap.getKey();
            }
        }

        ArrayList<String> pathList = new ArrayList<>();
        for (Map<String, String> obsSection : path) {
            if (!bestTag.equals("#")) {
                pathList.add(0, obsSection.get(bestTag));
                bestTag = obsSection.get(bestTag);
            }
        }

        String pathString = "";
        for (int ind = 1; ind < pathList.size(); ind++) {
            pathString += pathList.get(ind) + " ";
            if (ind == pathList.size()-1 ) pathString += ".";
        }
        return pathString;
    }

    public static void main(String[] args) throws IOException {
        HardCodedTrainer test = new HardCodedTrainer();
        System.out.println(test.viterbi("cat chase watch ."));
        System.out.println(test.viterbi("cat watch dog ."));
        System.out.println(test.viterbi("cat and dog watch cat ."));
    }
}
