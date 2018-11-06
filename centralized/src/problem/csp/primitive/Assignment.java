package problem.csp.primitive;

import java.util.ArrayList;
import java.util.List;

import centralized.PDPVariable;
import centralized.value.TaskValue.ValueType;

/**
 * The Assignment interface represent a concrete realization of the variables' set X
 * 
 * @author Amaury Combes
 * @author Theo Nikles
 *
 */
public class Assignment<B extends Variable<V>, V extends Value> {
	private final List<List<B.RealizedVariable>> realizations;
	
	public Assignment(Assignment<B, V> assignment) {
		this(assignment.getRealizations());
	}
	
	public Assignment(List<List<B.RealizedVariable>> realizations) {
		this.realizations = new ArrayList<>(realizations.size());
		for(List<B.RealizedVariable> r : realizations) {
			this.realizations.add(new ArrayList<>(r));
		}
	}
	
	public int size() {
		return realizations.size();
	}
	
	public int getTotalSize() {
		int sizeStack = 0;
		
		for (List<B.RealizedVariable> list: realizations) {
			sizeStack += list.size();
		}
		
		return sizeStack;
	}
	
	public List<List<B.RealizedVariable>> getRealizations() {
		return realizations;
	}
	
	public B.RealizedVariable get(int x, int y) {
		return realizations.get(y).get(x);
	}
	
	public B.RealizedVariable get(int i) {
		if(realizations.size() == 0 || realizations.get(0).size() == 0) {
			throw new IllegalStateException("Tried to access element from a list that is empty");
		}
		int x = i % realizations.get(0).size();
		int y = i / realizations.get(0).size();
		return get(x, y);
	}
	
	public List<B.RealizedVariable> getPlan(int vehicleIndex) {
		return realizations.get(vehicleIndex);
	}
	
	@Override
	public String toString() {
		String stringRepresentation = "";
		
		for (int i=0; i<realizations.size(); ++i) {
			stringRepresentation += "Vehicule " + i + "\n";
			
			for (Variable<V>.RealizedVariable realization: realizations.get(i)) {
				PDPVariable.RealizedVariable pdpRealization = (PDPVariable.RealizedVariable) realization;
				if (pdpRealization.getValue().getType() == ValueType.NONE) {
					stringRepresentation += pdpRealization.getValue().getType().name() + " ";
				} else {
					stringRepresentation += pdpRealization.getValue().getType().name() + " " + pdpRealization.getValue().getTask() + " -> ";
				}
				
			}
			
			stringRepresentation += "\n";
		}
		
		stringRepresentation += "-------------\n";
		
		return stringRepresentation;
	}
}
