package problem.csp.primitive;

import java.util.ArrayList;
import java.util.List;

/**
 * The Assignment interface represent a concrete realization of the variables' set X
 * 
 * @author Amaury Combes
 *
 */
public class Assignment<B extends Variable<V>, V extends Value> {

	private final List<List<B.RealizedVariable>> realizations;
	private final List<Integer> capacities;
	
	public Assignment(List<List<B.RealizedVariable>> realizations, List<Integer> capacities) {
		this.realizations = new ArrayList<>(realizations.size());
		for(List<B.RealizedVariable> r : realizations) {
			this.realizations.add(new ArrayList<>(r));
		}
		this.capacities = capacities;
	}
	
	public int size() {
		return realizations.size();
	}
	
	public List<List<B.RealizedVariable>> getRealizations() {
		return realizations;
	}
	
	public int getCapacityForVehicle(int i) {
		return capacities.get(i);
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
}
