package com.galbo.plugin.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;

public class BindingVisitor extends ASTVisitor
{
	private IBinding binding;
	private StringBuilder refString = new StringBuilder();
	private int count;
	private Set<String> methodsFound = new HashSet<String>();
	
	public BindingVisitor(IBinding bindingVar)
	{
		binding = bindingVar;
	}
	
	public boolean visit(SimpleName node)
	{
		System.out.println("VISITING: " + node.getFullyQualifiedName());
		if (node.resolveBinding().getKey().equals(binding.getKey()))
		{
			IBinding methodBinding = ASTUtil.getMethod(node);
			IBinding classBinding = ASTUtil.getClass(node);
			if (methodBinding == null || classBinding == null)
			{
				return false;
			}
			
			count++;
			
			if (methodsFound.contains(methodBinding.getName()))
			{
				return false;
			}
			
			methodsFound.add(methodBinding.getName());
					
			System.out.println("FOUND: " + binding.getJavaElement().getElementName() + " (" + binding.getKey() + ")");
			refString.append("    ");
			refString.append(classBinding.getName());
			refString.append(".java - ");
			refString.append(methodBinding.getName());
			refString.append("()\n");
			
			//System.out.println(binding.getName() + " count: " + count);
		}
		return true;
	}
	
	public String getReferences()
	{
		return refString.toString();
	}
	
	public int getCount()
	{
		return count;
	}
}
