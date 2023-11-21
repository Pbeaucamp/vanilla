package bpm.gateway.runtime2.transformation.nosql;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.nosql.SparkTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;

public class RunSpark extends RuntimeStep {

	private Runnable thread;

	public RunSpark(IRepositoryContext repositoryCtx, Transformation transformation, int bufferSize) {
		super(repositoryCtx, transformation, bufferSize);
	}

	@Override
	public void performRow() throws Exception {
		thread.run();
	}

	@Override
	public void releaseResources() {
		thread = null;
	}

	@Override
	public void init(Object adapter) throws Exception {
		ClassLoader loader = URLClassLoader.newInstance(new URL[] { new URL(((SparkTransformation) this.getTransformation()).getJarPath()) }, getClass().getClassLoader());
		Class<?> clazz = Class.forName(((SparkTransformation) this.getTransformation()).getClassName(), true, loader);
		Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);
		Constructor<? extends Runnable> ctor = runClass.getConstructor();
		thread = ctor.newInstance();

		isInited = true;

	}

}
