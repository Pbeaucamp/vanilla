package bpm.fd.api.core.model.parsers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.FdAndroidModel;
import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.FdVanillaFormModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.ComponentStyle;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;

public class FdModelParser {
	private HashMap<Exception, String> errors = new HashMap<Exception, String>();
	private MultiPageFdProject project;
	private FdModel model;
	private Dictionary dictionary;
	private FactoryStructure factoryStructure;

	public FdModelParser(FactoryStructure factoryStructure, Dictionary dictionary) {
		this.dictionary = dictionary;
		this.factoryStructure = factoryStructure;
	}

	public HashMap<Exception, String> getErrors() {
		return errors;
	}

	public MultiPageFdProject parse(Document document) throws Exception {
		parse(document.getRootElement());
		return project;
	}

	public MultiPageFdProject parse(InputStream stream) throws Exception {
		Document doc = DocumentHelper.parseText(IOUtils.toString(stream, "UTF-8"));
		return parse(doc);

	}

	public FdProject getProject() throws Exception {
		return project;
	}

	public FdModel getModel() throws Exception {
		if(project == null) {
			throw new Exception("File has not been parsed of the parsing failed");
		}
		return project.getFdModel();
	}

	public FdProjectDescriptor getDescriptor() throws Exception {
		if(project == null) {
			throw new Exception("File has not been parsed of the parsing failed");
		}
		return project.getProjectDescriptor();
	}

	private void parse(Element root) throws Exception {
		FdProjectDescriptor descriptor = parseProjectDescriptor(root);
		FdModel fdModel = parseModel(root);
		project = new MultiPageFdProject(descriptor, fdModel);
	}

	private FdProjectDescriptor parseProjectDescriptor(Element root) {
		FdProjectDescriptor desc = new FdProjectDescriptor();

		Element base = root.element("projectDescriptor");
		desc.setAuthor(base.element("author").getText());
		desc.setCreation(base.element("creation").getText());
		desc.setDescription(base.element("description").getText());
		desc.setDictionaryName(base.element("dictionaryName").getText());
		desc.setModelName(base.element("modelName").getText());
		desc.setProjectName(base.element("projectName").getText());
		desc.setProjectVersion(base.element("projectVersion").getText());
		if(base.element("internalApiDesignVersion") != null) {
			desc.setInternalApiDesignVersion(base.element("internalApiDesignVersion").getText());
		}

		if(root.attribute("modelRepositoryItemId") != null) {
			desc = new FdProjectRepositoryDescriptor(desc);
			((FdProjectRepositoryDescriptor) desc).setModelDirectoryItemId(Integer.parseInt(root.attributeValue("modelRepositoryItemId")));

		}
		if(root.attribute("dictionaryRepositoryItemId") != null) {
			if(!(desc instanceof FdProjectRepositoryDescriptor)) {
				desc = new FdProjectRepositoryDescriptor(desc);
			}

			((FdProjectRepositoryDescriptor) desc).setDictionaryDirectoryItemId(Integer.parseInt(root.attributeValue("dictionaryRepositoryItemId")));

		}

		return desc;
	}

	private FdModel parseModel(Element root) {

		if(root.attribute("type") != null && root.attributeValue("type").equals(FdVanillaFormModel.class.getName())) {
			model = new FdVanillaFormModel(factoryStructure);
		}
		else if(root.attribute("type") != null && root.attributeValue("type").equals(FdAndroidModel.class.getName())) {
			model = new FdAndroidModel(factoryStructure);
		}
		else {
			model = new FdModel(factoryStructure);
		}
		model.setName(root.element("document-properties").element("name").getText());

		EventParser.parse(model, root);

		if(root.attribute("cssClass") != null) {
			model.setCssClass(root.attributeValue("cssClass"));
		}

		Element structure = root.element("structure");

		if(structure == null) {

			return model;
		}

		for(Element t : (List<Element>) structure.elements("table")) {
			model.addToContent(parseTable(t));

		}

		for(Element t : (List<Element>) structure.elements("folder")) {
			model.addToContent(parseFolder(t));

		}
		for(Element t : (List<Element>) structure.elements("cell")) {
			Cell c = parseCell(t);
			// empty Cell are messing up the model opening
			// they are created when a component is not fully defined whe added to the model
			// it only happen on the new Design mode
			if(!c.getContent().isEmpty()) {
				model.addToContent(parseCell(t));
			}

		}
		for(Element t : (List<Element>) structure.elements("stackablecell")) {
			model.addToContent(parseStackedCell(t));

		}
		for(Element t : (List<Element>) structure.elements("drillDrivenStackableCell")) {
			model.addToContent(parseDrivenStackedCell(t));

		}
		
		for(Element t : (List<Element>) structure.elements("divcell")) {
			model.addToContent(parseDivCell(t));
		}

		return model;
	}

	private DivCell parseDivCell(Element eCell) {
		DivCell cell = model.getStructureFactory().createDivCell(eCell.attribute("name").getValue(), Integer.parseInt(eCell.attribute("id").getValue()));

		if(eCell.attribute("x") != null) {
			cell.setPosition(Integer.valueOf(eCell.attributeValue("x")), Integer.valueOf(eCell.attributeValue("y")));
			cell.setSize(Integer.valueOf(eCell.attributeValue("width")), Integer.valueOf(eCell.attributeValue("height")));
		}

		if(eCell.attribute("cssClass") != null) {
			cell.setCssClass(eCell.attributeValue("cssClass"));
		}
		EventParser.parse(cell, eCell);

		for(Element t : (List<Element>) eCell.elements()) {
			if("table".equals(t.getName())) {
				cell.addToContent(parseTable(t));
			}
			else if("cell".equals(t.getName())) {
				try {
					cell.addToContent(parseCell(t));

				} catch(Exception e) {
					errors.put(e, "Error when looking for cell content");
				}
			}
		}
		return cell;
	}

	private Folder parseFolder(Element elFolder) {
		Folder folder = model.getStructureFactory().createFolder(elFolder.attributeValue("name"), Integer.parseInt(elFolder.attribute("id").getValue()));

		if(elFolder.attribute("x") != null) {
			folder.setPosition(Integer.valueOf(elFolder.attributeValue("x")), Integer.valueOf(elFolder.attributeValue("y")));
			folder.setSize(Integer.valueOf(elFolder.attributeValue("width")), Integer.valueOf(elFolder.attributeValue("height")));
		}

		if(elFolder.attributeValue("cssClass") != null) {
			folder.setCssClass(elFolder.attributeValue("cssClass"));
		}
		// component style
		Element style = elFolder.element("componentStyle");
		if(style != null) {
			ComponentStyle cStyle = folder.getComponentStyle();
			for(Element el : (List<Element>) style.elements("style")) {
				cStyle.setStyleFor(el.attributeValue("objectName"), el.getStringValue());

			}

		}

		for(Element r : (List<Element>) elFolder.elements("folderPage")) {
			FolderPage folderPage = model.getStructureFactory().createFolderPage(r.attributeValue("name"), Integer.parseInt(r.attribute("id").getValue()));

			if(r.element("content-model-name") != null) {
				folderPage.setModelName(r.element("content-model-name").getText());
			}

			folderPage.setTitle(r.attributeValue("title"));
			folder.addToContent(folderPage);
		}
		EventParser.parse(folder, elFolder);
		return folder;
	}

	private DrillDrivenStackableCell parseDrivenStackedCell(Element t) {
		DrillDrivenStackableCell stack = model.getStructureFactory().createDrillDrivenStackableCell(t.attribute("name").getValue(), Integer.parseInt(t.attribute("id").getValue()));

		if(t.attribute("x") != null) {
			stack.setPosition(Integer.valueOf(t.attributeValue("x")), Integer.valueOf(t.attributeValue("y")));
			stack.setSize(Integer.valueOf(t.attributeValue("width")), Integer.valueOf(t.attributeValue("height")));
		}
		if(t.attribute("cssClass") != null) {
			stack.setCssClass(t.attributeValue("cssClass"));
		}
		EventParser.parse(stack, t);

		for(Element elem : (List<Element>) t.elements()) {
			// for a cell
			if("cell".equals(elem.getName())) {
				stack.addToContent(parseCell(elem));
			}
			// for a component
			if("component-ref".equals(elem.getName())) {
				try {
					IComponentDefinition def = parseComponentRef(elem);
					stack.addBaseElementToContent(def);
					DrillDrivenComponentConfig config = (DrillDrivenComponentConfig) stack.getConfig(def);
					// component parameter
					for(Element pE : (List<Element>) elem.elements("parameter")) {
						String pName = pE.attributeValue("name");
						String dVal = pE.attributeValue("defaultValue");
						String pVal = pE.getText();

						for(ComponentParameter p : config.getParameters()) {
							if(p.getName().equals(pName)) {
								config.setParameterOrigin(p, pVal);
								p.setDefaultValue(dVal);

								if(pE.attribute("fieldNumber") != null) {
									try {
										config.setComponentOutputName(p, pE.attributeValue("fieldNumber"));
									} catch(Exception ex) {

									}
								}
								if(pE.attribute("controller-component-ref") != null) {
									try {
										config.setController(dictionary.getComponent(pE.attributeValue("controller-component-ref")));
									} catch(Exception ex) {

									}
								}
							}
						}
					}

				} catch(Exception e) {
					errors.put(e, "Error when looking for cell content");
				}
			}

		}
		return stack;
	}

	private StackableCell parseStackedCell(Element t) {
		StackableCell stack = model.getStructureFactory().createStackableCell(t.attribute("name").getValue(), Integer.parseInt(t.attribute("id").getValue()), Integer.parseInt(t.attribute("colSpan").getValue()), Integer.parseInt(t.attribute("rowSpan").getValue()));

		if(t.attribute("x") != null) {
			stack.setPosition(Integer.valueOf(t.attributeValue("x")), Integer.valueOf(t.attributeValue("y")));
			stack.setSize(Integer.valueOf(t.attributeValue("width")), Integer.valueOf(t.attributeValue("height")));
		}
		if(t.attribute("type") != null) {
			stack.setType(t.attribute("type").getValue());
		}

		if(t.attribute("cssClass") != null) {
			stack.setCssClass(t.attributeValue("cssClass"));
		}

		EventParser.parse(stack, t);

		for(Element elem : (List<Element>) t.elements()) {
			// for a cell
			if("cell".equals(elem.getName())) {
				stack.addToContent(parseCell(elem));
			}
			// for a component
			if("component-ref".equals(elem.getName())) {
				try {
					IComponentDefinition def = parseComponentRef(elem);
					stack.addBaseElementToContent(def);
					ComponentConfig config = stack.getConfig(def);
					// component parameter
					for(Element pE : (List<Element>) elem.elements("parameter")) {
						String pName = pE.attributeValue("name");
						String dVal = pE.attributeValue("defaultValue");
						String pVal = pE.getText();

						for(ComponentParameter p : config.getParameters()) {
							if(p.getName().equals(pName)) {
								config.setParameterOrigin(p, pVal);
								p.setDefaultValue(dVal);

								if(pE.attribute("fieldNumber") != null) {
									try {
										config.setComponentOutputName(p, pE.attributeValue("fieldNumber"));
									} catch(Exception ex) {

									}
								}

								break;
							}
						}
					}

				} catch(Exception e) {
					errors.put(e, "Error when looking for cell content");
				}
			}

		}
		return stack;
	}

	private Cell parseCell(Element eCell) {
		Cell cell = model.getStructureFactory().createCell(eCell.attribute("name").getValue(), Integer.parseInt(eCell.attribute("id").getValue()), Integer.parseInt(eCell.attribute("colSpan").getValue()), Integer.parseInt(eCell.attribute("rowSpan").getValue()));

		if(eCell.attribute("x") != null) {
			cell.setPosition(Integer.valueOf(eCell.attributeValue("x")), Integer.valueOf(eCell.attributeValue("y")));
			cell.setSize(Integer.valueOf(eCell.attributeValue("width")), Integer.valueOf(eCell.attributeValue("height")));
		}

		if(eCell.attribute("cssClass") != null) {
			cell.setCssClass(eCell.attributeValue("cssClass"));
		}
		EventParser.parse(cell, eCell);

		for(Element t : (List<Element>) eCell.elements()) {
			if("table".equals(t.getName())) {
				cell.addToContent(parseTable(t));
			}
			else if("component-ref".equals(t.getName())) {
				try {
					IComponentDefinition def = parseComponentRef(t);
					cell.addBaseElementToContent(def);
					ComponentConfig config = cell.getConfig(def);
					// component parameter
					for(Element pE : (List<Element>) t.elements("parameter")) {
						String pName = pE.attributeValue("name");
						String dVal = pE.attributeValue("defaultValue");
						String pVal = pE.getText();

						for(ComponentParameter p : config.getParameters()) {
							if(p.getName().equals(pName)) {
								config.setParameterOrigin(p, pVal);
								p.setDefaultValue(dVal);

								if(pE.attribute("fieldNumber") != null) {
									try {
										config.setComponentOutputName(p, pE.attributeValue("fieldNumber"));
									} catch(Exception ex) {

									}
								}

								break;
							}
						}
					}

				} catch(Exception e) {
					errors.put(e, "Error when looking for cell content");
				}
			}

			// if its a stackable cell
			else if("stackablecell".equals(t.getName())) {

				cell.addToContent(parseStackedCell(t));
			}
			// if its a stackable cell
			else if("drillDrivenStackableCell".equals(t.getName())) {
				cell.addToContent(parseDrivenStackedCell(t));
			}

		}
		return cell;
	}

	private Table parseTable(Element elTable) {
		Table table = model.getStructureFactory().createTable(elTable.attributeValue("name"), Integer.parseInt(elTable.attribute("id").getValue()));

		if(elTable.attribute("cssClass") != null) {
			table.setCssClass(elTable.attributeValue("cssClass"));
		}

		for(Element r : (List<Element>) elTable.elements("row")) {
			List<Cell> row = new ArrayList<Cell>();
			for(Element c : (List<Element>) r.elements()) {

				if("cell".equals(c.getName())) {
					row.add(parseCell(c));
				}
				else if("empty".equals(c.getName())) {
					row.add(null);
				}

			}

			table.addDetailsRow(row);
		}
		EventParser.parse(table, elTable);
		return table;

	}

	private IComponentDefinition parseComponentRef(Element element) throws Exception {
		for(IComponentDefinition def : dictionary.getComponents()) {
			if(def.getName().equals(element.attributeValue("name"))) {
				return def;
			}
		}
		throw new Exception("Component " + element.attributeValue("name") + " not found in dictionary");
	}
}
