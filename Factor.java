import java.util.*;
import java.util.stream.Collectors;

public class Factor {
    public List<Variable> scope=new ArrayList<>(); // רשימת המשתנים שהפקטור תלוי בהם
    public List<Double> values= new ArrayList<>();  // הערכים ההסתברותיים לפי סדר מסוים (למשל בינארי)

    public Factor(List<Variable> scope, List<Double> values) {
        this.scope = scope;
        this.values = values;
    }
    public Factor(Factor f) {
        this.scope = f.scope;
        this.values = f.values;
    }
    public Factor(CPT c) {

        this.values = c.getProbabilities();
        List<Double> temp = new ArrayList<>();
        temp.addAll(c.getProbabilities());
        for (Double d : temp) {
            this.values.add(1-d);//אולי ההוספה בסדר הזה לא נכונה
        }
        this.scope.addAll(c.parents);
        this.scope.add(c.variable);

    }
    public List<Double> getvalues() {
        return values;
    }
    public List<Variable> getvarubels() {
        return scope;
    }
    private boolean allfalse(boolean[] arr){
        for(boolean b : arr){
            if(b) return false;
        }
        return true;
    }

    private Double calc_pos(boolean[] arr, Factor f2, List<String> allVarNames){
        Map<String, Integer> assignment = new HashMap<>();

        for (int i = 0; i < arr.length; i++) {
            assignment.put(allVarNames.get(i), arr[i] ? 0 : 1);
        }

        int index1 = getIndexFor(assignment, this);
        int index2 = getIndexFor(assignment, f2);

        return this.getvalues().get(index1) * f2.getvalues().get(index2);
    }
    private int getIndexFor(Map<String, Integer> assignment, Factor f) {
        List<Variable> vars = new ArrayList<>();
        vars=f.getvarubels();
        int index = 0;
        for (int i = 0; i < vars.size(); i++) {
            int val = assignment.get(vars.get(i).getName());
            index = (index << 1) | val;
        }
        return index;
    }
    public Factor unione(Factor f2) {
        // Step 1: איחוד ה-scope לפי שמות המשתנים
        List<Variable> newScope = new ArrayList<>();
        Set<String> names = new HashSet<>();

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

        // Step 2: צור רשימת שמות מסודרת לפי newScope
        List<String> allVarNames = newScope.stream().map(Variable::getName).collect(Collectors.toList());

        // Step 3: צור את כל ההשמות האפשריות
        int totalAssignments = (int) Math.pow(2, allVarNames.size());
        List<Double> newValues = new ArrayList<>();

        for (int i = 0; i < totalAssignments; i++) {
            Map<String, Integer> assignment = new HashMap<>();
            for (int j = 0; j < allVarNames.size(); j++) {
                int bit = (i >> (allVarNames.size() - j - 1)) & 1;
                assignment.put(allVarNames.get(j), bit);
            }

            int idx1 = getIndexFor(assignment, this);
            int idx2 = getIndexFor(assignment, f2);

            double v1 = (idx1 >= 0 && idx1 < this.values.size()) ? this.values.get(idx1) : 1.0;
            double v2 = (idx2 >= 0 && idx2 < f2.values.size()) ? f2.values.get(idx2) : 1.0;

            newValues.add(v1 * v2);
        }
        System.out.println("variable_Elimination:"+"newScope:"+newScope+"/n"+newValues);
        return new Factor(newScope, newValues);
    }
    public void normalize() {
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i) / sum);
        }
    }
//    public List<Double> unione(Factor f2){
//        Set<String> allVars = new HashSet<>();
//        Set<String> seen = new HashSet<>();
//        for(Variable v : this.getvarubels()){
//            allVars.add(v.getName());
//            seen.add(v.getName());
//        }
//        for(Variable v : f2.getvarubels()){
//            allVars.add(v.getName());
//            seen.add(v.getName());
//        }
//
//        int totalvarse = (int) Math.pow(2, allVars.size());
//        List<Double>ans =new ArrayList<>();
//        for (int i = 0; i < totalvarse; i++) {
//            Map<String, Integer> assignment = new HashMap<>();
//            for (int j = 0; j < allVars.size(); j++) {
//                int bit = (i >> (allVars.size() - j - 1)) & 1;
//                assignment.put(allVars.stream().toList().get(j), bit);
//            }
//
//            int index1 = getIndexFor(assignment, this);
//            int index2 = getIndexFor(assignment, f2);
//
//            double v1 = (index1 >= 0 && index1 < this.getvalues().size()) ? this.getvalues().get(index1) : 1.0;
//            double v2 = (index2 >= 0 && index2 < f2.getvalues().size()) ? f2.getvalues().get(index2) : 1.0;
//
//            ans.add(v1 * v2);
//        }
//
//        return ans;
//    }
    public Factor variable_Elimination(List<String> varName) {
        // שלב 1: יצירת scope חדש רק עם המשתנים שנשמרים
        List<Variable> scope = new ArrayList<>();
        for (Variable v : this.getvarubels()) {
            if (varName.contains(v.getName())) {
                scope.add(v);
            }
        }

        // שלב 2: שמות המשתנים שנשמרים
        List<String> allVars = this.getvarubels().stream()
                .map(Variable::getName)
                .collect(Collectors.toList());

        List<String> keepNames = new ArrayList<>();
        for (String name : allVars) {
            if (varName.contains(name)) {
                keepNames.add(name);
            }
        }

        // שלב 3: מציאת המשתנים שמעלימים
        List<String> eliminateNames = new ArrayList<>();
        for (String name : allVars) {
            if (!keepNames.contains(name)) {
                eliminateNames.add(name);
            }
        }
        double sumall = 0.0;

        // שלב 4: חישוב הסתברויות חדשות ע"י סכימה על כל המשתנים שלא בשמירה
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
            sumall+=sum;
        }
        List<Double> tempans = new ArrayList<>();

        for (Double d : ans) {
            tempans.add(d=d/sumall);
        }

        // שלב 5: יצירת הפקטור החדש
        Factor fnew = new Factor(scope, tempans);
        System.out.println("variable_Elimination:"+"newScope:"+scope+"/n"+tempans);

        return fnew;
    }

















    public Factor restrict(String varName, int value) {
        // משתנים שנשארים אחרי הסרת varName
        List<Variable> newScope = new ArrayList<>();
        for (Variable v : this.scope) {
            if (!v.getName().equals(varName)) {
                newScope.add(v);
            }
        }

        // שמות משתנים לפני ואחרי הסינון
        List<String> allVarNames = this.scope.stream().map(Variable::getName).collect(Collectors.toList());
        List<String> newVarNames = newScope.stream().map(Variable::getName).collect(Collectors.toList());

        int totalAssignments = (int) Math.pow(2, newVarNames.size());
        List<Double> newValues = new ArrayList<>();

        for (int i = 0; i < totalAssignments; i++) {
            Map<String, Integer> assignment = new HashMap<>();
            for (int j = 0; j < newVarNames.size(); j++) {
                int bit = (i >> (newVarNames.size() - j - 1)) & 1;
                assignment.put(newVarNames.get(j), bit);
            }

            // מוסיפים את הערך הקבוע של varName
            assignment.put(varName, value);

            int index = getIndexFor(assignment, this);
            newValues.add(this.values.get(index));
        }

        return new Factor(newScope, newValues);
    }


}
