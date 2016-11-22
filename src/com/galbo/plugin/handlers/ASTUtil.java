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
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ASTUtil
{

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
		System.out.println("Creating AST for " + unit.getPath());
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(unit);
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

}
