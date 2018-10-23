package template.world_representation.action;

import logist.task.Task;

public class Deliver extends Action {
	
	private final Task task;


	public Deliver(Task task) {
		super(ActionType.DELIVER);
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
	@Override
	public String toString() {
		return "Deliver action with task: " + task;
	}

	@Override
	public Double getWeight() {
		return 0.0;
	}

	@Override
	public logist.plan.Action toLogistAction() {
		return new logist.plan.Action.Delivery(task);
	}

}
