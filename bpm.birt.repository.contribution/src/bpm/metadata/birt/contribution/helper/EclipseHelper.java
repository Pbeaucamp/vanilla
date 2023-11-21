package bpm.metadata.birt.contribution.helper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

public class EclipseHelper {

	/**
	 * Lists all current eclipse projects.
	 * 
	 * @return
	 */
	public static List<IProject> listProjects() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		return Arrays.asList(root.getProjects());
	}
	
	/**
	 * Fetches all .rptdesign files from said project
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static List<IFile> listRPTFiles(IProject project) throws CoreException {
		List<IFile> files = new ArrayList<IFile>();
		
		String endPattern = ".rptdesign";
		
		//project.cr
		
		for (IResource r : project.members()) {
			
			if (r.getType() == IResource.FILE &&
					r.getName().endsWith(endPattern)) {
				files.add((IFile)r);
			}
			else if (r.getType() == IResource.FOLDER) {
				files.addAll(recListRptFiles((IFolder) r, endPattern));
			}
		}
		
		return files;
	}
	
	/**
	 * Recursively fetch all file members matching end pattern
	 * 
	 * @param folder
	 * @return
	 * @throws CoreException
	 */
	private static List<IFile> recListRptFiles(IFolder folder, String endPattern) throws CoreException {
		List<IFile> files = new ArrayList<IFile>();
		
		for (IResource r : folder.members()) {
			if (r.getType() == IResource.FILE &&
					r.getName().endsWith(endPattern)) { 
				files.add((IFile) r);
			}
			else if (r.getType() == IResource.FOLDER) {
				files.addAll(recListRptFiles((IFolder) r, endPattern));
			}
		}
		
		return files;
	}
	
	/**
	 * Lookup a file with that name.
	 * 
	 * @param container, can be null, we ll be looking in root then.
	 * @param filename
	 * @return
	 * @throws CoreException 
	 */
	public static IFile findFile(IContainer container, String filename) throws CoreException {
		
		if (container == null) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			container = root;
		}
		//container.findMember(filename + ".properties").getName();
		
		for (IResource r : container.members()) {
			String name = r.getName().replace("." + r.getFileExtension(), "");
			
			if (r.getType() == IResource.FILE &&
					name.equals(filename)) {
				return (IFile) r;
			}
		}
		//IResource resource = container.findMember(filename);
		
		//System.out.println();
		
		return null;
	}
	
	public static void createFile(IPath path, String fileName, String content) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IContainer parent = (IContainer) root.findMember(path);
		
		IPath filePath = path.append(fileName);
		
		IFile newFile = root.getFile(filePath);
		//root.
		
		if (newFile.exists())
			newFile.setContents(IOUtils.toInputStream(content), IFile.FORCE, new NullProgressMonitor());
		else
			newFile.create(IOUtils.toInputStream(content), IFile.FORCE, new NullProgressMonitor());
	}
	
	public static IFile createEmptyFile(IPath path, String fileName) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IContainer parent = (IContainer) root.findMember(path);
		
		IPath filePath = path.append(fileName);
		
		IFile newFile = root.getFile(filePath);

		if (newFile.exists())
			newFile.setContents((InputStream)null, IFile.FORCE, new NullProgressMonitor());
		else
			newFile.create(null, IFile.FORCE, new NullProgressMonitor());
		
		return newFile;
	}
	
	public static void writeFile(IFile file, InputStream stream) throws CoreException {
		file.setContents(stream, IFile.FORCE, new NullProgressMonitor());
	}
}
