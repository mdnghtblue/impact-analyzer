package com.galbo.plugin.view;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class ImpactAnalysisView extends ViewPart
{
	private Label label;
	
	@Override
	public void createPartControl(Composite parent)
	{
		label = new Label(parent, 0);
		label.setText("");
	}

	@Override
	public void setFocus()
	{
		
	}
	
	public void updateLabel(String text)
	{
		label.setText(text);
	}

}
