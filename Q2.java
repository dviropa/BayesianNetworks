import java.util.*;

public class Q2 implements bace {
    private String question;
    private String fileName;

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
        Map<String, Variable> variableMap = bace.getVariable(fileName);
        Map<String, List<String>> q = bace.questionsToMap(question);
        String queryVar = q.keySet().iterator().next();
        List<String> evidenceVars = q.get(queryVar);

        List<Factor> factors = new ArrayList<>();
        Set<String> added = new HashSet<>();
        perents(added, factors, variableMap);
        // אני רוצה שהפונקציה שמעל לא תביא את כולם אלה רק את ההורים של האבינס כלומר מתחילים מזה ולאט לאט שוב מחברים הורים
        System.out.println("[Q2] Initial number of factors: " + factors.size());

        Map<String, Integer> evidenceMap = new HashMap<>();
        for (String ev : evidenceVars) {
            if (question.contains(ev + "=T")) {
                evidenceMap.put(ev, 1);
            } else {
                evidenceMap.put(ev, 0);
            }
        }

        System.out.println("[Q2] Evidence map: " + evidenceMap);

        for (int i = 0; i < factors.size(); i++) {
            for (Map.Entry<String, Integer> entry : evidenceMap.entrySet()) {
                if (factors.get(i).getvarubels().stream().anyMatch(v -> v.getName().equals(entry.getKey()))) {
                    factors.set(i, factors.get(i).restrict(entry.getKey(), entry.getValue()));
                }
            }
        }

        System.out.println("[Q2] After restrict - number of factors: " + factors.size());

        while (factors.size() > 1) {
            Factor f1 = factors.remove(0);
            Factor f2 = factors.remove(0);

            Factor newFactor = f1.unione(f2);
            factors.add(newFactor);

            System.out.println("[Q2] Union completed. Remaining factors: " + factors.size());
        }

        Factor joined = factors.get(0);
        List<String> varsToKeep = new ArrayList<>(evidenceVars);
        varsToKeep.add(queryVar);
        Factor reduced = joined.variable_Elimination(varsToKeep);
        reduced.normalize();

        int index = 0;
        if (question.contains(queryVar + "=T")) {
            index = 1;
        }

        System.out.println("[Q2] Final values after normalization: " + reduced.getvalues());
        return reduced.getvalues().get(index);
    }

    public static void main(String[] args) {

        Map<String, Variable> variableMap = bace.getVariable("alarm_net.xml");

        Factor fM = new Factor(variableMap.get("M").getCPT());
        Factor fJ = new Factor(variableMap.get("J").getCPT());
        Factor fA = new Factor(variableMap.get("A").getCPT());
        Factor fE = new Factor(variableMap.get("E").getCPT());
        Factor fB = new Factor(variableMap.get("B").getCPT());

        Factor fMJ = fM.unione(fJ);
        Factor fMJ_A = fMJ.unione(fA);
        Factor fAll = fMJ_A.unione(fE).unione(fB);

        List<String> keep = List.of("J", "M", "B");
        Factor result = fAll.variable_Elimination(keep);
        System.out.println(result);
        result.normalize();

        System.out.println("________________________________");
         keep = List.of("E", "A");
        System.out.println(fA.variable_Elimination(keep));

//        Factor fM= new Factor(variableMap.get("M").getCPT());
//        Factor fJ= new Factor(variableMap.get("J").getCPT());
//        Factor fA= new Factor(variableMap.get("A").getCPT());
//        Factor fE= new Factor(variableMap.get("E").getCPT());
//        Factor fB= new Factor(variableMap.get("B").getCPT());
//
//        Factor fJA= fJ.unione(fA);
//        Factor fJAM= fJA.unione(fM);
//        Factor fJAME= fJAM.unione(fE);
//        Factor fJAMEB= fJAME.unione(fB);
//        List<String> cip = new ArrayList<>();
//        cip.add("J");
//        cip.add("M");
//        cip.add("B");
//
//        System.out.println(fJAMEB.variable_Elimination(cip));
//        Q2 s = new Q2("P(B=T|J=T,M=T)", "alarm_net.xml");
//        System.out.println("Result: " + s.calc());
    }
}
