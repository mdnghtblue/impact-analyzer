package com.galbo.plugin.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTStore
{
	private static final Map<String, ICompilationUnit> UNIT_MAP = new HashMap<>();
	private static final Map<String, CompilationUnit> AST_MAP = new HashMap<>();
	private static final Map<String, String> SOURCE_MAP = new HashMap<>();
	private static final Map<String, IJavaProject> PROJECT_MAP = new HashMap<>();
	
	private static ASTStore INSTANCE;
	
	private ASTStore()
	{
	}
	
	public static ASTStore getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ASTStore();
		}
		
		return INSTANCE;
	}
	
	public void putUnit(String key, ICompilationUnit unit)
	{
		UNIT_MAP.put(key, unit);
	}
	
	public ICompilationUnit getUnit(String key)
	{
		return UNIT_MAP.get(key);
	}
	
	public void putAST(String key, CompilationUnit parser)
	{
		AST_MAP.put(key, parser);
	}
	
	public CompilationUnit getAST(String key)
	{
		return AST_MAP.get(key);
	}
	
	public void putSource(String key, String source)
	{
		SOURCE_MAP.put(key, source);
	}
	
	public String getSource(String key)
	{
		return SOURCE_MAP.get(key);
	}
	
	public void putProject(String key, IJavaProject source)
	{
		System.out.println("Saving project: " + key);
		PROJECT_MAP.put(key, source);
	}
	
	public IJavaProject getProject(String key)
	{
		System.out.println("Retrieving project: " + key);
		return PROJECT_MAP.get(key);
	}
	
}
