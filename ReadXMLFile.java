import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class ReadXMLFile {

    public static Map<String, Variable> reade(String fileName) throws ParserConfigurationException, SAXException, IOException {
        if (fileName == null) {
            fileName = "alarm_net.xml";
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(fileName);
        document.getDocumentElement().normalize();

        Map<String, Variable> variableMap = new HashMap<>();

        // קריאת <VARIABLE>
        NodeList variableNodes = document.getElementsByTagName("VARIABLE");
        for (int i = 0; i < variableNodes.getLength(); i++) {
            Element varElem = (Element) variableNodes.item(i);
            String varName = varElem.getElementsByTagName("NAME").item(0).getTextContent().trim();
            Variable variable = new Variable(varName);

            NodeList outcomes = varElem.getElementsByTagName("OUTCOME");
            for (int j = 0; j < outcomes.getLength(); j++) {
                String outcomeValue = outcomes.item(j).getTextContent().trim();
                variable.addOutcome(outcomeValue);        // לשימוש פנימי
                variable.addToOutcomes(outcomeValue);     // לשמירה ישירה מה-XML
            }

            variableMap.put(varName, variable);
        }

        // קריאת <DEFINITION>
        NodeList definitions = document.getElementsByTagName("DEFINITION");
        for (int i = 0; i < definitions.getLength(); i++) {
            Element defElem = (Element) definitions.item(i);

            String forName = defElem.getElementsByTagName("FOR").item(0).getTextContent().trim();
            Variable forVar = variableMap.get(forName);
            CPT cpt = new CPT(forVar);

            NodeList givenNodes = defElem.getElementsByTagName("GIVEN");
            for (int j = 0; j < givenNodes.getLength(); j++) {
                String parentName = givenNodes.item(j).getTextContent().trim();
                Variable parent = variableMap.get(parentName);

                if (parent != null) {
                    cpt.addParent(parent);
                    forVar.addParent(parent);
                }
            }

            String tableText = defElem.getElementsByTagName("TABLE").item(0).getTextContent().trim();
            String[] tokens = tableText.split("\\s+");
            List<Double> probabilities = new ArrayList<>();
            for (String token : tokens) {
                probabilities.add(Double.parseDouble(token));
            }
            cpt.setProbabilities(probabilities);
            forVar.setCPT(cpt);
        }

        return variableMap;
    }
}
