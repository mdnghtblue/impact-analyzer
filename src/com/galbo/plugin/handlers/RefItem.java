package com.galbo.plugin.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import com.galbo.plugin.refs.ClassRef;
import com.galbo.plugin.refs.MethodRef;

public class RefItem
{
	private String projectName;
	private Map<String, ClassRef> classNames = new HashMap<String, ClassRef>();
	//private List<String> methodNames = new ArrayList<String>();
	private IFile file;
	private int lineNumber;
	
	public RefItem()
	{
		
	}
	
	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public Map<String, ClassRef> getClassNames()
	{
		return classNames;
	}

	public void setClassNames(Map<String, ClassRef> classNames)
	{
		this.classNames = classNames;
	}

	public IFile getFile()
	{
		return file;
	}

	public void setFile(IFile file)
	{
		this.file = file;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}
	
	public void addInfo(String className, String methodName)
	{
		if (!classNames.containsKey(className))
		{
			classNames.put(className, new ClassRef(className, file));
		}
			
		ClassRef classRef = classNames.get(className);
		
		if (!classRef.getMethodRefs().containsKey(methodName))
		{
			classRef.getMethodRefs().put(methodName, new MethodRef(methodName, file));
		}
	}
}
