package bpm.gateway.ui.gef.commands;

import java.awt.Point;

import org.eclipse.gef.commands.Command;

import bpm.gateway.ui.gef.model.Link;

public class LinkCreateBendPointCommand extends Command{
	private int index;
	private Link link;
	private Point location;
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
	/**
	 * @param location the location to set
	 */
	public void setLocation(Point location) {
		this.location = location;
	}
	
	@Override
	public void execute() {
		link.addBendPoint(index, location);
	}
}
