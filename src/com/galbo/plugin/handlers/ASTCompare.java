package com.galbo.plugin.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ASTCompare
{
	private static final Set<Integer> IGNORED_NODES = new HashSet<>();
	private static Set<Integer> linesModified = new HashSet<>();
	private static Set<SimpleName> varsModified = new HashSet<>();
	private static Set<IBinding> bindings = new HashSet<>();
	private static boolean isEqual = true;
	
	
	public static void reset()
	{
		isEqual = true;
		IGNORED_NODES.clear();
		IGNORED_NODES.add(ASTNode.COMPILATION_UNIT);
		IGNORED_NODES.add(ASTNode.BLOCK);
		linesModified.clear();
		varsModified.clear();
		bindings.clear();
	}
	
	public static boolean isEqual()
	{
		return isEqual;
	}
	
	public static Set<Integer> getLinesModified()
	{
		return linesModified;
	}
	
	public static Set<IBinding> getBindings()
	{
		return bindings;
	}
	
	public static void printVarsModified()
	{
		System.out.println("vars: " + varsModified);
	}
	
	public static void printBindings()
	{
		for (IBinding binding : bindings)
		{
			System.out.println("  - " + binding.getName() + " (" + binding.getKey() + ")");
		}
	}
	
	private static void markLine(ASTNode oldNode, ASTNode node, int pos)
	{
		isEqual = false;
		if (IGNORED_NODES.contains(node.getNodeType()))
		{
			System.out.println("ignoring " + ASTNode.nodeClassForType(node.getNodeType()) + " node");
			return;
		}
		System.out.println("line: " + pos + "; node: " + ASTNode.nodeClassForType(node.getNodeType()) + "; " + node.getClass());
		linesModified.add(pos);
		
		System.out.println("var: " + ASTNode.nodeClassForType(oldNode.getParent().getNodeType()) + "; " + ASTNode.nodeClassForType(oldNode.getNodeType()));
		
		ASTNode assignee = ASTUtil.getAssignee(node);
		
		if (assignee != null)
		{
			System.out.println("assignee: " + assignee.getClass());
			if (assignee instanceof SimpleName)
			{
				SimpleName obj = (SimpleName) assignee;
				System.out.println("adding var: " + obj.getIdentifier() + "; binding: " + obj.resolveBinding());
				varsModified.add(obj);
				bindings.add(obj.resolveBinding());
			}
			else if (assignee instanceof VariableDeclarationFragment)
			{
				VariableDeclarationFragment obj = (VariableDeclarationFragment) assignee;
				bindings.add(obj.resolveBinding());
			}
			else if (assignee instanceof MethodDeclaration)
			{
				MethodDeclaration obj = (MethodDeclaration) assignee;
				bindings.add(obj.resolveBinding());
			}
			else if (assignee instanceof QualifiedName)
			{
				QualifiedName obj = (QualifiedName) assignee;
				System.out.println("adding qualified var: " + obj.getName().getFullyQualifiedName() + "; binding: " + obj.resolveBinding());
				varsModified.add(obj.getName());
				bindings.add(obj.getName().resolveBinding());
			}
		}
		
		/*if (node instanceof NumberLiteral)
		{
			NumberLiteral obj = (NumberLiteral) node;
			System.out.println("obj: " + obj.getToken());
		}*/
	}
	
	@SuppressWarnings("unchecked")
	static void traverse(ASTNode left, ASTNode right)
	{
		// if both are null, they are equal, but if only one, they aren't
		if (left == null && right == null)
		{
			return;
		} else if (left == null || right == null)
		{
			isEqual = false;
			return;
		}
		// if node types are the same we can assume that they will have the same
		// properties
		if (left.getNodeType() != right.getNodeType())
		{
			System.out.println("not the same type");
			markLine(left, right, right.getStartPosition());
		}
		List<StructuralPropertyDescriptor> props = left.structuralPropertiesForType();
		for (StructuralPropertyDescriptor property : props)
		{
			Object leftVal = left.getStructuralProperty(property);
			Object rightVal = right.getStructuralProperty(property);
			if (property.isSimpleProperty())
			{
				if (leftVal == null || rightVal == null)
				{
					continue;
				}
				
				// check for simple properties (primitive types, Strings, ...)
				// with normal equality
				if (!leftVal.equals(rightVal))
				{
					System.out.println("check normal equality");
					markLine(left, right, right.getStartPosition());
				}
			} else if (property.isChildProperty())
			{
				// recursively call this function on child nodes
				traverse((ASTNode) leftVal, (ASTNode) rightVal);
				/*if (!traverse((ASTNode) leftVal, (ASTNode) rightVal))
				{
					//System.out.println("recursive call on child nodes");
					//markLine(right, right.getStartPosition());
					return false;
				}*/
			} else if (property.isChildListProperty())
			{
				Iterator<ASTNode> leftValIt = ((Iterable<ASTNode>) leftVal).iterator();
				Iterator<ASTNode> rightValIt = ((Iterable<ASTNode>) rightVal).iterator();
				while (leftValIt.hasNext() && rightValIt.hasNext())
				{
					// recursively call this function on child nodes
					traverse(leftValIt.next(), rightValIt.next());
					/*if (!traverse(leftValIt.next(), rightValIt.next()))
					{
						//System.out.println("recursive call on child list nodes");
						//markLine(right, right.getStartPosition());
						return false;
					}*/
				}
				// one of the value lists have additional elements
				if (leftValIt.hasNext() || rightValIt.hasNext())
				{
					System.out.println("additional elements");
					markLine(left, right, right.getStartPosition());
					//return false;
				}
			}
		}
		//return true;
	}
}
