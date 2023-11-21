package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.CompressionActivity;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.utils.IOWriter;
import bpm.workflow.commons.beans.Result;

public class CompressionActivityRunner extends ActivityRunner<CompressionActivity> {

	public CompressionActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, CompressionActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
	}

	@Override
	protected void run(Locale locale) {
		String outputFile = activity.getOutputFile(parameters, variables);
		InputStream parentStream = null;
		if(result.isBigFile()) {
			int n = result.getInputStream().available();
			byte[] bytes = new byte[n];
			result.getInputStream().read(bytes, 0, n);
			String f = new String(bytes, StandardCharsets.UTF_8);
			try {
				parentStream = new FileInputStream(f);
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			parentStream = result.getInputStream();
		}

		addInfo(Labels.getLabel(locale, Labels.StartCompression));
		
		ZipEntry entry = new ZipEntry(result.getFileName());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos);) {
			zos.putNextEntry(entry);
			IOWriter.write(parentStream, zos, false, false);
			zos.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
			addError(e.getMessage());
			setResult(Result.ERROR);
			
			return;
		} catch (Exception e) {
			e.printStackTrace();
			addError(e.getMessage());
			setResult(Result.ERROR);
			
			return;
		}

		result.setFileName(outputFile);
		byte[] fileCompress = baos.toByteArray();
		result.setInputStream(new ByteArrayInputStream(fileCompress));

		addInfo(Labels.getLabel(locale, Labels.EndCompression));
		
		setResult(Result.SUCCESS);
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
	protected void clearResources() { }
}
