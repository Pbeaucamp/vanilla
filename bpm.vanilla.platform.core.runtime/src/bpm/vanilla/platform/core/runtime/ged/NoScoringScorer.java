package bpm.vanilla.platform.core.runtime.ged;

import java.io.IOException;

import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

public class NoScoringScorer extends Scorer {
	public static final NoScoringScorer INSTANCE = new NoScoringScorer();

	
	
	protected NoScoringScorer() {
		super((Weight)null);
		//original :
		//super(null);
		//super()
	}

	@Override
	public float score() throws IOException {
		return 1.0f;
	}

	@Override
	public int advance(int doc) throws IOException {
		return 0;
	}

	@Override
	public int docID() {
		return 0;
	}

	@Override
	public int nextDoc() throws IOException {
		return 0;
	}

	@Override
	public int freq() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long cost() {
		// TODO Auto-generated method stub
		return 0;
	}

}
