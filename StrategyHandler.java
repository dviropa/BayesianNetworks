public class StrategyHandler {
    private baceStrategy strategy;

    public void setStrategyByNumber(String question, String fileName,int number) {
        switch (number) {
            case 1 -> strategy = new Simple(question,fileName);
            case 2 -> strategy = new Algorithm2();
            case 3 -> strategy = new Algorithm3();
            default -> strategy = new JointProbability(question,fileName);
        }
    }

    public double run() {
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set");
        }
        return strategy.calc();
    }
}
