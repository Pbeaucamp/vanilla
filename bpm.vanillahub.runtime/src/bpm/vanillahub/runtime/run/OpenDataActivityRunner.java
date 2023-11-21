package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.OpenDataActivity;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvResource;
import bpm.vanillahub.core.utils.OpenDataHelper;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.workflow.commons.beans.Result;

public class OpenDataActivityRunner extends ActivityRunner<OpenDataActivity> {

	public OpenDataActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, OpenDataActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
	}

	@Override
	public void run(Locale locale) {
		String outputFileName = activity.getOutputName(parameters, variables);

		ByteArrayInputStream bis = getStreamFromService(locale, activity);
		if (bis != null) {
			result.setFileName(outputFileName);
			result.setInputStream(bis);

			clearResources();
			result.setResult(Result.SUCCESS);
		}
		else if (result.getResult() == null || result.getResult() != Result.ERROR) {
			addError(Labels.getLabel(locale, Labels.ServiceDidNotSendData));

			result.setResult(Result.ERROR);
		}
	}

	private ByteArrayInputStream getStreamFromService(Locale locale, OpenDataActivity activity) {
		try {
			return getData(locale, activity);
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToCallService));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
			return null;
		}
	}

	private ByteArrayInputStream getData(Locale locale, OpenDataActivity activity) throws IOException, JSONException {
		switch (activity.getTypeOpenData()) {
		case DATA_GOUV:
			return getDataGouvData(locale, activity);

		default:
			break;
		}
		return null;
	}

	private ByteArrayInputStream getDataGouvData(Locale locale, OpenDataActivity activity) throws IOException, JSONException {
		String datasetUrl = activity.buildDatasetUrl();
		if (datasetUrl == null) {
			addError(Labels.getLabel(locale, Labels.BadOpenDataDefinition));
			return null;
		}

		String resourceUrl = getResourceUrl(datasetUrl, activity);
		if (resourceUrl == null) {
			addError(Labels.getLabel(locale, Labels.CouldNotFindResource));
			return null;
		}

		return getResource(locale, resourceUrl);
	}

	private String getResourceUrl(String datasetUrl, OpenDataActivity activity) throws IOException, JSONException {
		String resourceId = activity.getDataGouv().getResourceId();

		DataGouvResource resource = OpenDataHelper.getResource(datasetUrl, resourceId);
		return resource.getUrl();
	}

	private ByteArrayInputStream getResource(Locale locale, String resourceUrl) throws MalformedURLException, IOException {
		try (InputStream is = new URL(resourceUrl).openStream()) {

			byte[] buff = new byte[8000];

			int bytesRead = 0;

			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			while ((bytesRead = is.read(buff)) != -1) {
				bao.write(buff, 0, bytesRead);
			}

			byte[] data = bao.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			
//			File targetFile = new File("D:/DATA/Test/VHub/Ouput/sub/targetFile2.xlsx");
//			if (!targetFile.exists()) {
//				targetFile.createNewFile();
//			}
//
//			java.nio.file.Files.copy(bis, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			return bis;
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToGetFileContent));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
			return null;
		}
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(null);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

	@Override
	protected void clearResources() {
	}
}
