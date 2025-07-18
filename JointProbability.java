import java.util.*;

public class JointProbability implements baceStrategy {
    private String question;
    private String fileName;
    public static double multCount = 0;
    public static double addCount = 0;
    private Map<String, Double> calcMap ;


    public JointProbability(String question, String fileName, Map<String, Double>calcMap) {
        this.question = question;
        this.fileName = fileName;
        multCount = 0;
        addCount = 0;
        this.calcMap = calcMap;
    }
    @Override
    public List<Double> calc() {
        multCount = 0;
        addCount = 0;

        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        Map<String, String> assignment = baceStrategy.parseQuestion(question);


        double ANS = 1.0;

        for (String var : assignment.keySet()) {
            Variable variable = variableMap.get(var);
            String valStr = assignment.get(var);

            List<String> parentVals = new ArrayList<>();
            for (Variable parent : variable.getParents()) {
                String parentVal = assignment.get(parent.getName());
                parentVals.add(parentVal);
            }
//            if(!calcMap.containsKey(var+"="+valStr)) {
//                double prob = variable.getCPT().getProb(valStr, parentVals);
//                if(prob !=1) {
//                    ANS *= prob;
//                    multCount++;
//                }
//                calcMap.put(var+"="+valStr, prob);
//            }
//            else {
//                ANS *= calcMap.get(var+"="+valStr);
//            }
            double prob = variable.getCPT().getProb(valStr, parentVals);
            if(ANS==1){
                ANS = prob;
            }
            else {
                ANS *= prob;
                multCount++;
            }

        }
        List<Double> list = new ArrayList<>();
        list.add(ANS);
        list.add(multCount);
        list.add(addCount);
//        return Math.round((num / denom) * 100000.0) / 100000.0;
        return list;
//        return Math.round(ANS * 100000.0) / 100000.0;
//        return ANS;
}




    public static void main(String[] args) {
//        Map<String, Double> calcMap =new HashMap<>();
//        JointProbability s = new JointProbability("P(B=F,E=T,A=T,M=T,J=F)", "alarm_net.xml",calcMap);
//        System.out.println("Result: " + s.calc());
//        System.out.println(multCount);

        Map<String, Double> calcMap =new HashMap<>();
        JointProbability s = new JointProbability("P(A1=T,A2=F,A3=F,B0=v3,B1=F,B2=T,B3=F,C1=F,C2=v2,C3=F,D1=T)", "big_net.xml",calcMap);
        System.out.println("Result: " + s.calc());
        System.out.println(multCount);

    }
}
