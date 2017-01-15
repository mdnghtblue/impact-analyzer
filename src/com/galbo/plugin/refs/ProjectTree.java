package com.galbo.plugin.refs;

import java.util.HashMap;
import java.util.Map;

public class ProjectTree
{
	private Map<String, ProjectRef> projectRefs = new HashMap<String, ProjectRef>();
	
	public ProjectTree()
	{
		
	}

	public Map<String, ProjectRef> getProjectRefs()
	{
		return projectRefs;
	}

	public void setProjectRefs(Map<String, ProjectRef> projectRefs)
	{
		this.projectRefs = projectRefs;
	}
	
}
