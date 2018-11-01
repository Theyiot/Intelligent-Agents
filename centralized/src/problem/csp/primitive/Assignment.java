package problem.csp.primitive;

/**
 * The Assignment interface represent a concrete realization of the variables' set X
 * 
 * @author Amaury Combes
 *
 */
public interface Assignment {

	public boolean isSolution();

	public double cost();

}
