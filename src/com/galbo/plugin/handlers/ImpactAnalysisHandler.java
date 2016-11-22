package com.galbo.plugin.handlers;

import java.util.HashSet;
import java.util.Set;

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

import tracer.differencing.core.data.GlobalData;
import tracer.differencing.core.diff.DiffVisitor;
import tracer.differencing.core.pairs.ProjectPair;

/**
 * Diff/TS: A Tool for Fine-Grained Structural Change Analysis
 * http://ieeexplore.ieee.org/document/4656419/?part=1
 * @author stephaniegalbo
 *
 */
public class ImpactAnalysisHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		System.out.println("Providing impact analysis...");

		// for each file, compare the files
		IProject[] projects = ASTUtil.getProjects();
		
		try
		{
			for (IProject project : projects)
			{
				System.out.println("project: " + project.getName());
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragment[] packages = javaProject.getPackageFragments();
				
				IJavaProject oldProject = ASTStore.getInstance().getProject(project.getName());
				getEquality(oldProject, javaProject);
				
				for (IPackageFragment frag : packages)
				{
					if (frag.getKind() == IPackageFragmentRoot.K_SOURCE)
					{
						for (ICompilationUnit unit : frag.getCompilationUnits())
						{
							System.out.println("==========================================");
							String path = unit.getPath().toString();
							//String oldSource = ASTStore.getInstance().getSource(path);
							//String newSource = unit.getSource();
							CompilationUnit oldAST = ASTStore.getInstance().getAST(path);
							CompilationUnit newAST = ASTUtil.createAST(unit);
							ASTCompare.reset();
							boolean isEqual = ASTCompare.equals(oldAST, newAST);
							System.out.println("Path: " + path + " (is the same: " + isEqual + ")");
							Set<Integer> lineNumbers = convertLineNumbers(newAST, ASTCompare.getLinesModified());
							System.out.println("lines modified: " + lineNumbers);
							
							//getEquality(ASTStore.getInstance().getUnit(path), unit);
						}
					}
				}
			}
		} catch (JavaModelException e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
	private Set<Integer> convertLineNumbers(CompilationUnit cu, Set<Integer> lineNumbers)
	{
		Set<Integer> convertedNumbers = new HashSet<>();
		
		for (Integer num : lineNumbers)
		{
			System.out.println(num + " = " + cu.getLineNumber(num));
			convertedNumbers.add(cu.getLineNumber(num));
		}
		
		return convertedNumbers;
	}
	
	private void getEquality(IJavaProject proj1, IJavaProject proj2)
	{
		GlobalData.data.clear();
		GlobalData.atomicData.clear();
		GlobalData.overridden_rel.clear();
		
		GlobalData.proj1 = proj1;
		GlobalData.proj2 = proj2;

		GlobalData.projNames.add(proj1.getElementName());
		GlobalData.projNames.add(proj2.getElementName());
		
		//ComUnitPair pair = new ComUnitPair(unit1, unit2);
		ProjectPair pair = new ProjectPair(proj1, proj2);
		
		DiffVisitor visitor = new DiffVisitor();
		try
		{
			pair.accept(visitor);
			System.out.println("project names: " + GlobalData.projNames);
			System.out.println("global data: " + GlobalData.data);
			System.out.println("global atomic data: " + GlobalData.atomicData);
			System.out.println("added: " + DiffVisitor.added);
			System.out.println("match1: " + DiffVisitor.match1);
			System.out.println("match2: " + DiffVisitor.match2);
			System.out.println("deleted: " + DiffVisitor.deleted);
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
		
	}


}
