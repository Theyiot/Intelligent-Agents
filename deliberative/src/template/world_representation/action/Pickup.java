package template.world_representation.action;

import logist.task.Task;

public class Pickup extends Action {
	
	private final Task task;
	
	
	public Pickup(Task task) {
		super(ActionType.PICKUP);
		this.task = task;
	}


	public Task getTask() {
		return task;
	}
	
	@Override
	public String toString() {
		return "Pickup action with task: " + task;
	}


	@Override
	public Double getWeight() {
		return 0.0;
	}


	@Override
	public logist.plan.Action toLogistAction() {
		return new logist.plan.Action.Pickup(task);
	}

}
