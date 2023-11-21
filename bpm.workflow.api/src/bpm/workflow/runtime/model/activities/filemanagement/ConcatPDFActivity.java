package bpm.workflow.runtime.model.activities.filemanagement;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.variables.ListVariable;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

public class ConcatPDFActivity extends AbstractActivity implements IComment, IOutputProvider, IAcceptInput {
	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public List<String> filesToConcat = new ArrayList<String>();
	protected String comment;
	protected String varRefName;
	protected static int number = 0;
	protected String storingFilePath;
	protected String pathToStore = ListVariable.VANILLA_TEMPORARY_FILES;
	protected String outputName;

	public ConcatPDFActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);

		number++;
	}

	public ConcatPDFActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;

		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);

		storingFilePath = this.getId() + "_outputpath";

	}

	@Override
	public void setName(String name) {
		super.setName(name);
		storingFilePath = this.getId() + "_outputpath";
	}

	public String getOutputVariable() {
		return this.storingFilePath;
	}

	public List<String> getInputs() {
		return filesToConcat;
	}

	public void setInputs(List<String> filesToConcat) {
		this.filesToConcat = filesToConcat;
	}

	public void addInput(String filePath) {
		this.filesToConcat.add(filePath);
	}

	public void removeInput(String filePath) {
		this.filesToConcat.remove(filePath);

	}

	public ConcatPDFActivity copy() {
		ConcatPDFActivity a = new ConcatPDFActivity();

		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		if(varRefName != null)
			a.setVariable(varRefName);
		return a;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("concatPDFActivity");
		if(comment != null) {
			e.addElement("comment").setText(comment);
		}
		if(!this.filesToConcat.isEmpty()) {
			Element files = e.addElement("filestoconcat");
			for(String p : this.filesToConcat) {
				files.addElement("path").setText(p);
			}

		}

		if(outputName != null) {
			e.addElement("pathout").setText(this.outputName);
		}

		e.addElement("pathtostore").setText(this.pathToStore);

		if(varRefName != null) {
			e.addElement("varRefName").setText(varRefName);
		}

		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if(filesToConcat.isEmpty()) {
			buf.append("For activity " + name + ", no input file is set.\n");
		}

		return buf.toString();
	}

	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);

		return listeVar;
	}

	public String getVariable() {
		return varRefName;
	}

	public void setVariable(String varRefName) {
		this.varRefName = varRefName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	public Class<?> getServerClass() {
		return FileServer.class;
	}

	public void decreaseNumber() {
		number--;
	}

	public String getPathToStore() {
		return pathToStore;
	}

	public void setPathToStore(String pathToStore) {
		this.pathToStore = pathToStore;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public String getPhrase() {
		return "Select the activities which provide files";
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				pathToStore = workflowInstance.parseString(pathToStore);

				if(!pathToStore.endsWith(File.separator)) {
					pathToStore += File.separator;
				}

				String pathout = pathToStore + outputName;
				String temppath = pathToStore + "temp" + new Object().hashCode() + File.separator + outputName;
				String temppath2 = pathToStore + "temp" + File.separator + outputName;
				
				File ff = new File(temppath);
				if(!ff.getParentFile().exists()) {
					ff.getParentFile().mkdir();
				}
				ff = new File(temppath2);
				if(!ff.getParentFile().exists()) {
					ff.getParentFile().mkdir();
				}
				
				
				boolean first = true;
				synchronized(filesToConcat) {
					Document document = null;
					PdfCopy copy = null;
					PdfCopy copy2 = null;
					Document document2 = null;
					try {

						if(first) {
							first = false;
							document = new Document();
							copy = new PdfCopy(document, new FileOutputStream(temppath));
							document.open();
							PdfReader reader;
							int n;

							for(String s : filesToConcat) {
								s = workflowInstance.parseString(s);
								reader = new PdfReader(s);
								n = reader.getNumberOfPages();
								for(int page = 0; page < n;) {
									copy.addPage(copy.getImportedPage(reader, ++page));
								}
							}

							copy.close();
							document.close();

						}

						else {
							File f = new File(pathout);
							f.renameTo(new File(temppath));
							document = new Document();
							copy = new PdfCopy(document, new FileOutputStream(temppath2));
							document.open();
							PdfReader reader;
							int n;

							reader = new PdfReader(temppath);
							n = reader.getNumberOfPages();

							for(int page = 0; page < n;) {
								copy.addPage(copy.getImportedPage(reader, ++page));
							}

							copy.close();
							document.close();

							document2 = new Document();
							copy2 = new PdfCopy(document2, new FileOutputStream(temppath2));
							document2.open();
							PdfReader reader2;
							int n2;

							reader2 = new PdfReader(temppath);
							n2 = reader2.getNumberOfPages();
							for(int page = 0; page < n2;) {
								copy2.addPage(copy2.getImportedPage(reader2, ++page));
							}

							PdfReader reader3;
							int n3;

							for(String s : filesToConcat) {
								s = workflowInstance.parseString(s);
								reader3 = new PdfReader(s);
								n3 = reader3.getNumberOfPages();
								for(int page = 0; page < n3;) {
									copy2.addPage(copy2.getImportedPage(reader3, ++page));
								}
							}

							copy2.close();
							document2.close();

						}

					} catch(Exception e) {
						if(document != null && document.isOpen()) {
							try {
								document.close();
							} catch(Exception _e) {}
						}
						if(document2 != null && document2.isOpen()) {
							try {
								document2.close();
							} catch(Exception _e) {}
						}
						if(copy != null) {
							try {
								copy.close();
							} catch(Exception _e) {}
						}
						if(copy2 != null) {
							try {
								copy2.close();
							} catch(Exception _e) {}
						}
						Logger.getLogger(getClass()).error(e.getMessage(), e);
					}

					File f = new File(temppath);
					f.renameTo(new File(pathout));

					File _f = new File(temppath);
					_f.delete();
				}
				
				workflowInstance.getOrCreateVariable(getOutputVariable()).addValue(pathout);

				activityResult = true;
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}

	}

}
