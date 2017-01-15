package com.galbo.plugin.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ParserStore
{
	private Map<String, CompilationUnit> unitMap = new HashMap<String, CompilationUnit>();
	private Map<String, IPath> pathMap = new HashMap<String, IPath>();
	
	public ParserStore(IJavaProject project)
	{
		try
		{
			for (IPackageFragment frag : project.getPackageFragments())
			{
				for (ICompilationUnit unit : frag.getCompilationUnits())
				{
					CompilationUnit cUnit = JavaParser.parse(unit.getSource());
					unitMap.put(unit.getHandleIdentifier(), cUnit);
					pathMap.put(cUnit.toString(), unit.getPath());
					System.out.println("JavaParser: adding: " + unit.getHandleIdentifier());
				}
					
			}
		} catch (JavaModelException e)
		{
			e.printStackTrace();
		}
	}
	
	public Map<String, CompilationUnit> getMap()
	{
		return unitMap;
	}
	
	public Map<String, IPath> getPathMap()
	{
		return pathMap;
	}
	
	
}
