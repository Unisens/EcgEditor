/**
 * This class is the manages all data associated with one MedicalClass
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Forms.Form_Main;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.MedicalClass;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.events.MySelectionEvent;
import de.fzi.ekgeditor.events.MySelectionListener;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.TimeUtil;

public class Listbox_MedicalClasses implements MySelectionListener, SignalListener
{

	/** link to medical classes list */
	private MedicalClassList mClasses;

	/** our combobox to select the medical class */
	public Combo listOfClasses;

	/** Which medicalClass is currently selected */
	private MedicalClass currentSelection = null;

	/**
	 * This method is called if some selection event occurs (Listener-Handler)
	 * 
	 * @param e
	 *            Event attributes
	 */
	public void MyselectionChanged(MySelectionEvent e)
	{
		resetControls();
	}

	/**
	 * This method is called if some signalChange event occurs
	 * (Listener-Handler)
	 * 
	 * @param e
	 *            Event attributes
	 */
	public void signalChanged(SignalEvent e)
	{
		resetControls();
	}

	private SignalViewerModel dataModel = null;

	private Text textComment;

	/**
	 * Standard constructor
	 * 
	 * @param shell
	 *            parent
	 */
	public Listbox_MedicalClasses(Composite shell, SignalViewerModel dataModel)
	{
		this.dataModel = dataModel;
		// Load MedicalClasses from XML-File:
		mClasses = new MedicalClassList();

		Group g = new Group(shell, SWT.SHADOW_ETCHED_IN | SWT.LEFT);
		g.setLayout(new FillLayout());
		g.setText("Signalklasse:");

		listOfClasses = new Combo(g, SWT.LEFT | SWT.READ_ONLY);
		listOfClasses.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ListBox_Classes_Changed(e);
			}
		});

		listOfClasses.add(Constants.pleaseSelect);
		// Put them in our ComboBox
		for (MedicalClass m : mClasses.mClasses)
		{
			listOfClasses.add(m.title);
		}

		// Select First item (please select)
		listOfClasses.setText(Constants.pleaseSelect);
		listOfClasses.setEnabled(false);
		listOfClasses.setVisibleItemCount(12);

		dataModel.selection.addSelectionListener(this);
		dataModel.signalModel.addSignalListener(this);

		textComment = new Text(g, SWT.BORDER);
		textComment.setText(Constants.comment);
		textComment.setEnabled(false);
		textComment.addFocusListener(new FocusListener()
		{
			// Delete "Kommentar", if focused
			public void focusGained(FocusEvent arg0)
			{
				if (textComment.getText().equalsIgnoreCase(Constants.comment))
				{
					textComment.setText("");
				}
			}

			public void focusLost(FocusEvent arg0)
			{
				if (textComment.getText().equalsIgnoreCase(""))
				{
					textComment.setText(Constants.comment);
				}
			}
			
		});

		/*
		 * GridData g = new GridData(100,100);
		 * 
		 * listOfClasses.setLayoutData(g); //listOfClasses.setBackground(new
		 * Color(display,100,100,100));
		 * 
		 */
	}

	/** reset all controls for listbox MedicalClasses */
	private void resetControls()
	{
		boolean b = Common.getInstance().signalModel.isSignalLoaded();
		b = b & (dataModel.selection.getSelectionStart() != Constants.notSelected);

		Form_Main mf = Common.getInstance().mainForm;
		if (mf.button_saveSequence != null)
		{
			mf.button_saveSequence.setEnabled(false);
		}
		if (mf.button_cancelSequence != null)
		{
			mf.button_cancelSequence.setEnabled(false);
		}
		if (mf.button_automaticSaveSequence != null)
		{
			mf.button_automaticSaveSequence.setEnabled(false);
		}
		listOfClasses.setEnabled(b);
		listOfClasses.setText(Constants.pleaseSelect);
				textComment.setEnabled(b);
	}

	/** Return the current selected medicalClass */
	public MedicalClass getCurrentSelection()
	{
		return currentSelection;
	}

	/**
	 * This method is called when some new medical class was selected
	 * 
	 * @param e
	 *            Event attributes
	 */
	public void ListBox_Classes_Changed(SelectionEvent e)
	{
		int iSelection = 0;

		if (e.widget != null)
		{
			currentSelection = null;
			try
			{
				String titleTxt = ((Combo) e.widget).getText();
				if (titleTxt.compareTo(Constants.pleaseSelect) != 0)
				{
					currentSelection = mClasses.FindMedicalClass(titleTxt);
				} else
					currentSelection = null;
			} catch (Exception ex)
			{
				Common.getInstance().ShowErrorBox(Constants.error, ex.getMessage());
			}

			if (currentSelection == null)
			{
				// Common.getInstance().ShowErrorBox(Constants.error,Constants.internError);

				// Some error occured, go back to selection
				listOfClasses.setText(Constants.pleaseSelect);
			} else
			{
				iSelection = listOfClasses.getSelectionIndex();
				Form_Main mf = Common.getInstance().mainForm;
				mf.label_minimumLength.setText(TimeUtil.getTimeString(currentSelection.minimalLength,
						Constants.withMilliSecs));
				// mf.signalView.setSelectionEnd(mf.signalView.getSelection().getSelectionStart()+currentSelection.minimalLength);
				SignalModel sM = dataModel.signalModel;

				long selectionEnd = (long) Math.ceil(dataModel.selection.getSelectionStart()
						+ currentSelection.minimalLength);
				dataModel.selection.setSelectionEnd(selectionEnd);

				if (sM.getRemovedSelection().inList(dataModel.selection))
				{
					Common
							.getInstance()
							.ShowErrorBox(Constants.error,
									"Der Bereich des (neuen) Testdatensatzes überschneidet sich mit einem gelöschten Bereich. Operation abgebrochen!");
				} else
				{
					if (sM.getTestdataSelection().inList(dataModel.selection,
							Constants.maxIntersectionOfTestDataPercentage))
					{
						Selection helperSelection = sM.automaticTestDataExportHelper(dataModel.selection,
								currentSelection.minimalLength);

						if (helperSelection == null)
						{
							Common
									.getInstance()
									.ShowErrorBox(
											Constants.error,
											"Der Bereich des (neuen) Testdatensatzes überschneidet zu stark sich mit einem schon exportierten Bereich.\n"
													+ "Operation abgebrochen, da auch keine automatische Anpassung gefunden werden konnte.");
						} else
						{
							Common
									.getInstance()
									.ShowMessageBox(
											"Automatische Auswahlanpassung",
											"Der von Ihnen gewählte Bereich kann nicht exportiert werden, da er sich zu stark mit einem\n"
													+ "schon exportierten Bereich überschneidet.\n"
													+ "\n"
													+ "Allerdings konnte die Auswahl geringfügig angepasst werden, so dass ein Exportieren möglich ist.\n"
													+ "Bitte überprüfen Sie die neue exportierte Auswahl und wiederholen dann den Vorgang.",
											SWT.ICON_INFORMATION);

							dataModel.setSelection(helperSelection);
						}

						listOfClasses.setText(Constants.pleaseSelect);
					} else
					{
						// erfolgreiche Auswahl der Rhythmusklasse 
						mf.signalViewerComposite.redrawComponents();
						listOfClasses.select(iSelection);
						if (mf.button_saveSequence != null)
						{
							mf.button_saveSequence.setEnabled(true);
						}
						if (mf.button_cancelSequence != null)
						{
							mf.button_cancelSequence.setEnabled(true);
						}
						if (mf.button_automaticSaveSequence != null)
						{
							mf.button_automaticSaveSequence.setEnabled(true);
						}
						listOfClasses.setEnabled(false);
					}
				}
			}
		}
	} // End ListBox_Classes_Changed

	/**
	 * Sets the status of the listbox
	 * 
	 * @param enabled
	 *            enable/disable the listbox
	 */
	public void setEnabled(Boolean enabled)
	{
		if (enabled)
		{
			listOfClasses.setText(Constants.pleaseSelect);
			textComment.setText(Constants.comment);
		}
		listOfClasses.setEnabled(enabled);
		textComment.setEnabled(enabled);
	}
	
	/**
	 * returns the text of the comment text box
	 * @return comment
	 */
	public String getTestSignalComment()
	{
		return textComment.getText();
	}
}
