import java.util.ArrayList;
import java.util.Set;

public enum Movement {
		LEFT(-1, 0), 
		RIGHT(1, 0),
		UP(0, 1),
		DOWN(0, -1),
		STAY(0, 0);
		
		private int x;
		private int y;
		
		private Movement(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public static Movement getRandomMoveWithout(Set<Movement> bannedMoves) {
			if(bannedMoves.contains(DOWN) && bannedMoves.contains(UP) && bannedMoves.contains(LEFT) && bannedMoves.contains(RIGHT)) {
				return STAY;
			} else {
				ArrayList<Movement> legalMoves = new ArrayList<> (4);
				for(Movement movement : Movement.values()) {
					if(movement != Movement.STAY && !bannedMoves.contains(movement)) {
						legalMoves.add(movement);
					}
				}
				
				return legalMoves.get((int)(Math.random() * legalMoves.size()));
			}
			
		}

}
