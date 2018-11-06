package centralized.value;

import java.util.Objects;

import logist.task.Task;
import problem.csp.primitive.Value;

public class TaskValue implements Value {
	private final Task task;
	private final ValueType type;
	
	public TaskValue(Task task, ValueType type) {
		this.task = task;
		this.type = type;
	}
	
	public TaskValue() {
		this.task = null;
		this.type = ValueType.NONE;
	}
	
	public Task getTask() {
		return task;
	}
	
	public ValueType getType() {
		return type;
	}
	
	public int getWeight() {
		switch(type) {
		case PICKUP:
			return task.weight;
		case DELIVER:
			return -task.weight;
		case NONE:
			return 0;
		default:
			throw new IllegalStateException("Task with unknown type");
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof TaskValue)) {
			return false;
		} else {
			TaskValue otherTaskValue = (TaskValue) other;
			if (task == null) {
				if (otherTaskValue.task == null) {
					return type == otherTaskValue.type;
				} else {
					return false;
				}
			} else {
				return task.id == otherTaskValue.getTask().id && otherTaskValue.getType() == type;
			}
		}
	}
	
	@Override
	public int hashCode() {
		if (task == null) {
			return Objects.hash(-1, type);
		} else {
			return Objects.hash(task.id, type);
		}
	}
	
	public enum ValueType {
		PICKUP, DELIVER, NONE
	}

}
