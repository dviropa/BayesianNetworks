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
    public static Map<String, List<String>> questionsToMap(String question) {
        Map<String, List<String>> map = new HashMap<>();

        if (!question.contains("|")) {
            // שאלה לא מותנית: P(B=T)
            String[] parts = question.replace("P(", "").replace(")", "").split("=");
            String var = parts[0].trim();
            map.put(var, new ArrayList<>());
        } else {
            // שאלה מותנית: P(B=T | J=T, M=T)
            question = question.replace("P(", "").replace(")", "");
            String[] parts = question.split("\\|");

            String left = parts[0].trim();  // B=T
            String right = parts[1].trim(); // J=T, M=T

            String queryVar = left.split("=")[0].trim();
            String[] evidenceAssignments = right.split(",");

            List<String> evidenceVars = new ArrayList<>();
            for (String assign : evidenceAssignments) {
                String ev = assign.split("=")[0].trim();
                evidenceVars.add(ev);
            }

            map.put(queryVar, evidenceVars);
        }

        return map;
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
            if (valIndex == -1) {
                throw new IllegalArgumentException("Value '" + value + "' not found in variable '" + currVar.getName() + "'");
            }

            index += valIndex * multiplier;
            multiplier *= domainSizes.get(i);
        }

        return index;
    }






}
