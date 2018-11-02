package problem.csp.primitive;

public interface Constraint<V extends Value> {
	
	public int getInputSize();
	
	public boolean valueAt(Assignment<V> point);

}
