package bpm.fd.api.core.model.components.definition.timer;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class TimerOptions implements IComponentOptions{
	private static final int KEY_START_ON_LOAD = 0;
	private static final int KEY_DELAY = 1;
	private static final int KEY_LABEL = 2;
	
	
	public static String[] standarKeys = new String[]{"startOnLoad", "delay", "label"};
	public static String[] i18nKeys = {};
	private boolean startOnLoad = true;
	private int delay = 60000;
	private String label = "Next Refresh in ";
	

	
	


	/**
	 * @return the startOnLoad
	 */
	public boolean isStartOnLoad() {
		return startOnLoad;
	}

	/**
	 * @param startOnLoad the startOnLoad to set
	 */
	public void setStartOnLoad(boolean startOnLoad) {
		this.startOnLoad = startOnLoad;
	}

	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public String getDefaultLabelValue(String key) {
		
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("class", TimerOptions.class.getName());
		
		for(int i = 0; i < standarKeys.length; i++){
			e.addElement(standarKeys[i]).setText(getValue(standarKeys[i]));
		}
		
//		for(int i = 0; i < i18nKeys.length; i++){
//			e.addElement(i18nKeys[i]).setText(getValue(i18nKeys[i + standarKeys.length - 1]));
//		}
		return e;
	}

	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	public String[] getNonInternationalizationKeys() {
		return standarKeys;
	}

	public String getValue(String key) {
		int index = -1;
		
		for(int i = 0; i < standarKeys.length; i ++){
			if (standarKeys[i].equals(key)){
				index = i;
				break;
			}
		}
		switch (index) {
		case KEY_DELAY:
			return getDelay()+ "";
		case KEY_START_ON_LOAD:
			return isStartOnLoad() + "";
		case KEY_LABEL:
			return getLabel() + "";
		default:
			break;
		}
		return null;
	}

	@Override
	public IComponentOptions copy() {
		TimerOptions copy = new TimerOptions();
		
		copy.setDelay(delay);
		copy.setLabel(label);
		copy.setStartOnLoad(startOnLoad);
		
		return copy;
	}
	
}
