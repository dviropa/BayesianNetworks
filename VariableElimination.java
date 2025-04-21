import java.util.*;
import java.util.stream.Collectors;
public class VariableElimination implements baceStrategy {
    private String question;
    private String fileName;
    public static double multCount = 0;
    public static double addCount = 0;
    public static int  multC = 0;
    public static int addC = 0;

    public VariableElimination(String question, String fileName) {
        this.question = question;
        this.fileName = fileName;
    }
    private String getMinNameAlphabetically(List<Factor> factors, String queryVar) {
        String minName = null;
        for (Factor factor : factors) {
            for (String name : factor.getnams()) {
                if (minName == null || (int)(name.charAt(0)) < (int)(minName.charAt(0))) {
                    if(!queryVar.equals(name)) minName = name;
                }
            }
        }
        return minName;
    }
    private List<Factor> getFactorscontiningminchar(String minName,List<Factor> factors) {
        List<Factor> finale = new ArrayList<>();
        for (Factor factor : factors) {
            if (factor.getnams().contains(minName)) {
                finale.add(factor);
            }
        }
        return finale;
    }
    private List<Factor> sortFactorsByVariableCount(List<Factor> factors) {
        return factors.stream()
                .sorted(Comparator.comparingInt(f -> f.getvarubels().size()))
                .collect(Collectors.toList());
    }
    private Factor eliminateAllVariables(List<Factor> factors,String queryVar) {
        Factor finalFactor=null;
        while (factors.size() > 1) {
            Factor merged=null;
            String charToRemove = getMinNameAlphabetically(factors,queryVar);
            if (charToRemove==null) {
                List<Factor> factorsWithChar = getFactorscontiningminchar(queryVar, factors);
                while (factorsWithChar.size() > 1) {

                    // מיין לפי מספר משתנים
                    List<Factor> sorted = sortFactorsByVariableCount(factorsWithChar);

                    Factor f1 = sorted.get(0);
                    Factor f2 = sorted.get(1);

                    merged = f1.unione(f2);
//                    multCount+=f1.multCount;
                    multC += merged.values.size();
                    factorsWithChar.remove(f1);
                    factorsWithChar.remove(f2);
                    factorsWithChar.add(merged);
                    factors.remove(f1);
                    factors.remove(f2);
                    factors.add(merged);
                }
                return merged;
            }
            List<Factor> factorsWithChar = getFactorscontiningminchar(charToRemove, factors);

            while (factorsWithChar.size() > 1) {
                // מיין לפי מספר משתנים
                List<Factor> sorted = sortFactorsByVariableCount(factorsWithChar);

                Factor f1 = sorted.get(0);
                Factor f2 = sorted.get(1);

                merged = f1.unione(f2);
                multC += merged.values.size();

//                multCount+=f1.multCount;

                factorsWithChar.remove(f1);
                factorsWithChar.remove(f2);
                factorsWithChar.add(merged);
                factors.remove(f1);
                factors.remove(f2);
                factors.add(merged);


                for (String s : merged.getnams()) {
                    List<Factor> temp =getFactorscontiningminchar(s, factors);
                    if(temp.size()==1&&!s.equals(queryVar)) {
                        merged=temp.get(0);
                        factors.remove(merged);
                        factorsWithChar.remove(merged);
                        int addtemp=merged.values.size();
                        finalFactor = merged.variable_Elimination(
                                merged.getnams().stream()
                                        .filter(name -> !name.equals(s))
                                        .collect(Collectors.toList())
                        );
                        addC += addtemp-finalFactor.values.size();
//                        addCount+=merged.addCount;
                        factors.add(finalFactor);
                        factorsWithChar.add(finalFactor);
                    }
                }
            }
            //            factors.remove(merged);
//            merged=factorsWithChar.get(0);
//            for (String s : merged.getnams()) {
//                List<Factor> temp =getFactorscontiningminchar(s, factors);
//                if(temp.size()==1&&!s.equals(queryVar)) {
//                    merged=temp.get(0);
//                    factors.remove(merged);
//                    int addtemp=merged.values.size();
//                    finalFactor = merged.variable_Elimination(factorsWithChar.get(0).getnams().stream().filter(name -> !name.equals(charToRemove)).collect(Collectors.toList()));
//                    addC += addtemp-finalFactor.values.size();
//                    addCount+=merged.addCount;
//                    factors.add(finalFactor);
//                }
//            }
//            factors.remove(merged);
//            int addtemp=merged.values.size();
//             finalFactor = merged.variable_Elimination(factorsWithChar.get(0).getnams().stream().filter(name -> !name.equals(charToRemove)).collect(Collectors.toList()));
//            addC += addtemp-finalFactor.values.size();
//            addCount+=merged.addCount;
//            factors.add(finalFactor);


        }
        return finalFactor;
    }
    @Override
    public List<Double> calc() {
//        multCount = 0;
//        addCount = 0;

        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        Map<String, List<String>> q = baceStrategy.questionsToMap(question);
        String queryVar = q.keySet().iterator().next();
        List<String> evidenceVars = q.get(queryVar);


        Map<String, String> evidence = baceStrategy.extractEvidence(question);
        Map<String, String> queryAssign = baceStrategy.extractQueryAssignment(question);
        Map<String, String> all = new HashMap<>(evidence);
        all.putAll(queryAssign);


        Map<String, String> allAssignments = baceStrategy.extractEvidence(question);
        boolean chek = true;
        List<Factor> factors = new ArrayList<>();

        List<String> factorNames =getAllRelevantVariables(question);

        for (Variable v : variableMap.values()) {
            chek = true;
            Factor t=new Factor(v.getCPT(),fileName).restrict(allAssignments);
            if(!(t.getvalues().size()==1||t.getvalues().size()==0)){
                if(factorNames.contains(v.getName())){
                    factors.add(t);
                }
            }



            Factor t1=new Factor(v.getCPT(),fileName);
            if(t1.nams.size()==baceStrategy.extractEvidence(question).size()+baceStrategy.extractQueryAssignment(question).size()) {

                for (String s : all.keySet()) {
                    if (!t1.nams.contains(s) ) {
                        chek = false;
                        break;
                    }
                }
                if(chek){
                    t1=new Factor(v.getCPT(),fileName).restrict(allAssignments);
                    List<String> keys = new ArrayList<>(all.keySet());

                    int addtemp=t1.values.size();
                    t1=t1.variable_Elimination(keys);//הוספה של שאר משתנה השאלה
                    addC += addtemp-t1.values.size();

//                    multCount+=t1.multCount;
//                    addCount+=t1.addCount;
                    List<Double> list = new ArrayList<>();
                    list.add((double) Math.round((t1.getProbability(baceStrategy.extractQueryAssignment(question))) * 100000.0) / 100000.0);
                    list.add(multC*1.0);
                    list.add(addC*1.0);
                    return list;
                }
            }
        }
        Factor finall =eliminateAllVariables(factors,queryVar);


        int addtemp=finall.values.size();

        finall=finall.variable_Elimination(List.of(queryVar));
        addC += addtemp-finall.values.size();
//        addCount+=finall.addCount;

        finall.normalize();
        addC += finall.values.size()-1;
//        addCount+=finall.addCount;
        List<Double> list = new ArrayList<>();
        list.add((double) Math.round(finall.getProbability(baceStrategy.extractQueryAssignment(question)) * 100000.0) / 100000.0);
        list.add(multC*1.0);
        list.add(addC*1.0);

        return list;
    }



    private List<String> getAllRelevantVariables(String question) {
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        Set<String> relevant = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        // הוסף את משתנה השאילתה ואת משתני העדויות
        queue.add(baceStrategy.getQueryVariable(question));
        queue.addAll(baceStrategy.extractEvidence(question).keySet());

        while (!queue.isEmpty()) {
            String var = queue.poll();
            if (relevant.add(var)) { // מוסיף אם עדיין לא היה
                Variable v = variableMap.get(var);
                if (v != null) {
                    for (Variable parent : v.getParents()) {
                        queue.add(parent.getName());
                    }
                }
            }
        }

        return new ArrayList<>(relevant);
    }


//    @Override
//    public List<Double> calc() {
//        multCount = 0;
//        addCount = 0;
//       int  multC = 0;
//        int addC = 0;
//        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
//        Map<String, List<String>> q = baceStrategy.questionsToMap(question);
//        String queryVar = q.keySet().iterator().next();
//        List<String> evidenceVars = q.get(queryVar);
//
//
//        Map<String, String> evidence = baceStrategy.extractEvidence(question);
//        Map<String, String> queryAssign = baceStrategy.extractQueryAssignment(question);
//        Map<String, String> all = new HashMap<>(evidence);
//        all.putAll(queryAssign);
//
//
//        Map<String, String> allAssignments = baceStrategy.extractEvidence(question);
//        boolean chek = true;
//        List<Factor> factors = new ArrayList<>();
//        for (Variable v : variableMap.values()) {
//            chek = true;
////            factors.add(new Factor(v.getCPT(),fileName));
//            Factor t=new Factor(v.getCPT(),fileName).restrict(allAssignments);
//            factors.add(t);
//            Factor t1=new Factor(v.getCPT(),fileName);
//            if(t1.nams.size()==baceStrategy.extractEvidence(question).size()+baceStrategy.extractQueryAssignment(question).size()) {
//
//                for (String s : all.keySet()) {
//                    if (!t1.nams.contains(s) ) {
//                        chek = false;
//                        break;
//                    }
//                }
//                if(chek){
//                    List<String> keys = new ArrayList<>(all.keySet());
//                    t1=t1.variable_Elimination(keys);//הוספה של שאר משתנה השאלה
//                    multCount+=t1.multCount;
//                    addCount+=t1.addCount;                List<Double> list = new ArrayList<>();
//                    list.add((double) Math.round((t1.getProbability(baceStrategy.extractQueryAssignment(question))) * 100000.0) / 100000.0);
//                    list.add(multCount);
//                    list.add(addCount);
//                    return list;
//                }
//            }
//
////            t.restrict(allAssignments);
////            factors.add(t);
//        }
//
//
//        Factor finall = null;
//        while (factors.size() > 0) {
//            Factor smaller=factors.get(0);
//            int l=0;
//            int c=0;
//            while (factors.size() > l) {
//
//
//                int t=compareFactorsAlphabetically(smaller, factors.get(l));
//                if(!(t < 0)){
//                    smaller=factors.get(l);
//                    c=l;
//                }
//                l++;
//            }
//            factors.remove(c);
//            if(finall==null){
//                finall=smaller;
//            }
//            else {
//                finall=finall.unione(smaller);
//                multC += finall.values.size();
//                multCount+=finall.multCount;
//                addCount+=finall.addCount;
//
//
//
//            }
//        }
//        /// ///////////
//        int addtemp=finall.values.size();
//        Factor f=finall.variable_Elimination(List.of(queryVar));
//        addC += addtemp-f.values.size();
//        multCount+=finall.multCount;
//        addCount+=finall.addCount;
//        f.normalize();
//        addC += f.values.size()-1;
//        List<Double> list = new ArrayList<>();
//        list.add((double) Math.round(f.getProbability(baceStrategy.extractQueryAssignment(question)) * 100000.0) / 100000.0);
//        list.add(multCount);
//        list.add(addCount);
//        /// ///////////
//
////        evidenceVars.add(queryVar);
////        Factor top=finall.variable_Elimination(evidenceVars);
////        multCount+=finall.multCount;
////        addCount+=finall.addCount;
////        top.normalize();
////        List<String> evidenceOnly = new ArrayList<>(evidenceVars);
////        evidenceOnly.remove(queryVar);
////        Factor buton=finall.variable_Elimination(evidenceOnly);
////        multCount+=finall.multCount;
////        addCount+=finall.addCount;
////        if(buton.getvarubels().size()>1){
////            buton.normalize();
////        }
////        Map<String, String> topmap =queryToMap(question,fileName);
////        Map<String, String> butonmap =queryToMap(question,fileName);
////        butonmap.remove(queryVar);
////        List<Double> list = new ArrayList<>();
////        list.add(Math.round((top.getProbability(topmap) / buton.getProbability(butonmap)) * 100000.0) / 100000.0);
////        list.add(multCount);
////        list.add(addCount);
//        System.out.println("addCount: " + addC);
//        System.out.println("multCount: " + multC);
//return list;
//    }
    private int compareFactorsAlphabetically(Factor f1, Factor f2) {
        // קבל את רשימת שמות המשתנים של כל פקטור
        List<String> names1 = f1.getvarubels().stream().map(Variable::getName).sorted().toList();

        List<String> names2 = f2.getvarubels().stream().map(Variable::getName).sorted().toList();
        int size = Math.min(names1.size(), names2.size());
        for (int i = 0; i < size; i++) {
            int cmp = names1.get(i).compareTo(names2.get(i));
            if (cmp != 0) return cmp; // אם מצאנו הבדל, נחזיר אותו
        }
        return Integer.compare(names1.size(), names2.size());
    }

    private static Map<String, String> queryToMap(String query, String fileName) {
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        Map<String, String> map = new HashMap<>();
        String inside = query.substring(2, query.length() - 1); // מוריד את P( )
        for (String part : inside.split("[,|]")) {
            String[] split = part.split("="); // מפרק ל: משתנה וערך
            String varName = split[0].trim();
            String outcome = split[1].trim();
            Variable var = variableMap.get(varName);
            if (var == null) {
                throw new RuntimeException("Variable not found: " + varName);
            }
            List<String> outcomes = var.getOUTCOMES();
            if (!outcomes.contains(outcome)) {
                throw new RuntimeException("Outcome '" + outcome + "' not found in variable " + varName);
            }
            map.put(varName, outcome);
        }
        return map;
    }


    public static void main(String[] args) {
        VariableElimination s = new VariableElimination("P(J=T|B=T)", "alarm_net.xml");

        System.out.println("Result: " + s.calc());
        System.out.println(s.multCount);
        System.out.println(s.addCount);
        VariableElimination s1 = new VariableElimination("P(B0=v1|A1=T)", "big_net.xml");
        System.out.println("Result: " + s1.calc());
        System.out.println("addCount: " + addCount);
        System.out.println("multCount: " + multCount);
    }










//
//    public static void main(String[] args) {
////        Q2 s = new Q2("P(B=T|J=T,M=T)", "alarm_net.xml");
////        System.out.println("Result: " + s.calc());
//        System.out.println("_________________________");
//        String filename="alarm_net.xml";
//        Map<String, Variable> variableMap = baceStrategy.getVariable("alarm_net.xml");
//        Factor f3=new Factor(variableMap.get("A").getCPT(),filename);
//        Factor f2=new Factor(variableMap.get("B").getCPT(),filename);
//        Factor f4=new Factor(variableMap.get("J").getCPT(),filename);
//        Factor f5=new Factor(variableMap.get("M").getCPT(),filename);
//        Factor f1=new Factor(variableMap.get("E").getCPT(),filename);
////        f4=f4.restrict("J",1);
////        f5=f5.restrict("M",1);
//        Factor fA = f3.unione(f4).unione(f5);
//        Factor fAE = fA.unione(f1);
//        Factor fBE = fAE.variable_Elimination(List.of("B", "J","M")); // keep B and E
//        Factor fB = fBE.unione(f2);
//        fB.normalize();
//        System.out.println("____________________________");
//
//        // P(J,T,M=T)
//        Factor fJ = new Factor(variableMap.get("J").getCPT(),filename);//.restrict("J", 1)
//        Factor fM = new Factor(variableMap.get("M").getCPT(),filename);//.restrict("M", 1)
//        fA = new Factor(variableMap.get("A").getCPT(),filename);
//        Factor fb=new Factor(variableMap.get("B").getCPT(),filename);
//        Factor fE = new Factor(variableMap.get("E").getCPT(),filename);
//
//        Factor full = f1.unione(f2).unione(f3).unione(f4).unione(f5);
//        full = full.variable_Elimination(List.of("J", "M")); // נשאיר רק אותם
////        full = full.restrict("J", 1).restrict("M", 1);
//        full.normalize();
//        System.out.println(full.getvalues());
//        System.out.println("P(B=T | J=T, M=T) = " + fB.getvalues().get(0) / full.getvalues().get(0));
//
//        System.out.println("____________________________");
//    }




//public static void main(String[] args) {
////        Q2 s = new Q2("P(B=T|J=T,M=T)", "alarm_net.xml");
////        System.out.println("Result: " + s.calc());
//    System.out.println("_________________________");
//    String filename="alarm_net.xml";
//    Map<String, Variable> variableMap = baceStrategy.getVariable("alarm_net.xml");
//    Factor f3=new Factor(variableMap.get("A").getCPT(),filename).variable_Elimination(List.of("B"));
//    Factor f2=new Factor(variableMap.get("B").getCPT(),filename);
//    Factor f4=new Factor(variableMap.get("J").getCPT(),filename).variable_Elimination(List.of("J"));
//    Factor f5=new Factor(variableMap.get("M").getCPT(),filename).variable_Elimination(List.of("M"));
//    Factor f1=new Factor(variableMap.get("E").getCPT(),filename);
////        f4=f4.restrict("J",1);
////        f5=f5.restrict("M",1);
//    Factor fA = f3.unione(f4).unione(f5);
//    Factor fAE = fA.unione(f1).variable_Elimination(List.of("J","B"));
//    Factor fBE = fAE.variable_Elimination(List.of("B", "J","M")); // keep B and E
//    Factor fB = fBE.unione(f2);
//    fB.normalize();
//    System.out.println("____________________________");
//
//    // P(J,T,M=T)
//    Factor fJ = new Factor(variableMap.get("J").getCPT(),filename).variable_Elimination(List.of("J"));//.restrict("J", 1)
//    Factor fM = new Factor(variableMap.get("M").getCPT(),filename).variable_Elimination(List.of("M"));//.restrict("M", 1)
//    fA = new Factor(variableMap.get("A").getCPT(),filename);
//    Factor fE = new Factor(variableMap.get("E").getCPT(),filename);
//    Factor fb = new Factor(variableMap.get("B").getCPT(),filename);
//    Factor ft=fb.unione(fA).unione(fE);
//fJ=fJ.unione(fM).variable_Elimination(List.of("J","M"));
//fJ.unione(ft).variable_Elimination(List.of("J","M"));
//    Factor full = f1.unione(f2).unione(f3).unione(f4).unione(f5);
//    full = full.variable_Elimination(List.of("J", "M")); // נשאיר רק אותם
////        full = full.restrict("J", 1).restrict("M", 1);
//    full.normalize();
//    System.out.println(full.getvalues());
//    System.out.println("P(B=T | J=T, M=T) = " + fB.getvalues().get(0) / fJ.getvalues().get(0));
//
//    System.out.println("____________________________");
//}


}






