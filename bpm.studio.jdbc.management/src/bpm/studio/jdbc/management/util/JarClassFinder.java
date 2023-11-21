package bpm.studio.jdbc.management.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * Helper class to load dynamically Jar files and get the Classes implementing
 * java.sql.Driver from the given jarFilePath
 * @author ludo
 *
 */
public class JarClassFinder {
	private static List<URLClassLoader> jarClassLoaders = new ArrayList<URLClassLoader>();
	
	public static List<Class<? extends Driver>> getClassImplementingJdbcDriver(String jarFilePath) throws Exception{
		
		File f = new File(jarFilePath);
		if (!f.exists()){
			throw new Exception("The JarFile " + jarFilePath + " doesn't exist");
		}
		URL jarurl = f.toURL();
		
		
		for(URLClassLoader ucl : jarClassLoaders){
			for(URL u : ucl.getURLs()){
				if (u.equals(jarurl)){
					return getClasses(ucl, jarFilePath);
				}
			}
		}
		
		URLClassLoader ucl = loadJar(jarurl);
		return getClasses(ucl, jarFilePath);
	}
	
	private static URLClassLoader loadJar(URL jarurl) throws Exception{
		
		URLClassLoader child = new URLClassLoader (new URL[]{jarurl}, JarClassFinder.class.getClassLoader());
		jarClassLoaders.add(child);
		return child;
	}
	
	private  static List<Class<? extends Driver>> getClasses(URLClassLoader ucl, String jarFilePath) throws Exception{
		List<Class<? extends Driver>> list = new ArrayList<Class<? extends Driver>>();
		
		JarInputStream jarFile = null;
		
		try {
			jarFile = new JarInputStream (new FileInputStream (jarFilePath));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception("Unable to load file : " + e.getMessage(), e);
		} 
		JarEntry jarEntry;

		
		while( (jarEntry = jarFile.getNextJarEntry ()) != null){
			if (jarEntry.getName().endsWith (".class")){
				String className = jarEntry.getName();
				className = className.substring(0, className.lastIndexOf(".class")).replaceAll("/", "\\.");
				
				Class<?> c;
				try {
					c = Class.forName(className, true, ucl);
					if (Driver.class.isAssignableFrom(c)){
						list.add((Class<? extends Driver>)c);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
}
