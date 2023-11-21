package bpm.gateway.runtime2.transformation.subtrans;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.gateway.core.AbrstractDigesterTransformation;
import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.Activator;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayDigester;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.SubTransformation;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RunSubTrans extends RuntimeStep {

	private List<RuntimeStep> substeps = new ArrayList<RuntimeStep>();
	private DocumentGateway doc;
	private Thread mainThread;

	private String finalStepName;
	private RuntimeStep finalStep;
	private Object adapter;

	public RunSubTrans(IRepositoryContext repContext, SubTransformation transformation, int bufferSize) {
		super(repContext, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.adapter = adapter;
		SubTransformation sub = (SubTransformation) getTransformation();

		if(getRepositoryContext() == null) {
			String filePath = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), sub.getDefinition());

			if(doc == null) {
				doc = new GatewayDigester(new File(filePath), Activator.getAdditionalDigesters()).getDocument(getRepositoryContext());
			}
		}
		else {
			if(doc == null) {

				IRepositoryApi sock = new RemoteRepositoryApi(getRepositoryContext());

				RepositoryItem it = sock.getRepositoryService().getDirectoryItem(Integer.parseInt(sub.getDefinition()));
				String xml = sock.getRepositoryService().loadModel(it);
				doc = new GatewayDigester(IOUtils.toInputStream(xml, "UTF-8"), new ArrayList<AbrstractDigesterTransformation>()).getDocument(getRepositoryContext());
			}
		}

		for(Variable v : doc.getResourceManager().getVariables(Variable.ENVIRONMENT_VARIABLE)) {
			Variable _v = getTransformation().getDocument().getResourceManager().getVariable(v.getName());
			v.setValue(_v.getValueAsString());
		}

		finalStepName = sub.getFinalStep();
		isInited = true;
		info(" inited");

	}

	private void buildModel() {
		// remove the final step as input
		for(RuntimeStep step : getOutputs()) {
			step.removeInput(finalStep);
		}

		substeps.clear();
		// create all runtimes
		for(Transformation t : doc.getTransformations()) {
			RuntimeStep rs = t.getExecutioner(null, RuntimeEngine.MAX_ROWS);
			substeps.add(rs);
			((AbstractTransformation) t).setDocumentGateway(doc);

			if(t.getName().equals(finalStepName)) {
				finalStep = rs;
			}
		}

		for(RuntimeStep rs : substeps) {
			rs.setLogger(getLogger());
		}

		for(RuntimeStep r : substeps) {

			for(Transformation t : r.getTransformation().getOutputs()) {
				r.addOutput(findRunFor(t, substeps));
			}

			for(Transformation t : r.getTransformation().getInputs()) {
				r.addInput(findRunFor(t, substeps));
			}

		}

		if(finalStep != null) {
			for(RuntimeStep rs : getOutputs()) {
				rs.addInput(finalStep);
				finalStep.addOutput(rs);
			}
		}

	}

	private RuntimeStep findRunFor(Transformation t, List<RuntimeStep> all) {
		for(RuntimeStep tr : all) {
			if(tr.getTransformation() == t) {
				return tr;
			}
		}
		return null;
	}

	private boolean isStillRunning() {
		for(RuntimeStep t : substeps) {
			if(t.isAlive() && !t.isInterrupted()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void performRow() throws Exception {
		if(!inputs.isEmpty()) {
			if(areInputStepAllProcessed()) {
				if(inputEmpty()) {
					setEnd();
				}
			}

			if(isEnd() && inputEmpty()) {
				return;
			}

			if(!isEnd() && inputEmpty()) {
				try {
					Thread.sleep(10);
					return;
				} catch(Exception e) {

				}
			}
		}

		buildModel();

		mainThread = new Thread() {
			public void run() {

				for(RuntimeStep tr : substeps) {
					try {
						tr.init(adapter);
					} catch(Exception e1) {
						e1.printStackTrace();
					}
				}

				for(RuntimeStep tr : substeps) {
					tr.start();
				}
				while(isStillRunning()) {
					try {
						sleep(2000);

					} catch(InterruptedException e) {
						interrupt();
					}
				}

				for(RuntimeStep tr : substeps) {
					tr.releaseResources();

				}

			}
		};
		mainThread.setName(getTransformation().getName());
		if(!inputs.isEmpty()) {
			Row r = readRow();
			setParameters(r);
		}

		mainThread.start();
		mainThread.join();

		if(inputs.isEmpty()) {
			setEnd();
		}
		writedRows++;
	}

	@Override
	public void releaseResources() {
		for(RuntimeStep rs : substeps) {
			if(rs.isAlive()) {
				try {
					rs.interrupt();
				} catch(Exception e) {

				}
			}
			rs.releaseResources();
		}
		if(mainThread != null && mainThread.isAlive()) {
			try {
				mainThread.interrupt();
			} catch(Exception e) {

			}

		}
		info(" resources released");
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private void setParameters(Row row) throws Exception {
		SubTransformation sub = (SubTransformation) getTransformation();

		for(int i = 0; i < row.getMeta().getSize(); i++) {

			if(sub.getMappingFor(i) != null) {
				Object o = row.get(i);

				if(o != null) {
					if(o instanceof Date) {
						doc.getParameter(sub.getMappingFor(i)).setValue(o.toString());
						doc.getResourceManager().getParameter(sub.getMappingFor(i)).setValue(sdf.format((Date) o));
					}
					else {
						doc.getParameter(sub.getMappingFor(i)).setValue(o.toString());
						doc.getResourceManager().getParameter(sub.getMappingFor(i)).setValue(o.toString());
					}
				}

			}

		}

	}
}
