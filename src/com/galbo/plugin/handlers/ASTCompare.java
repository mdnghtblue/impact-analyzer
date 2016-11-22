package com.galbo.plugin.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

public class ASTCompare
{
	private static final Set<Integer> IGNORED_NODES = new HashSet<>();
	private static Set<Integer> linesModified = new HashSet<>();
	
	public static void reset()
	{
		IGNORED_NODES.clear();
		IGNORED_NODES.add(ASTNode.COMPILATION_UNIT);
		IGNORED_NODES.add(ASTNode.BLOCK);
		linesModified.clear();
	}
	
	public static Set<Integer> getLinesModified()
	{
		return linesModified;
	}
	
	private static void markLine(ASTNode node, int pos)
	{
		if (IGNORED_NODES.contains(node.getNodeType()))
		{
			System.out.println("ignoring " + ASTNode.nodeClassForType(node.getNodeType()) + " node");
			return;
		}
		System.out.println("line: " + pos + "; node: " + ASTNode.nodeClassForType(node.getNodeType()));
		linesModified.add(pos);
	}
	
	@SuppressWarnings("unchecked")
	static boolean equals(ASTNode left, ASTNode right)
	{
		// if both are null, they are equal, but if only one, they aren't
		if (left == null && right == null)
		{
			return true;
		} else if (left == null || right == null)
		{
			return false;
		}
		// if node types are the same we can assume that they will have the same
		// properties
		if (left.getNodeType() != right.getNodeType())
		{
			System.out.println("not the same type");
			markLine(right, right.getStartPosition());
			return false;
		}
		List<StructuralPropertyDescriptor> props = left.structuralPropertiesForType();
		for (StructuralPropertyDescriptor property : props)
		{
			Object leftVal = left.getStructuralProperty(property);
			Object rightVal = right.getStructuralProperty(property);
			if (property.isSimpleProperty())
			{
				// check for simple properties (primitive types, Strings, ...)
				// with normal equality
				if (!leftVal.equals(rightVal))
				{
					System.out.println("check normal equality");
					markLine(right, right.getStartPosition());
					return false;
				}
			} else if (property.isChildProperty())
			{
				// recursively call this function on child nodes
				if (!equals((ASTNode) leftVal, (ASTNode) rightVal))
				{
					//System.out.println("recursive call on child nodes");
					//markLine(right, right.getStartPosition());
					return false;
				}
			} else if (property.isChildListProperty())
			{
				Iterator<ASTNode> leftValIt = ((Iterable<ASTNode>) leftVal).iterator();
				Iterator<ASTNode> rightValIt = ((Iterable<ASTNode>) rightVal).iterator();
				while (leftValIt.hasNext() && rightValIt.hasNext())
				{
					// recursively call this function on child nodes
					if (!equals(leftValIt.next(), rightValIt.next()))
					{
						//System.out.println("recursive call on child list nodes");
						//markLine(right, right.getStartPosition());
						return false;
					}
				}
				// one of the value lists have additional elements
				if (leftValIt.hasNext() || rightValIt.hasNext())
				{
					System.out.println("additional elements");
					markLine(right, right.getStartPosition());
					return false;
				}
			}
		}
		return true;
	}
}
