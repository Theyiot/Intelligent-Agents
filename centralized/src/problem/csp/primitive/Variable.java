package problem.csp.primitive;

public abstract class Variable<V extends Value> {
	private final Domain<V> domain;
	
	public Variable(Domain<V> domain) {
		this.domain = domain;
	}
	
	public boolean contains(V value) {
		return domain.contains(value);
	}
	
	public int domainSize() {
		return domain.getSize();
	}
	
	public Domain<V> getDomain() {
		return domain;
	}
	
	public boolean isRealization(RealizedVariable realizedVariable) {
		return realizedVariable.getParent().equals(this);
	}
	
	public RealizedVariable realize(V value) {
		return this.new RealizedVariable(value);
	}
	
	public class RealizedVariable {
		private final V value;
		
		public RealizedVariable(V value) {
			if (!Variable.this.contains(value)) {
				throw new IllegalArgumentException("Tried to realized a variable with an illegal value");
			}
			this.value = value;
		}
		
		public V getValue() {
			return value;
		}
		
		public Variable<V> getParent() {
			return Variable.this;
		}
	}
}
