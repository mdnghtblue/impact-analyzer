package com.galbo.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.galbo.plugin.util.ASTUtil;

public class SCMHandler extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// simulator commit to SCM
		saveAllASTs();
		System.out.println("simulated code commit");
		
		return null;
	}
	
	private void saveAllASTs()
	{
		IProject[] projects = ASTUtil.getProjects();
		System.out.println("projects: " + projects.length);
		try
		{
			for (IProject project : projects)
			{
				System.out.println("project: " + project.getName());
				IJavaProject javaProject = JavaCore.create(project);
				ASTStore.getInstance().putProject(project.getName(), javaProject);
				IPackageFragment[] packages = javaProject.getPackageFragments();
				System.out.println("packages: " + packages.length);
				for (IPackageFragment frag : packages)
				{
					if (frag.getKind() == IPackageFragmentRoot.K_SOURCE)
					{
						System.out.println(
								"package: " + frag.getElementName() + "; units: " + frag.getCompilationUnits().length);
						for (ICompilationUnit unit : frag.getCompilationUnits())
						{
							String key = unit.getPath().toString();
							CompilationUnit ast = ASTUtil.createAST(unit);
							ASTStore.getInstance().putUnit(key, unit);
							ASTStore.getInstance().putAST(key, ast);
							ASTStore.getInstance().putSource(key, unit.getSource());
						}
					}
				}
			}
		} catch (JavaModelException e)
		{
			e.printStackTrace();
		}
	}
}
