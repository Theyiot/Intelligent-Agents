package template;

public enum ActionType {
	MOVE, DELIVER;
	
	@Override
	public String toString() {
		switch (this) {
		case MOVE:
			return ", moving";
		case DELIVER:
			return ", delivering";
		default:
			throw new IllegalStateException("Illegal Action Type");
		}
	}
}
