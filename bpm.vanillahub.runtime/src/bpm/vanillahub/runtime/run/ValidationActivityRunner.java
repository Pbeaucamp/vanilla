package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.ValidationActivity;
import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.workflow.commons.beans.Result;

public class ValidationActivityRunner extends ActivityRunner<ValidationActivity> {

	private FileManager fileManager;
	private List<FileXSD> fileXsds;
	
	public ValidationActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, ValidationActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<FileXSD> fileXsds, FileManager fileManager) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.fileManager = fileManager;
		this.fileXsds = fileXsds;
	}

	@Override
	public void run(Locale locale) {
		ByteArrayInputStream parentStream = result.getInputStream();
		parentStream.reset();
		
		File xsdFile = fileManager.getFile(((FileXSD)activity.getResource(fileXsds)).getFile());

		Source xmlFile = new StreamSource(parentStream);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			if(result.isBigFile()) {
				throw new Exception("This activity doesn't support files > 500MB");
			}
			Schema schema = schemaFactory.newSchema(xsdFile);
			
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
			
			addInfo(Labels.getLabel(locale, Labels.XMLValid));
			setResult(Result.SUCCESS);
		} catch (SAXException e) {
			e.printStackTrace();
			
			addError(Labels.getLabel(locale, Labels.XMLNotValid));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			
			addError(Labels.getLabel(locale, Labels.XMLNotValid));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());
		}
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(fileXsds);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(fileXsds);
	}

	@Override
	protected void clearResources() { }
}
