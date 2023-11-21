package bpm.fd.api.core.model.components.definition.chart;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.IStatuable;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.ComponentStyle;
import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.IComponentRenderer;
import bpm.fd.api.core.model.components.definition.IDimensionableDatas;
import bpm.fd.api.core.model.components.definition.ISizeable;
import bpm.fd.api.core.model.components.definition.OutputParameter;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillInfo;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillReportOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericNonPieOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.LineCombinationOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class ComponentChartDefinition implements IComponentDefinition, IStatuable, ISizeable{

	public static final String PROPERTY_NATURE_CHANGED = "bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition.natureChanged";
	
	protected IChartData datas;
	protected ChartRenderer renderer ;//= ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART);
	protected ChartNature nature;
	protected String name;
	protected Dictionary dictionary;
	protected String comment = "";
	protected int width = 400;
	protected int height = 400;
	protected ChartDrillInfo drillInfo = new ChartDrillInfo(this);
	protected String cssClass = null;
	
	protected List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	protected LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	
	protected List<OutputParameter> additionalParameters = new ArrayList<OutputParameter>();
	
	protected ComponentStyle dimensionStyle;
	
	protected HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	protected ChartValueFormat valueFormat = new ChartValueFormat();
	
	protected List<ColorRange> colorRanges = new ArrayList<ColorRange>();
	
	public ChartDrillInfo getDrillDatas(){
		return drillInfo;
	}
		
	/**
	 * @return the valueFormat
	 */
	public ChartValueFormat getValueFormat() {
		return valueFormat;
	}

	public ComponentChartDefinition(String name, Dictionary dictionary) {
		this.name = name;
		this.dictionary = dictionary;
		
		if( this instanceof ComponentChartDefinition ){
			this.renderer = ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART);
		}
		if( this instanceof ComponentRChartDefinition ){
			this.renderer = ChartRenderer.getRenderer(ChartRenderer.RCHART);
		}
		
		
		setComponentOption(new NumberFormatOptions());
		setComponentOption(new ChartDrillReportOption());
		
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}

	public void addComponentParameter(ComponentParameter parameter){
		for(ComponentParameter p : parameters.values()){
			if (p.getName().equals(parameter.getName())){
				return;
			}
		}
		
		for(ComponentParameter p : additionalParameters){
			if (p.getName().equals(parameter.getName())){
				return;
			}
		}
		additionalParameters.add((OutputParameter)parameter);
	}
	
	
	public Collection<OutputParameter> getOutputParameters(){
		return additionalParameters;
	}
	
	
	
	public void removeComponentParameter(ComponentParameter parameter){
		additionalParameters.remove(parameter);
	}
	
	public ComponentStyle getComponentStyle(){
		return dimensionStyle;
	}
	
	public void setName(String name){
		String oldName = this.name;
		this.name = name;
		firePropertyChange(PROPERTY_NAME_CHANGED, oldName, this.name);
	}
	
	public IComponentDatas getDatas() {
		return datas;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setNature(ChartNature nature){
		ChartNature old = this.nature;
		this.nature = nature;
		for(IComponentOptions opt: getOptions()){
			options.remove(opt);
			IComponentOptions nopt = opt.getAdapter(nature);
			if (nopt != null){
				options.add(nopt);
			}
		}
		
		if (!(nature.getNature() == ChartNature.PIE  || nature.getNature() == ChartNature.PIE_3D)){
			
			if (getOptions(GenericNonPieOptions.class) == null){
				options.add(new GenericNonPieOptions());
			}

		}
		//CombinationOptions
		if (nature.isLineCombnation() && (old == null || !old.isLineCombnation())){
			LineCombinationOption o = new LineCombinationOption();
			options.add(o);
		}
		
		
		firePropertyChange(PROPERTY_NATURE_CHANGED, old, nature);
	}
	
	public ChartNature getNature(){
		return nature;
	}
	
	public void setComponentDatas(IChartData datas){
		if (datas instanceof IDimensionableDatas && !(this.datas instanceof IDimensionableDatas)){
			dimensionStyle = new ComponentStyle();
			dimensionStyle.setStyleFor("table", null);
			dimensionStyle.setStyleFor("td", null);
			dimensionStyle.setStyleFor("a", null);
		}
		else if (!(datas instanceof IDimensionableDatas)){
			dimensionStyle = null;
		}
		
		this.datas = datas;	
	}
	
	public List<IComponentOptions> getOptions() {
		return new ArrayList<IComponentOptions>(options);
	}

	public Collection<ComponentParameter> getParameters() {
		if (datas != null && datas.getDataSet() != null ){
			for(ParameterDescriptor p : datas.getDataSet().getDataSetDescriptor().getParametersDescriptors()){
				 if (parameters.get(p) != null){

				 }
				 else{
					 parameters.put(p, new ComponentParameter(p, p.getPosition()));
				 }
			}
			List<ParameterDescriptor> l = datas.getDataSet().getDataSetDescriptor().getParametersDescriptors();
			List<ParameterDescriptor> toRemove = new ArrayList<ParameterDescriptor>();
			for(ParameterDescriptor p : parameters.keySet()){
				if (!l.contains(p)){
					toRemove.add(p);
				}
			}
			for(ParameterDescriptor p : toRemove){
				parameters.remove(p);
			}
		}
		
		List<ComponentParameter> l = new ArrayList<ComponentParameter>();
		l.addAll(parameters.values());
		l.addAll(additionalParameters);
		
		return l;
	}

	public IComponentRenderer getRenderer() {
		return renderer;
	}

	public boolean hasParameter() {
		if (datas != null){
			return datas.getDataSet().getDataSetDescriptor().getParametersDescriptors().size() > 0  || additionalParameters.size() > 0;
		}
		return false;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);
		
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("chart");
		e.addAttribute("name", getName());
		e.addElement("comment").setText(getComment());
		e.addAttribute("width", getWidth() + "");
		e.addAttribute("height", getHeight() + "");
		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		if (getNature() != null){
			Element nat = DocumentHelper.createElement("chartNature");
			nat.addAttribute("style", "" + getNature().getNature());
			
			e.add(nat);
		}
		if (getDatas() != null){
			e.add(getDatas().getElement());
		}
		if (getRenderer() != null){
			e.add(getRenderer().getElement());
		}
		
		Element colorRanges = e.addElement("colorRanges");
		for(ColorRange r : getColorRanges()){
			Element er = colorRanges.addElement("colorRange");
			er.addAttribute("color", r.getHex());
			
			er.addAttribute("legend", r.getName());
			if (r.getMax() != null){
				er.addAttribute("maxValue", r.getMax() + "");
			}
			if (r.getMin() != null){
				er.addAttribute("minValue", r.getMin() + "");
			}
			
		}
		
		Element options = e.addElement("options");
		for(IComponentOptions opt : getOptions()){
			options.add(opt.getElement());
		}
		if (getDrillDatas() != null){
			e.add(getDrillDatas().getElement());
		}
		
		if (getComponentStyle() != null){
			e.add(getComponentStyle().getElement());
		}
		
		for(ComponentParameter p : additionalParameters){
			e.addElement("outputParameter").addAttribute("name", p.getName());
		}
		
		Element events = e.addElement("events");
		for(ElementsEventType evt : getEventsType()){
			String sc = getJavaScript(evt);
			if ( sc != null && !"".equals(sc)){
				Element _e = events.addElement("event").addAttribute("type", evt.name());
				_e.addCDATA(sc);
			}
		}
		
		e.add(getValueFormat().getElement());
		return e;
	}

	public String getId() {
		return getName().replace(" ", "_");
	}

	public String getName() {
		return name;
	}
	
	public void setRenderer(ChartRenderer renderer) throws Exception{
		if (getNature() != null && !renderer.supportNature(getNature().getNature())){
			throw new Exception("Cannot switch renderer for this Nature type");
		}
		this.renderer = renderer;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}

	public IComponentOptions getOptions(Class<?> classOption) {
		for(IComponentOptions opt : getOptions()){
			if (opt.getClass() == classOption){
				return opt;
			}
		}
		return null;
	}

	public void setComponentOption(IComponentOptions options) {
		if (options == null){
			return;
		}
		for(IComponentOptions opt : getOptions()){
			if (opt.getClass() == options.getClass()){
				this.options.remove(opt);
			}
		}
		this.options.add(options.getAdapter(getNature()));
		
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Exception> getProblems() {
		return new ArrayList<Exception>();
	}

	public int getStatus() {
		boolean flag = getNature() != null && getDatas() != null && getDatas().getDataSet().getStatus() == IStatuable.OK && getRenderer() != null;
		
		if (getDatas() instanceof IDimensionableDatas){
			if (((IDimensionableDatas)getDatas()).getDimensionDataSet() == null){
				return IStatuable.ERROR;
			}
			flag = flag && ((IDimensionableDatas)getDatas()).getDimensionDataSet().getStatus() == IStatuable.OK;
		}
		
		if (!flag){
			return IStatuable.ERROR;
		}
		
		return IStatuable.OK;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the cssClass
	 */
	public String getCssClass() {
		return cssClass;
	}

	/**
	 * @param cssClass the cssClass to set
	 */
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	/**
	 * @see {@link IComponentDefinition}
	 * note : if the Chart is multiseries and the drill
	 * options are set to send the Measures Value, the index 
	 * if the Matching for the First Aggregation Mesure
	 */
	public Integer getDatasOutputFieldIndex() {
		if (datas != null && drillInfo != null && drillInfo.isDrillable()){
			
			if (drillInfo.isCategorieAsParameterValue()){
				datas.getCategorieFieldIndex();
			}
			else{
				//XXX :  not very good, dont know how ro proceed
				//on multiSeries chart if the Drill is on the value
				
				if (!datas.getAggregation().isEmpty()){
					datas.getAggregation().get(0).getValueFieldIndex();
				}
				
			}
		}
		return null;
		
	}
	
	public Collection<ElementsEventType> getEventsType() {
		return eventScript.keySet();
	}

	public String getJavaScript(ElementsEventType type) {
		return eventScript.get(type);
	}

	@Override
	public HashMap<ElementsEventType, String> getEventScript() {
		return eventScript;
	}
	
	public void setJavaScript(ElementsEventType type, String script) {
		if (script != null && eventScript.keySet().contains(type)){
			eventScript.put(type, script);
		}
		firePropertyChange(IBaseElement.EVENTS_CHANGED, null, script);
	}
	
	public boolean hasEvents() {
		for(ElementsEventType e : eventScript.keySet()){
			if (eventScript.get(e) != null && !"".equals(eventScript.get(e))){
				return true;
			}
		}
		return false;
	}

	@Override
	public IComponentDefinition copy() {
		ComponentChartDefinition copy = new ComponentChartDefinition("Copy_of_" + name, dictionary);
		
		copy.setCssClass(cssClass);
		for(IComponentOptions opt : getOptions()) {
			try {
				copy.setComponentOption( opt.copy());
			} catch (Exception e) {
			}
		}
		
		Collection<ElementsEventType> events = getEventsType();
		if(events != null && !events.isEmpty()) {
			for(ElementsEventType type : events) {
				copy.setJavaScript(type, getJavaScript(type));
			}
		}
		
//		Collection<ComponentParameter> params = getParameters();
//		if(params != null && !params.isEmpty()) {
//			for(ComponentParameter type : params) {
//				copy.addComponentParameter(type);
//			}
//		}
		
		copy.setComponentDatas((IChartData) datas.copy());
		copy.setHeight(height);
		copy.setNature(nature);
		try {
			copy.setRenderer(renderer);
		} catch (Exception e) {
		}
		copy.setWidth(width);
		
		copy.setComment(comment);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}

	public List<ColorRange> getColorRanges() {
		return colorRanges;
	}

	public void setColorRanges(List<ColorRange> colorRanges) {
		this.colorRanges = colorRanges;
	}
	
	public void addColorRange(ColorRange range){
		colorRanges.add(range);
	}
	
	public void removeColorRange(ColorRange range){
		colorRanges.remove(range);
	}
}
