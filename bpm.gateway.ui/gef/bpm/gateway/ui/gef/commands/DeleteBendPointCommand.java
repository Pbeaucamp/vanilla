package bpm.gateway.ui.gef.commands;

import java.awt.Point;

import org.eclipse.gef.commands.Command;

import bpm.gateway.ui.gef.model.Link;

public class DeleteBendPointCommand extends Command{
	private int index;
	private Link link;
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(Link link) {
		this.link = link;
	}
	
	@Override
	public void execute() {
		link.removeBendPoint(index);
	}
}
