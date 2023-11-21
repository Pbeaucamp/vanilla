package bpm.gateway.ui.gef.commands;

import java.awt.Point;

import org.eclipse.gef.commands.Command;

import bpm.gateway.ui.gef.model.Link;

public class MoveBendPointCommand extends Command{
	private Point newLocation;
	private int index;
	private Link link;

	
	public void execute() {
	    link.setBendPoint(index, newLocation);
	  }
	
	/**
	 * @param newLocation the newLocation to set
	 */
	public void setNewLocation(Point newLocation) {
		this.newLocation = newLocation;
	}
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
	
	
}
