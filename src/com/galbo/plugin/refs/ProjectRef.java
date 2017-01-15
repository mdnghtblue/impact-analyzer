package com.galbo.plugin.refs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectRef
{
	private String name;
	private Map<String, ClassRef> classRefs = new HashMap<String, ClassRef>();
	
	public ProjectRef(String pName)
	{
		name = pName;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Map<String, ClassRef> getClassRefs()
	{
		return classRefs;
	}
	public void setClassRefs(Map<String, ClassRef> classRefs)
	{
		this.classRefs = classRefs;
	}
	
}
