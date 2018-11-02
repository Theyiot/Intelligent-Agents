package problem.csp.primitive;

public interface ObjectiveFunction<V extends Value> {
	
	public double valueAt(Assignment<V> point);

}
