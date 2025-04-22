import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public interface baceStrategy {
    List<Double> calc();
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
    public static Map<String, List<String>> questionsToMap(String question) {
        Map<String, List<String>> map = new HashMap<>();

        if (!question.contains("|")) {
            String[] parts = question.replace("P(", "").replace(")", "").split("=");
            String var = parts[0].trim();
            map.put(var, new ArrayList<>());
        } else {
            question = question.replace("P(", "").replace(")", "");
            String[] parts = question.split("\\|");

            String left = parts[0].trim();
            String right = parts[1].trim();

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

        for (Variable p : parents) {
            domainSizes.add(p.getValues().size());
        }
        domainSizes.add(var.getValues().size());

        int index = 0;
        int multiplier = 1;

        for (int i = domainSizes.size() - 1; i >= 0; i--) {
            Variable currVar = (i < parents.size()) ? parents.get(i) : var;
            String value = orderedValues.get(i);
            int valIndex = currVar.getOUTCOMES().indexOf(value);

            if (valIndex == -1) {
                valIndex = currVar.getOUTCOMES().indexOf(value.toLowerCase());
                if (valIndex == -1) {
                    throw new IllegalArgumentException("Value '" + value + "' not found in variable '" + currVar.getName() + "'");
                }            }

            index += valIndex * multiplier;
            multiplier *= domainSizes.get(i);
        }

        return index;
    }

    public static String getQueryVariable(String question) {
        String inner = question.substring(2, question.length() - 1);
        String[] parts = inner.split("\\|");
        String queryPart = parts[0].trim();
        return queryPart.split("=")[0].trim();
    }


    public static Map<String, String> parseQuestion(String question) {
        Map<String, String> result = new LinkedHashMap<>();

        String inside = question.substring(question.indexOf('(') + 1, question.indexOf(')'));
        String[] parts = inside.split(",");

        for (String part : parts) {
            String[] split = part.split("=");
            String var = split[0].trim();
            String val = split[1].trim().toUpperCase();
            result.put(var, val);
        }

        return result;
    }
    public static Map<String, String> extractQueryAssignment(String question) {
        Map<String, String> result = new HashMap<>();

        if (!question.contains("|")) {
            String inside = question.substring(2, question.length() - 1);
            String[] assignments = inside.split(",");
            for (String part : assignments) {
                String[] split = part.split("=");
                result.put(split[0].trim(), split[1].trim());
            }
        } else {
            String inside = question.substring(2, question.length() - 1);
            String[] parts = inside.split("\\|");
            String[] queryAssignments = parts[0].trim().split(",");
            for (String part : queryAssignments) {
                String[] split = part.split("=");
                result.put(split[0].trim(), split[1].trim());
            }
        }

        return result;
    }

    public static Map<String, String> extractEvidence(String question) {
        Map<String, String> evidenceMap = new LinkedHashMap<>();

        if (!question.contains("|")) {
            return evidenceMap;
        }

        String[] parts = question.substring(2, question.length() - 1).split("\\|");
        if (parts.length < 2) return evidenceMap;

        String evidencePart = parts[1].trim();
        String[] assignments = evidencePart.split(",");

        for (String assign : assignments) {
            if (assign.contains("=")) {
                String[] kv = assign.split("=");
                evidenceMap.put(kv[0].trim(), kv[1].trim());
            }
        }

        return evidenceMap;
    }


}
