package com.galbo.plugin.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.galbo.plugin.refs.ClassRef;
import com.galbo.plugin.refs.MethodRef;
import com.galbo.plugin.refs.ProjectRef;
import com.galbo.plugin.refs.VariableRef;
import com.galbo.plugin.util.ASTUtil;
import com.galbo.plugin.util.PluginUtil;
import com.galbo.plugin.view.ImpactAnalysisView;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

/**
 * Diff/TS: A Tool for Fine-Grained Structural Change Analysis
 * http://ieeexplore.ieee.org/document/4656419/?part=1
 * 
 * @author stephaniegalbo
 *
 */
public class ImpactAnalysisHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		System.out.println("Providing impact analysis...");

		// for each file, compare the files
		IProject[] projects = ASTUtil.getProjects();

		List<ParserStore> parserList = new ArrayList<ParserStore>();

		List<ASTInfo> infoList = new ArrayList<ASTInfo>();
		try
		{
			for (IProject project : projects)
			{
				System.out.println("project: " + project.getName());
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragment[] packages = javaProject.getPackageFragments();

				ParserStore projectParser = new ParserStore(javaProject);
				parserList.add(projectParser);

				for (IPackageFragment frag : packages)
				{
					if (frag.getKind() == IPackageFragmentRoot.K_SOURCE)
					{
						for (ICompilationUnit unit : frag.getCompilationUnits())
						{
							System.out.println("==========================================");
							String path = unit.getPath().toString();
							// String oldSource =
							// ASTStore.getInstance().getSource(path);
							// String newSource = unit.getSource();
							CompilationUnit oldAST = ASTStore.getInstance().getAST(path);
							CompilationUnit newAST = ASTUtil.createAST(unit);
							ASTCompare.reset();
							ASTCompare.traverse(oldAST.getRoot(), newAST.getRoot());
							boolean isEqual = ASTCompare.isEqual();
							System.out.println("Path: " + path + " (is the same: " + isEqual + ")");
							// ASTCompare.printVarsModified();
							ASTCompare.printBindings();
							Set<IBinding> bindings = new HashSet<>();
							bindings.addAll(ASTCompare.getBindings());
							System.out.println("binding count: " + bindings.size());
							ASTUtil.countReferences(newAST, ASTCompare.getBindings());

							ASTInfo info = new ASTInfo(path, newAST, bindings, unit);
							infoList.add(info);
						}
					}
				}
			}
		} catch (JavaModelException e)
		{
			e.printStackTrace();
		}

		openView(infoList, parserList);

		return null;
	}

	private void openView(List<ASTInfo> astInfoList, List<ParserStore> parserStore)
	{
		System.out.println("============Opening view...");
		String viewID = "org.eclipse.ui.articles.views.impactview";

		Map<String, ProjectRef> projectRefs = new HashMap<String, ProjectRef>();
		StringBuilder sb = new StringBuilder();
		for (ASTInfo info : astInfoList)
		{
			System.out.println("analyzing info for: " + info.getName() + " (" + info.getBindings().size() + ")");
			for (IBinding binding : info.getBindings())
			{
				BindingVisitor visitor = new BindingVisitor(binding);
				info.getNode().accept(visitor);
				System.out.println("visiting binding: " + binding.getName());

				sb.append(" - Variable: ");
				sb.append(binding.getName());
				sb.append(" (");
				sb.append(visitor.getCount());
				sb.append(" reference(s) found)\n");

				System.out.println("====java parser====");
				RefItem refItem = new RefItem();
				System.out.println("IPATH: " + info.getUnit().getPath());
				IFile file = PluginUtil.getIFile(info.getUnit().getPath());
				System.out.println("IFILE: " + file);
				refItem.setFile(file);
				refItem.setProjectName(info.getUnit().getJavaProject().getElementName());
				javaParser(parserStore, binding, refItem);
				System.out.println("====end java parser====");

				addVariableRef(projectRefs, binding.getName(), visitor.getCount(), refItem);
			}
		}

		try
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			ImpactAnalysisView viewPart = (ImpactAnalysisView) page.findView(viewID);
			viewPart.updateView(projectRefs);

			System.out.println("label text: \n\n" + sb.toString());
			page.showView(viewID);
		} catch (PartInitException e)
		{
			e.printStackTrace();
		}
	}

	private void javaParser(List<ParserStore> storeList, IBinding binding, RefItem refItem)
	{
		try
		{
			System.out.println("binding: " + binding.getName());

			for (ParserStore store : storeList)
			{
				for (com.github.javaparser.ast.CompilationUnit compUnit : store.getMap().values())
				{
					System.out.println("IPACKAGE: " + compUnit.getPackageDeclaration().get() + "; ");
					compUnit.getNodesByType(SimpleName.class).stream()
							.filter(f -> f.getIdentifier().contains(binding.getName())).forEach(f ->
							{
								if (f.getAncestorOfType(MethodDeclaration.class).isPresent())
								{
									String className = "";
									if (f.getAncestorOfType(TypeDeclaration.class).isPresent())
									{
										TypeDeclaration typeDeclaration = f.getAncestorOfType(TypeDeclaration.class).get();
										className = typeDeclaration.getNameAsString();
									}
									String methodName = f.getAncestorOfType(MethodDeclaration.class).get()
											.getNameAsString();

									IFile file = PluginUtil.getIFile(store.getPathMap().get(compUnit.toString()));
									refItem.setFile(file);
									refItem.addInfo(className, methodName);
									
									System.out.println("+++ class: " + className + "; method: " + methodName + " (id: "
											+ f.getIdentifier() + ")");
								}
							});
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void addVariableRef(Map<String, ProjectRef> projectRefs, String varName, int refCount, RefItem refItem)
	{
		// add project if not added
		if (!projectRefs.containsKey(refItem.getProjectName()))
		{
			projectRefs.put(refItem.getProjectName(), new ProjectRef(refItem.getProjectName()));
		}

		Map<String, ClassRef> classRefs = projectRefs.get(refItem.getProjectName()).getClassRefs();
		
		// add class if not added
		classRefs.putAll(refItem.getClassNames());
		
		/*Map<String, MethodRef> methodRefs = classRefs.get(refItem.getClassName()).getMethodRefs();
		
		// add method if not added
		if (!methodRefs.containsKey(refItem.getMethodName()))
		{
			methodRefs.put(refItem.getMethodName(), new MethodRef(refItem.getMethodName()));
		}*/
		
		for (Entry<String, ClassRef> classEntry : classRefs.entrySet())
		{
			for (Entry<String, MethodRef> methodEntry : classEntry.getValue().getMethodRefs().entrySet())
			{
				Map<String, VariableRef> varRefs = methodEntry.getValue().getVariableRefs();
				
				// add variable if not added
				if (!varRefs.containsKey(varName))
				{
					varRefs.put(varName, new VariableRef(varName, refCount));
				}

			}
		}
		
		
	}
}
