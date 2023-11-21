package bpm.workflow.runtime.model;

/**
 * The model of an object of the process
 * @author CHARBONNIER, MARTIN
 *
 */
public abstract class WorkflowObject {
	protected String id;
	protected String name;
	protected int xPos = 1;
	protected int yPos = 1;
	protected int yRel = 10;
	protected int width = 50;
	protected int height = 50;
	protected String description="";
	protected String parent = "";
	
	/**
	 * 
	 * @return the container of the node
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * Set the container of the node
	 * @param parent
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * 
	 * @return the name of the node
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @return the name of the node
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the height of the node
	 */
	public int getPositionHeight() {
		return height;
	}

	/**
	 * 
	 * @return the width of the node
	 */
	public int getPositionWidth() {
		return width;
	}

	/**
	 * 
	 * @return the x position of the node
	 */
	public int getPositionX() {
		return xPos;
	}

	/**
	 * 
	 * @return the y position of the node
	 */
	public int getPositionY() {
		return yPos;
	}
	
	public int getRelativePositionY() {
		return yRel;
	}

	/**
	 * Set the name of the node
	 * @param name
	 */
	public void setName(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "");
		
		//remove special characters
		id = name.replaceAll("[^a-zA-Z0-9]", "");
		
//		id = name.replace(" ", "_");
	}

	/**
	 * Set the height of the node
	 * @param height
	 */
	public void setPositionHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Set the height of the node
	 * @param height
	 */
	public void setPositionHeight(String height) {
		try{
			this.height = Integer.parseInt(height);
		}catch(NumberFormatException e){
			
		}
		
	}

	/**
	 * Set the width of the node
	 * @param width
	 */
	public void setPositionWidth(int width) {
		this.width = width;
		
	}

	/**
	 * Set the width of the node
	 * @param width
	 */
	public void setPositionWidth(String width) {
		try{
			this.width = Integer.parseInt(width);
		}catch(NumberFormatException e){
			
		}
		
	}
	/**
	 * Set the x position of the node
	 * @param x
	 */
	public void setPositionX(int x) {
		this.xPos = x;
		
	}
	/**
	 * Set the x position of the node
	 * @param xPos
	 */
	public void setPositionX(String xPos) {
		try{
			this.xPos = Integer.parseInt(xPos);
		}catch(NumberFormatException e){
			
		}
		
	}

	/**
	 * Set the y position of the node
	 * @param y
	 */
	public void setPositionY(int y) {
		this.yPos = y;	
	}
	
	
	/**
	 * Set the y position of the node
	 * @param yPos
	 */
	public void setPositionY(String yPos) {
		try{
			this.yPos = Integer.parseInt(yPos);
		}catch(NumberFormatException e){
			
		}	
	}
	
	public void setRelativePositionY(int y) {
		this.yRel = y;	
	}
	
	
	public void setRelativePositionY(String yPos) {
		try{
			this.yRel = Integer.parseInt(yPos);
		}catch(NumberFormatException e){
			
		}	
	}
	
	/**
	 * 
	 * @return the description of the node
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the node
	 * @param txt
	 */
	public void setDescription(String txt) {
		this.description = txt;
	}

}
