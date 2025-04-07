import java.util.*;

public class Q2 implements bace {
    private String question ;
    private String fileName ;

    public Q2(String question, String fileName) {
        this.question = question;
        this.fileName = fileName;
    }
    private void perents(Set<String> added, List<Factor> factors, Map<String, Variable> variableMap) {
        for (Variable v : variableMap.values()) {
            if (!added.contains(v.getName())) {
                factors.add(new Factor(v.getCPT()));
                added.add(v.getName());
            }
        }
    }

    @Override
    public Double calc() {
        // שלב 1: טען משתנים מהרשת
        Map<String, Variable> variableMap = bace.getVariable(fileName);

        // שלב 2: נתח את השאלה לשאילתה וראיות
        Map<String, List<String>> q = bace.questionsToMap(question);
        String queryVar = q.keySet().iterator().next();
        List<String> evidenceVars = q.get(queryVar);

        // שלב 3: בנה את כל הפקטורים המקוריים בלי כפילויות
        List<Factor> factors = new ArrayList<>();
        Set<String> added = new HashSet<>();
        perents(added, factors, variableMap); // שימוש בפונקציה שלך

        // שלב 4: צמצם לפי ראיות (אם יש)
        Map<String, Integer> evidenceMap = new HashMap<>();
        for (String ev : evidenceVars) {
            if (question.contains(ev + "=T")) {
                evidenceMap.put(ev, 1);
            } else {
                evidenceMap.put(ev, 0);
            }
        }

        // הפעל restrict על כל פקטור לפי הראיות
        for (int i = 0; i < factors.size(); i++) {
            for (Map.Entry<String, Integer> entry : evidenceMap.entrySet()) {
                if (factors.get(i).getvarubels().stream().anyMatch(v -> v.getName().equals(entry.getKey()))) {
                    factors.set(i, factors.get(i).restrict(entry.getKey(), entry.getValue()));
                }
            }
        }

        // שלב 5: בצע איחודים עד שיש פקטור אחד
        while (factors.size() > 1) {
            Factor f1 = factors.remove(0);
            Factor f2 = factors.remove(0);

            Factor newFactor = f1.unione(f2); // מחזיר List<Double>

            List<Variable> newScope = new ArrayList<>();
            for (Variable v : f1.scope)
                if (!newScope.contains(v)) newScope.add(v);
            for (Variable v : f2.scope)
                if (!newScope.contains(v)) newScope.add(v);

            factors.add(newFactor);
        }

        // שלב 6: בצע אלימינציה על כל משתנה שלא ב-query או ב-evidence
        Factor joined = factors.get(0);
        List<String> varsToKeep = new ArrayList<>(evidenceVars);
        varsToKeep.add(queryVar);
        Factor reduced = joined.variable_Elimination(varsToKeep);

        // שלב 7: הפק את התוצאה
        int index = 0;
        if (question.contains(queryVar + "=T")) {
            index = 1;
        }
        return reduced.getvalues().get(index);
    }



    public static void main(String[] args) {
        Q2 s = new Q2("P(B=T|J=T,M=T)", "alarm_net.xml");
        s.calc();
        System.out.println(        s.calc()
        );
    }
}
