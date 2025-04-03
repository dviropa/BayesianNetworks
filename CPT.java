import java.util.ArrayList;
import java.util.List;

public class CPT {
    Variable variable; // המשתנה הראשי (FOR)
    List<Variable> parents = new ArrayList<>(); // GIVENs
    List<Double> probabilities = new ArrayList<>(); // לפי הסדר ב-TABLE

    public CPT(Variable variable) {
        this.variable = variable;
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
