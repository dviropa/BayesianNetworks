import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyHandler {
    private baceStrategy strategy;
    private String question;
    private String fileName;

    public StrategyHandler(String question, String fileName) {
        this.question = question;
        this.fileName = fileName;
    }

    public void setStrategyByNumber(int number) {
        Map<String, Double> calcMap =new HashMap<>();
        switch (number) {
            case 1 -> strategy = new Simple(question, fileName,calcMap);
            case 2 -> strategy = new VariableElimination(question, fileName);
            case 3 -> strategy = new Algorithm3(question, fileName);
            default -> strategy = new JointProbability(question, fileName,calcMap);
        }
    }

    public List<Double>calc() {
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set. Call setStrategyByNumber() first.");
        }
        return strategy.calc();
    }
}
