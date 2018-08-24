/** This class creates and manages all common menu entries
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Menu;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.unisens.Event;
import org.unisens.EventEntry;
import org.unisens.Group;
import org.unisens.SignalEntry;
import org.unisens.Unisens;
import org.unisens.UnisensParseException;
import org.unisens.UnisensParseExceptionTypeEnum;
import org.unisens.ri.UnisensImpl;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Forms.Dialog_AddArtefact;
import de.fzi.ekgeditor.Gui.Forms.Dialog_AddTrigger;
import de.fzi.ekgeditor.Gui.Forms.Dialog_Analysis;
import de.fzi.ekgeditor.Gui.Forms.Dialog_PreviewCut;
import de.fzi.ekgeditor.Gui.Forms.Dialog_SaveEventEntry;
import de.fzi.ekgeditor.Gui.Forms.Dialog_Settings;
import de.fzi.ekgeditor.Gui.Forms.Dialog_SignalEntry;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.data.TriggerType;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensAdapter;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.SelectionList;

public class MenuCommon
{

	/** link to parent shell */
	private Shell shell;

	/** Standard constructor
	 * 
	 * @param shell parent
	 */
	public MenuCommon(Shell shell)
	{
		this.shell = shell;
	}

	/** This method is called if some unisens-file should be opened */
	public void menu_FileOpen()
	{
		if(Common.getInstance().triggerModel != null && Common.getInstance().triggerModel.isActiveEventEntryChanged())
			menu_save_triggers_as();
		if(Common.getInstance().triggerModel != null && Common.getInstance().artefactModel.isActiveEventEntryChanged())
			menu_save_artefacts_as();
		FileDialog f = new FileDialog(shell, SWT.OPEN);
		//f.setMessage("Bitte wählen Sie eine Unisens-Verzeichnis zum Öffnen aus");
		f.setText("Unisens-Datei zum Öffnen auswählen");
		String FileName = f.open();
		if (FileName != null)
		{
			File fn = new File(FileName);
			if (fn.exists())
			{
				if (!fn.canRead())
				{
					Common.getInstance().ShowErrorBox(Constants.error, "Datei darf nicht gelesen werden.");
				} else
				{
					// cut file into path and entry parts
					String path = FileName.substring(0, FileName.lastIndexOf(System.getProperty("file.separator")) + 1);
					String entryName = FileName.substring(FileName.lastIndexOf(System.getProperty("file.separator")) + 1);
					Unisens unisens = null;
					try {
						unisens = new UnisensImpl(path);
					} catch (UnisensParseException e) {
						if(e.getUnisensParseExceptionType() == UnisensParseExceptionTypeEnum.UNISENS_GROUP_ENTRY_NOT_EXIST){
							Common.getInstance().ShowErrorBox("Unisens parse Fehler", "Ein Unisens GroupEntry-Element referenziert kein gültiges Entry-Element in der unisens.xml\n\n" + e.getMessage());
							return;
						}
					}
					
					Common.getInstance().unisens = unisens;
					
					List<SignalEntry> EKGEntries = UnisensAdapter.getECGGroup(unisens);

					SignalEntry chosenSignal = null;
					
					List<SignalEntry> tempEntries = UnisensAdapter.getSignalEntries(EKGEntries, entryName);
					if (tempEntries.size() == 1)
					{
						chosenSignal = tempEntries.get(0);
					} else
					{
						if (EKGEntries.size() > 0)
						{
							if (EKGEntries.size() > 1)
							{
								Dialog_SignalEntry dse = new Dialog_SignalEntry(shell, SWT.APPLICATION_MODAL, EKGEntries);
								chosenSignal = (SignalEntry) dse.open();
							} else
							{
								chosenSignal = EKGEntries.get(0);
							}
						} else
						{
							Common.getInstance().ShowErrorBox(Constants.error,
									"Es befinden sich in dieser Unisens-Datei keine EKG-Daten");
						}
					}
					
					if (chosenSignal != null){
						Group group = UnisensAdapter.findGroup(unisens, chosenSignal);
						Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.signalModel.setSignal(chosenSignal, unisens, group);
						Common.getInstance().triggerModel.loadEventEntries(unisens, group);
						Common.getInstance().artefactModel.loadEventEntries(unisens, group);
						Common.getInstance().eventModel.loadEventEntries(unisens, group);
						
						SelectionList testdata = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.signalModel.getTestdataSelection();
						Common.getInstance().mainForm.dataSetTable.rebuild(testdata);

						MenuMain.getMainMenu().getMenuAnalysis().setEnabled(true);
						Common.getInstance().currentFile = path;
						Common.getInstance().mainForm.setWindowTitleToStandard();
						Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
					}
				}
			} else
			{
				Common.getInstance().ShowErrorBox(Constants.error,
						"Datei " + FileName + " konnte nicht gefunden werden.");
			}
		}
	}

	/** This method is called if the about menuitem is clicked */
	public void menu_Info(){
		Common.getInstance().ShowMessageBox(Constants.aboutTitle, Constants.aboutInfo, SWT.ICON_INFORMATION);
	}

	/** This method is called if the about menuitem is clicked */
	public void menu_Unisens(){
		Common.getInstance().ShowMessageBox(Constants.unisensTitle, Constants.unisensInfo, SWT.ICON_INFORMATION);
	}

	/** This method is called if the file-save-as menuitem is clicked */
	public void menu_FileSaveAs()
	{

		String FileName = Common.getInstance().MyOwnFileSaveAsDialog();
		if (FileName != null)
		{
			SignalViewerModel dM = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
			dM.signalModel.Save(FileName, Common.getInstance().mainForm.mainWindow);

			Common.getInstance().mainForm.setWindowTitleToStandard();
		}
	}

	private void redrawAfterRemovedSelection()
	{
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setRedraw(true);
		Common.getInstance().mainForm.signalViewerComposite.signalViewerCanvas.redraw();
	}

	/** This method is called, if some section of the signal should be removed */
	public void menu_remove()
	{
		/*
		 Common.getInstance().mainForm.signalView.DeleteSelection();
		 Common.getInstance().mainForm.signalView.signalChanged();
		 */
		SignalViewerModel dM = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;

		Selection s = null;

		if (Constants.useAutomaticCut)
		{
			s = dM.signalModel.automaticCut((Selection) dM.selection.clone(), dM.getRealChannelNumber(0));
		} else
		{
			s = (Selection) dM.selection.clone();
		}

		Dialog_PreviewCut d = new Dialog_PreviewCut(this.shell);

		Integer res = d.open(dM.selection, s, dM);
		if (res != null)
		{
			switch (res)
			{

			case (SWT.OK):
			{
				dM.signalModel.addRemovedSelection(s);
				redrawAfterRemovedSelection();
				Common.getInstance().ShowMessageBox("Information", "Automatisch angepasster Schnitt durchgeführt!",
						SWT.ICON_INFORMATION);
				break;
			}
			case (SWT.YES):
			{
				dM.signalModel.addRemovedSelection((Selection) dM.selection.clone());
				redrawAfterRemovedSelection();
				Common.getInstance().ShowMessageBox("Information", "Manueller Schnitt durchgeführt!",
						SWT.ICON_INFORMATION);
				break;
			}
			}

		}
	}
	
	public void menu_delete_triggers(long startTimeInMs, long endTimeInMs){
		EventEntry eventEntry = Common.getInstance().triggerModel.getActiveEventEntry();
		long startEventSample = (long)(startTimeInMs * eventEntry.getSampleRate() / 1000);
		long endEventSample = (long)(endTimeInMs * eventEntry.getSampleRate() / 1000);
		
		Common.getInstance().triggerModel.deleteEvents(startEventSample, endEventSample);
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
	
	public void menu_delete_trigger_after_confirm(long timeInMs){
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		EventEntry eventEntry = Common.getInstance().triggerModel.getActiveEventEntry();
		
		long eventSample = (long)(timeInMs * eventEntry.getSampleRate() / 1000);
		
		List<Event> events = triggerModel.readEvents(eventSample - 10, eventSample + 10);
		Event eventToDelete = null;
		if(events.size() == 0)
			return;
		if(events.size() == 1){
			eventToDelete = events.get(0);
		}else{
			eventToDelete = events.get(events.size()/2);
		}
		
		if (Common.getInstance().ShowMessageBox(Constants.question, String.format("Soll der Trigger '%s' gelöscht werden?", eventToDelete.getType()), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL ) == SWT.OK){
			triggerModel.deleteEvents(eventToDelete.getSampleStamp(), eventToDelete.getSampleStamp() + 1);
			Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setRedraw(true);
			Common.getInstance().mainForm.signalViewerComposite.signalViewerCanvas.redraw();
		}
		
	}
	
	public void menu_add_trigger(){
		Dialog_AddTrigger addTriggerDialog = new Dialog_AddTrigger(new Shell(Display.getCurrent(), SWT.DIALOG_TRIM ));
		addTriggerDialog.open();
	}
	
	public void menu_add_artefact(){
		Dialog_AddArtefact addArtefactDialog = new Dialog_AddArtefact(new Shell(Display.getCurrent(), SWT.DIALOG_TRIM ));
		addArtefactDialog.open();
	}
	
	public void menu_delete_artefact(){
		SignalViewerModel signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		int mouseRightDownXPosition = signalViewerModel.getMouseRightDownXPosition();
		int mouseRightDownYPosition = signalViewerModel.getMouseRightDownYPosition();
		ArtefactModel artefactModel = Common.getInstance().artefactModel;
		Artefact selectedArtefact = artefactModel.getArtefactForPoint(mouseRightDownXPosition, mouseRightDownYPosition);
		if(selectedArtefact != null){
			int result = Common.getInstance().ShowMessageBox("Artefakt löschen?", String.format("Möchten Sie das Artefact(%s) wirklich löschen?", selectedArtefact.getType()), SWT.OK | SWT.CANCEL);
			if(result == SWT.OK){
				artefactModel.delete(selectedArtefact);
			}
		}
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
	
	public void menu_save_triggerlist(){
		Common.getInstance().triggerModel.saveActiveEventEntry();
	}
	
	/** This menuitem is called, if settings menuitem is clicked */
	public void menu_settings()
	{
		Dialog_Settings dialog = new Dialog_Settings(shell);
		dialog.open();
	}

	/** This method is called, if analysis-menuitem is clicked */
	public void menu_analysis()
	{
		if(Common.getInstance().triggerModel.isActiveEventEntryChanged())
			menu_save_triggers_as();
		if(Common.getInstance().artefactModel.isActiveEventEntryChanged())
			menu_save_artefacts_as();
		Dialog_Analysis analysisDialog = new Dialog_Analysis(shell);
		analysisDialog.open();
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}

	/** This method is called, if quit-menuitem was clicked */
	public void menu_quit()
	{
		menu_save_triggers_as();
		menu_save_artefacts_as();
		if (Common.getInstance().ShowMessageBox(Constants.question, "Wollen Sie das Programm wirklich beenden?",
				SWT.ICON_QUESTION | SWT.YES | SWT.NO) == SWT.YES)
		{
			System.exit(0);
		}
	}
	
	public void menu_save_triggers_as(){
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		if(triggerModel.getActiveEventEntry() != null){
			Dialog_SaveEventEntry saveEventEntryDialog = new Dialog_SaveEventEntry(shell, Common.getInstance().triggerModel);
			saveEventEntryDialog.open();
		}
	}
	
	public void menu_save_artefacts_as(){
		ArtefactModel artefactModel = Common.getInstance().artefactModel;
		if(artefactModel.getActiveEventEntry() != null){
			Dialog_SaveEventEntry saveEventEntryDialog = new Dialog_SaveEventEntry(shell, Common.getInstance().artefactModel);
			saveEventEntryDialog.open();
		}
	}
	
	public void menu_set_add_trigger_mode(boolean addTriggerMode){
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		triggerModel.setAddTriggerMode(addTriggerMode);
	}
	
	public void menu_set_delete_trigger_mode(boolean addTriggerMode){
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		triggerModel.setDeleteTriggerMode(addTriggerMode);
	}
	
	public void menu_add_trigger_mode_add_trigger(TriggerType triggerType){
		SignalViewerModel signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		EventEntry activeTriggerEntry = triggerModel.getActiveEventEntry();
		if(activeTriggerEntry == null){
			triggerModel.setTempTriggerEntryAsActive();
			activeTriggerEntry = triggerModel.getActiveEventEntry();
		}
		long mouseXLeftDownXPosition = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getMouseLeftDownXPosition();
		long samplestamp = signalViewerModel.getSampleNumber(mouseXLeftDownXPosition, activeTriggerEntry.getSampleRate());
		triggerModel.addEvent(new Event(samplestamp, triggerType.getNotation(), triggerType.getComment()));
		signalViewerModel.setRedraw(true);
		Common.getInstance().mainForm.signalViewerComposite.signalViewerCanvas.redraw();
	}
}
