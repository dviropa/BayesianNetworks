import java.util.*;
import java.util.stream.Collectors;

public class Factor {
    public List<Variable> scope = new ArrayList<>();
    public List<Double> values = new ArrayList<>();
    public List<String> nams = new ArrayList<>();
    private String fileName;
    public static int multCount = 0;
    public static int addCount = 0;

    public Factor(List<Variable> scope, List<Double> values, List<String> nams, String fileName) {
        this.scope = scope;
        this.values = values;
        this.nams = nams;
        this.fileName = fileName;

    }

    public Factor(Factor f) {
        this.scope = new ArrayList<>(f.scope);
        this.values = new ArrayList<>(f.values);
        this.nams = new ArrayList<>(f.nams);
        this.fileName = f.fileName;


    }

    public Factor(CPT c, String fileName) {
        this.values = new ArrayList<>(c.getProbabilities());
        this.scope.addAll(c.parents);
        this.scope.add(c.variable);
        this.fileName = fileName;
        for (Variable v : c.parents) {
            this.nams.add(v.getName());
        }
        this.nams.add(c.variable.getName());
//        System.out.println("cpt: " + c.toString());

    }

    public List<String> getnams() {
        return nams;
    }

    public List<Double> getvalues() {
        return values;
    }

    public List<Variable> getvarubels() {
        return scope;
    }

    private int getIndexFor(Map<String, String> assignment, List<String> orderedVarNames, String fileName) {
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        List<Map<String, String>> allCombinations = generateOutcomeCombinations(variableMap, orderedVarNames);

        for (int i = 0; i < allCombinations.size(); i++) {
            Map<String, String> combination = allCombinations.get(i);
            boolean match = true;

            for (String varName : orderedVarNames) {
                String valFromAssignment = assignment.get(varName);
                String valFromCombination = combination.get(varName);

                if (valFromAssignment == null || !valFromAssignment.equals(valFromCombination)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                return i;
            }
        }

        // הדפסה לדיבאג
//        System.out.println(" Combo not found: " + assignment);
        return -1;
    }


    public double getProbability(Map<String, String> assignment) {
        int index = getCombinationIndex(this.nams, assignment, fileName);
        if (index == -1) {
//            System.out.println(" Combo not found: " + assignment);
        }
        return this.values.get(index);
    }

    public static int getCombinationIndex(List<String> variableNames, Map<String, String> targetAssignment, String fileName) {
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        List<Map<String, String>> combinations = generateOutcomeCombinations(variableMap, variableNames);

        for (int i = 0; i < combinations.size(); i++) {
            if (combinations.get(i).equals(targetAssignment)) {
                return i;
            }
        }

        return -1;
    }

    public static List<Map<String, String>> generateOutcomeCombinations(Map<String, Variable> variableMap, List<String> variableNames) {
        List<Map<String, String>> result = new ArrayList<>();
        backtrack(variableMap, variableNames, 0, new HashMap<>(), result);
        return result;
    }

    private static void backtrack(Map<String, Variable> variableMap, List<String> variableNames, int index, Map<String, String> current, List<Map<String, String>> result) {
        if (index == variableNames.size()) {
            result.add(new HashMap<>(current));
            return;
        }

        String varName = variableNames.get(index);
        Variable var = variableMap.get(varName);

        if (var == null) {
            System.err.println("Variable not found: " + varName);
            return;
        }

        List<String> outcomes = var.getOUTCOMES();
        for (String outcome : outcomes) {
            current.put(varName, outcome);
            backtrack(variableMap, variableNames, index + 1, current, result);
            current.remove(varName);
        }
    }

    public Factor unione(Factor f2) {
        multCount =0;
        List<Variable> newScope = new ArrayList<>();
        Set<String> names = new HashSet<>();

        for (Variable v : this.scope) {
            if (names.add(v.getName())) {
                newScope.add(v);
            }
        }
        for (Variable v : f2.scope) {
            if (names.add(v.getName())) {
                newScope.add(v);
            }
        }

        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        List<String> allVarNames = newScope.stream().map(Variable::getName).collect(Collectors.toList());
        List<Map<String, String>> allCombinations = generateOutcomeCombinations(variableMap, allVarNames);

        List<Double> newValues = new ArrayList<>();
        for (Map<String, String> combo : allCombinations) {
            int idx1 = getIndexFor(combo, this.nams, fileName);
            int idx2 = getIndexFor(combo, f2.nams, fileName);

            if (idx1 == -1 || idx2 == -1) {
//                System.out.println("❌ Combo not found: " + combo + "\n  idx1: " + idx1 + " | idx2: " + idx2);
            }

            double v1 = (idx1 >= 0 && idx1 < this.values.size()) ? this.values.get(idx1) : 1.0;
            double v2 = (idx2 >= 0 && idx2 < f2.values.size()) ? f2.values.get(idx2) : 1.0;

            newValues.add(v1 * v2);
            multCount ++;

        }

//        System.out.println("[UNION] New scope: " + allVarNames);
//        System.out.println("[UNION] Values: " + newValues);

        return new Factor(newScope, newValues, allVarNames, fileName);
    }

    public void normalize() {
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        if (sum == 0) {
            throw new IllegalStateException("Cannot normalize with total sum 0.");
        }
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i) / sum);
        }
    }

    public Factor variable_Elimination(List<String> keepNames) {
        addCount=0;
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        List<String> allNames = this.scope.stream().map(Variable::getName).collect(Collectors.toList());
        List<String> eliminateNames = allNames.stream().filter(name -> !keepNames.contains(name)).collect(Collectors.toList());
        List<Map<String, String>> keepCombinations = generateOutcomeCombinations(variableMap, keepNames);

        List<Double> newValues = new ArrayList<>();
        for (Map<String, String> partialAssignment : keepCombinations) {
            double sum = 0.0;
            List<Map<String, String>> eliminateCombinations = generateOutcomeCombinations(variableMap, eliminateNames);
            for (Map<String, String> elimAssignment : eliminateCombinations) {
                Map<String, String> fullAssignment = new HashMap<>(partialAssignment);
                fullAssignment.putAll(elimAssignment);
                int index = getIndexFor(fullAssignment, this.nams, fileName);
                sum += this.values.get(index);
                addCount ++;
            }
            newValues.add(sum);
        }

        List<Variable> newScope = this.scope.stream().filter(v -> keepNames.contains(v.getName())).collect(Collectors.toList());
//        System.out.println("[ELIMINATION] Keep: " + keepNames + ", Eliminate: " + eliminateNames);
//        System.out.println("[ELIMINATION] Resulting values: " + newValues);
        return new Factor(newScope, newValues, keepNames, fileName);
    }
    public Factor restrict(Map<String, String> restrictions) {
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);

        // משתנים חדשים - אחרי הסרת המשתנים שהוצבו
        List<Variable> newScope = new ArrayList<>();
        List<String> newNams = new ArrayList<>();

        for (Variable v : scope) {
            if (!restrictions.containsKey(v.getName())) {
                newScope.add(v);
                newNams.add(v.getName());
            }
        }

        // כל הצירופים האפשריים של המשתנים החדשים
        List<Map<String, String>> combinations = generateOutcomeCombinations(variableMap, newNams);

        List<Double> newValues = new ArrayList<>();

        for (Map<String, String> partialAssign : combinations) {
            // מצרף את ההגבלות (ההשמות) למיפוי הנוכחי
            Map<String, String> fullAssign = new HashMap<>(partialAssign);
            fullAssign.putAll(restrictions);

            int index = getIndexFor(fullAssign, this.nams, fileName);
            if (index != -1) {
                newValues.add(this.values.get(index));
            }
        }

        return new Factor(newScope, newValues, newNams, fileName);
    }


}
