package com.galbo.plugin.view;


import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.galbo.plugin.refs.ClassRef;
import com.galbo.plugin.refs.MethodRef;
import com.galbo.plugin.refs.ProjectRef;
import com.galbo.plugin.refs.ProjectTree;
import com.galbo.plugin.util.PluginUtil;

public class ImpactAnalysisView extends ViewPart
{
	private Composite comp;
	private Composite _parent;
	private TreeViewer treeViewer;
	
	@Override
	public void createPartControl(Composite parent)
	{
		System.out.println("CREATING PART CONTROL");
		_parent = parent;
		//comp = new Composite(parent, SWT.NONE);		
		//comp.setLayout(new FillLayout());
		//comp.setLayoutData(new FillData());
		//comp.setBounds(0, 0, 800, 1000);
		//comp.setBackground(new Color(parent.getDisplay(), 100, 100, 100));
		//comp.pack();
		
	}

	@Override
	public void setFocus()
	{
		
	}
	
	public void updateView(Map<String, ProjectRef> refInfo)
	{
		System.out.println("Updating impact analysis view...(ref count: " + refInfo.size() + ")");
		
		disposeChildren(_parent);
		
		//System.out.println("children: " + comp.getChildren().length);
		
		ProjectTree projectTree = new ProjectTree();
		projectTree.setProjectRefs(refInfo);
		
		treeViewer = new TreeViewer(_parent, SWT.SINGLE);
		treeViewer.setContentProvider(new ProjectContentProvider());
		treeViewer.setLabelProvider(new ProjectLabelProvider());
		treeViewer.setInput(projectTree);
		
		treeViewer.expandAll();
		
		treeViewer.getTree().addListener(SWT.Expand, new Listener() {
		      public void handleEvent(Event e) {
		    	  
		        System.out.println("Expand={" + e.item + "}");
		        
		        TreeItem item = (TreeItem) e.item;
		        System.out.println("children: " + item.getItemCount());
		      }
		    });
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				
				System.out.println("double click: " + event.getViewer() + "; " + event.getSelection().getClass());
				
				if (event.getSelection() instanceof IStructuredSelection)
				{
					Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
					System.out.println("item class: " + item.getClass());
					
					IFile file = null;
					if (item instanceof ClassRef)
					{
						ClassRef classRef = (ClassRef) item;
						System.out.println("opening class: " + classRef.getName() + "; file: " + classRef.getFile());
						file = classRef.getFile();
					}
					else if (item instanceof MethodRef)
					{
						MethodRef methodRef = (MethodRef) item;
						file = methodRef.getFile();
					}
					
					
					if (file != null)
					{
						try
						{
							IDE.openEditor(PluginUtil.getWorkbenchPage(), file, "org.eclipse.jdt.ui.CompilationUnitEditor", true);
						} catch (PartInitException e)
						{
							e.printStackTrace();
						}
					}
					
				}
			}
		});

		
		//TreeViewer treeViewer = new TreeViewer(tree);
		//treeViewer.expandAll();
		_parent.pack(true);
	}
	
	public void disposeChildren(Composite comp)
	{
		for (Control control : comp.getChildren())
		{
			control.dispose();
		}
	}
	
}
