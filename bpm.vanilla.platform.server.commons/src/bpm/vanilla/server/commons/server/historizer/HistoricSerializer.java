package bpm.vanilla.server.commons.server.historizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.server.commons.server.tasks.ITask;

import com.thoughtworks.xstream.XStream;

public class HistoricSerializer {

	private List<ITask> currentTasks = Collections.synchronizedList(new ArrayList<ITask>(25));
	private File historizationFolder;
	private Semaphore locker = new Semaphore(1);

	public HistoricSerializer(File historizationFolder) {
		this.historizationFolder = historizationFolder;
		if (!this.historizationFolder.exists()) {
			this.historizationFolder.mkdirs();
		}

		Logger.getLogger(getClass()).info("Deleting historized tasks ...");
		int count = 0;
		for (String s : historizationFolder.list()) {
			if (!s.contains("resume")) {
				File f = new File(historizationFolder, s);
				if (f.exists() && f.isFile()) {
					if (f.delete()) {
						count++;
					}
				}
			}
		}
		Logger.getLogger(getClass()).info("Deleted " + count + " historized tasks");

	}

	public void addTask(ITask task) throws Exception {
		currentTasks.add(task);
		if (currentTasks.size() >= 25) {
			flush();
		}
	}

	public void flush() throws Exception {
//		locker.acquire();
		if (!historizationFolder.exists()) {
			historizationFolder.mkdirs();
		}
		try {
			if (currentTasks.isEmpty()) {
				return;
			}
			File f = new File(historizationFolder, UUID.randomUUID().toString());

			while (f.exists()) {
				f = new File(historizationFolder, UUID.randomUUID().toString());
			}
			synchronized (currentTasks) {
				FileOutputStream fos = new FileOutputStream(f);
				String xml = new XStream().toXML(currentTasks);
				IOWriter.write(IOUtils.toInputStream(xml, "UTF-8"), fos, true, true);
				Logger.getLogger(getClass()).info("Historized " + currentTasks.size() + " tasks into " + f.getAbsolutePath());
				currentTasks.clear();
			}
		} finally {
//			locker.release();
		}

	}

	/**
	 * the given ois must read a List<ITask>
	 * 
	 * @param ois
	 * @return
	 * @throws Exception
	 */
	public List<ITask> readHistorizedTasks() throws Exception {
		List<ITask> result = new ArrayList<ITask>();
		locker.acquire();
		result.addAll(currentTasks);
		try {
			for (String s : historizationFolder.list()) {

				if (!s.contains("resume")) {
					File f = new File(historizationFolder, s);
					if (!f.exists() || !f.isFile()) {
						continue;
					}

					FileInputStream fis = new FileInputStream(f);
					try {
						List<ITask> l = (List<ITask>) new XStream().fromXML(fis);

						for (ITask t : l) {
							t.getTaskState();
						}

						result.addAll(l);
					} catch (Exception ex) {
						ex.printStackTrace();
						Logger.getLogger(getClass()).error("failed to read task historic on file " + f.getAbsolutePath() + " : " + ex.getMessage(), ex);
					} finally {
						if (fis != null) {
							fis.close();
						}
					}
				}

			}
		} catch (Exception e) {
			//
		} finally {
			locker.release();
		}

		return result;

	}
}
