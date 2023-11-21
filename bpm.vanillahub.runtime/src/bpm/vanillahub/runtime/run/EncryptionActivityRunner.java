package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.utils.PGPFileProcessor;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.EncryptionActivity;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.workflow.commons.beans.Result;

public class EncryptionActivityRunner extends ActivityRunner<EncryptionActivity> {

	private FileManager fileManager;
	private List<Certificat> certificats;

	public EncryptionActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, EncryptionActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Certificat> certificats, FileManager fileManager) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.certificats = certificats;
		this.fileManager = fileManager;
	}

	@Override
	public void run(Locale locale) {
		boolean encryption = activity.isEncryption();
		Certificat certif = (Certificat) activity.getResource(certificats);
		String password = certif.getPassword();

		InputStream parentStream = null;
		if (result.isBigFile()) {
			int n = result.getInputStream().available();
			byte[] bytes = new byte[n];
			result.getInputStream().read(bytes, 0, n);
			String f = new String(bytes, StandardCharsets.UTF_8);
			try {
				parentStream = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			parentStream = result.getInputStream();
		}

		switch (certif.getTypeCertificat()) {
		case OPEN_PGP:
			encryptFilePGP(locale, encryption, certif.getFile(), password, parentStream);
			break;
		case PRIVATE_KEY:
			encryptFileWithPassword(locale, encryption, password, parentStream);
			break;
		default:
			break;
		}
	}

	private void encryptFilePGP(Locale locale, boolean encryption, String keyPath, String password, InputStream parentStream) {
		File keyFile = fileManager.getFile(keyPath);

		try {
			addInfo(Labels.getLabel(locale, Labels.EncryptionFilePublicKey));

			ByteArrayOutputStream bos = null;
			if (encryption) {
				bos = PGPFileProcessor.encrypt(keyFile, parentStream, false, true);
			}
			else {
				bos = PGPFileProcessor.decrypt(keyFile, parentStream, password);
			}
			byte[] fileCompress = bos.toByteArray();
			result.setInputStream(new ByteArrayInputStream(fileCompress));

			setResult(Result.SUCCESS);
		} catch (Throwable e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.EncryptionImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
		}
	}

	private void encryptFileWithPassword(Locale locale, boolean encryption, String password, InputStream parentStream) {
		try {
			addInfo(Labels.getLabel(locale, Labels.EncryptionFilePrivateKey));

			ByteArrayOutputStream bos = null;
			if (encryption) {
				bos = PGPFileProcessor.encrypt(parentStream, password, false, true);
			}
			else {
				bos = PGPFileProcessor.decrypt(parentStream, password);
			}
			byte[] fileCompress = bos.toByteArray();
			result.setInputStream(new ByteArrayInputStream(fileCompress));

			setResult(Result.SUCCESS);
		} catch (Throwable e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.EncryptionImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
		}
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(certificats);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(certificats);
	}

	@Override
	protected void clearResources() {
		if (result.isBigFile()) {
			ByteArrayInputStream newVersion = result.getInputStream();
			int n = newVersion.available();
			byte[] bytes = new byte[n];
			newVersion.read(bytes, 0, n);
			String f = new String(bytes, StandardCharsets.UTF_8); // Or any
																	// encoding.
			// String f = IOUtils.toString(newVersion);
			new File(f).delete();
		}
	}
}
