package bpm.fd.api.core.model.components.definition.comment;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class CommentOptions implements IComponentOptions {

	public static final int KEY_SHOW_ALL_COMMENTS = 0;
	public static final int KEY_ALLOW_ADD_COMMENTS = 1;
	public static final int KEY_LIMIT_COMMENTS = 2;
	public static final int KEY_LIMIT_NUMBER = 3;
	public static final int KEY_VALIDATION = 4;
	
	public static String[] standardKeys = new String[]{"showComments","allowAddComments","limitComment","limit","validation"};
	public static String[] i18nKeys = new String[]{};
	
	private boolean showComments = true;
	private boolean allowAddComments = true;
	
	private boolean limitComment;
	private int limit;
	private boolean validation;
	
	@Override
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	@Override
	public String getDefaultLabelValue(String key) {
		
		return null;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("class", CommentOptions.class.getName());
		
		for(int i = 0; i < standardKeys.length; i++){
			e.addElement(standardKeys[i]).setText(getValue(standardKeys[i]));
		}
		return e;
	}

	@Override
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	@Override
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	@Override
	public String getValue(String key) {
		int index = -1;
		
		for(int i = 0; i < standardKeys.length; i ++){
			if (standardKeys[i].equals(key)){
				index = i;
				break;
			}
		}
		switch (index) {
		case KEY_ALLOW_ADD_COMMENTS:
			return isAllowAddComments() + "";
		case KEY_SHOW_ALL_COMMENTS:
			return isShowComments() + "";
		case KEY_LIMIT_COMMENTS:
			return isLimitComment() + "";
		case KEY_LIMIT_NUMBER:
			return getLimit() + "";
		case KEY_VALIDATION:
			return isValidation() + "";
		default:
			break;
		}
		return null;
	}

	public boolean isShowComments() {
		return showComments;
	}

	public void setShowComments(boolean showComments) {
		this.showComments = showComments;
	}

	public boolean isAllowAddComments() {
		return allowAddComments;
	}

	public void setAllowAddComments(boolean allowAddComments) {
		this.allowAddComments = allowAddComments;
	}

	@Override
	public IComponentOptions copy() {
		CommentOptions copy = new CommentOptions();
		
		copy.setAllowAddComments(allowAddComments);
		copy.setShowComments(showComments);
		
		return copy;
	}

	public boolean isLimitComment() {
		return limitComment;
	}

	public void setLimitComment(boolean limitComment) {
		this.limitComment = limitComment;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}
	
}
