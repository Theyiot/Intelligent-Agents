package problem.csp.primitive;

public interface Constraint {
	
	public int getInputSize();
	
	public boolean valueAt(Assignment point);

}
