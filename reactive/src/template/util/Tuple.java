package template.util;

public class Tuple<X, Y> {
	private final X x;
	private final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public X getLeft() {
		return x;
	}
	
	public Y getRight() {
		return y;
	}
	
	@Override
	public boolean equals(Object other) {
		try {
			@SuppressWarnings("unchecked")
			Tuple<X, Y> cOther = (Tuple<X, Y>) other;
			return cOther.getLeft().equals(getLeft()) && cOther.getRight().equals(getRight());
		} catch (Exception e) {
			return false;
		}
	}
}
