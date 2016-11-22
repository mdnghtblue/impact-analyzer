package com.galbo.plugin.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public class ImpactAnalysisHandler2 extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		// IJavaModel javaModel = JavaCore.create(workspaceRoot);

		IProject[] projects = workspaceRoot.getProjects();
		System.out.println("projects: " + projects.length);
		try
		{
			for (IProject project : projects)
			{
				System.out.println("project: " + project.getName());
				IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
				System.out.println("packages: " + packages.length);
				for (IPackageFragment frag : packages)
				{
					if (frag.getKind() == IPackageFragmentRoot.K_SOURCE)
					{
						System.out.println(
								"package: " + frag.getElementName() + "; units: " + frag.getCompilationUnits().length);
						for (ICompilationUnit unit : frag.getCompilationUnits())
						{
							CompilationUnit parser = ASTUtil.createAST(unit);
							ASTUtil.printAST(parser);
						}
					}
				}
			}
		} catch (JavaModelException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private void printAST(ICompilationUnit unit)
	{

		String testCode = "public class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; }";
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(unit);
		// parser.setSource("/*abc*/".toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		// ASTNode node = parser.createAST(null);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor()
		{

			Set names = new HashSet();

			public boolean visit(VariableDeclarationFragment node)
			{
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out
						.println("Declaration of '" + name + "' at line " + cu.getLineNumber(name.getStartPosition()));
				return false; // do not continue to avoid usage info
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

	private void getPath(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		IEditorInput input = editor.getEditorInput();
		IPath path = ((FileEditorInput) input).getPath();

		System.out.println("path: " + path + "; " + path.getDevice() + "; " + path.getFileExtension());

	}

	private void parseDoc(ISelectionService service)
	{
		TextSelection selection = (TextSelection) service.getSelection();
		System.out.println("text: " + selection.getText());

	}

	private void logTree(ISelectionService service)
	{
		/*
		 * ISelectionService service = window.getSelectionService();
		 * 
		 * System.out.println("selection: " + service.getSelection());
		 * 
		 * if (service.getSelection() instanceof TextSelection) {
		 * parseDoc(service); } else { logTree(service); }
		 */

		if (service.getSelection() == null)
		{
			return;
		}

		ISelection selection = service.getSelection();
		if (selection instanceof IStructuredSelection)
		{
			if (selection instanceof ITreeSelection)
			{
				TreeSelection treeSelection = (TreeSelection) selection;
				TreePath[] treePaths = treeSelection.getPaths();
				TreePath treePath = treePaths[0];

				System.out.println("Last");
				Object lastSegmentObj = treePath.getLastSegment();
				Class<?> currClass = lastSegmentObj.getClass();
				while (currClass != null)
				{
					System.out.println("  Class=" + currClass.getName());
					Class<?>[] interfaces = currClass.getInterfaces();
					for (Class<?> interfacey : interfaces)
					{
						System.out.println("   I=" + interfacey.getName());
					}
					currClass = currClass.getSuperclass();
				}
				if (lastSegmentObj instanceof IAdaptable)
				{
					System.out.println("found adaptable class");
					IFile file = (IFile) ((IAdaptable) lastSegmentObj).getAdapter(IFile.class);
					System.out.println("file: " + file);
					if (file != null)
					{
						System.out.println("File=" + file.getName());
						String path = file.getRawLocation().toOSString();
						System.out.println("path: " + path);
					}
				}
			}
		}
	}
}
