package com.galbo.plugin.refs;

public class VariableRef
{
	private String name;
	private int count;
	
	public VariableRef(String vName, int vCount)
	{
		name = vName;
		count = vCount;
	}
	
	public String getName()
	{
		return name;
	}

	public int getCount()
	{
		return count;
	}
}
