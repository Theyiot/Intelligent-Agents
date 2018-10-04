package template;

public enum ActionType {
	MOVE, DELIVER;
	
	@Override
	public String toString() {
		if(this == MOVE) return ", moving";
		else return ", delivering";
	}
}
