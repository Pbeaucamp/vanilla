package bpm.gateway.core;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;

import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * This class is the base class for all Transformations present in a BigateawayModel
 * 
 * @author LCA
 *
 */
public abstract class AbstractTransformation extends GatewayObject implements
		Transformation {

	public static final String PROPERTY_LAYOUT="layout";
	public static final String PROPERTY_CONTENTS="contents";
	protected List<Transformation> outputs = new ArrayList<Transformation>();
	protected List<Transformation> inputs = new ArrayList<Transformation>();
	
	protected int x, y ;
	protected DocumentGateway owner;
	
	private String temporaryFilename = "";
	private char temporarySpliterChar = ';';
	
	private List<StreamElement> loadedFields = new ArrayList<StreamElement>();
	
	private HashMap<Variable, Integer> outputVariables = new HashMap<Variable, Integer>();
	private HashMap<String, Integer> bufferedOutputVariables = new HashMap<String, Integer>();
	
	/**
	 * the key is an object to allow to store bendPoints based on the output transformations name
	 * for the digester. 
	 */
	private HashMap<Object, List<Point>> outputsBendPoints = new HashMap<Object, List<Point>>();
	
	protected boolean inited = false;
	
	public List<Point> getBendPoints(Transformation target){
		List<Point> l =  outputsBendPoints.get(target);
		if (l == null){
			l =  outputsBendPoints.get(target.getName());
		}
		if (l == null){
			return new ArrayList<Point>();
		}
		return l;
	}

	public void setBendPoints(Transformation target, List<Point> modelConstraint){
		outputsBendPoints.put(target, modelConstraint);
	}

	
	public void setInited(){
		refreshDescriptor();
		this.inited = true;
		
		Activator.getLogger().debug(getName() + " is Inited");
		try{
			Activator.getLogger().debug(getName() + " is refreshed , descriptor size = " + getDescriptor(this).getColumnCount());
		}catch(Exception ex){
			
		}
	}
	
	public boolean isInited(){
		return inited;
	}
	
	public Variable getOutputVariable(StreamElement element){
		try{
			int i = getDescriptor(this).getStreamElements().indexOf(element);
			for(Variable v : outputVariables.keySet()){
				if ( i == outputVariables.get(v)){
					return v;
				}
			}
		}catch(Exception e){
			
		}
		
		return null;
	}
	
	protected HashMap<Variable, Integer> getOututVariables(){
		return outputVariables;
	}
	
	public final void addStreamElement(String name){
		StreamElement e = new StreamElement();
		e.name = name;
		loadedFields.add(e);
	}
	
	
	public void addOutputVariable(StreamElement element, Variable var){
		try{
			int i = getDescriptor(this).getStreamElements().indexOf(element);
			outputVariables.put(var, i);
		}catch(Exception e){
			outputVariables.put(var, null);
		}
	}
	
	
	
	public void removeOutputVariable(Variable v){
		outputVariables.remove(v);
	}
	
	public void addOutputVariable(String streamNumber, String variableName){
		try{
			bufferedOutputVariables.put(variableName, Integer.parseInt(streamNumber));
						
		}catch(Exception e){
			
		}
	}
	
	public StreamElement getOutputVariable(Variable v){
		Integer index = outputVariables.get(v);
		if ( index == null){
			return null;
		}
		else{
			try {
				getDescriptor(this).getStreamElements().get(index);
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	final protected List<StreamElement> getLoadedFields(){
		return new ArrayList<StreamElement>(loadedFields);
	}
	
	final protected void clearFieldsBuffer(){
		loadedFields = null;
	}
	
	
	/*
	 * for GlobalInputDefinition only
	 */
	private String containerName;
	
	/**
	 * @return the char used as column splitter for temporary files generated at runtime
	 */
	public char getTemporarySpliterChar() {
		return temporarySpliterChar;
	}

	/**
	 * set the splitter char for temporary files
	 * @param the char used as column splitter for temporary files generated at runtime
	 */
	public void setTemporarySpliterChar(char temporarySpliterChar) {
		this.temporarySpliterChar = temporarySpliterChar;
	}
	
	/**
	 * set the splitter char for temporary files
	 * @param temporarySpliterChar
	 */
	public void setTemporarySpliterChar(String temporarySpliterChar) {
		try{
			this.temporarySpliterChar = temporarySpliterChar.charAt(0);
		}catch(IndexOutOfBoundsException e){
			
		}
		
	}

	/**
	 * @return the temporary fileName that may be generated
	 */
	public String getTemporaryFilename(){
		return temporaryFilename;
	}
	
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	
	/**
	 * add a listener 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	/**
	 * remove the listener
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
	}
	
	/**
	 * must be called from all subclasses
	 * this method notify listeners taht a change has occured
	 */
	public boolean addInput(Transformation stream) throws Exception {
		if (!inputs.contains(stream) && stream != null){
			
			
			if (!isInited()){
				inputs.add(stream);
				return true;
			}
			else{
				if (stream.getDescriptor(this) == null){
					throw new Exception("Cannot add Input because the transformation " + stream.getName() + " is not yet defined");
				}
				
				inputs.add(stream);
			}
			
			
			
			
			if (getContainer() != null){
				listeners.firePropertyChange(PROPERTY_CONTENTS, null, stream);
			}
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * add a Transformation in Poutput and fire an event to all listeners
	 * @param the ouptut Stream
	 */
	public void addOutput(Transformation stream) {
		if (!outputs.contains(stream) && stream != null){
			outputs.add(stream);
			if (getContainer() != null){
				listeners.firePropertyChange(PROPERTY_CONTENTS, null, stream);
			}
			listeners.addPropertyChangeListener(stream);
			
		}

	}
	
	/**
	 * used by digester to reload bendPoints 
	 * @param stream
	 * @param outputBendPoints
	 */
	public void addOutput(Transformation stream, List<Point> outputBendPoints) {
		if (!outputs.contains(stream) && stream != null){
			setBendPoints(stream, outputBendPoints);
			outputs.add(stream);
			if (getContainer() != null){
				listeners.firePropertyChange(PROPERTY_CONTENTS, null, stream);
			}
			listeners.addPropertyChangeListener(stream);
			
		}
	}

	/**
	 * @return return the descriptor of the step (fields that will be provider by the step)
	 */
	public abstract StreamDescriptor getDescriptor(Transformation transfo) throws ServerException ;

	
	/**
	 * @return the XML definition for the Transformation
	 */
	public abstract Element getElement() ;

	/**
//	 * @return return the Object that will Execute the transformation for Runtime
//	 */
	public abstract RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize);
	/**
	 * @return all the Input Transformation for this step
	 */
	public List<Transformation> getInputs() {
		return inputs;
	}

	
	/**
	 * @return all the output Transformation for this step
	 */
	public List<Transformation> getOutputs() {
		return outputs;
	}

	/**
	 * @return the position X for the view part to draw the box
	 */
	public int getPositionX() {
		return x;
	}

	
	/**
	 * @return the position Y for the view part to draw the box
	 */
	public int getPositionY() {
		return y;
	}

	/**
	 * must be called from all subclasses
	 * this method unregister the Stream and fire an event to refresh all Outputs and then  
	 */
	public void removeInput(Transformation transfo) {
		inputs.remove(transfo);
		if (getContainer() != null){
			listeners.firePropertyChange(PROPERTY_CONTENTS, null, transfo);
		}
		listeners.removePropertyChangeListener(transfo);
		

	}

	/**
	 * must be called from all subclasses
	 * this method unregister the Stream and fire an event to refresh all Outputs and then  
	 */
	public void removeOutput(Transformation transfo) {
		outputs.remove(transfo);
		if (getContainer() != null){
			listeners.firePropertyChange(PROPERTY_CONTENTS, null, transfo);
		}

	}

	/**
	 * @param set the X position and fire an event
	 */
	public void setPositionX(int x) {
		this.x = x;
		listeners.firePropertyChange(PROPERTY_LAYOUT, null, x);
	}

	/**
	 * @param set the Y position and fire an event
	 */
	public void setPositionY(int y) {
		this.y = y;
		listeners.firePropertyChange(PROPERTY_LAYOUT, null, x);

	}
	
	
	/**
	 * 
	 * @param x
	 */
	public void setPositionX(String x){
		try{
			this.x = Integer.parseInt(x);
		}catch(NumberFormatException e){
			
		}
		
	}
	
	/**
	 * 
	 * @param y
	 */
	public void setPositionY(String y){
		try{
			this.y = Integer.parseInt(y);
		}catch(NumberFormatException e){
			
		}
		
	}

	/**
	 * refresh the descriptor or the streamElement Names if an input name has changed
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(Transformation.PROPERTY_INPUT_CHANGED)){
			
			refreshDescriptor();
			for(Variable v : outputVariables.keySet()){
				try{
					if (outputVariables.get(v) != null && outputVariables.get(v) >= getDescriptor(this).getColumnCount()){
						outputVariables.remove(v);
					}
				}catch(Exception e){
					
				}
				
			}
			
		}
		else if(event.getPropertyName().equals(Transformation.PROPERTY_NAME_CHANGED)){
			refreshStreamElementsNames();
		}

	}
	
	
	/**
	 * 
	 */
	private void refreshStreamElementsNames(){
		
		
		try {
			for(StreamElement e : getDescriptor(this).getStreamElements()){
				e.transfoName = getName();
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * this method is called when a change on a transformation modify the 
	 * despcriptor
	 * 
	 * an event must be fired when the modification occur
	 */
	abstract public void refreshDescriptor();

	
	/**
	 * fire an event to all the listeners
	 */
	protected void fireChangedProperty(){
		listeners.firePropertyChange(PROPERTY_INPUT_CHANGED, null, null);
	}
	
	protected void fireProperty(String propertyName){
		listeners.firePropertyChange(propertyName, null, null);
	}
	
	/**
	 * set the Document owning this Transformation
	 * @param doc
	 */
	public void setDocumentGateway(DocumentGateway doc){
		this.owner = doc;
		try {
			if (bufferedOutputVariables != null){
				for(String s : bufferedOutputVariables.keySet()){
					Variable v = getDocument().getVariable(s);
					if (v != null){
						outputVariables.put(v, bufferedOutputVariables.get(s));
						
					}
				}
			}
			
			bufferedOutputVariables = null;
		} catch(Exception e) {}
	}
	
	
	/**
	 * @return the BiGatewayModel owning this Transformation
	 */
	public DocumentGateway getDocument(){
		return owner;
	}
	
	

	/**
	 * init the descriptor must be override to be init the descriptor without firing event 
	 * to listeners
	 */
	public void initDescriptor(){
		setInited();
		refreshDescriptor();
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		
		
		try{
			if (getDescriptor(this) != null){
				for(StreamElement e : getDescriptor(this).getStreamElements()){
					e.transfoName = name;
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	
	/**
	 * @param define the temporary fileName
	 */
	public void setTemporaryFilename(String f){
		this.temporaryFilename = f;
	}
	
	
	
	public String getContainer(){
		return containerName;
	}

	
	public void setContainer(String container){
		this.containerName = container;
	}
}
