package me.lumpchen.xafp.tool.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import me.lumpchen.xafp.Document;
import me.lumpchen.xafp.NoOperation;
import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.TagLogicalElement;

public class NavigatorPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
	private JList<String> docIndexList;
	private JList<String> pageIndexList;

	private static Object[] tleTableColumnHeaderLabels = new Object[]{"Name", "Value"};
	private DefaultTableModel tleTableModel;
	private JTable tleTable;
	
	private static Object[] nopTableColumnHeaderLabels = new Object[]{"Value"};
	private DefaultTableModel nopTableModel;
	private JTable nopTable;
	
	private PrintFile pf;
	private PageCanvas canvas;

	public NavigatorPanel() {
		super();
		this.setLayout(new BorderLayout());

		Border etchedLoweredBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

		this.docIndexList = new JList<String>();
		this.docIndexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Border titledBorderAtTop = BorderFactory.createTitledBorder(etchedLoweredBorder, "Document:", TitledBorder.LEFT,
				TitledBorder.TOP);
		JScrollPane docIndexScrollPane = new JScrollPane();
		docIndexScrollPane.setBorder(titledBorderAtTop);
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		this.docIndexList.setBorder(lineBorder);
		docIndexScrollPane.setViewportView(this.docIndexList);

		this.pageIndexList = new JList<String>();
		this.pageIndexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane pageIndexScrollPane = new JScrollPane();
		titledBorderAtTop = BorderFactory.createTitledBorder(etchedLoweredBorder, "Page:", TitledBorder.LEFT,
				TitledBorder.TOP);
		pageIndexScrollPane.setBorder(titledBorderAtTop);
		this.pageIndexList.setBorder(lineBorder);
		pageIndexScrollPane.setViewportView(this.pageIndexList);

		JPanel indexPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(indexPanel, BoxLayout.Y_AXIS);
		indexPanel.setLayout(boxLayout);

		indexPanel.add(docIndexScrollPane);
		indexPanel.add(pageIndexScrollPane);

		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.tabbedPane.add("Index", indexPanel);
		
		this.initTLETable();
		this.tabbedPane.add("TLE", new JScrollPane(this.tleTable));
		
		this.initNOPTable();
		this.tabbedPane.add("NOP", new JScrollPane(this.nopTable));

		this.add(tabbedPane, BorderLayout.CENTER);
	}
	
	private void initTLETable() {
		this.tleTableModel = new DefaultTableModel(tleTableColumnHeaderLabels, 0);
		this.tleTable = new JTable(this.tleTableModel);
	}
	
	private void initNOPTable() {
		this.nopTableModel = new DefaultTableModel(nopTableColumnHeaderLabels, 0);
		this.nopTable = new JTable(this.nopTableModel);
	}

	private void updateDocumentIndex() {
		List<Document> docs = this.pf.getDocuments();
		String[] elements = new String[docs.size()];
		for (int i = 0; i < docs.size(); i++) {
			elements[i] = (i + 1) + "";
		}
		this.docIndexList.setListData(elements);
		this.docIndexList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				int index = docIndexList.getSelectedIndex();
				if (index < 0) {
					return;
				}
				updatePageIndex(pf.getDocuments().get(index));
			}
		});
	}

	private void updatePageIndex(Document doc) {
		int pageCount = doc.getPageCount();
		String[] elements = new String[pageCount];
		for (int i = 0; i < pageCount; i++) {
			elements[i] = (i + 1) + "";
		}
		this.pageIndexList.setListData(elements);
		this.pageIndexList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				int pageIndex = pageIndexList.getSelectedIndex();
				if (pageIndex < 0) {
					return;
				}
				int docIndex = docIndexList.getSelectedIndex();
				canvas.updatePage(pf, docIndex, pageIndex);
			}
		});
	}

	private void updateTLE() {
		if (this.pf == null) {
			return;
		}
		List<TagLogicalElement> tleList = this.pf.getAllTLEs();
		for (int i = 0; i < tleList.size(); i++) {
			TagLogicalElement tle = tleList.get(i);
			this.tleTableModel.addRow(new String[]{tle.getAttributeName(), tle.getAttributeValue()});
		}
	}
	
	private void updateNOP() {
		if (this.pf == null) {
			return;
		}
		List<NoOperation> nopList = this.pf.getAllNOPs();
		for (int i = 0; i < nopList.size(); i++) {
			NoOperation nop = nopList.get(i);
			this.nopTableModel.addRow(new String[]{nop.getString()});
		}
	}
	
	public void updateDocument(PrintFile pf, PageCanvas canvas) {
		this.pf = pf;
		this.canvas = canvas;
		this.docIndexList.setListData(new String[0]);
		this.pageIndexList.setListData(new String[0]);
		this.tleTableModel.setRowCount(0);
		this.updateTLE();
		this.updateNOP();
		this.updateDocumentIndex(); 
	}
	
	public void closeFile() {
		this.docIndexList.setListData(new String[0]);
		this.pageIndexList.setListData(new String[0]);
		this.tleTableModel.setRowCount(0);
		this.nopTableModel.setRowCount(0);
	}
}
