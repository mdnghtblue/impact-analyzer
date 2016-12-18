package com.galbo.plugin.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTUtil
{
	private static int count;

	private ASTUtil()
	{

	}
	
	public static IProject[] getProjects()
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		return workspaceRoot.getProjects();
	}

	public static CompilationUnit createAST(ICompilationUnit unit) throws JavaModelException
	{
		//System.out.println("Creating AST for " + unit.getPath());
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu;
	}

	public static void printAST(CompilationUnit cu)
	{
		cu.accept(new ASTVisitor()
		{
			Set<String> names = new HashSet<String>();

			public boolean visit(VariableDeclarationFragment node)
			{
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out
						.println("Declaration of '" + name + "' at line " + cu.getLineNumber(name.getStartPosition()));
				return false; // do not continue to avoid usage info
			}
			
			public boolean visit(MethodDeclaration node)
			{
				System.out.println("Method declaration of '" + node.getName() + "' at line " + cu.getLineNumber(node.getStartPosition()));
				return true;
			}

			public boolean visit(SimpleName node)
			{
				if (this.names.contains(node.getIdentifier()))
				{
					System.out.println("Usage of '" + node + "' at line " + cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}

		});
	}

	public static ASTNode getAssignee(ASTNode node)
	{
		if (node == null)
		{
			System.out.println("returning null");
			return null;
		}
		
		System.out.println("node class: " + node.getClass());
		if (node instanceof Assignment)
		{
			ASTNode assignee = null;
			Assignment assignment = (Assignment) node;
			/*assignment.getLeftHandSide().accept(new ASTVisitor() {
				public boolean visit(SimpleName node)
				{
					System.out.println("Usage of '" + node + "'");
					return true;
				}
			});*/
			return assignment.getLeftHandSide();
		}
		else if (node instanceof VariableDeclarationFragment)
		{
			VariableDeclarationFragment frag = (VariableDeclarationFragment) node;
			System.out.println("frag binding: " + frag.resolveBinding());
			return frag;
		}
		else if (node instanceof VariableDeclarationStatement)
		{
			VariableDeclarationStatement stmt = (VariableDeclarationStatement) node;
			System.out.println("stmt binding: " + stmt.getType().resolveBinding());
			return stmt;
		}
		else if (node instanceof MethodDeclaration)
		{
			MethodDeclaration mi = (MethodDeclaration) node;
			System.out.println("method binding: " + mi.resolveBinding());
			return mi;
		}
		else
		{
			return getAssignee(node.getParent());
		}
	}
	
	public static void countReferences(ASTNode root, Set<IBinding> bindings)
	{
		for (IBinding binding : bindings)
		{
			count = 0;
			root.accept(new ASTVisitor() {
				public boolean visit(SimpleName node)
				{
					if (node.resolveBinding().equals(binding))
					{
						count++;
						//System.out.println(binding.getName() + " count: " + count);
					}
					return true;
				}
			});
			System.out.println(binding.getName() + ": " + count);
		}
	}
	
	public static IBinding getMethod(ASTNode node)
	{
		if (node == null)
		{
			return null;
		}
		
		while (!(node instanceof MethodDeclaration))
		{
			System.out.println("method node class: " + node.getClass());
			node = node.getParent();
			
			if (node == null)
			{
				break;
			}
		}
		
		if (node instanceof MethodDeclaration)
		{
			MethodDeclaration mi = (MethodDeclaration) node;
			return mi.resolveBinding();
		}
		
		return null;
		
	}
	
	public static IBinding getClass(ASTNode node)
	{
		if (node == null)
		{
			return null;
		}
		
		while (!(node instanceof TypeDeclaration))
		{
			System.out.println("type node class: " + node.getClass());
			node = node.getParent();
			
			if (node == null)
			{
				break;
			}
		}
		
		if (node instanceof TypeDeclaration)
		{
			TypeDeclaration td = (TypeDeclaration) node;
			return td.resolveBinding();
		}
		
		return null;
		
	}
}
