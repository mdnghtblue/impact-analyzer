package com.galbo.plugin.refs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

public class ClassRef
{
	private String name;
	private Map<String, MethodRef> methodRefs = new HashMap<String, MethodRef>();
	private IFile file;
	
	public ClassRef(String cName, IFile ifile)
	{
		name = cName;
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
	public Map<String, MethodRef> getMethodRefs()
	{
		return methodRefs;
	}
	public void setMethodRefs(Map<String, MethodRef> methodRefs)
	{
		this.methodRefs = methodRefs;
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
