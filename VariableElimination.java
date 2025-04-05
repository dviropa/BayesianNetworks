import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class VariableElimination implements bace {
    private String question;
    private final Map<Map<String, String>, Double> memo = new HashMap<>();

    private String fileName;

    public VariableElimination(String l, String fileName) {
        this.question = l;
        this.fileName = fileName;
    }

    @Override
    public Double calc() {
        Map<String, Variable> variableMap = bace.getVariable(fileName);

        // ניתוח השאילתה
        String inner = question.replace("P(", "").replace(")", "");
        String[] parts = inner.split("\\|");
        String queryPart = parts[0].trim();
        String[] querySplit = queryPart.split("=");
        String queryVarName = querySplit[0].trim();
        String queryVal = querySplit[1].trim();
        Variable queryVar = variableMap.get(queryVarName);

        // מיפוי עדויות
        Map<String, String> evidenceMap = new HashMap<>();
        if (parts.length > 1) {
            String[] evidences = parts[1].split(",");
            for (String e : evidences) {
                String[] evSplit = e.trim().split("=");
                evidenceMap.put(evSplit[0].trim(), evSplit[1].trim());
            }
        }

        List<Variable> allVars = new ArrayList<>(variableMap.values());
        List<Variable> finalOrder = topologicalSort(allVars);

        for (Variable v : allVars) {
            if (!finalOrder.contains(v)) {
                finalOrder.add(v);
            }
        }

        Map<String, String> fullEvidence = new HashMap<>(evidenceMap);
        fullEvidence.put(queryVarName, queryVal);
        double numerator = enumerateAll(finalOrder, fullEvidence, variableMap);

        double denominator = 0.0;
        for (String val : queryVar.getValues()) {
            Map<String, String> extendedEvidence = new HashMap<>(evidenceMap);
            extendedEvidence.put(queryVarName, val);
            denominator += enumerateAll(finalOrder, extendedEvidence, variableMap);
        }

        if (denominator == 0.0) {
            throw new ArithmeticException("Denominator is 0 – can't normalize. Evidence may be invalid or inconsistent.");
        }

        return numerator / denominator;
    }

    private double enumerateAll(List<Variable> vars, Map<String, String> evidence, Map<String, Variable> variableMap) {
        if (vars.isEmpty()) return 1.0;

        if (memo.containsKey(evidence)) {
            return memo.get(evidence);
        }

        Variable first = vars.get(0);
        List<Variable> rest = vars.subList(1, vars.size());
        String varName = first.getName();

        double result;

        if (evidence.containsKey(varName)) {
            String val = evidence.get(varName);
            List<String> parentVals = getParentValuesByName(first, evidence);
            if (parentVals == null) return 0.0;
            double prob = first.getCPT().getProb(val, parentVals);
            result = prob * enumerateAll(rest, evidence, variableMap);
        } else {
            result = 0.0;
            for (String val : first.getValues()) {
                Map<String, String> newEvidence = new HashMap<>(evidence);
                newEvidence.put(varName, val);
                List<String> parentVals = getParentValuesByName(first, newEvidence);
                if (parentVals != null) {
                    double prob = first.getCPT().getProb(val, parentVals);
                    result += prob * enumerateAll(rest, newEvidence, variableMap);
                }
            }
        }

        memo.put(new HashMap<>(evidence), result);
        return result;
    }

    private static List<String> getParentValuesByName(Variable var, Map<String, String> evidence) {
        List<String> values = new ArrayList<>();
        for (Variable parent : var.getParents()) {
            String name = parent.getName();
            if (!evidence.containsKey(name)) return null;
            values.add(evidence.get(name));
        }
        return values;
    }

    private static List<Variable> topologicalSort(List<Variable> variables) {
        List<Variable> sorted = new ArrayList<>();
        Map<String, Boolean> visited = new HashMap<>();

        for (Variable var : variables) {
            visited.put(var.getName(), false);
        }

        for (Variable var : variables) {
            if (!visited.get(var.getName())) {
                dfsVisit(var, visited, sorted);
            }
        }

        return sorted;
    }

    private static void dfsVisit(Variable var, Map<String, Boolean> visited, List<Variable> sorted) {
        visited.put(var.getName(), true);
        for (Variable parent : var.getParents()) {
            if (!visited.get(parent.getName())) {
                dfsVisit(parent, visited, sorted);
            }
        }
        if (!sorted.contains(var)) {
            sorted.add(var);
        }
    }

    public static void main(String[] args) {
        VariableElimination v = new VariableElimination("P(B=T|J=T,M=T)", "alarm_net.xml");
        System.out.println("Result: " + v.calc());
    }
}
