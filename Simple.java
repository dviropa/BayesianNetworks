import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simple implements baceStrategy {
    private String question;
    private String fileName;

    public static int multCount = 0;
    public static int addCount = 0;


    public Simple(String question, String fileName) {
        this.question = question;
        this.fileName = fileName;
    }

    @Override
    public Double calc() {
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);

        // חלק את השאלה ל-P(X=...|Y=...) → P(X,Y) / P(Y)
        String[] parts = question.substring(2, question.length() - 1).split("\\|");

        Map<String, String> queryVars = baceStrategy.parseQuestion("P(" + parts[0] + ")");
        Map<String, String> evidenceVars = parts.length > 1 ? baceStrategy.parseQuestion("P(" + parts[1] + ")") : new HashMap<>();

        // מונה - X ∪ Y
        Map<String, String> numerator = new HashMap<>(queryVars);
        numerator.putAll(evidenceVars);

        // מחשב מונה ומכנה על כל ההשלמות האפשריות
        double num = sumOverCombinations(variableMap, numerator);
        System.out.println(num);
        double denom = sumOverCombinations(variableMap, evidenceVars);
        System.out.println(denom);

        return Math.round((num / denom) * 100000.0) / 100000.0;
    }

    private double sumOverCombinations(Map<String, Variable> variableMap, Map<String, String> known) {
        List<String> missing = new ArrayList<>();
        for (String var : variableMap.keySet()) {
            if (!known.containsKey(var)) {
                missing.add(var);
            }
        }

        int total = (int) Math.pow(2, missing.size());
        double sum = 0.0;
        JointProbability jp;
        for (int i = 0; i < total; i++) {
            Map<String, String> fullAssign = new HashMap<>(known);
            for (int j = 0; j < missing.size(); j++) {
                String val = ((i >> j) & 1) == 1 ? "T" : "F";
                fullAssign.put(missing.get(j), val);
            }

            StringBuilder q = new StringBuilder("P(");
            boolean first = true;
            for (Map.Entry<String, String> entry : fullAssign.entrySet()) {
                if (!first) q.append(",");
                q.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            q.append(")");

            jp = new JointProbability(q.toString(), fileName);
            sum += jp.calc();
            addCount++;
            addCount+=jp.addCount;
            multCount+=jp.multCount;
        }

        return sum;
    }

    public static void main(String[] args) {
        Simple s = new Simple("P(B=T|J=T,M=T)", "alarm_net.xml");
        System.out.println("Result: " + s.calc());
        System.out.println("addCount: " + addCount);
        System.out.println("multCount: " + multCount);




    }
}
