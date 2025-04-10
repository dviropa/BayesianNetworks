import java.util.*;

public class JointProbability implements bace {
    private String question;
    private String fileName;
    public static int multCount = 0;
    public static int addCount = 0;


    public JointProbability(String question, String fileName) {
        this.question = question;
        this.fileName = fileName;
        multCount = 0;
        addCount = 0;
    }
    @Override
    public Double calc() {
        Map<String, Variable> variableMap = bace.getVariable(fileName);
        Map<String, String> assignment = bace.parseQuestion(question);


        double ANS = 1.0;

        for (String var : assignment.keySet()) {
            Variable variable = variableMap.get(var);
            String valStr = assignment.get(var);

            List<String> parentVals = new ArrayList<>();
            for (Variable parent : variable.getParents()) {
                String parentVal = assignment.get(parent.getName());
                parentVals.add(parentVal);
            }

            double prob = variable.getCPT().getProb(valStr, parentVals);
            ANS *= prob;
            multCount++;
        }

//        return Math.round(ANS * 100000.0) / 100000.0;
        return ANS;
}




    public static void main(String[] args) {
        JointProbability s = new JointProbability("P(B=F,E=T,A=T,M=T,J=F)", "alarm_net.xml");
        System.out.println("Result: " + s.calc());
        System.out.println(multCount);

    }
}
