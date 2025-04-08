import java.util.*;
import java.util.stream.Collectors;

public class Factor {
    public List<Variable> scope = new ArrayList<>();
    public List<Double> values = new ArrayList<>();

    public Factor(List<Variable> scope, List<Double> values) {
        this.scope = scope;
        this.values = values;
    }

    public Factor(Factor f) {
        this.scope = new ArrayList<>(f.scope);
        this.values = new ArrayList<>(f.values);
    }


    public Factor(CPT c) {
        this.values = new ArrayList<>(c.getProbabilities());
        this.scope.addAll(c.parents);
        this.scope.add(c.variable);
        System.out.println("cpt: " + c.toString());
    }

    public List<Double> getvalues() {
        return values;
    }

    public List<Variable> getvarubels() {
        return scope;
    }

    private int getIndexFor(Map<String, Integer> assignment, Factor f) {
        List<Variable> vars = f.getvarubels();
        int index = 0;
        for (int i = 0; i < vars.size(); i++) {
            int val = assignment.get(vars.get(i).getName());
            index = (index << 1) | val;
        }
        return index;
    }

    public Factor unione(Factor f2) {
        List<Variable> newScope = new ArrayList<>();
        Set<String> names = new HashSet<>();
        double d=0.0;

        for (Variable v : this.scope) {
            if (!names.contains(v.getName())) {
                newScope.add(v);
                names.add(v.getName());
            }
        }

        for (Variable v : f2.scope) {
            if (!names.contains(v.getName())) {
                newScope.add(v);
                names.add(v.getName());
            }
        }

        List<String> allVarNames = newScope.stream().map(Variable::getName).collect(Collectors.toList());
        int totalAssignments = (int) Math.pow(2, allVarNames.size());
        List<Double> newValues = new ArrayList<>();

        for (int i = 0; i < totalAssignments; i++) {
            Map<String, Integer> assignment = new HashMap<>();
            for (int j = 0; j < allVarNames.size(); j++) {
                int bit = (i >> (allVarNames.size() - j - 1)) & 1;
                assignment.put(allVarNames.get(j), bit);/////מוריד -1 בדיקה
            }

            int idx1 = getIndexFor(assignment, this);
            int idx2 = getIndexFor(assignment, f2);

            double v1 = (idx1 >= 0 && idx1 < this.values.size()) ? this.values.get(idx1) : 1.0;
            double v2 = (idx2 >= 0 && idx2 < f2.values.size()) ? f2.values.get(idx2) : 1.0;

            newValues.add(v1 * v2);
            d+=v1 * v2;
        }
//        List<Double> t = new ArrayList<>();
//        for (int i = 0; i < newValues.size(); i++) {
//            t.add(newValues.get(i)/d);
//        }

        System.out.println("[UNION] New scope: " + newScope.stream().map(Variable::getName).toList());
        System.out.println("[UNION] Values: " + newValues);

        return new Factor(newScope, newValues);
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


    public Factor variable_Elimination(List<String> varName) {
        List<Variable> scope = new ArrayList<>();
        for (Variable v : this.getvarubels()) {
            if (varName.contains(v.getName())) {
                scope.add(v);
            }
        }

        List<String> allVars = this.getvarubels().stream()
                .map(Variable::getName)
                .collect(Collectors.toList());

        List<String> keepNames = new ArrayList<>();
        for (String name : allVars) {
            if (varName.contains(name)) {
                keepNames.add(name);
            }
        }

        List<String> eliminateNames = new ArrayList<>();
        for (String name : allVars) {
            if (!keepNames.contains(name)) {
                eliminateNames.add(name);
            }
        }

        int totalKeepAssignments = (int) Math.pow(2, keepNames.size());
        List<Double> ans = new ArrayList<>();

        for (int i = 0; i < totalKeepAssignments; i++) {
            Map<String, Integer> assignment = new HashMap<>();
            for (int j = 0; j < keepNames.size(); j++) {
                int bit = (i >> (keepNames.size() - j - 1)) & 1;
                assignment.put(keepNames.get(j), bit);
            }

            int totalElimAssignments = (int) Math.pow(2, eliminateNames.size());
            double sum = 0.0;

            for (int k = 0; k < totalElimAssignments; k++) {
                Map<String, Integer> fullAssignment = new HashMap<>(assignment);
                for (int m = 0; m < eliminateNames.size(); m++) {
                    int bit = (k >> (eliminateNames.size() - m - 1)) & 1;
                    fullAssignment.put(eliminateNames.get(m), bit);
                }

                int index = getIndexFor(fullAssignment, this);
                sum += this.values.get(index);
            }

            ans.add(sum);
        }



        System.out.println("[ELIMINATION] Keep: " + keepNames + ", Eliminate: " + eliminateNames);
        System.out.println("[ELIMINATION] Resulting values: " + ans);
        return   new Factor(scope, ans);
    }

    public Factor restrict(String varName, int value) {
        List<Variable> newScope = new ArrayList<>();
        for (Variable v : this.scope) {
            if (!v.getName().equals(varName)) {
                newScope.add(v);
            }
        }

        List<String> newVarNames = newScope.stream().map(Variable::getName).collect(Collectors.toList());
        int totalAssignments = (int) Math.pow(2, newVarNames.size());
        List<Double> newValues = new ArrayList<>();

        for (int i = 0; i < totalAssignments; i++) {
            Map<String, Integer> assignment = new HashMap<>();
            for (int j = 0; j < newVarNames.size(); j++) {
                int bit = (i >> (newVarNames.size() - j - 1)) & 1;
                assignment.put(newVarNames.get(j), bit);
            }

            assignment.put(varName, value);
            int index = getIndexFor(assignment, this);
            newValues.add(this.values.get(index));
        }

        System.out.println("[RESTRICT] Removed: " + varName + "=" + value + ", New scope: " + newVarNames);
        System.out.println("[RESTRICT] New values: " + newValues);

        return new Factor(newScope, newValues);
    }
}