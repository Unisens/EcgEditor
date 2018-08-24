/**
 * This class represents the testdata-table
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Widgets;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.unisens.UnisensParseException;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Layouts;
import de.fzi.ekgeditor.Gui.Forms.Form_Main;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.MedicalClass;
import de.fzi.ekgeditor.data.Registry;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.SelectionList;
import de.fzi.ekgeditor.utils.TestData;

public class Table_dataSet
{
	/** link to ourselfes */
	private Table table_dataSets = null;
	private SignalViewerModel m;
	private Form_Main main;
	private boolean secondOpinionMode;
	private HashMap<Long, Long> range;

	/**
	 * Standard constructor
	 * 
	 * @param shell
	 *            parent
	 */
	public Table_dataSet(Composite shell, SignalViewerModel model, Form_Main main)
	{
		this.m = model;
		this.main = main;
		table_dataSets = new Table(shell, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		String strSecondOpinionMode = Common.getInstance().reg.reg.getProperty(Registry.prop_secondOpinionMode);
		if (strSecondOpinionMode != null && Boolean.parseBoolean(strSecondOpinionMode))
		{
			this.secondOpinionMode = true;
		}
		ScrollBar b = table_dataSets.getVerticalBar();
		b.setVisible(true);
		b.setEnabled(true);

		table_dataSets.setHeaderVisible(true);
		table_dataSets.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_ItemSelected_Clicked();
			}
		});

		TableColumn col;
		col = new TableColumn(table_dataSets, SWT.LEFT);
		col.setText("Nr.");
		col.setWidth(50);
		col = new TableColumn(table_dataSets, SWT.LEFT);
		col.setText("Rhythmus");
		col.setWidth(150);
		col = new TableColumn(table_dataSets, SWT.LEFT);
		col.setText("Zeit [ms]");
		col.setWidth(110);
		col = new TableColumn(table_dataSets, SWT.LEFT);
		col.setText("Datensatz");
		col.setWidth(200);

		GridData d = Layouts.GetLayoutFillOneRow(GridData.FILL_BOTH);
		/*
		 * d.horizontalSpan=3; d.horizontalAlignment=SWT.RIGHT;
		 */
		table_dataSets.setLayoutData(d);

		table_dataSets.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseUp(MouseEvent arg0)
			{
			}

			@Override
			public void mouseDown(MouseEvent event)
			{
				if (event.button == 3)
				{
					Menu menu = new Menu(Table_dataSet.this.table_dataSets.getShell(), SWT.POP_UP);
					Point loc = new Point(event.x, event.y);

					TableItem selectedItem = Table_dataSet.this.table_dataSets.getItem(loc);

					if (selectedItem == null)
					{
						return;
					}

					final int index = Table_dataSet.this.table_dataSets.indexOf(selectedItem);
					final String fileName = Table_dataSet.this.table_dataSets.getItems()[index].getText(3);

					MenuItem itmDelete = new MenuItem(menu, SWT.PUSH);
					itmDelete.setText("Löschen");
					itmDelete.setEnabled(!Table_dataSet.this.secondOpinionMode);
					itmDelete.addSelectionListener(new SelectionListener()
					{

						@Override
						public void widgetSelected(SelectionEvent event)
						{
							MessageBox confirm = new MessageBox(Table_dataSet.this.table_dataSets.getShell(),
									SWT.ICON_WARNING | SWT.YES | SWT.NO);
							confirm.setText("Löschen eines Testdatensatzes");
							confirm.setMessage("Möchten Sie den ausgeählten Datensatz wirklich löschen?");
							int result = confirm.open();
							// delete item
							if (result == SWT.YES)
							{
								Table_dataSet.this.m.signalModel.DeleteTestData(fileName);
								Table_dataSet.this.table_dataSets.remove(index);
								Table_dataSet.this.main.signalViewerComposite.redrawComponents();
							}
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent arg0)
						{
						}
					});

					MenuItem itmReclassify = new MenuItem(menu, SWT.CASCADE);
					itmReclassify.setText("Reklassifizieren");
					itmReclassify.setEnabled(!Table_dataSet.this.secondOpinionMode);
					Menu mnuReclassify = new Menu(menu);
					itmReclassify.setMenu(mnuReclassify);

					final MedicalClassList mList = new MedicalClassList();

					for (MedicalClass mClass : mList.mClasses)
					{
						MenuItem item = new MenuItem(mnuReclassify, SWT.PUSH);
						item.setText(mClass.title);
						item.addSelectionListener(new SelectionListener()
						{

							@Override
							public void widgetSelected(SelectionEvent event)
							{
								if (new File(fileName).exists())
								{
									String title = ((MenuItem) event.getSource()).getText();
									MedicalClass mClass = mList.FindMedicalClass(title);

									String subFolderPath = fileName.substring(0, fileName.lastIndexOf("\\"));
									String destPath = subFolderPath + "\\"
											+ TestData.getNextTestdataDirectory(subFolderPath, mClass.abbrev);

									Table_dataSet.this.m.signalModel.reclassifyTestData(fileName, mClass);

									Table_dataSet.this.table_dataSets.getItem(index).setText(3, destPath);
									Table_dataSet.this.table_dataSets.getItem(index).setText(1, mClass.title);
								}
								else
								{
									Common.getInstance()
											.ShowErrorBox("Fehler beim Reklassifizieren.",
													"Der Datensatz konnte nicht reklassifiziert werden, da er auf der Festplatte nicht existiert.");
								}
							}

							@Override
							public void widgetDefaultSelected(SelectionEvent arg0)
							{
							}
						});
					}

					MenuItem itmSecondOpinion = new MenuItem(menu, SWT.CASCADE);
					itmSecondOpinion.setText("Zweite Begutachtung");
					Menu mnuSecondOpinion = new Menu(menu);
					itmSecondOpinion.setMenu(mnuSecondOpinion);

					for (MedicalClass mClass : mList.mClasses)
					{
						MenuItem item = new MenuItem(mnuSecondOpinion, SWT.PUSH);
						item.setText(mClass.title);
						item.addSelectionListener(new SelectionListener()
						{

							@Override
							public void widgetSelected(SelectionEvent event)
							{
								if (new File(fileName).exists())
								{
									String title = ((MenuItem) event.getSource()).getText();
									MedicalClass mClass = mList.FindMedicalClass(title);
									try
									{
										TestData.saveSecondOpinion(fileName, mClass);
										Table_dataSet.this.table_dataSets.getItem(index).setText(1,
												"[ZB] " + mClass.title);
									}
									catch (Exception e)
									{
										Common.getInstance().ShowErrorBox("Fehler bei Klassifizierung",
												"Die Klassifizierung der Testdaten schlug fehl: \n" + e.getMessage());
										System.err.println("Unable to save second opinion: " + e.getMessage());
									}
								}
								else
								{
									Common.getInstance()
											.ShowErrorBox("Fehler beim Reklassifizieren.",
													"Der Datensatz konnte nicht reklassifiziert werden, da er auf der Festplatte nicht existiert.");
								}
							}

							@Override
							public void widgetDefaultSelected(SelectionEvent arg0)
							{
							}
						});
					}

					MenuItem itmOpenFolder = new MenuItem(menu, SWT.PUSH);
					itmOpenFolder.setText("Ordner öffnen");
					itmOpenFolder.setEnabled(Desktop.isDesktopSupported());
					itmOpenFolder.addSelectionListener(new SelectionListener()
					{

						@Override
						public void widgetSelected(SelectionEvent arg0)
						{
							File file = new File(fileName);
							if (file.exists())
							{
								try
								{
									Desktop.getDesktop().open(file);
								}
								catch (IOException ex)
								{
									Common.getInstance().ShowErrorBox("Fehler beim Öffnen",
											"Der Ordner kann nicht geöffnet werden: \n" + ex.getMessage());
								}
							}
							else
							{
								Common.getInstance().ShowErrorBox("Fehler beim Öffnen",
										"Der angegebene Pfad existiert nicht.");
							}
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent arg0)
						{
						}
					});

					loc = Table_dataSet.this.table_dataSets.toDisplay(loc);
					menu.setLocation(loc);
					menu.setVisible(true);
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0)
			{
			}
		});
	}

	/**
	 * Adds another entry to the table
	 * 
	 * @param nr
	 *            number
	 * @param Rhythmus
	 *            Rhythmus of the signal
	 * @param Zeit
	 *            Time
	 * @param Datensatz
	 *            FileName/DataSet name
	 */
	public void Add(int nr, String Rhythmus, long Zeit, String Datensatz)
	{
		if (this.secondOpinionMode)
		{
			// set rhythm-info for second opinion
			try
			{
				Rhythmus = TestData.getSecondOpinion(Datensatz);
			}
			catch (UnisensParseException e)
			{
				Rhythmus = null;
			}
			if (Rhythmus != null && Rhythmus != "")
			{
				Rhythmus = "[ZB] " + Rhythmus;
			}
			else
			{
				Rhythmus = "Keine Zweit-Beurteilung gegeben.";
			}
		}


		// FIXME Hier könnte man auch noch eine richtige Zeit anzeigen, nicht
		// nur Millisekunden
		// long t =
		// this.m.signalModel.getSignal().getUnisens().getTimestampStart().getTime()
		// + Zeit;
		// String[] tableLine = { Integer.toString(nr), Rhythmus,
		// (new Date(t)).toString(), Datensatz };


		String[] tableLine = { Integer.toString(nr), Rhythmus, Long.toString(Zeit), Datensatz };


		TableItem item = new TableItem(table_dataSets, SWT.LEFT);
		item.setText(tableLine);
	}

	public void ClearAll()
	{
		table_dataSets.clearAll();
	}

	public void rebuild(SelectionList l)
	{
		table_dataSets.removeAll();
		this.range = new HashMap<Long, Long>();
		
		// dataSetTable.Add(number,Rhythmus,time,FileName);

		int counter = 0;
		for (Selection s : l)
		{
			String Rhythmus;
			MedicalClass m = s.m;
			if (m != null)
			{
				Rhythmus = m.title;
			}
			else
			{
				Rhythmus = Constants.undefined;
			}

			this.Add(counter, Rhythmus, s.getSelectionStart(), s.FileName);
			this.range.put(s.getSelectionStart(), s.getLength());
			counter++;
		}

		table_dataSets.getVerticalBar().setVisible(true);
		table_dataSets.getVerticalBar().setEnabled(true);
	}

	/**
	 * This function is called at each left mouse button click on an item in the
	 * test data list. The EKG Editor jumps to the selected test signal and
	 * highlights it.
	 */
	public void Button_ItemSelected_Clicked()
	{
		// Read the start time in ms from the test data table and jump to the start time.
		String txt = this.table_dataSets.getSelection()[0].getText(2);
		long time = Long.parseLong(txt);
		m.goToTime(time);
		
		// Highlight the corresponding signal range.
		this.main.signalViewerComposite.getSignalViewerCanvas().OnMouseDown();
		this.main.signalViewerComposite.getSignalViewerCanvas().setSelection(new Selection(time, time + this.range.get(time)));
	}
}
