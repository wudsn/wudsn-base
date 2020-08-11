package com.wudsn.tools.base.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JTable;

import com.wudsn.tools.base.common.TextUtility;

/**
 * Simple table printer.
 * 
 * @author Peter Dell
 * 
 * @see <a
 *      href="http://wiki.byte-welt.de/wiki/JTable_Druckfunktion_anpassen">JTable
 *      wiki</a>
 */
final class AttributeTablePrinter implements Printable {

    private Printable tablePrintable;
    private String headerText;
    private String subHeaderText;
    private String footerText;
    private PageFormat tablePageFormat;

    AttributeTablePrinter(final JTable table) {
	if (table == null) {
	    throw new IllegalArgumentException("Parameter 'table' must not be null.");
	}
	tablePrintable = table.getPrintable(JTable.PrintMode.FIT_WIDTH, null, null);
    }

    boolean print(String headerText, String subHeaderText, String footerText) throws PrinterException {
	if (headerText == null) {
	    throw new IllegalArgumentException("Parameter 'headerText' must not be null.");
	}
	if (subHeaderText == null) {
	    throw new IllegalArgumentException("Parameter 'subHeaderText' must not be null.");
	}
	if (footerText == null) {
	    throw new IllegalArgumentException("Parameter 'footerText' must not be null.");
	}
	this.headerText = headerText;
	this.subHeaderText = subHeaderText;
	this.footerText = footerText;
	PrinterJob job = PrinterJob.getPrinterJob();
	job.setPrintable(this);
	// create an attribute set to store attributes from the print dialog
	PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
	boolean printAccepted = job.printDialog(attr);
	if (printAccepted) {// if the user didn't cancel the dialog
	    job.print(attr);// do the printing
	    return true;
	}
	return false;

    }

     @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex) throws PrinterException {
	Graphics2D g = (Graphics2D) graphics;
	int x1 = (int) pageFormat.getImageableX();
	int y1 = (int) pageFormat.getImageableY();
	int w1 = (int) pageFormat.getImageableWidth();
	int h1 = (int) pageFormat.getImageableHeight();
	if (tablePageFormat == null) {
	    tablePageFormat = (PageFormat) pageFormat.clone();
	    Paper paperJTable = tablePageFormat.getPaper();
	    if (tablePageFormat.getOrientation() == PageFormat.PORTRAIT) {
		paperJTable.setImageableArea(x1, y1 + 60,// skip header area
			w1, h1 - 90);// reserve space for header and footer
	    } else {
		paperJTable.setImageableArea(y1 + 60, x1,// skip header area
			h1 - 90, w1);// reserve space for header and footer
	    }
	    tablePageFormat.setPaper(paperJTable);
	}
	String header = headerText;
	String subHeader = subHeaderText;
	Font f = g.getFont();
	g.setFont(g.getFont().deriveFont(15f));
	FontMetrics fm = g.getFontMetrics();
	g.drawString(header, x1 + (w1 - fm.stringWidth(header)) / 2, y1 + 15);
	g.setFont(f);
	fm = g.getFontMetrics();
	g.drawString(subHeader, x1 + (w1 - fm.stringWidth(subHeader)) / 2, y1 + 30);
	g.drawRect(x1, y1, w1, 40);
	String footer = TextUtility.format(footerText, TextUtility.formatAsDecimal(pageIndex + 1));
	g.drawString(footer, x1 + (w1 - fm.stringWidth(footer)) / 2, y1 + h1 - 10);
	// print the table:
	Graphics gCopy = g.create();
	int retVal = tablePrintable.print(gCopy, tablePageFormat, pageIndex);
	gCopy.dispose();

	return retVal;
    }
}
