import java.io.*;
import java.util.*;
public class Main {
    public static void main(String[] args) throws IOException {
//        String inputfile = "inputf.txt";
////        String question = "P(B=T|J=T,M=T)";
////        String fileName = "alarm_net.xml";
//        int methodNumber = 2; // 1 = Simple, 2 = VariableElimination, 3 = Algorithm3
//
//        StrategyHandler handler = new StrategyHandler(question, fileName);
//        handler.setStrategyByNumber(methodNumber); // קובע איזו אסטרטגיה לרוץ
//
//        double result = handler.calc(); // מפעיל את האלגוריתם הרצוי
//        System.out.println("Result: " + result);
//        InferenceFileParser.ParsedFile parsed = InferenceFileParser.parseInferenceTasksFromFile("path/to/your/input.txt");
//
//        System.out.println("File name: " + parsed.fileName);
//        for (InferenceFileParser.InferenceTask task : parsed.tasks) {
//            System.out.println(task);
//        }

        String inputFile = "input.txt";

        InferenceFileParser.ParsedFile parsed = InferenceFileParser.parseInferenceTasksFromFile(inputFile);
        System.out.println("📂 File: " + parsed.fileName);

        for (InferenceFileParser.InferenceTask task : parsed.tasks) {
            StrategyHandler handler = new StrategyHandler(task.question, parsed.fileName);
            handler.setStrategyByNumber(task.algorithmNumber);
            double result = handler.calc();
            System.out.println("📌 " + task.question + " using Algorithm " + task.algorithmNumber + " → Result: " + result);
        }
    }
    public class InferenceFileParser {

        public static class InferenceTask {
            public String question;
            public int algorithmNumber;

            public InferenceTask(String question, int algorithmNumber) {
                this.question = question;
                this.algorithmNumber = algorithmNumber;
            }

            @Override
            public String toString() {
                return "Question: " + question + ", Algorithm: " + algorithmNumber;
            }
        }

        public static class ParsedFile {
            public String fileName;
            public List<InferenceTask> tasks = new ArrayList<>();
        }

        public static ParsedFile parseInferenceTasksFromFile(String path) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            ParsedFile parsed = new ParsedFile();

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (isFirstLine) {
                    parsed.fileName = line;
                    isFirstLine = false;
                } else {
                    int algorithm = 4; // ברירת מחדל

                    // בדיקה חכמה: אם יש בדיוק פסיק אחד (שחורץ בין שאלה למספר)
                    int lastComma = line.lastIndexOf(',');
                    if (lastComma != -1) {
                        String possibleNumber = line.substring(lastComma + 1).trim();
                        try {
                            algorithm = Integer.parseInt(possibleNumber);
                            line = line.substring(0, lastComma); // חתוך את המספר מהשאלה
                        } catch (NumberFormatException ignored) {
                            // אם זה לא מספר - נשאיר algorithm = 4
                        }
                    }

                    parsed.tasks.add(new InferenceTask(line, algorithm));
                }
            }

            reader.close();
            return parsed;
        }

    }

}


