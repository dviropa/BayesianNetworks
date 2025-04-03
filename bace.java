import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface bace {
     Double calc();
     static Map<String, Variable> getVariable(String fileName){
        Map<String, Variable> variableMap;
        try {
            variableMap = ReadXMLFile.reade(fileName);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return variableMap;
    }
     static Map<String, String> questionsToMap(List<String> questions) {
        Map<String, String> assignment = new HashMap<>();
        for (String q : questions) {
            if (q.contains("=")) {
                String[] parts = q.split("=");
                String var = parts[0].trim();
                String val = parts[1].trim();
                assignment.put(var, val);
            }
        }
        return assignment;
    }
    static int getCPTIndex(Variable var, List<String> orderedValues) {
        List<Variable> parents = var.getParents();
        List<Integer> domainSizes = new ArrayList<>();

        // סדר: ההורים ואז המשתנה עצמו
        for (Variable p : parents) {
            domainSizes.add(p.getValues().size());
        }
        domainSizes.add(var.getValues().size());

        int index = 0;
        int multiplier = 1;

        for (int i = domainSizes.size() - 1; i >= 0; i--) {
            Variable currVar = (i < parents.size()) ? parents.get(i) : var;
            String value = orderedValues.get(i);
            int valIndex = currVar.getValues().indexOf(value);
            index += valIndex * multiplier;
            multiplier *= domainSizes.get(i);
        }

        return index;
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

            int index = getCPTIndex(var, valuesInOrder);
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
        Map<String, Variable> variableMap = getVariable( fileName);
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





}
