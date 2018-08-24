/** This is the mainform-class
 * it handles all issues related to the main window of the application 
 */
package de.fzi.ekgeditor.Gui.Forms;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Layouts;
import de.fzi.ekgeditor.Gui.Menu.MenuMain;
import de.fzi.ekgeditor.Gui.Widgets.ArtefactsTab;
import de.fzi.ekgeditor.Gui.Widgets.EKGToolbar;
import de.fzi.ekgeditor.Gui.Widgets.EventListTab;
import de.fzi.ekgeditor.Gui.Widgets.Listbox_MedicalClasses;
import de.fzi.ekgeditor.Gui.Widgets.SignalInfo;
import de.fzi.ekgeditor.Gui.Widgets.StatusBar;
import de.fzi.ekgeditor.Gui.Widgets.Table_dataSet;
import de.fzi.ekgeditor.Gui.Widgets.TachogramTab;
import de.fzi.ekgeditor.Gui.Widgets.TachogramTab1hr;
import de.fzi.ekgeditor.Gui.Widgets.TachogramTab24hrs;
import de.fzi.ekgeditor.Gui.Widgets.Widget_overview;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerComposite;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.data.MedicalClass;
import de.fzi.ekgeditor.data.Registry;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.TestData;

// Requisites: Common is initialized
public class Form_Main implements SignalListener
{


	/** Handle to main window */
	public Shell mainWindow;

	// public Window-Elements
	public Button button_saveSequence;

	public Button button_automaticSaveSequence;

	public Button button_cancelSequence;

	public Button playStop;

	public Scale playSpeedScale;

	public Listbox_MedicalClasses listbox_medicalClasses;

	public Label label_minimumLength;

	public Table_dataSet dataSetTable;

	public SignalInfo signalInfo;

	public ArtefactsTab artefactsTab;

	public TachogramTab tachogramTab;
	public TachogramTab24hrs tachogram24hrsTab;
	public TachogramTab1hr tachogram1hrTab;

	public SignalViewerComposite signalViewerComposite;

	public Widget_overview widget_overview;

	public Group tabitem_group_overview;

	public Group tabitem_group_testdatabase;
	
	public EKGToolbar ekgToolbar;


	// private Helper-variables
	private Display display;

	private SignalModel signalModel = null;

	private EventListTab eventListTab;

	/**
	 * This method creates all neccessary elements to show the signal
	 * 
	 * @param shell
	 *            parent windows
	 */
	public void CreateEditView(Shell shell)
	{

		signalViewerComposite = new SignalViewerComposite(shell, signalModel);
		signalViewerComposite.init();

		shell.layout();
		signalViewerComposite.setFocus();

		// Layout
		signalViewerComposite.setLayoutData(Layouts.GetLayoutFillOneRow(GridData.FILL_BOTH));
		// ContextMenü
		// MenuEdit m = new MenuEdit(shell);
		// signalViewerComposite.setMenu(m.menu);

	}

	/**
	 * This method creates alle neccessary elements for showing the minimum
	 * length group
	 * 
	 * @param shell
	 *            parent window
	 */
	public Label createMinimumLengthGroup(Composite shell)
	{
		// Composite c = new Composite();

		Group g = new Group(shell, SWT.SHADOW_ETCHED_IN | SWT.LEFT);
		g.setLayout(new FillLayout());
		g.setText("Mindestdauer:");

		Label label_minimumLength = new Label(g, SWT.LEFT);
		label_minimumLength.setText(Constants.undefined);

		return label_minimumLength;
	}

	/** This method disables the saveSequence/cancelSequence-buttons */
	private void DisableButtons()
	{
		button_saveSequence.setEnabled(false);
		button_cancelSequence.setEnabled(false);
		button_automaticSaveSequence.setEnabled(false);
		listbox_medicalClasses.listOfClasses.setText(Constants.pleaseSelect);
	}

	/** This method is called if some testsquence should be saved */
	public void button_Save_Clicked(boolean automatic)
	{
		boolean doSave = true;

		String pathName = null;
		if (!automatic)
		{
			pathName = Common.getInstance().MyOwnFileSaveAsDialog();
		}
		if ((pathName != null) || automatic)
		{
			SignalViewerModel dM = this.signalViewerComposite.signalViewerModel;
			Selection currentSelection = (Selection) dM.selection.clone();

			if (currentSelection.isSelected())
			{
				MedicalClass m = listbox_medicalClasses.getCurrentSelection();
				String testSignalComment = listbox_medicalClasses.getTestSignalComment();
				if (testSignalComment.equalsIgnoreCase(Constants.comment))
				{
					testSignalComment = null;
				}

				if (currentSelection.getSelectionEnd() <= Common.getInstance().signalModel.getMsLength())
				{
					if (automatic)
					{
						// Testdata_XX_####
						// XX: rhythm class (abbreviation)
						// ####: number
						String saveDir = Common.getInstance().reg.reg.getProperty(Registry.prop_saveDir);
//						String numberOfDigits = Common.getInstance().reg.reg.getProperty(Registry.prop_numberOfDigits);
//						if (numberOfDigits == null)
//						{
//							numberOfDigits = Integer.toString(NUMBER_OF_DIGITS);
//						}
//						int iNumberOfDigits = Integer.parseInt(numberOfDigits);
						if (saveDir != null && new File(saveDir).exists())
						{
//							// Calculate current number for new dataset name and create fileName and pathName
							String fileName = TestData.getNextTestdataDirectory(saveDir, m.abbrev);
							pathName = saveDir + File.separator + fileName;

							// create folder
							File databaseEntry = new File(pathName);
							if (!databaseEntry.mkdir())
							{
								Common.getInstance().ShowErrorBox(Constants.error,
										"Der Ordner " + pathName + " konnte nicht erstellt werden.\n");
								doSave = false;
							}

							System.out.println("Automatischer Dateiname:" + pathName);
						}
						else
						{
							Common.getInstance()
									.ShowErrorBox(
											Constants.error,
											"Es wurde kein gültiges Sicherungsverzeichnis angegeben.\nBitte wählen Sie vorher in den Einstellungen ein Sicherungsverzeichnis!");
							doSave = false;
						}
					}
				}
				else
				{
					Common.getInstance().ShowErrorBox(Constants.error,
							"Bereich über Signallänge gewählt. Operation abgebrochen.");
					doSave = false;
				}

				// Perform the saving
				if (doSave)
				{
					dM.signalModel.AddTestdataSelection((Selection) currentSelection.clone(), m, pathName);
					signalViewerComposite.redrawComponents();

					if (dM.signalModel.SaveTestData(pathName, testSignalComment, currentSelection, m))
					{
						// String
						// time=Long.toString(currentSelection.getSelectionStart());
						// dataSetTable.Add(number,Rhythmus,time,FileName);
						dataSetTable.rebuild(dM.signalModel.getTestdataSelection());
						listbox_medicalClasses.setEnabled(true);
						DisableButtons();
						this.label_minimumLength.setText(Constants.undefined);

						// Common.getInstance().ShowMessageBox("Operation erfolgreich",
						// "Das Speichern ist erfolgreich verlaufen.", SWT.OK);
					}
					else
					{
						Common.getInstance().ShowErrorBox(Constants.error, "Fehler beim Speichern.");
					}
				}
			}
			else
			{
				Common.getInstance().ShowErrorBox("Signalbereich nicht gewählt.",
						"Bitte zuerst einen passenden Signalbereich wählen.");
			}
		}

	}

	/**
	 * This method is called if Cancel is clicked while saving some testsequence
	 */
	public void button_Cancel_Clicked()
	{
		listbox_medicalClasses.setEnabled(true);
		this.label_minimumLength.setText(Constants.undefined);
		DisableButtons();
	}

	/**
	 * This method creates all necessary elements for test-sequence-saving
	 * 
	 * @param shell
	 *            parent
	 */
	public void CreateButtonGroup(Composite shell)
	{
		Group group_sequenceButton = new Group(shell, SWT.SHADOW_ETCHED_IN | SWT.LEFT);
		group_sequenceButton.setLayout(new FillLayout());
		group_sequenceButton.setText("Sequenz:");

		button_saveSequence = new Button(group_sequenceButton, SWT.NONE);
		button_saveSequence.setText("Speichern");
		button_saveSequence.setEnabled(false);
		button_saveSequence.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				button_Save_Clicked(false);
			}
		});

		button_automaticSaveSequence = new Button(group_sequenceButton, SWT.NONE);
		button_automaticSaveSequence.setText("Automatisch Speichern");
		button_automaticSaveSequence.setEnabled(false);
		button_automaticSaveSequence.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				button_Save_Clicked(true);
			}
		});

		button_cancelSequence = new Button(group_sequenceButton, SWT.NONE);
		button_cancelSequence.setText("Verwerfen");
		button_cancelSequence.setEnabled(false);
		button_cancelSequence.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				button_Cancel_Clicked();
			}
		});
	}

	/**
	 * Sets the window-Title
	 * 
	 * @param s
	 *            title of the main window
	 */
	public void setWindowTitle(String s)
	{
		mainWindow.setText(s);
	}

	/** Sets the window-title to some standard */
	public void setWindowTitleToStandard()
	{
		String s = Constants.Programname + " " + Constants.Programversion;
		if (Common.getInstance().currentFile.compareTo("") != 0)
		{
			s = s + " - " + Common.getInstance().currentFile;
		}

		setWindowTitle(s);
	}

	/** Creates all necessary elements for the table */
	public void CreateTabGroup()
	{
		TabFolder tab = new TabFolder(mainWindow, SWT.NONE);
		tab.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 200;
		gridData.horizontalSpan = 4;
		tab.setLayoutData(gridData);

		TabItem tabItem_overview = new TabItem(tab, SWT.NONE);
		tabItem_overview.setText("Übersicht");

		tabitem_group_overview = new Group(tab, SWT.NONE);
		tabitem_group_overview.setLayout(new GridLayout(8, false));
		tabItem_overview.setControl(tabitem_group_overview);

		Button goLeft = new Button(tabitem_group_overview, SWT.PUSH);
		goLeft.setText("Rückwärts");
		goLeft.setLayoutData(new GridData(SWT.NONE, SWT.LEFT, false, false, 1, 1));
		goLeft.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_goLeft_Pressed();
			}
		});

		Button goCurrent = new Button(tabitem_group_overview, SWT.PUSH);
		goCurrent.setText("Zentrieren");
		goCurrent.setLayoutData(new GridData(SWT.NONE, SWT.LEFT, false, false, 1, 1));
		goCurrent.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_goCurrent_Pressed();
			}
		});

		Button goRight = new Button(tabitem_group_overview, SWT.PUSH);
		goRight.setText("Vorwärts");
		goRight.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
		goRight.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_goRight_Pressed();
			}
		});

		playStop = new Button(tabitem_group_overview, SWT.PUSH);
		playStop.setText("Abspielen");
		playStop.setToolTipText("Automatisch blättern");
		playStop.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
		playStop.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_playStop_Pressed();
			}
		});
		Label slowerPlayLabel = new Label(tabitem_group_overview, SWT.NONE);
		slowerPlayLabel.setText("langsamer");

		playSpeedScale = new Scale(tabitem_group_overview, SWT.HORIZONTAL);
		playSpeedScale.setToolTipText("Geschwindigkeit");
		GridData gd = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
		gd.widthHint = 100;
		gd.heightHint = 25;
		playSpeedScale.setLayoutData(gd);
		playSpeedScale.setSize(100, 10);
		playSpeedScale.setMinimum(0);
		playSpeedScale.setMaximum(4);
		playSpeedScale.setPageIncrement(1);
		playSpeedScale.setSelection(2);

		playSpeedScale.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_playSpeedScale_Pressed();
			}
		});

		Label fasterPlayLabel = new Label(tabitem_group_overview, SWT.NONE);
		fasterPlayLabel.setText("schneller");

		Button changeAmplitude = new Button(tabitem_group_overview, SWT.PUSH);
		changeAmplitude.setText("Amplitude");
		changeAmplitude.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false, 1, 1));
		changeAmplitude.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_changeAmplitude_Pressed();
			}
		});

		SignalViewerModel previewDataModel = new SignalViewerModel(signalModel);
		previewDataModel.ZOOM_LEVEL_X[SignalViewerModel.userZoomX] = 1;
		previewDataModel.setZoomLevelX(SignalViewerModel.userZoomX, Constants.NO_NOTIFICATION);
		previewDataModel.numberOfChannelsToActivate = 1;
		previewDataModel.layers = 3;
		if (Constants.showPreview)
		{
			widget_overview = new Widget_overview(tabitem_group_overview, SWT.NONE, previewDataModel,
					this.signalViewerComposite.signalViewerModel);
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 8;
			widget_overview.setLayoutData(gridData);

		}

		TabItem tabItem_testdatabase = new TabItem(tab, SWT.NONE);
		tabItem_testdatabase.setText("Testdatensätze");

		tabitem_group_testdatabase = new Group(tab, SWT.NONE);
		tabitem_group_testdatabase.setLayout(Layouts.GetmyLayout());
		tabItem_testdatabase.setControl(tabitem_group_testdatabase);


		signalInfo = new SignalInfo(tab);

		artefactsTab = new ArtefactsTab(tab);

		tachogramTab = new TachogramTab(tab);

		tachogram24hrsTab = new TachogramTab24hrs(tab);

		tachogram1hrTab = new TachogramTab1hr(tab);

		eventListTab = new EventListTab(tab);

		tab.pack();
	}

	public void Button_goLeft_Pressed()
	{
		this.widget_overview.goLeft();
	}

	public void Button_goRight_Pressed()
	{
		this.widget_overview.goRight();
	}

	public void Button_playStop_Pressed()
	{
		playStop.setText(playStop.getText().equalsIgnoreCase("stop") ? "Abspielen" : "Stop");
		if (widget_overview.isPlaying())
			this.widget_overview.play(0);
		else
			this.widget_overview.play(Common.getInstance().getAutomaticPlaySpeed()
					/ (int) Math.pow(2, playSpeedScale.getSelection()));
	}

	public void Button_playSpeedScale_Pressed()
	{
		if (widget_overview.isPlaying())
			this.widget_overview.play(Common.getInstance().getAutomaticPlaySpeed()
					/ (int) Math.pow(2, playSpeedScale.getSelection()));
	}

	public void Button_goCurrent_Pressed()
	{
		this.widget_overview.goCurrent();
	}

	public void Button_changeAmplitude_Pressed()
	{
		this.widget_overview.toggleAmplitude();
	}

	public void signalChanged(SignalEvent e)
	{
		if (e.newSignal == Constants.NEWSIGNAL)
		{
			dataSetTable.ClearAll();
		}
		listbox_medicalClasses.setEnabled(true);
		DisableButtons();
		this.label_minimumLength.setText(Constants.undefined);
	}

	/**
	 * Standard constructor for form_main Attention: you have to run init to
	 * really initialize the window
	 * 
	 * @param display
	 *            parent window
	 */
	public Form_Main(Display display)
	{
		this.display = display;
		mainWindow = new Shell(display);
		mainWindow.setMaximized(true);
		mainWindow.addShellListener(new ShellAdapter()
		{
			@Override
			public void shellClosed(ShellEvent e)
			{
				ArtefactModel artefactModel = Common.getInstance().artefactModel;
				TriggerModel triggerModel = Common.getInstance().triggerModel;

				if (artefactModel.isActiveEventEntryChanged() || artefactModel.isActiveEventEntryTemp())
					Common.getInstance().mc.menu_save_artefacts_as();

				if (triggerModel.isActiveEventEntryChanged() || triggerModel.isActiveEventEntryTemp())
					Common.getInstance().mc.menu_save_triggers_as();

				if (Common.getInstance().ShowMessageBox("Programm beenden",
						"Möchten Sie das Programm wirklich beenden?", SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL) == SWT.OK)
				{
					super.shellClosed(e);
				}
				else
				{
					e.doit = false;
					super.shellClosed(e);
				}
			}
		});
	}

	/** This method does all the initialization work */
	public void Init()
	{
		signalModel = Common.getInstance().signalModel;
		setWindowTitleToStandard();
		mainWindow.setImage(Common.getInstance().im.get(ImageManager.ico_Program));

		// Formular füllen
		ekgToolbar = new EKGToolbar(mainWindow);
		CreateEditView(mainWindow);

		new MenuMain();

		CreateTabGroup();

		listbox_medicalClasses = new Listbox_MedicalClasses(this.tabitem_group_testdatabase,
				this.signalViewerComposite.signalViewerModel);
		CreateButtonGroup(this.tabitem_group_testdatabase);

		label_minimumLength = createMinimumLengthGroup(this.tabitem_group_testdatabase);
		// label_herzfrequenz =
		// createHerzFrequenzGroup(this.tabitem_group_overview);
		dataSetTable = new Table_dataSet(this.tabitem_group_testdatabase, this.signalViewerComposite.signalViewerModel, this);

		StatusBar status = new StatusBar(mainWindow, signalModel);
		status.setText("Bereit.");

		mainWindow.setLayout(Layouts.GetmyLayout());
		// Formular gefüllt
	}

	/** Run the current window. Calls readAndDispatch-loop */
	public void Run()
	{
		mainWindow.open();
		signalModel.addSignalListener(this);

		// Remove Splash-Screen
		if (java.awt.SplashScreen.getSplashScreen() != null)
		{
			java.awt.SplashScreen.getSplashScreen().close();
		}
		while (!mainWindow.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		} // end while

		display.dispose();
	}
}
