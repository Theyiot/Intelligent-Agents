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
	
	public class RealizedVariable {
		private final V value;
		private final Variable<V> parentVariable;
		
		public RealizedVariable(V value) {
			this.value = value;
			parentVariable = Variable.this;
		}
		
		public Value getValue() {
			return value;
		}
		
		public Variable<V> getParent() {
			return parentVariable;
		}
	}
}
