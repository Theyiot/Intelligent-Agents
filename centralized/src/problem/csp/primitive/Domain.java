package problem.csp.primitive;

import java.util.Set;

public class Domain<V extends Value> {
	private final Set<V> domain;
	
	public Domain(Set<V> values) {
		this.domain = values;
	}
	
	public boolean contains(V value) {
		return domain.contains(value);
	}
	
	public Set<V> getDomain() {
		return domain;
	}
	
	public int getSize() {
		return domain.size();
	}

}
