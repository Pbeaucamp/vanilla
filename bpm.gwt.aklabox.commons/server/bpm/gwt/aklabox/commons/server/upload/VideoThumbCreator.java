package bpm.gwt.aklabox.commons.server.upload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import bpm.document.management.core.model.DocPages;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.server.security.CommonSession;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;

/**
 * Please use method on runtime size !
 */
@Deprecated
public class VideoThumbCreator {
	private final double SECONDS_BETWEEN_FRAMES;
	private String outputFilePrefix;
	// The video stream index, used to ensure we display frames from one and
	// only one video stream from the media container.
	private int mVideoStreamIndex = -1;
	// Time of last frame write
	private long mLastPtsWrite = Global.NO_PTS;
	private final long MICRO_SECONDS_BETWEEN_FRAMES;
	private CommonSession session;
	private Documents doc;
	private int versionNumber;
	private int pageNumber = 1;

	public VideoThumbCreator(String inputFilename, String outputFilePrefix) {
		SECONDS_BETWEEN_FRAMES = 15;
		MICRO_SECONDS_BETWEEN_FRAMES = (long) (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);
		this.outputFilePrefix = outputFilePrefix;
		this.outputFilePrefix = outputFilePrefix.replace(AklaboxConstant.LOCATION_VIDEO, AklaboxConstant.LOCATION_VIDEO_THUMBNAIL);
		IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
		// we want BufferedImages created in BGR 24bit color space
		mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		mediaReader.addListener(new ImageSnapListener());
		// read out the contents of the media file and
		// dispatch events to the attached listener
		while (mediaReader.readPacket() == null)
			;

	}

	public VideoThumbCreator(int versionNumber, Documents doc, CommonSession session, String inputFilename, String outputFilePrefix, int sec) {
		SECONDS_BETWEEN_FRAMES = sec;
		MICRO_SECONDS_BETWEEN_FRAMES = (long) (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);
		this.outputFilePrefix = outputFilePrefix;
		this.outputFilePrefix = outputFilePrefix.replace(AklaboxConstant.LOCATION_VIDEO, AklaboxConstant.LOCATION_VIDEO_THUMBNAIL);
		this.session = session;
		this.doc = doc;
		this.versionNumber = versionNumber;
		IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
		// we want BufferedImages created in BGR 24bit color space
		mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		mediaReader.addListener(new ImageSnapListener());
		// read out the contents of the media file and
		// dispatch events to the attached listener
		while (mediaReader.readPacket() == null)
			;
	}

	private class ImageSnapListener extends MediaListenerAdapter {

		public void onVideoPicture(IVideoPictureEvent event) {

			if (event.getStreamIndex() != mVideoStreamIndex) {
				// if the selected video stream id is not yet set, go ahead an
				// select this lucky video stream
				if (mVideoStreamIndex == -1) {
					mVideoStreamIndex = event.getStreamIndex();
				} // no need to show frames from this video stream
				else {
					return;
				}
			}

			// if uninitialized, back date mLastPtsWrite to get the very first
			// frame
			if (mLastPtsWrite == Global.NO_PTS) {
				mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
			}

			// if it's time to write the next frame
			if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {

				String outputFilename = null;
				try {
					outputFilename = dumpImageToFile(event.getImage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// indicate file written
				double seconds = ((double) event.getTimeStamp()) / Global.DEFAULT_PTS_PER_SECOND;
				System.out.printf("at elapsed time of %6.3f seconds wrote: %s\n", seconds, outputFilename);

				// update last write time
				mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
			}

		}

		private String dumpImageToFile(BufferedImage image) throws Exception {
			String output = "";
			try {
				
				if (SECONDS_BETWEEN_FRAMES == 15) {
					ImageIO.write(image, "png", new File(outputFilePrefix + ".png"));
				}
				else {
					List<DocPages> list = new ArrayList<DocPages>();
					output = outputFilePrefix + pageNumber + ".png";
					ImageIO.write(image, "png", new File(output));
					DocPages p = new DocPages();
					p.setDocVersion(versionNumber);
					p.setDocId(doc.getId());
					p.setPageNumber(pageNumber);
					p.setImagePath(output);
					pageNumber++;
					list.add(p);
					session.getAklaboxService().savePages(list);

				}
				return outputFilePrefix;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
