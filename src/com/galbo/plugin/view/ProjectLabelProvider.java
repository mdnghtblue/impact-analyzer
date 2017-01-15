package com.galbo.plugin.view;

import org.eclipse.jface.viewers.LabelProvider;

import com.galbo.plugin.refs.ClassRef;
import com.galbo.plugin.refs.MethodRef;
import com.galbo.plugin.refs.ProjectRef;
import com.galbo.plugin.refs.ProjectTree;
import com.galbo.plugin.refs.VariableRef;

public class ProjectLabelProvider extends LabelProvider
{

	@Override
	public String getText(Object element)
	{
		if (element instanceof ProjectTree)
		{
			return "Project Tree";
		}
		else if (element instanceof ProjectRef)
		{
			return ((ProjectRef) element).getName();
		}
		else if (element instanceof ClassRef)
		{
			return ((ClassRef) element).getName() + ".java";
		}
		else if (element instanceof MethodRef)
		{
			return ((MethodRef) element).getName() + "()";
		}
		else if (element instanceof VariableRef)
		{
			return ((VariableRef) element).getName();
		}
		
		return "test";
	}

}
