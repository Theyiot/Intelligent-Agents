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
		
		public static Movement getRandomLegalMove() {
			int choice = (int) (Math.random() * 4);
			
			return Movement.values()[choice];
		}
		
		public static Movement getRandomMoveWithout(Set<Movement> bannedMoves) {
			if(bannedMoves.contains(DOWN) && bannedMoves.contains(UP) && bannedMoves.contains(LEFT) && bannedMoves.contains(RIGHT)) {
				return STAY;
			} else {
				Movement choice = getRandomLegalMove();
				
				while(bannedMoves.contains(choice)) {
					choice = getRandomLegalMove();
				}
				
				return choice;
			}
			
		}

}
