import java.util.*;
import java.util.stream.Collectors;

public class VariableElimination implements baceStrategy {
    private String question;
    private String fileName;
    public static double multCount = 0;
    public static double addCount = 0;

    public VariableElimination(String question, String fileName) {
        this.question = question;
        this.fileName = fileName;
    }

    @Override
    public List<Double> calc() {
        Map<String, Variable> variableMap = baceStrategy.getVariable(fileName);
        Map<String, List<String>> q = baceStrategy.questionsToMap(question);
        String queryVar = q.keySet().iterator().next();
        List<String> evidenceVars = q.get(queryVar);


        List<Factor> factors = new ArrayList<>();
        for (Variable v : variableMap.values()) {
            factors.add(new Factor(v.getCPT(),fileName));
        }
        Factor finall = null;
        while (factors.size() > 0) {
            Factor smaller=factors.get(0);
            int l=0;
            int c=0;
            while (factors.size() > l) {
                int t=compareFactorsAlphabetically(smaller, factors.get(l));
                if(!(t < 0)){
                    smaller=factors.get(l);
                    c=l;
                }
                l++;
            }
            factors.remove(c);
            if(finall==null){
                finall=smaller;
            }
            else {
                finall=finall.unione(smaller);
                multCount+=finall.multCount;
                addCount+=finall.addCount;



            }
        }
        evidenceVars.add(queryVar);
        Factor top=finall.variable_Elimination(evidenceVars);
        multCount+=finall.multCount;
        addCount+=finall.addCount;
        top.normalize();
        List<String> evidenceOnly = new ArrayList<>(evidenceVars);
        evidenceOnly.remove(queryVar);
        Factor buton=finall.variable_Elimination(evidenceOnly);
        multCount+=finall.multCount;
        addCount+=finall.addCount;
        if(buton.getvarubels().size()>1){
            buton.normalize();
        }
//        System.out.println(top.getvalues());
//        System.out.println(buton.getvalues());
        Map<String, String> topmap =queryToMap(question,fileName);
        Map<String, String> butonmap =queryToMap(question,fileName);
        butonmap.remove(queryVar);
        List<Double> list = new ArrayList<>();
        list.add(Math.round((top.getProbability(topmap) / buton.getProbability(butonmap)) * 100000.0) / 100000.0);
        list.add(multCount);
        list.add(addCount);

//        return Math.round((top.getProbability(topmap) / buton.getProbability(butonmap)) * 100000.0) / 100000.0;
return list;
    }
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
        VariableElimination s = new VariableElimination("P(B=T|J=T,M=T)", "alarm_net.xml");

        System.out.println("Result: " + s.calc());
        System.out.println(s.multCount);
        System.out.println(s.addCount);
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






