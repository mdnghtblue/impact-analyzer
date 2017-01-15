package com.galbo.plugin.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefInfo
{
	private String variableName;
	private int refCount;
	private Map<String, List<RefItem>> refItemMap = new HashMap<String, List<RefItem>>();
	
	public RefInfo()
	{
		
	}
	
	/*public void addRefItem(String className, String methodName)
	{
		if (!refItemMap.containsKey(className))
		{
			refItemMap.put(className, new ArrayList<RefItem>());
		}
		
		boolean alreadyAdded = false;
		for (RefItem item : refItemMap.get(className))
		{
			if (item.getMethodName().equals(methodName))
			{
				alreadyAdded = true;
			}
			
		}
		
		if (!alreadyAdded)
		{
			refItemMap.get(className).add(new RefItem(className, methodName));
		}
	}*/

	public String getVariableName()
	{
		return variableName;
	}

	public void setVariableName(String variableName)
	{
		this.variableName = variableName;
	}

	public int getRefCount()
	{
		return refCount;
	}

	public void setRefCount(int refCount)
	{
		this.refCount = refCount;
	}

	public Map<String, List<RefItem>> getRefItemMap()
	{
		return refItemMap;
	}
}
