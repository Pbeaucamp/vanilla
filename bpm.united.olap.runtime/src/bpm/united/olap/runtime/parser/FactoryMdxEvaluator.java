package bpm.united.olap.runtime.parser;

import bpm.mdx.parser.result.RootItem;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class FactoryMdxEvaluator {

	public static IMdxEvaluator getMdxEvaluator(RootItem tree, ICubeInstance cubeInstance, IVanillaLogger logger, IRuntimeContext runtimeCtx, Projection projection, ICacheServer server) {
		if(projection == null) {
			return new MdxEvaluator(tree, cubeInstance, logger, runtimeCtx);
		}
		else {
			return new ProjectionMdxEvaluator(tree, cubeInstance, logger, runtimeCtx, projection, server);
		}
	}
	
}
