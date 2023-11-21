package bpm.workflow.ui.gef.model;

import bpm.workflow.runtime.model.IActivity;

public class Link {
	private Node source;
	private Node target;
	private boolean isConnected = false;
	private String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Link(Node source, Node target) throws Exception {
		this.source = source;
		this.target = target;
		reconnect(source, target);
	}

	public Link(Node source, Node target, String color) throws Exception {
		this.source = source;
		this.target = target;
		this.color = color;
		reconnect(source, target);
	}

	public Node getSource() {
		return source;
	}

	public Node getTarget() {
		return target;
	}

	public void reconnect() throws Exception {
		if(!isConnected) {
			source.addLink(this);
			// source.getGatewayModel().addOutput(target.getGatewayModel());

			target.addLink(this);
			// target.getGatewayModel().addInput(source.getGatewayModel());

			isConnected = true;
		}
	}

	public void reconnect(Node newSource, Node newTarget) throws Exception {
		if(newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}

	public void disconnect() {
		if(isConnected) {
			source.removeLink(this);
			if(source.getWorkflowObject() instanceof IActivity && target.getWorkflowObject() instanceof IActivity) {
				((IActivity) source.getWorkflowObject()).deleteTarget((IActivity) target.getWorkflowObject());

				target.removeLink(this);
				((IActivity) target.getWorkflowObject()).deleteSource((IActivity) source.getWorkflowObject());
				isConnected = false;
			}
		}
	}
}
