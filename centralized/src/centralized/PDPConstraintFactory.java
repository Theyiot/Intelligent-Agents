package centralized;

import java.util.HashSet;
import java.util.Set;

import centralized.value.TaskValue;
import centralized.value.TaskValue.ValueType;
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

	public Set<Constraint<PDPVariable, TaskValue>> getAllConstraints() {
		Set<Constraint<PDPVariable, TaskValue>> constraints = new HashSet<>();

		constraints.add(constraint1());
		constraints.add(constraint2());
		constraints.add(constraint3());
		constraints.add(constraint4());
		constraints.add(constraint5());
		constraints.add(constraint6());
		constraints.add(constraint7());

		return constraints;
	}

	// Task t cannot be the same than nextTask(t)
	private Constraint<PDPVariable, TaskValue> constraint1() {
		Constraint<PDPVariable, TaskValue> constraint = new Constraint<PDPVariable, TaskValue>() {

			@Override
			public int getInputSize() {
				return inputSize;
			}

			@Override
			public boolean valueAt(Assignment<PDPVariable, TaskValue> point) {
				if (point.size() != vehicleCount * inputSize) {
					return false;
				}
				for (int y = 0; y < vehicleCount; y++) {
					for (int x = 0; x < inputSize - 1; x++) {
						if (point.get(x, y).getValue().equals(point.get(x + 1, y).getValue())) {
							return false;
						}
					}
				}
				return true;
			}

		};

		return constraint;
	}

	// Task(Vx) must have type pickup for every x
	private Constraint<PDPVariable, TaskValue> constraint2() {
		Constraint<PDPVariable, TaskValue> constraint = new Constraint<PDPVariable, TaskValue>() {

			@Override
			public int getInputSize() {
				return inputSize;
			}

			@Override
			public boolean valueAt(Assignment<PDPVariable, TaskValue> point) {
				if (point.size() != vehicleCount * inputSize) {
					return false;
				}
				for (int y = 0; y < vehicleCount; y++) {
					if (((TaskValue) point.get(0, y).getValue()).getType() == ValueType.DELIVER) {
						// It is OK for a vehicle to do nothing or to begin by picking a task up
						return false;
					}
				}
				return true;
			}

		};

		return constraint;
	}

	// Check that a vehicle that pickups delivers the task and that it delivers it
	// after picking it up
	private Constraint<PDPVariable, TaskValue> constraint3() {
		Constraint<PDPVariable, TaskValue> constraint = new Constraint<PDPVariable, TaskValue>() {

			@Override
			public int getInputSize() {
				return inputSize;
			}

			@Override
			public boolean valueAt(Assignment<PDPVariable, TaskValue> point) {
				if (point.size() != vehicleCount * inputSize) {
					return false;
				}
				for (int y = 0; y < vehicleCount; y++) {
					for (int x = 0; x < inputSize - 1; x++) {
						TaskValue task1 = (TaskValue) (point.get(x, y).getValue());
						if (task1.getType() == ValueType.PICKUP) {
							boolean found = false;
							for (int xPrime = x + 1; xPrime < inputSize; xPrime++) {
								TaskValue task2 = (TaskValue) (point.get(xPrime, y).getValue());
								if (task2.getType() == ValueType.DELIVER && task1.getTask().equals(task2.getTask())) {
									found = true;
								}
							}
							if (!found) {
								return false;
							}
						}
					}
				}
				return true;
			}

		};

		return constraint;
	}

	// Verify that nextTask(None) = None
	private Constraint<PDPVariable, TaskValue> constraint4() {
		Constraint<PDPVariable, TaskValue> constraint = new Constraint<PDPVariable, TaskValue>() {

			@Override
			public int getInputSize() {
				return inputSize;
			}

			@Override
			public boolean valueAt(Assignment<PDPVariable, TaskValue> point) {
				if (point.size() != vehicleCount * inputSize) {
					return false;
				}
				for (int y = 0; y < vehicleCount; y++) {
					boolean isNone = false;
					for (int x = 0; x < inputSize; x++) {
						TaskValue taskValue = (TaskValue) (point.get(x, y).getValue());
						if (taskValue.getType() != ValueType.NONE) {
							if (isNone) {
								// We found a non-None task after finding a None one
								return false;
							}
						} else {
							isNone = true;
						}
					}
				}
				return true;
			}

		};

		return constraint;
	}

	// Verify that we don't exceed the maximum weight a vehicle can carry
	private Constraint<PDPVariable, TaskValue> constraint5() {
		Constraint<PDPVariable, TaskValue> constraint = new Constraint<PDPVariable, TaskValue>() {

			@Override
			public int getInputSize() {
				return inputSize;
			}

			@Override
			public boolean valueAt(Assignment<PDPVariable, TaskValue> point) {
				if (point.size() != vehicleCount * inputSize) {
					return false;
				}
				for (int y = 0; y < vehicleCount; y++) {
					int maxCapacity = point.getCapacityForVehicle(y);
					int actualWeight = 0;
					for (int x = 0; x < inputSize; x++) {
						TaskValue taskValue = (TaskValue) (point.get(x, y).getValue());
						actualWeight += taskValue.getWeight();
						if (actualWeight > maxCapacity) {
							return false;
						}
					}
				}
				return true;
			}

		};

		return constraint;
	}

	// Verify that all tasks are delivered
	private Constraint<PDPVariable, TaskValue> constraint6() {
		Constraint<PDPVariable, TaskValue> constraint = new Constraint<PDPVariable, TaskValue>() {

			@Override
			public int getInputSize() {
				return inputSize;
			}

			@Override
			public boolean valueAt(Assignment<PDPVariable, TaskValue> point) {
				if (point.size() != vehicleCount * inputSize) {
					return false;
				}
				Set<TaskValue> taskValues = new HashSet<>();
				int deliveredTask = 0;
				for (int y = 0; y < vehicleCount; y++) {
					for (int x = 0; x < inputSize; x++) {
						TaskValue taskValue = (TaskValue) (point.get(x, y).getValue());
						if (taskValue.getType() != ValueType.NONE) {
							taskValues.add(taskValue);
							deliveredTask++;
						}
					}
				}
				if (deliveredTask != inputSize || deliveredTask != taskValues.size()) {
					return false;
				}
				return true;
			}

		};

		return constraint;
	}

	private Constraint<PDPVariable, TaskValue> constraint7() {
		Constraint<PDPVariable, TaskValue> constraint = new Constraint<PDPVariable, TaskValue>() {

			@Override
			public int getInputSize() {
				return inputSize;
			}

			@Override
			public boolean valueAt(Assignment<PDPVariable, TaskValue> point) {
				// Point contains a list of values. First inputPerVehicle correspond to plan for
				// vehicle 1, next inputPerVehicle correspond to plan for vehicle 2... etc
				for (int i = 0; i < point.size() - 1; i++) {
				}
				return true;
			}

		};

		return constraint;
	}

	// TODO Create other constraints similarly to constraint1(). Do not forget to
	// add them to getAllConstraints()

}
