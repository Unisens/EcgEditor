/**
 * This class manages some general system mouse-cursors
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public class Cursors {

	/** cursor that is shown while some operation is in progess */
	public static Cursor waitCursor=new Cursor(Display.getDefault(),SWT.CURSOR_WAIT);
	/** cursor that is shown if sth. can be sized horizontally*/
	public static Cursor sizeCursor=new Cursor(Display.getDefault(),SWT.CURSOR_SIZEE);
	/** cursor that is shown if sth. can be sized (in every orientation */
	public static Cursor sizeAllCursor=new Cursor(Display.getDefault(),SWT.CURSOR_SIZEALL);
	/** standard cursor */
	public static Cursor defaultCursor=null;
}
