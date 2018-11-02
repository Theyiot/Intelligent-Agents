package problem.csp.primitive;

import java.util.Set;

public class Domain {
	private final Set<Value> domain;
	
	public Domain(Set<Value> values) {
		this.domain = values;
	}
	
	public boolean contains(Value value) {
		return domain.contains(value);
	}
	
	public Set<Value> getDomain() {
		return domain;
	}
	
	public int getSize() {
		return domain.size();
	}

}
