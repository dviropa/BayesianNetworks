import java.util.ArrayList;
import java.util.List;

public class Variable {
    private String name;
    private List<String> values = new ArrayList<>();
    private List<String> OUTCOMES = new ArrayList<>();  // חובה לאתחל!

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

    public void addToOutcomes(String value) {
        this.OUTCOMES.add(value);
    }
    public List<String> getOUTCOMES() {
        return this.OUTCOMES;
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
