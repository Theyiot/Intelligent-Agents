package template;

import java.lang.Iterable;

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
}
