import java.util.ArrayList;
import java.util.List;

public class Variable {
    private String name;
    private List<String> values = new ArrayList<>();  // חובה לאתחל!
    private List<Variable> parents = new ArrayList<>();
    private CPT cpt;  // טבלת הסתברויות מותנות

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getValues() {
        return this.values;
    }

    public CPT getCPT() {
        return this.cpt;
    }

    public void setCPT(CPT cpt) {
        this.cpt = cpt;
    }

    public void addOutcome(String value) {
        values.add(value);
    }

    public void addParent(Variable parent) {
        parents.add(parent);
    }
    public List<Variable> getParents() {
        return this.parents;
    }

    @Override
    public String toString() {
        return "Variable{name='" + name + "', values=" + values + "', Parents=" + this.getParents() +"}";
    }
}
