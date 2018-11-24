package problem.csp.primitive;

public interface ObjectiveFunction<B extends Variable<V>, V extends Value> {
	
	public double valueAt(Assignment<B, V> point);

}
