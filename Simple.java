import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simple implements bace{
    private String question ;
    private String fileName ;

    public Simple(String l,String fileName) {
        this.question = l;
        this.fileName = fileName;
    }



    @Override
    public Double calc() {
        if(this.question.contains("|")){
            System.out.println(bace.conditionalProbability("P(B=F,E=T,A=T,M=T,J=F)","alarm_net.xml"));
            return bace.conditionalProbability(question,fileName);
        }else {
            return bace.jointProbability(question,fileName);
        }
    }

    public static void main(String[] args) {
        Simple s = new Simple("P(B=T|J=T,M=T)", "alarm_net.xml");
        System.out.println(        s.calc()
);
    }

}
