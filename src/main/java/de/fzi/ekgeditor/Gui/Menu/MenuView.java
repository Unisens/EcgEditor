/** This class creates and manages all view menu entries
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.unisens.EventEntry;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Forms.Dialog_GoToSample;
import de.fzi.ekgeditor.Gui.Forms.Dialog_InputBox;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.ChannelData;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.events.ArtefactModelListener;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.events.TriggerModelEvent;
import de.fzi.ekgeditor.events.TriggerModelListner;
import de.fzi.ekgeditor.events.ViewEvent;
import de.fzi.ekgeditor.events.ViewListener;

public class MenuView implements SignalListener, ViewListener, TriggerModelListner, ArtefactModelListener {
	
	/** constant unit text for a amplitude */
	private static final String amplitudeUnit="mm/mV";
	/** constant unit text for feed */
	private static final String feedUnit="mm/s";
	
	/** all amplitude MenuItems */
	private MenuItem[] amplitudeMenuItems = new MenuItem[SignalViewerModel.ZOOM_LEVEL_Y_LENGTH];
	/** all feed MenuItems */
	private MenuItem[] feedMenuItems = new MenuItem[SignalViewerModel.ZOOM_LEVEL_X_LENGTH];
	/** all channel Menu Items */
	private ArrayList<MenuItem> channelMenuItemList = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> channelPolarityMenuItemList = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> triggerMenuItemList = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> secondaryTriggerMenuItemList = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> artefactMenuItemList = new ArrayList<MenuItem>();
	
	private Menu channelSubMenu = null;
	private Menu channelPolaritySubMenu = null;
	private Menu triggerSubMenu = null;
	private Menu secondaryTriggerSubMenu = null;
	private Menu artefactSubMenu = null;
	private Shell shell = null;
	
	/** Show trigger? */
	//private MenuItem triggerItem=null;
	/** Show reticule? */
	private MenuItem reticuleItem=null; // Gitternetz
	/** Show centerLine? */
	private MenuItem centerLineItem=null; // Mittellinie
	
	private MenuItem goToSample=null;
	private MenuItem secondaryTriggerMenuItem = null;
	
	/** Standard constructor that creates standard main-menu items
	 * 
	 * @param mainMenu parent menu
	 */
	public MenuView(Menu mainMenu)
	{
		CreateViewMenu(mainMenu);
	}
	
	private String getUserTextForAmplitude()
	{
		int i=SignalViewerModel.ZOOM_LEVEL_Y_LENGTH-1;
		Double zY=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_Y[i];
		return "Benutzerdefiniert: "+Double.toString(zY)+" "+amplitudeUnit;
	}
	private String getUserTextForFeed()
	{
		int i=SignalViewerModel.ZOOM_LEVEL_X_LENGTH-1;
		Double zX=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_X[i];
		return "Benutzerdefiniert: "+Double.toString(zX)+" "+feedUnit;
	}
	
	/** Creates all the view menu items
	 * 
	 * @param mainMenu parent menu
	 */
	private void CreateViewMenu(Menu mainMenu)
	{
		Common.getInstance().triggerModel.addTriggerModelListener(this);
		Common.getInstance().artefactModel.addArtefactModelListener(this);
		shell = mainMenu.getShell();
		ImageManager im = Common.getInstance().im;
		
		MenuItem viewItem = new MenuItem(mainMenu,SWT.CASCADE);
		viewItem.setText("Ansicht");
		
		Menu viewSubMenu = new Menu(shell,SWT.DROP_DOWN);
		viewItem.setMenu(viewSubMenu);
		
		MenuItem amplitudeItem = new MenuItem(viewSubMenu,SWT.CASCADE);
		amplitudeItem.setText("Amplitude");
		amplitudeItem.setImage(im.get(ImageManager.ico_view_zoom_y));
		
		Menu amplitudeSubMenu = new Menu(viewSubMenu);
		amplitudeItem.setMenu(amplitudeSubMenu);

		for (int i=0;i<SignalViewerModel.ZOOM_LEVEL_Y_LENGTH;i++)
		{
			amplitudeMenuItems[i] = new MenuItem(amplitudeSubMenu,SWT.CHECK);
			if (i!=SignalViewerModel.ZOOM_LEVEL_Y_LENGTH-1)
			{
				Double zY=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_Y[i];
				amplitudeMenuItems[i].setText(Double.toString(zY)+" "+amplitudeUnit);
				//amplitudeMenuItems[i].setImage(im.get(amplitudeIco[i]));
				final int tempI=i;
				amplitudeMenuItems[i].addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_AmplitudeZoom_Clicked(tempI);}});
			}
			else
			{
				amplitudeMenuItems[i].setText(getUserTextForAmplitude());
				amplitudeMenuItems[i].addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_AmplitudeUserZoom_Clicked();}});
			}
			amplitudeMenuItems[i].setSelection(false);
		}
		
		
		
		MenuItem feedItem = new MenuItem(viewSubMenu,SWT.CASCADE);
		feedItem.setText("Vorschub");
		feedItem.setImage(im.get(ImageManager.ico_view_zoom_x));
		
		Menu feedSubMenu = new Menu(viewSubMenu);
		feedItem.setMenu(feedSubMenu);

		for (int i=0;i<SignalViewerModel.ZOOM_LEVEL_X_LENGTH;i++)
		{
			feedMenuItems[i] = new MenuItem(feedSubMenu,SWT.CHECK);

			if (i!=SignalViewerModel.ZOOM_LEVEL_X_LENGTH-1)
			{
				Double zX=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_X[i];
				feedMenuItems[i].setText(Double.toString(zX)+" "+feedUnit);
				//FeedMenuItems[i].setImage(im.get(feedIco[i]));
				final int tempI=i;
				feedMenuItems[i].addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_FeedZoom_Clicked(tempI);}});
			}
			else
			{
				feedMenuItems[i].setText(getUserTextForFeed());
				feedMenuItems[i].addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_FeedZoomUser_Clicked();}});
			}
			feedMenuItems[i].setSelection(false);
		}

		MenuItem channelMenuItem = new MenuItem(viewSubMenu,SWT.CASCADE);
		channelMenuItem.setText("Kanäle");
		channelMenuItem.setImage(im.get(ImageManager.ico_view_channels));
		
		channelSubMenu = new Menu(viewSubMenu);
		channelMenuItem.setMenu(channelSubMenu);
		
		MenuItem channelPolarityMenuItem = new MenuItem(viewSubMenu,SWT.CASCADE);
		channelPolarityMenuItem.setText("Kanal umpolen");
		channelPolarityMenuItem.setImage(im.get(ImageManager.ico_view_pole_change));
		
		channelPolaritySubMenu = new Menu(viewSubMenu);
		channelPolarityMenuItem.setMenu(channelPolaritySubMenu);
			
		MenuItem triggerMenuItem = new MenuItem(viewSubMenu,SWT.CASCADE);
		triggerMenuItem.setText("Trigger");
		triggerMenuItem.setImage(im.get(ImageManager.ico_view_trigger));
		
		triggerSubMenu = new Menu(viewSubMenu);
		triggerMenuItem.setMenu(triggerSubMenu);
		
		MenuItem artefactMenuItem = new MenuItem(viewSubMenu,SWT.CASCADE);
		artefactMenuItem.setText("Artefakt");
		artefactMenuItem.setImage(im.get(ImageManager.ico_view_trigger));
		
		artefactSubMenu = new Menu(viewSubMenu);
		artefactMenuItem.setMenu(artefactSubMenu);
		
		// add event observer
		SignalViewerModel dM=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		dM.signalModel.addSignalListener(this);
		dM.addViewListener(this);
		
		reticuleItem = new MenuItem(viewSubMenu,SWT.CHECK);
		reticuleItem.setText("Gitternetz zeigen");
		reticuleItem.setSelection(true);
		reticuleItem.setImage(im.get(ImageManager.ico_view_grid));
		reticuleItem.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_reticule_Clicked();}});
		
		centerLineItem = new MenuItem(viewSubMenu,SWT.CHECK);
		centerLineItem.setText("Mittellinie zeigen");
		centerLineItem.setSelection(false);
		centerLineItem.setImage(im.get(ImageManager.ico_view_separator));
		centerLineItem.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_centerLine_Clicked();}});
		
		goToSample = new MenuItem(viewSubMenu,SWT.PUSH);
		goToSample.setText("Gehe zu...");
		goToSample.setImage(im.get(ImageManager.ico_view_goto));
		goToSample.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_goToSample_Clicked();}});
		
		signalChanged(null);
	}
	
	/** utility function to deselect all amplitude-view menu items */
	private void deselectAmplitudeMenuItems()
	{
		for (MenuItem m:amplitudeMenuItems)
		{
			m.setSelection(false);
		}
	}
	
	/** utility function to select one amplitude-view menu items 
	 * 
	 * @param index index of the menuitem to select
	 * */
	private void selectAmplitudeMenuItem(int index)
	{
		deselectAmplitudeMenuItems();
		amplitudeMenuItems[index].setSelection(true);
		amplitudeMenuItems[amplitudeMenuItems.length-1].setText(getUserTextForAmplitude());
	}
	
	/** This method is called if some amplitude-menuitem is clicked 
	 * 
	 * @param ZoomIndex Zoom-Index that was selected  
	 */
	private void Button_AmplitudeZoom_Clicked(int ZoomIndex)
	{
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setZoomLevelY(ZoomIndex,Constants.NOTIFICATION);
	}
	
	private void Button_AmplitudeUserZoom_Clicked()
	{
		Dialog_InputBox dInput = new Dialog_InputBox(shell);
		double zY=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_Y[SignalViewerModel.userZoomY];
		Integer result=dInput.open(
				"Amplitude mm/mV",
				"Bitte geben Sie eine benutzerdefinierte Zahl an.",
				1,
				200,
				(int) zY,
				Constants.NoSampleSelect);
		if (result!=null)
		{
			Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_Y[SignalViewerModel.userZoomY]=result;
			Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setZoomLevelY(SignalViewerModel.userZoomY, Constants.NOTIFICATION);
		}
	}
	
	/** utility function to deselect all feed-view menu items */
	private void deselectFeedMenuItems()
	{
		for (MenuItem m:feedMenuItems)
		{
			m.setSelection(false);
		}
	}
	
	/** utility function to select one feed-view menu items 
	 * 
	 * @param index index of the menuitem to select
	 * */
	private void selectFeedMenuItem(int index)
	{
		deselectFeedMenuItems();
		feedMenuItems[index].setSelection(true);
		feedMenuItems[feedMenuItems.length-1].setText(this.getUserTextForFeed());
	}
	
	private void Button_FeedZoomUser_Clicked()
	{
		Dialog_InputBox dInput = new Dialog_InputBox(shell);
		double zX=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_X[SignalViewerModel.userZoomX];
		Integer result=dInput.open(
				"Vorschub mm/ms",
				"Bitte geben Sie eine benutzerdefinierte Zahl an.",
				1,
				200,
				(int) zX,
				Constants.NoSampleSelect);
		if (result!=null)
		{
			Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.ZOOM_LEVEL_X[SignalViewerModel.userZoomX]=result;
			Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setZoomLevelX(SignalViewerModel.userZoomX, Constants.NOTIFICATION);
		}
	}
	
	/** This method is called if some feed-menuitem is clicked 
	 * 
	 * @param ZoomIndex Zoom-Index that was selected  
	 */
	private void Button_FeedZoom_Clicked(int ZoomIndex)
	{
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setZoomLevelX(ZoomIndex,Constants.NOTIFICATION);
	}
	
	/** This method is called if trigger-menuitem is clicked 
	 */
	private void Button_Trigger_Clicked(EventEntry eventEntry)
	{
		if(Common.getInstance().triggerModel.isActiveEventEntryChanged()){
			Common.getInstance().mc.menu_save_triggers_as();
		}
		Common.getInstance().triggerModel.setActiveEventEntry(eventEntry);
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
	
	private void Button_Secondary_Trigger_Clicked(EventEntry eventEntry){
		if(Common.getInstance().triggerModel.getSecondaryEventEntry() == eventEntry){
			Common.getInstance().triggerModel.setSecondaryEventEntry(null);
		}else{
			Common.getInstance().triggerModel.setSecondaryEventEntry(eventEntry);
		}
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
	
	/** This method is called if artefact-menuitem is clicked 
	 */
	private void Button_Artefact_Clicked(EventEntry eventEntry)
	{
		if(Common.getInstance().artefactModel.isActiveEventEntryChanged() || Common.getInstance().artefactModel.isActiveEventEntryTemp())
			Common.getInstance().mc.menu_save_artefacts_as();
		Common.getInstance().artefactModel.setActiveEventEntry(eventEntry);
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
		
	/** This method is called if some channel menuitem is selected
	 * 
	 * @param e SelectionEvent for the selected Menuitem 
	 */
	private void Button_Channel_Clicked(SelectionEvent e){
		ChannelData channel = (ChannelData)((MenuItem)e.getSource()).getData();
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setPaintChannel(channel.getNumber() , ((MenuItem)e.getSource()).getSelection());
		buildChannelPolarityList(channelPolaritySubMenu);
	}
	
	private void Button_Channel_Polarity_Clicked(SelectionEvent e){
		ChannelData channel = (ChannelData)((MenuItem)e.getSource()).getData();
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setChannelPolarityReversed(channel.getNumber() , ((MenuItem)e.getSource()).getSelection());
	}
	
	/** This method is called if reticule-menuitem is clicked 
	 */
	private void Button_reticule_Clicked()
	{
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setShowGrid(this.reticuleItem.getSelection(), Constants.NOTIFICATION);
	}
	
	/** This method is called if centerLine-menuitem is clicked 
	 * @deprecated 
	 */
	private void Button_centerLine_Clicked()
	{
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setShowCenterLine(this.centerLineItem.getSelection(), Constants.NOTIFICATION);
	}
	
	private void Button_goToSample_Clicked(){
		Dialog_GoToSample goToSampleDialog = new Dialog_GoToSample(new Shell(SWT.DIALOG_TRIM));
		goToSampleDialog.open();
	}
	
	/** utility function to complete build all channels 
	 * 
	 * @param channelSubMenu SubMenu to insert channels
	 */
	private void buildChannelList(Menu channelSubMenu)
	{	
		for (MenuItem m:channelMenuItemList)
		{
			if (m!=null)
			{
				m.dispose();
				m=null;
			}
		}
		channelMenuItemList.clear();
		ArrayList<ChannelData> l = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.signalModel.getChannelList();
		if (!l.isEmpty())
		{
			for (ChannelData c:l)
			{
				MenuItem m = new MenuItem(channelSubMenu,SWT.CHECK);
				m.setText("Kanal "+c.getName());
				m.setData(c);
				m.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_Channel_Clicked(e);}});
				boolean paint=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.isPaintChannel(c.getNumber());
				m.setSelection(paint);
				channelMenuItemList.add(m);
			}
		}
	}
	
	private void buildChannelPolarityList(Menu channelPolaritySubMenu)
	{	
		for (MenuItem m:channelPolarityMenuItemList)
		{
			if (m!=null)
			{
				m.dispose();
				m=null;
			}
		}
		channelPolarityMenuItemList.clear();
		ArrayList<ChannelData> l = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.signalModel.getChannelList();
		if (!l.isEmpty())
		{
			for (ChannelData c:l)
			{
				if(Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.isPaintChannel(c.getNumber())){
					MenuItem m = new MenuItem(channelPolaritySubMenu,SWT.CHECK);
					m.setText("Kanal "+c.getName());
					m.setData(c);
					m.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_Channel_Polarity_Clicked(e);}});
					boolean paint=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.isChannelPolarityReversed(c.getNumber());
					m.setSelection(paint);
					channelPolarityMenuItemList.add(m);
				}
			}
		}
	}
	
	public void buildTriggerList(){
		for( MenuItem triggerMenuItem : triggerMenuItemList){
			if (triggerMenuItem!=null){
				triggerMenuItem.dispose();
				triggerMenuItem=null;
			}
		}
		triggerMenuItemList.clear();
		
		if(secondaryTriggerMenuItem != null){
			for (MenuItem m:secondaryTriggerMenuItemList){
				if (m!=null){
					m.dispose();
					m=null;
				}
			}
			secondaryTriggerMenuItemList.clear();
			secondaryTriggerMenuItem.dispose();
			secondaryTriggerSubMenu.dispose();
		}
		
		EventEntry activeTriggerEntry = Common.getInstance().triggerModel.getActiveEventEntry();
		EventEntry activeSecondaryEntry = Common.getInstance().triggerModel.getSecondaryEventEntry();
		
		List<EventEntry> triggerEntries = Common.getInstance().triggerModel.getEventEntries();
		if (!triggerEntries.isEmpty()){
			for( int i=0 ; i<triggerEntries.size() ; i++){
				final EventEntry eventEntry = triggerEntries.get(i);
				MenuItem triggerMenuItem = new MenuItem(triggerSubMenu,SWT.CHECK);
				triggerMenuItem.setText(String.format("Triggerliste %s (%s)", eventEntry.getComment() != null ? eventEntry.getComment() : "", eventEntry.getId()));
				triggerMenuItem.addSelectionListener(	new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Button_Trigger_Clicked(eventEntry);
					}
				});
				boolean paint = (activeTriggerEntry != null) && (activeTriggerEntry.getId() == eventEntry.getId());
				triggerMenuItem.setSelection(paint);
				
				triggerMenuItemList.add(triggerMenuItem);
			}
		}
		if(triggerEntries.size() > 1){
			secondaryTriggerMenuItem = new MenuItem(triggerSubMenu,SWT.CASCADE);
			secondaryTriggerMenuItem.setText("weitere Triggerliste");
			
			secondaryTriggerSubMenu = new Menu(triggerSubMenu);
			secondaryTriggerMenuItem.setMenu(secondaryTriggerSubMenu);
			
			if (!triggerEntries.isEmpty()){
				for( int i=0 ; i<triggerEntries.size() ; i++){
					final EventEntry eventEntry = triggerEntries.get(i);
					if(activeTriggerEntry != eventEntry){
						MenuItem secondaryTriggerMenuItem = new MenuItem(secondaryTriggerSubMenu, SWT.CHECK);
						secondaryTriggerMenuItem.setText(String.format("Triggerliste %s (%s)", eventEntry.getComment() != null ? eventEntry.getComment() : "", eventEntry.getId()));
						
						secondaryTriggerMenuItem.addSelectionListener(	new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								Button_Secondary_Trigger_Clicked(eventEntry);
							}
						});
						boolean paint = (activeSecondaryEntry != null) && (activeSecondaryEntry == eventEntry);
						secondaryTriggerMenuItem.setSelection(paint);
						
						secondaryTriggerMenuItemList.add(secondaryTriggerMenuItem);
					}
				}
			}
		}

	}
	
	public void buildArtefactList(){
		for (MenuItem m:artefactMenuItemList){
			if (m!=null){
				m.dispose();
				m=null;
			}
		}
		artefactMenuItemList.clear();
		EventEntry activeArtefactEntry = Common.getInstance().artefactModel.getActiveEventEntry();
		List<EventEntry> artefactEntries = Common.getInstance().artefactModel.getEventEntries();
		if(!artefactEntries.isEmpty()){
			for(int i=0 ; i < artefactEntries.size() ; i++){
				final EventEntry artefactEntry = artefactEntries.get(i);
				MenuItem m = new MenuItem(artefactSubMenu, SWT.CHECK);
				m.setText(String.format("Artefakt %s (%s)", artefactEntry.getComment() != null ? artefactEntry.getComment() : "", artefactEntry.getId()));
				
				m.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Button_Artefact_Clicked(artefactEntry);
					}
				});
				boolean paint = (activeArtefactEntry != null) && (activeArtefactEntry.getId() == artefactEntry.getId());
				m.setSelection(paint);
				
				artefactMenuItemList.add(m);
			}
		}
	}
	
	/** This method is called, if zoom was changed (Listener-Handler) */
	public void zoomChanged(ViewEvent e)
	{
		rebuildMenuCheckItems();
	}
	
	/** utility function to rebuild all amplitude and feed checkmarks */
	private void rebuildMenuCheckItems()
	{
		selectAmplitudeMenuItem(Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getZoomLevelY());
		selectFeedMenuItem(Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getZoomLevelX());
	}
	
	/** This method is called, if section was changed (Listener-Handler) */
	public void viewSectionChanged(ViewEvent e){}
	
	public void channelViewChanged(ViewEvent e){}
	
	/** This method is called, if signal was changed (Listener-Handler) */
	public void signalChanged(SignalEvent e)
	{
		buildChannelList(channelSubMenu);
		//buildTriggerList();
		//buildArtefactList();
		buildChannelPolarityList(channelPolaritySubMenu);
		rebuildMenuCheckItems();
	}

	@Override
	public void activeTriggerEntryChanged(TriggerModelEvent triggerModelEvent) {
		buildTriggerList();
	}
	
	@Override
	public void activeArtefactEntryChanged() {
		buildArtefactList();
	}

	@Override
	public void artefactSelected() {
		//nothing to do	
	}

	@Override
	public void selectedArtefactModified() {
		
	}
}
