import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simple implements baceStrategy {
    private String question;
    private String fileName;

    public static double multCount = 0;
    public static double addCount = 0;
    private Map<String, Double> calcMap=new HashMap<>();


    public Simple(String question, String fileName,Map<String, Double> calcMap) {
        this.question = question;
        this.fileName = fileName;
        this.calcMap = calcMap;
    }

    @Override
    public List<Double> calc() {
        Map<String, Double> calcMap=new HashMap<>();
        multCount = 0;
        addCount = 0;

        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);

        String[] parts = question.substring(2, question.length() - 1).split("\\|");

        Map<String, String> queryVars = baceStrategy.parseQuestion("P(" + parts[0] + ")");
        Map<String, String> evidenceVars = parts.length > 1 ? baceStrategy.parseQuestion("P(" + parts[1] + ")") : new HashMap<>();

        Map<String, String> numerator = new HashMap<>(queryVars);
        numerator.putAll(evidenceVars);
        for (Variable var : variableMap.values()) {
            Factor f = new Factor(var.getCPT(), fileName);
            List<String> factorVars = f.getnams();

            if (factorVars.containsAll(numerator.keySet()) && numerator.keySet().containsAll(factorVars)) {
                double result = f.getProbability(numerator);
                List<Double> list = new ArrayList<>();
                list.add(Math.round(result * 100000.0) / 100000.0);
                list.add(0.0);
                list.add(0.0);
                return list;
            }
        }
        double num = sumOverCombinations(variableMap, numerator);
        double denom = sumOverCombinations(variableMap, evidenceVars);
        List<Double> list = new ArrayList<>();
        list.add(Math.round((num / denom) * 100000.0) / 100000.0);
        list.add(multCount);
        list.add(addCount);
        return list;
    }
    private double sumOverCombinations(Map<String, Variable> variableMap, Map<String, String> known) {
        List<String> missing = new ArrayList<>();
        for (String var : variableMap.keySet()) {
            if (!known.containsKey(var)) {
                missing.add(var);
            }
        }

        List<Map<String, String>> allCombinations = Factor.generateOutcomeCombinations(variableMap, missing);

        double sum = 0.0;

        for (Map<String, String> partial : allCombinations) {
            Map<String, String> fullAssign = new HashMap<>(known);
            fullAssign.putAll(partial);

            StringBuilder q = new StringBuilder("P(");
            boolean first = true;
            for (Map.Entry<String, String> entry : fullAssign.entrySet()) {
                if (!first) q.append(",");
                q.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            q.append(")");

            String s=containskey(q.toString());
            if(s!=null) {
                if(sum==0) {
                    sum += calcMap.get(s);
                }
                else {
                    sum += calcMap.get(s);
                }

            }
            else {
                JointProbability jp = new JointProbability(q.toString(), fileName,calcMap);
                double ans=jp.calc().get(0);
                if(sum==0) {
                    sum += ans;
                    calcMap.put(q.toString(), ans);
                    addCount += jp.addCount;
                    multCount += jp.multCount;
                }
                else {
                    sum += ans;
                    calcMap.put(q.toString(), ans);
                    addCount++;
                    addCount += jp.addCount;
                    multCount += jp.multCount;

                }

            }
        }

        return sum;
    }
    private String containskey(String var) {
        for (String key : calcMap.keySet()) {
            Map<String, String>vartemp=baceStrategy.parseQuestion(var);
            Map<String, String>temp= baceStrategy.parseQuestion(key);
            if(temp.equals(vartemp)){
                return key;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Map<String, Double> calcMap= new HashMap<>();
        Simple s = new Simple("P(B0=v1|A1=T)", "big_net.xml",calcMap);
        System.out.println("Result: " + s.calc());
        System.out.println("addCount: " + addCount);
        System.out.println("multCount: " + multCount);

    }
}
