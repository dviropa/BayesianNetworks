import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simple implements bace{
    private String question ;
    private String fileName ;

    public Simple(String l,String fileName) {
        this.question = l;
        this.fileName = fileName;
    }

    static Double jointProbability(String question,String fileName) {
        Map<String, Variable> variableMap = bace.getVariable( fileName); // הנחת בסיס שהרשת כבר קיימת
        String[] parts = question.replace("P(", "").replace(")", "").split(",");
        List<String> flatQuery = new ArrayList<>();
        for (String part : parts) {
            flatQuery.add(part.trim());
        }

        Map<String, String> assignment = bace.questionsToMap(flatQuery);
        double result = 1.0;

        for (String varName : assignment.keySet()) {
            Variable var = variableMap.get(varName);
            String varValue = assignment.get(varName);

            CPT cpt = var.getCPT();
            List<Variable> parents = var.getParents();

            // בניית רשימת ערכים לפי הסדר של ההורים + ערך המשתנה עצמו
            List<String> valuesInOrder = new ArrayList<>();
            for (Variable parent : parents) {
                valuesInOrder.add(assignment.get(parent.getName()));
            }
            valuesInOrder.add(varValue);

            int index = bace.getCPTIndex(var, valuesInOrder);
            double prob = cpt.getProbabilities().get(index);

            result *= prob;
        }

        return result;
    }
    static double conditionalProbability(String queryLine,String fileName) {
        String inner = queryLine.replace("P(", "").replace(")", "");
        String[] parts = inner.split("\\|");

        // חלק שמאלי - השאילתה (כמו B=T)
        String query = parts[0].trim();

        // חלק ימני - ה-evidence
        String[] evidence = (parts.length > 1) ? parts[1].split(",") : new String[0];

        // בניית המונה
        List<String> numerator = new ArrayList<>();
        numerator.add(query);
        for (String e : evidence) numerator.add(e.trim());
        double num = jointProbability("P(" + String.join(",", numerator) + ")",fileName);

        String varName = query.split("=")[0].trim();
        Map<String, Variable> variableMap = bace.getVariable( fileName);
        Variable var = variableMap.get(varName);

        double denom = 0;
        for (String val : var.getValues()) {
            List<String> terms = new ArrayList<>();
            terms.add(varName + "=" + val);
            for (String e : evidence) terms.add(e.trim());
            denom += jointProbability("P(" + String.join(",", terms) + ")",fileName);
        }

        return num / denom;
    }


    @Override
    public Double calc() {
        if(this.question.contains("|")){
            System.out.println(conditionalProbability("P(B=F,E=T,A=T,M=T,J=F)","alarm_net.xml"));
            return conditionalProbability(question,fileName);
        }else {
            return jointProbability(question,fileName);
        }
    }

    public static void main(String[] args) {
        Simple s = new Simple("P(B=T|J=T,M=T)", "alarm_net.xml");
        System.out.println(        s.calc()
);
    }

}
