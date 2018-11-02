package problem.csp.primitive;

import java.util.Set;

public interface Domain {
	
	public boolean contains(Value value);
	
	public Set<Value> getDomain();
	
	public int getSize();

}
