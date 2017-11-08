package me.lumpchen.xafp.tool.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import me.lumpchen.xafp.AFPFileReader;
import me.lumpchen.xafp.PrintFile;

public class XAFPViewer {

	private NavigatorPanel navigatorPanel = new NavigatorPanel();
	private AFPPagePanel pagePanel = new AFPPagePanel();
	
	private JToolBar toolBar = new JToolBar();
	
	public static int[] ZOOM_RATIO = {25, 50, 75, 100, 150, 200, 300, 600, 1200};
	private int zoomIndex = 1;
	private int zoom;
	private ViewerParameter paras = new ViewerParameter();
	
	private File openedFile;
	
	public XAFPViewer() {
		JFrame frame = new JFrame("JFrame");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setJMenuBar(this.createMenuBar());

	    Container container = frame.getContentPane();
	    
	    JButton zoomIn = new JButton(new ZoomAction("ZoomIn", true));
	    zoomIn.setToolTipText("Zoom in");
	    this.toolBar.add(zoomIn);
	    
	    JButton zoomOut = new JButton(new ZoomAction("ZoomOut", false));
	    zoomOut.setToolTipText("zoom out");
	    this.toolBar.add(zoomOut);
	    
	    container.add(this.toolBar, BorderLayout.NORTH);
	    
		JScrollPane pageCanvasScrollPane = new JScrollPane();
		pageCanvasScrollPane.setViewportView(this.pagePanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	    splitPane.setDividerSize(3);
	    splitPane.setDividerLocation(300);
	    
	    splitPane.setLeftComponent(this.navigatorPanel);
	    splitPane.setRightComponent(pageCanvasScrollPane);
	    container.add(splitPane, BorderLayout.CENTER);
	    
		frame.setVisible(true);
	}
	
	class ZoomAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		private boolean zoomIn;
		
	    public ZoomAction(String action, boolean zoomIn)  {
	        super(action);
	        this.zoomIn = zoomIn;
	        this.putValue(SHORT_DESCRIPTION, zoomIn ? "Zoom in" : "Zoom out");
	        this.putValue(MNEMONIC_KEY, zoomIn ? KeyEvent.VK_ADD : KeyEvent.VK_MINUS);
	    }
	    
	    @Override
	    public void actionPerformed(ActionEvent e)  { 
	        if (zoomIn) {
	        	if (zoomIndex < ZOOM_RATIO.length - 1) {
	        		zoom = ZOOM_RATIO[++zoomIndex];
	        		pagePanel.setZoom(zoom);
	        	}
	        } else {
	        	if (zoomIndex > 0) {
	        		zoom = ZOOM_RATIO[--zoomIndex];
	        		pagePanel.setZoom(zoom);
	        	}
	        }
	    }
	}
	
	private void openFile(File f) {
		if (this.openedFile == null) {
			this.openedFile = f;
		} else {
			this.closeFile();
		}
		
		AFPFileReader reader = new AFPFileReader();
		try {
			reader.read(f);
			PrintFile pf = reader.getPrintFile();
			paras.zoom = zoom;
			this.navigatorPanel.updateDocument(pf, pagePanel);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.openedFile = f;
	}
	
	private void closeFile() {
		this.navigatorPanel.closeFile();
		this.pagePanel.closeFile();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem openMenuItem = new JMenuItem("Open");
		JMenuItem closeMenuItem = new JMenuItem("Close");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		openMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileFilter filter = new FileFilter() {
					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}

						String fileName = f.getName().toLowerCase();
						if (fileName.toLowerCase().endsWith(".afp")) {
							return true;
						}

						return false;
					}

					@Override
					public String getDescription() {
						return "AFP Document";
					}
				};

				fileChooser.setFileFilter(filter);
				int returnValue = fileChooser.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					openFile(selectedFile);
				}
			}
		});
		
		closeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeFile();
			}
		});
		
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(openMenuItem);
		fileMenu.add(closeMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);

		JMenu helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		XAFPViewer viewer = new XAFPViewer();
	}
}
