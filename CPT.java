import java.util.ArrayList;
import java.util.List;

public class CPT {
    Variable variable;
    List<Variable> parents = new ArrayList<>();
    List<Double> probabilities = new ArrayList<>();

    public CPT(Variable variable) {
        this.variable = variable;
    }
    public double getProb(String value, List<String> parentValues) {
        List<String> full = new ArrayList<>(parentValues);
        full.add(value);
        int index = baceStrategy.getCPTIndex(this.variable, full);
        return this.probabilities.get(index);
    }

    public void addParent(Variable parent) {
        parents.add(parent);
    }
    public List<Variable> getParents(Variable parent) {
        return this.parents;
    }
    public void setProbabilities(List<Double> probs) {
        this.probabilities = probs;
    }
    public List<Double> getProbabilities( ) {
        return this.probabilities;
    }

    @Override
    public String toString() {
        return "CPT{for=" + variable.getName() +
                ", parents=" + parents.stream().map(p -> p.getName()).toList() +
                ", probabilities=" + probabilities + "}";
    }
}
