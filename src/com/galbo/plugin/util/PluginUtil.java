package com.galbo.plugin.util;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class PluginUtil
{

	private PluginUtil()
	{

	}
	
	public static IWorkbenchPage getWorkbenchPage()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return page;
	}

	public static IFile getIFile(IPath path)
	{
		IFile file1 = ResourcesPlugin.getWorkspace().getRoot().getFile(path);  
		System.out.println("IFILE 1: " + file1);
		
		/*IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		IFile file2 =  ResourcesPlugin.getWorkspace().getRoot().getFile(path.makeRelativeTo(workspacePath));  
		System.out.println("IFILE 2: " + file2);
		
		IFile file3 = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		System.out.println("IFILE 3: " + file3);
		
		IFile file4 = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path.makeRelativeTo(workspacePath));
		System.out.println("IFILE 4: " + file4);*/
		
		return file1;
	}
	
	
	public static ITextEditor getEditor()
	{
		IWorkbenchPage page = getWorkbenchPage();

		IEditorPart editor = page.getActiveEditor();

		if (editor instanceof ITextEditor)
		{
			return (ITextEditor) editor;
		}

		return null;
	}
	

}
