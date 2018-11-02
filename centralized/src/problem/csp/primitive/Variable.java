package problem.csp.primitive;

public abstract class Variable {
	private final Domain domain;
	
	public Variable(Domain domain) {
		this.domain = domain;
	}
	
	public boolean contains(Value value) {
		return domain.contains(value);
	}
	
	public int domainSize() {
		return domain.getSize();
	}
	
	public boolean isRealization(RealizedVariable realizedVariable) {
		return realizedVariable.getParent().equals(this);
	}
	
	public class RealizedVariable {
		private final Value value;
		private final Variable parentVariable;
		
		public RealizedVariable(Value value) {
			this.value = value;
			parentVariable = Variable.this;
		}
		
		public Value getValue() {
			return value;
		}
		
		public Variable getParent() {
			return parentVariable;
		}
	}
}
