package com.galbo.plugin.util;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

public class TreeClickListener implements IDoubleClickListener
{
	private TreeViewer treeViewer;
	public TreeClickListener()
	{
		
	}
	
	@Override
	  public void doubleClick(final DoubleClickEvent event)
	  {
	    final IStructuredSelection selection = (IStructuredSelection)event.getSelection();
	    if (selection == null || selection.isEmpty())
	      return;

	    final Object sel = selection.getFirstElement();

	    final ITreeContentProvider provider = (ITreeContentProvider)treeViewer.getContentProvider();

	    if (!provider.hasChildren(sel))
	      return;

	    if (treeViewer.getExpandedState(sel))
	      treeViewer.collapseToLevel(sel, AbstractTreeViewer.ALL_LEVELS);
	    else
	      treeViewer.expandToLevel(sel, 1);
	  }
}
