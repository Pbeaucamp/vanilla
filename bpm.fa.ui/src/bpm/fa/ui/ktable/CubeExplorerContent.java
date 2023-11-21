package bpm.fa.ui.ktable;


import java.awt.Point;

import bpm.fa.api.item.Item;

public class CubeExplorerContent {
	private Item item;
	private Point coordinates;
	
	public CubeExplorerContent(Item it, Point coordinates)
	{
		this.item = it;
		this.coordinates = coordinates;
	}
	
	public Point getCoordinates(){
		return coordinates;
	}
	
	public String getLabel(){
		return item.getLabel();
	}
	
	public Item getItem(){
		return item;
	}
}
