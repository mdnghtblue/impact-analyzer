package com.galbo.plugin.handlers;

import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;

public class ASTInfo
{
	private String name;
	private ASTNode node;
	private Set<IBinding> bindings;
	private ICompilationUnit unit;
	
	public ASTInfo(String nameInfo, ASTNode nodeInfo, Set<IBinding> bindingInfo, ICompilationUnit cUnit)
	{
		name = nameInfo;
		node = nodeInfo;
		bindings = bindingInfo;
		unit = cUnit;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ASTNode getNode()
	{
		return node;
	}

	public void setNode(ASTNode node)
	{
		this.node = node;
	}

	public Set<IBinding> getBindings()
	{
		return bindings;
	}

	public void setBindings(Set<IBinding> bindings)
	{
		this.bindings = bindings;
	}

	public ICompilationUnit getUnit()
	{
		return unit;
	}

	public void setUnit(ICompilationUnit unit)
	{
		this.unit = unit;
	}
	
	
}
