package bpm.gateway.core.transformations.nosql;

import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.nosql.RunSpark;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SparkTransformation extends AbstractTransformation implements Spark {

	private String jobName;
	
	private String jarPath;
	private String className;
	
	@Override
	public Transformation copy() {
		SparkTransformation copy = new SparkTransformation();
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setTemporaryFilename(getTemporaryFilename());
		copy.setTemporarySpliterChar(getTemporarySpliterChar());

		copy.setJobName(jobName);
		copy.setJarPath(jarPath);
		copy.setClassName(className);

		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSpark(null, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		// TODO Auto-generated method stub
		
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
