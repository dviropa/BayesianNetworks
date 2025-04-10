public class Main {
    public static void main(String[] args) {
        String question = "P(B=T|J=T,M=T)";
        String fileName = "alarm_net.xml";
        int methodNumber = 2; // 1 = Simple, 2 = VariableElimination, 3 = Algorithm3

        StrategyHandler handler = new StrategyHandler(question, fileName);
        handler.setStrategyByNumber(methodNumber); // קובע איזו אסטרטגיה לרוץ

        double result = handler.calc(); // מפעיל את האלגוריתם הרצוי
        System.out.println("Result: " + result);
    }
}
