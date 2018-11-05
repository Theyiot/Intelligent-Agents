package problem.csp.primitive;

public interface Constraint<B extends Variable<V>, V extends Value> {
	
	public int getInputSize();
	
	public boolean valueAt(Assignment<B, V> point);

}
