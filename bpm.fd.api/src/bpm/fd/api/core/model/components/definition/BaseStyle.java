package bpm.fd.api.core.model.components.definition;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.resources.FileCSS;

public class BaseStyle {
	private FileCSS fileCss;
	private String classCss;
	
	public BaseStyle(FileCSS fileCss, String classCss){
		this.fileCss = fileCss;
		this.classCss = classCss;
	}

	/**
	 * @return the fileCss
	 */
	public FileCSS getFileCss() {
		return fileCss;
	}

	/**
	 * @param fileCss the fileCss to set
	 */
	public void setFileCss(FileCSS fileCss) {
		this.fileCss = fileCss;
	}

	/**
	 * @return the classCss
	 */
	public String getClassCss() {
		return classCss;
	}

	/**
	 * @param classCss the classCss to set
	 */
	public void setClassCss(String classCss) {
		this.classCss = classCss;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("css-style");
		e.addAttribute("fileCss-ref", fileCss.getName());
		e.addAttribute("css-class", getClassCss());
		return e;
	}
}
