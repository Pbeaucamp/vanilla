package bpm.vanilla.platform.core.runtime.ged;

import java.io.IOException;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;

public class AccessibleTopHitCollector extends AccessibleHitCollector {

	private TopScoreDocCollector tdc;
	private TopDocs topDocs = null;
	private int size;

	public AccessibleTopHitCollector(int size, boolean outOfOrder, boolean shouldScore) {
		// tdc = TopScoreDocCollector.create(size, outOfOrder);
		tdc = TopScoreDocCollector.create(size);
		this.shouldScore = shouldScore;
		this.outOfOrder = outOfOrder;
		this.size = size;
	}

	@Override
	public int getDocId(int pos) {
		if (topDocs == null) {
			topDocs = tdc.topDocs();
		}
		return topDocs.scoreDocs[pos].doc;
	}

	@Override
	public float getScore(int pos) {
		if (topDocs == null) {
			topDocs = tdc.topDocs();
		}
		return topDocs.scoreDocs[pos].score;
	}

	@Override
	public int getTotalHits() {
		return tdc.getTotalHits();
	}

	// @Override
	// public boolean acceptsDocsOutOfOrder() {
	// return tdc.acceptsDocsOutOfOrder();
	// }
	//
	// @Override
	// public void collect(int doc) throws IOException {
	// tdc.collect(doc);
	// }
	//
	// @Override
	// public void setNextReader(IndexReader r, int base) throws IOException {
	// this.docBase = base;
	// tdc.setNextReader(r, base);
	// }
	//
	// @Override
	// public void setScorer(Scorer scorer) throws IOException {
	// if (shouldScore) {
	// tdc.setScorer(scorer);
	// } else {
	// tdc.setScorer(NoScoringScorer.INSTANCE);
	// }
	// }

	@Override
	public void reset() {
		// tdc = TopScoreDocCollector.create(size, outOfOrder);
		tdc = TopScoreDocCollector.create(size);
		topDocs = null;
	}

	@Override
	public LeafCollector getLeafCollector(LeafReaderContext arg0) throws IOException {
		return tdc.getLeafCollector(arg0);
	}

}
