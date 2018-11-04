package centralized;

import java.util.HashSet;
import java.util.Set;

import centralized.value.TaskValue;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;

public class PDPConstraintFactory {
	private final int inputSize;
	private final int vehicleCount;
	private final int inputPerVehicle;
	
	public PDPConstraintFactory(int inputSize, int vehicleCount) {
		this.inputSize = inputSize;
		this.vehicleCount = vehicleCount;
		this.inputPerVehicle = (int) (((double) inputSize) / ((double) vehicleCount));
	}
	
	public Set<Constraint<TaskValue>> getAllConstraints() {
		Set<Constraint<TaskValue>> constraints = new HashSet<>();
		
		constraints.add(constraint1());
		// TODO Add all other constraints
		
		return constraints;
	}
	
	private Constraint<TaskValue> constraint1() {
		Constraint<TaskValue> constraint = new Constraint<TaskValue>() {
			
			@Override 
			public int getInputSize() {
				return inputSize;
			}
			
			@Override
			public boolean valueAt(Assignment<TaskValue> point) {
				// Point contains a list of values. First inputPerVehicle correspond to plan for vehicle 1, next inputPerVehicle correspond to plan for vehicle 2... etc
				return true;
			}
			
		};
		
		return constraint;
	}
	
	// TODO Create other constraints similarly to constraint1(). Do not forget to add them to getAllConstraints() 

}
