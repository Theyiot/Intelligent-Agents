package problem.csp.primitive;

import java.util.ArrayList;
import java.util.List;

/**
 * The Assignment interface represent a concrete realization of the variables' set X
 * 
 * @author Amaury Combes
 *
 */
public class Assignment<V extends Value> {

	private final List<Variable<V>.RealizedVariable> realizations;
	
	public Assignment(List<Variable<V>.RealizedVariable> realizations) {
		this.realizations = new ArrayList<>(realizations);
	}
	
	public int size() {
		return realizations.size();
	}
	
	public List<Variable<V>.RealizedVariable> getRealizations() {
		return realizations;
	}
	
	public Variable<V>.RealizedVariable get(int i) {
		return realizations.get(i);
	}

}
