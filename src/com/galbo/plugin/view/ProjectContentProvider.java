package com.galbo.plugin.view;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.galbo.plugin.refs.ClassRef;
import com.galbo.plugin.refs.MethodRef;
import com.galbo.plugin.refs.ProjectRef;
import com.galbo.plugin.refs.ProjectTree;
import com.galbo.plugin.refs.VariableRef;

public class ProjectContentProvider implements ITreeContentProvider
{

	@Override
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		//System.out.println("getting childen");
		if (parentElement instanceof ProjectTree)
		{
			ProjectTree projectTree = (ProjectTree) parentElement;
			ProjectRef[] projectRefs = new ProjectRef[projectTree.getProjectRefs().size()];
			int i = 0;
			for (Entry<String, ProjectRef> entry : projectTree.getProjectRefs().entrySet())
			{
				projectRefs[i] = entry.getValue();
				i++;
			}
			//System.out.println("project refs: " + projectRefs.length);
			return projectRefs;
		}
		if (parentElement instanceof ProjectRef)
		{
			ProjectRef projectRef = (ProjectRef) parentElement;
			ClassRef[] classRefs = new ClassRef[projectRef.getClassRefs().size()];
			int i = 0; 
			for (Entry<String, ClassRef> entry : projectRef.getClassRefs().entrySet())
			{
				classRefs[i] = entry.getValue();
				i++;
			}
			//System.out.println("class refs: " + classRefs.length);
			return classRefs;
		}
		if (parentElement instanceof ClassRef)
		{
			ClassRef classRef = (ClassRef) parentElement;
			MethodRef[] methodRefs = new MethodRef[classRef.getMethodRefs().size()];
			int i = 0;
			for (Entry<String, MethodRef> entry : classRef.getMethodRefs().entrySet())
			{
				methodRefs[i] = entry.getValue();
				i++;
			}
			//System.out.println("method refs: " + methodRefs.length);
			return methodRefs;
		}
		if (parentElement instanceof MethodRef)
		{
			MethodRef methodRef = (MethodRef) parentElement;
			VariableRef[] varRefs = new VariableRef[methodRef.getVariableRefs().size()];
			int i = 0;
			for (Entry<String, VariableRef> entry : methodRef.getVariableRefs().entrySet())
			{
				varRefs[i] = entry.getValue();
				i++;
			}
			//System.out.println("var refs: " + varRefs.length);
			return varRefs;
		}
			
		return new ProjectRef[0];
	}

	@Override
	public Object getParent(Object element)
	{
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

}
