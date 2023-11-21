package bpm.vanilla.platform.core.runtime.components.listener.internal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.IVanillaListener;


public class EventManager {
	 private final ExecutorService pool;
	 public EventManager(int poolSize){
		 pool = Executors.newCachedThreadPool();
	 }
	 
	 
	 public void eventFired(IVanillaEvent event, List<IVanillaListener> listeners){
		 
		 for(IVanillaListener l : listeners){
			 try{
				 pool.execute(new EventHandler(event, l));
			 }catch(Throwable t ){
				 Logger.getLogger(getClass()).error(t.getMessage(), t);
			 }

		 }
		 
	 }
	 
	 
	public void shutdownAndAwaitTermination() {
		   pool.shutdown(); // Disable new tasks from being submitted
		   try {
		     // Wait a while for existing tasks to terminate
		     if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
		       pool.shutdownNow(); // Cancel currently executing tasks
		       // Wait a while for tasks to respond to being cancelled
		       if (!pool.awaitTermination(60, TimeUnit.SECONDS))
		           System.err.println("Pool did not terminate");
		     }
		   } catch (InterruptedException ie) {
		     // (Re-)Cancel if current thread also interrupted
		     pool.shutdownNow();
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		   }
		 }
	 
	 private class EventHandler implements Runnable{
		 private IVanillaEvent event;
		 private IVanillaListener listener;
		 
		 private EventHandler(IVanillaEvent event, IVanillaListener listener){
			 this.event = event;
			 this.listener = listener;
		 }
		 
		 public void run(){
			 listener.handleEvent(event);
		 }
	 }
}
