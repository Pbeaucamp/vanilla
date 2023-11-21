package bpm.gateway.core;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.dom4j.Element;

import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * The Transformation interface.
 * @author LCA
 *
 */
public interface Transformation extends PropertyChangeListener{

	public static final String PROPERTY_INPUT_CHANGED = "inputChanged";
	public static final String PROPERTY_NAME_CHANGED = "nameChanged";
	
	
	/**
	 * 
	 * @return the position x
	 */
	public int getPositionX();
	
	/**
	 * 
	 * @return the position y
	 */
	public int getPositionY();
	
	/**
	 * 
	 * @return the transformation name
	 */
	public String getName();
	
	/**
	 * 
	 * @return the Transformation description
	 */
	public String getDescription();
	
	
	/**
	 * 
	 * @return the List of the InputDataStream for this Transformation
	 */
	public List<Transformation> getInputs();
	
	
	/**
	 * 
	 * @return the List of the OutputDataStream for this Transformation
	 */
	public List<Transformation> getOutputs();
	
	
	/**
	 * 
	 * @param transfo
	 */
	public void removeInput(Transformation transfo);
	
	/**
	 * 
	 * @param transfo
	 */
	public void removeOutput(Transformation transfo);
	
	/**
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public boolean addInput(Transformation stream) throws Exception;
	
	/**
	 * 
	 * @param stream
	 */
	public void addOutput(Transformation stream);
	public void addOutput(Transformation stream, List<Point> outputBendPoints) ;
//	/**
//	 * 
//	 * @param runtimeEngine
//	 * @return the Object that will perform the run for this transformation
//	 */
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine);
	
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize);

	public void setPositionX(int x);
	public void setPositionY(int y);
	/**
	 * 
	 * @return a dom4j element for saving the resource
	 */
	public Element getElement();

	public void setName(String name);

	public void setDescription(String description);
	
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException;
	
	public DocumentGateway getDocument();
	
	public void refreshDescriptor() ;
	
	/**
	 * this method must rebuild the descriptor for the
	 * Transformation
	 * is called when loading a model for such step like
	 * Input CSV or XLS to build the descriptor 
	 */
	public void initDescriptor();

	
	public Transformation copy();
	
	public String getTemporaryFilename();

	public void setTemporaryFilename(String string);
	public char getTemporarySpliterChar();
	public void setTemporarySpliterChar(char separator);

	
	public String getContainer();
	public void setContainer(String container);

	
	
	/**
	 * Map the given Variable for the given StreamElement
	 * @param element
	 * @param var
	 */
	public void addOutputVariable(StreamElement element, Variable var);
	
	/**
	 * remove the mapping between the Variable v and a StreamElement
	 * @param v
	 */
	public void removeOutputVariable(Variable v);
	
	/**
	 * 
	 * @param v
	 * @return the StreamElement mapped on the given Variable or null
	 */
	public StreamElement getOutputVariable(Variable v);

	public Variable getOutputVariable(StreamElement element);
	
	
	/**
	 * 
	 * @return a details  documentation of the step definition
	 */
	public String getAutoDocumentationDetails();

	/**
	 * used to store bend points between a transformation and its outputs
	 * @param target
	 * @return
	 */
	public List<Point> getBendPoints(Transformation target);

	public void setBendPoints(Transformation target, List<Point> modelConstraint);
	
	public void setDocumentGateway(DocumentGateway doc);
}
