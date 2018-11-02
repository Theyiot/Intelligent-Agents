package problem.csp.primitive;

import java.util.List;

import problem.csp.primitive.Variable.RealizedVariable;

/**
 * The Assignment interface represent a concrete realization of the variables' set X
 * 
 * @author Amaury Combes
 *
 */
public interface Assignment {

	public boolean isSolution();

	public double cost();
	
	public List<RealizedVariable> getRealizations();

}
