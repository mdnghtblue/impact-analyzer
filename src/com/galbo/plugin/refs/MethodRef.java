package com.galbo.plugin.refs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

public class MethodRef
{
	private String name;
	private Map<String, VariableRef> variableRefs = new HashMap<String, VariableRef>();
	private IFile file;
	
	public MethodRef(String mName, IFile ifile)
	{
		name = mName;
		file = ifile;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<String, VariableRef> getVariableRefs()
	{
		return variableRefs;
	}

	public void setVariableRefs(Map<String, VariableRef> variableRefs)
	{
		this.variableRefs = variableRefs;
	}

	public IFile getFile()
	{
		return file;
	}

	public void setFile(IFile file)
	{
		this.file = file;
	}
	
}
