package bpm.es.datasource.audit;

import bpm.es.datasource.analyzer.parsers.IAnalyzer;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class AuditConfig {
	private RepositoryItem itemAnalyzed;
	private RepositoryItem rootItem;
	private String xml;
	private Group group;
	private IAnalyzer analyzer;
	
	public AuditConfig(RepositoryItem rootItem, RepositoryItem itemAnalyzed, String xml, Group group, IAnalyzer analyzer) {
		super();
		this.xml = xml;
		this.rootItem = rootItem;
		this.itemAnalyzed = itemAnalyzed;
		this.group = group;
		this.analyzer = analyzer;
	}

	/**
	 * @return the item
	 */
	public RepositoryItem getRootItem() {
		return rootItem;
	}
	
	/**
	 * @return the item
	 */
	public RepositoryItem getAnalyzedItem() {
		return itemAnalyzed;
	}

	/**
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @return the analyzer
	 */
	public IAnalyzer getAnalyzer() {
		return analyzer;
	}
	
	
}
