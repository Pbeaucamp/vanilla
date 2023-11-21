package bpm.gateway.runtime2.transformation.vanilla;

import java.util.List;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.vanilla.VanillaCommentExtraction;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.Comment;

public class RunVanillaCommentExtraction extends RuntimeStep {

	private List<Comment> comments;
	private int currentRow = 0;

	public RunVanillaCommentExtraction(IRepositoryContext repositoryCtx, Transformation transformation, int bufferSize) {
		super(repositoryCtx, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		VanillaCommentExtraction transfo = (VanillaCommentExtraction) this.getTransformation();
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(getRepositoryContext());
		comments = repositoryApi.getDocumentationService().getComments(getRepositoryContext().getGroup().getId(), transfo.getItemId(), transfo.getType());
	}

	@Override
	public void performRow() throws Exception {

		if (currentRow < comments.size()) {
			Row row = RowFactory.createRow(this);

			Comment comment = comments.get(currentRow);

			row.set(0, comment.getComment());
			row.set(1, comment.getCreationDate());
			row.set(2, comment.getCreatorId());
			row.set(3, comment.getParentId());
			row.set(4, comment.getObjectId());

			writeRow(row);
			currentRow++;
		}
		else {
			if (!areInputsAlive()) {
				if (areInputStepAllProcessed()) {
					if (inputEmpty()) {
						setEnd();
					}
				}
			}
		}
	}

	@Override
	public void releaseResources() {
		comments = null;
	}

}
