import java.io.*;
import java.util.*;

public class Ex1 {
    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        InferenceFileParser.ParsedFile parsed = InferenceFileParser.parseInferenceTasksFromFile(inputFile);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("📂 File: " + parsed.fileName + "\n");

            for (InferenceFileParser.InferenceTask task : parsed.tasks) {
                StrategyHandler handler = new StrategyHandler(task.question, parsed.fileName);
                handler.setStrategyByNumber(task.algorithmNumber);
                List<Double> result = handler.calc();
                if(result != null)

                    System.out.println("📌 " + task.question + " using Algorithm " + task.algorithmNumber + " → Result: " + result.get(0)+","+result.get(1)+","+result.get(2) + "\n");
                if(result != null)
                    writer.write("📌 " + task.question + " using Algorithm " + task.algorithmNumber + " → Result: " + result.get(0)+","+result.get(1)+","+result.get(2) + "\n");
            }

            System.out.println("✅ Results written to: " + outputFile);
        }
    }

    public static class InferenceFileParser {

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

                    int lastComma = line.lastIndexOf(',');
                    if (lastComma != -1) {
                        String possibleNumber = line.substring(lastComma + 1).trim();
                        try {
                            algorithm = Integer.parseInt(possibleNumber);
                            line = line.substring(0, lastComma); // חתוך את המספר מהשאלה
                        } catch (NumberFormatException ignored) {
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
