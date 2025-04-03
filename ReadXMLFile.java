import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class ReadXMLFile {

    public static Map<String, Variable> reade(String fileName) throws ParserConfigurationException, SAXException, IOException {
        if (fileName==null){
            fileName="alarm_net.xml";
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(fileName);
        document.getDocumentElement().normalize();

        // משתנים לפי שם
        Map<String, Variable> variableMap = new HashMap<>();

        // קריאת <VARIABLE>
        NodeList variableNodes = document.getElementsByTagName("VARIABLE");
        for (int i = 0; i < variableNodes.getLength(); i++) {
            Element varElem = (Element) variableNodes.item(i);
            String varName = varElem.getElementsByTagName("NAME").item(0).getTextContent().trim();
            Variable variable = new Variable(varName);

            NodeList outcomes = varElem.getElementsByTagName("OUTCOME");
            for (int j = 0; j < outcomes.getLength(); j++) {
                variable.addOutcome(outcomes.item(j).getTextContent().trim());
            }

            variableMap.put(varName, variable);
        }

        // קריאת כל ההגדרות
        NodeList definitions = document.getElementsByTagName("DEFINITION");

        for (int i = 0; i < definitions.getLength(); i++) {
            Element defElem = (Element) definitions.item(i);

            // משתנה ראשי של ההגדרה (FOR)
            String forName = defElem.getElementsByTagName("FOR").item(0).getTextContent().trim();
            Variable forVar = variableMap.get(forName);
            CPT cpt = new CPT(forVar);

            // קריאת ההורים (GIVEN)
            NodeList givenNodes = defElem.getElementsByTagName("GIVEN");
            for (int j = 0; j < givenNodes.getLength(); j++) {
                String parentName = givenNodes.item(j).getTextContent().trim();
                Variable parent = variableMap.get(parentName);

                if (parent != null) {
                    cpt.addParent(parent);
                    forVar.addParent(parent);  // שומר גם במשתנה עצמו
                }
            }

            // קריאת הטבלה (TABLE)
            String tableText = defElem.getElementsByTagName("TABLE").item(0).getTextContent().trim();
            String[] tokens = tableText.split("\\s+");
            List<Double> probabilities = new ArrayList<>();
            for (String token : tokens) {
                probabilities.add(Double.parseDouble(token));
            }
            cpt.setProbabilities(probabilities);

            // קישור ה־CPT למשתנה
            forVar.setCPT(cpt);
        }

//        // ========= הדפסה מסכמת =========
//        System.out.println("========== Variables and CPTs ==========");
//        for (Variable var : variableMap.values()) {
//            System.out.println("Variable: " + var.getName());
//            System.out.println("Outcomes: " + var.getValues());
//            System.out.print("Parents: ");
//            for (Variable p : var.getParents()) {
//                System.out.print(p.getName() + " ");
//            }
//            System.out.println();
//            CPT cpt = var.getCPT();
//            if (cpt != null) {
//                System.out.println("CPT Probabilities: " + cpt.getProbabilities());
//            } else {
//                System.out.println("No CPT found.");
//            }
//            System.out.println("--------------------------------------");
//        }
        return variableMap;
    }
}
