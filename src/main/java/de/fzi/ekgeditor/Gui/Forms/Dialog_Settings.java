/**
 * This class represents the general program settings dialog.
 *
 * @author kirst, glose
 * @version 0.1
 */
package de.fzi.ekgeditor.Gui.Forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.data.Registry;
import de.fzi.ekgeditor.events.SettingsEvent;



public class Dialog_Settings extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Group groupVisualStyle;
	private Button buttonSetColors;
	private Scale scaleRounder;
	private Label labelRounder;
	private Button buttonCancel;
	private Button buttonOk;
	private Button buttonStandardDirectory;
	private Text textStandardDirectory;
	private Group groupStandardDirectory;
	private Label labelRounderValue;
	private Group groupNumerical;
	private Label labelDialog;
	private Label labelCurrentGridSize;
	private Scale scaleGridSize;
	private Label labelGridSize;
	private Button buttonDynamicGrid;
	private Button buttonSetFonts;

	public Dialog_Settings(Shell parent, int style) {
		super(parent, style);
	}

	public Dialog_Settings(Shell parent) {
		super(parent, SWT.NULL);
	}

	public void open() {
		try {
			Shell parent = getParent();
			SignalViewerModel dM = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;			
			
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialogShell.setLayout(null);
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setImage(Common.getInstance().im.get(ImageManager.ico_settings));
			dialogShell.setText("Einstellungen");
			dialogShell.setSize(437, 336);
			{
				labelDialog = new Label(dialogShell, SWT.NONE);
				labelDialog.setText("Hier können Sie alle Einstellungen für den EKG-Editor vornehmen.");
				labelDialog.setBounds(5, 5, 312, 13);
			}
			{
				groupVisualStyle = new Group(dialogShell, SWT.NONE);
				groupVisualStyle.setLayout(null);
				groupVisualStyle.setText("Aussehen");
				groupVisualStyle.setBounds(5, 23, 419, 116);
				{
					buttonSetColors = new Button(groupVisualStyle, SWT.PUSH | SWT.CENTER);
					buttonSetColors.setText("Farben ändern...");
					buttonSetColors.setBounds(8, 18, 95, 23);
					buttonSetColors.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							Button_Colors_Pressed();
						}
					});
				}
				{
					buttonSetFonts = new Button(groupVisualStyle, SWT.PUSH | SWT.CENTER);
					buttonSetFonts.setText("Schrift ändern...");
					buttonSetFonts.setBounds(108, 18, 95, 23);
					buttonSetFonts.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							Button_Fonts_Pressed();
						}
					});
				}
				{
					buttonDynamicGrid = new Button(groupVisualStyle, SWT.CHECK | SWT.LEFT);
					buttonDynamicGrid.setText("Dynamisches Gitter anstatt Millimeterpapier");
					buttonDynamicGrid.setSelection(dM.dynamicGrid);
					buttonDynamicGrid.setBounds(8, 46, 228, 16);
					buttonDynamicGrid.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							Button_dynamicGrid_Pressed();
						}
					});
				}
				{
					labelGridSize = new Label(groupVisualStyle, SWT.NONE);
					labelGridSize.setText("Gitterabstand:");
					labelGridSize.setBounds(8, 81, 70, 13);
				}
				{
					scaleGridSize = new Scale(groupVisualStyle, SWT.NONE);
					scaleGridSize.setMinimum(2);
					scaleGridSize.setMaximum(18);
					scaleGridSize.setSelection(dM.getStaticGridSize());
					scaleGridSize.setIncrement(1);
					scaleGridSize.setPageIncrement(2);
					scaleGridSize.setBounds(108, 67, 195, 41);
					scaleGridSize.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							Scale_Changed();
						}
					});
				}
				{
					labelCurrentGridSize = new Label(groupVisualStyle, SWT.NONE);
					labelCurrentGridSize.setText(Integer.toString(scaleGridSize.getSelection()) + " Pixel pro mm");
					labelCurrentGridSize.setBounds(308, 81, 85, 13);
				}
			}
			{
				groupNumerical = new Group(dialogShell, SWT.NONE);
				groupNumerical.setLayout(null);
				groupNumerical.setText("Rechnungsmechanik");
				groupNumerical.setBounds(5, 144, 419, 77);
				{
					labelRounder = new Label(groupNumerical, SWT.NONE);
					labelRounder.setText("Runden:");
					labelRounder.setBounds(8, 32, 41, 13);
				}
				{
					scaleRounder = new Scale(groupNumerical, SWT.NONE);
					scaleRounder.setMinimum(0);
					scaleRounder.setMaximum(6);
					scaleRounder.setIncrement(1);
					scaleRounder.setPageIncrement(1);
					scaleRounder.setBounds(108, 18, 195, 41);
					scaleRounder.setSelection((int) Math.round(Math.log10(Common.signalRound)));
					scaleRounder.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							Scale_rounder_Selected();
						}
					});
				}
				{
					labelRounderValue = new Label(groupNumerical, SWT.NONE);
					labelRounderValue.setText(Integer.toString(scaleRounder.getSelection()) + " Nachkommastellen");
					labelRounderValue.setBounds(308, 32, 97, 13);
				}
			}
			{
				groupStandardDirectory = new Group(dialogShell, SWT.NONE);
				groupStandardDirectory.setLayout(null);
				groupStandardDirectory.setText("Automatisches Speichern");
				groupStandardDirectory.setBounds(5, 226, 419, 49);
				{
					String standardDirectory = Common.getInstance().reg.reg.getProperty(Registry.prop_saveDir);
					if (standardDirectory == null)
					{
						standardDirectory = "Kein Verzeichnis ausgwählt.";
					}

					textStandardDirectory = new Text(groupStandardDirectory, SWT.BORDER);
					textStandardDirectory.setText(standardDirectory);
					textStandardDirectory.setBounds(8, 20, 301, 18);
					textStandardDirectory.setEditable(false);
				}
				{
					buttonStandardDirectory = new Button(groupStandardDirectory, SWT.PUSH | SWT.CENTER);
					buttonStandardDirectory.setText("Durchsuchen...");
					buttonStandardDirectory.setBounds(314, 18, 97, 23);
					buttonStandardDirectory.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							Button_changeSaveDir_Pressed();
						}
					});
				}
			}
			{
				buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				dialogShell.setDefaultButton(buttonOk);
				buttonOk.setText("OK");
				buttonOk.setBounds(111, 280, 101, 23);
				buttonOk.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						Button_OK_Pressed();
					}
				});
			}
			{
				buttonCancel = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				buttonCancel.setText("Abbrechen");
				buttonCancel.setBounds(217, 280, 101, 23);
				buttonCancel.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						Button_Cancel_Pressed();
					}
				});
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method is called if button OK is pressed. It sets the return value
	 * to SWT.OK and closes the dialog.
	 */
	public void Button_OK_Pressed()
	{
		dialogShell.close();
	}

	/**
	 * This method is called if button cancel is pressed. It sets the return
	 * value to SWT.CANCEL and closes the dialog.
	 */
	public void Button_Cancel_Pressed()
	{
		dialogShell.close();
	}

	/**
	 * This method is called if button fonts is pressed.
	 */
	public void Button_Fonts_Pressed()
	{
		Dialog_Settings_Fonts dialog = new Dialog_Settings_Fonts(this.getParent());
		dialog.open();
	}

	/**
	 * This method is called if button color is pressed.
	 */
	public void Button_Colors_Pressed()
	{
		Dialog_Settings_Colors dialog = new Dialog_Settings_Colors(this.getParent());
		Object o = dialog.open();
		if (o != null)
		{
			if ((Integer) o == SWT.OK)
			{
				for (int currentChannel = 0; currentChannel < dialog.myColor.size(); currentChannel++)
				{
					Color c = dialog.myColor.get(currentChannel);
					Common.getInstance().signalViewerColors.setChannelColor(currentChannel, c);
					String RegistryKey = Registry.prop_channelcolor + Integer.toString(currentChannel);
					Common.getInstance().reg.saveColorToRegistry(c, RegistryKey);
				}

				for (int currentSystemColor = 0; currentSystemColor < dialog.mySystemColor.size(); currentSystemColor++)
				{
					Color c = dialog.mySystemColor.get(currentSystemColor);
					Common.getInstance().signalViewerColors.setSignalViewerColor(currentSystemColor, c);
					String RegistryKey = Registry.prop_color + Integer.toString(currentSystemColor);
					Common.getInstance().reg.saveColorToRegistry(c, RegistryKey);
				}

				Common.getInstance().reg.save();

				Common.getInstance().notifySettingsChanged(new SettingsEvent(this));
			}
		}
	}

	public void Button_dynamicGrid_Pressed()
	{
		boolean value = this.buttonDynamicGrid.getSelection();
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.dynamicGrid = value;
		Common.getInstance().notifySettingsChanged(new SettingsEvent(this));

		String RegistryKey = Registry.prop_dynamicGrid;
		Common.getInstance().reg.reg.setProperty(RegistryKey, Boolean.toString(value));
		Common.getInstance().reg.save();
	}

	public void Button_changeSaveDir_Pressed()
	{
		String newSaveDir = Common.getInstance().MyOwnFileSaveAsDialog();
		if (newSaveDir != null)
		{
			this.textStandardDirectory.setText(newSaveDir);
			Common.getInstance().reg.reg.setProperty(Registry.prop_saveDir, newSaveDir);
			Common.getInstance().reg.save();
		}
	}

	public void Scale_rounder_Selected()
	{
		Integer value = this.scaleRounder.getSelection();
		this.labelRounderValue.setText(Integer.toString(value) + " Nachkommastellen");
		Common.signalRound = (int) Math.round(Math.pow(10, this.scaleRounder.getSelection()));

		String RegistryKey = Registry.prop_rounder;
		String result = Integer.toString(Common.signalRound);
		Common.getInstance().reg.reg.setProperty(RegistryKey, result);
		Common.getInstance().reg.save();
	}

	public void Scale_Changed()
	{
		int value = this.scaleGridSize.getSelection();
		this.labelCurrentGridSize.setText(Integer.toString(value) + " Pixel pro mm");
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setStaticGridSize(value);

		String RegistryKey = Registry.prop_gridSize;
		Common.getInstance().reg.reg.setProperty(RegistryKey, Integer.toString(value));
		Common.getInstance().reg.save();
	}
}
