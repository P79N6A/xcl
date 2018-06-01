package van.xcl;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import van.util.CommonUtils;
import van.util.evt.EventEntity;
import van.util.evt.EventHandler;
import van.util.evt.EventType;

public class XCLUI implements EventHandler {

	class XScrollBarUI extends BasicScrollBarUI {
		public XScrollBarUI() {
			super();
		}
		@Override
		protected void configureScrollBarColors() {
			trackColor = XCLConstants.backgroundColor;
		}
		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			super.paintTrack(g, c, trackBounds);
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			g.translate(thumbBounds.x, thumbBounds.y);
			g.setColor(XCLConstants.foregroundColor);
			g.drawRoundRect(5, 0, 6, thumbBounds.height - 1, 5, 5);
			Graphics2D g2 = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.addRenderingHints(rh);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
			g2.fillRoundRect(5, 0, 6, thumbBounds.height - 1, 5, 5);
		}
		@Override
		protected JButton createIncreaseButton(int orientation) {
			JButton button = super.createIncreaseButton(orientation);
			button.setBorder(null);
			button.setForeground(XCLConstants.foregroundColor);
			button.setBackground(XCLConstants.backgroundColor);
			return button;
		}
		@Override
		protected JButton createDecreaseButton(int orientation) {
			JButton button = super.createDecreaseButton(orientation);
			button.setBorder(null);
			button.setForeground(XCLConstants.foregroundColor);
			button.setBackground(XCLConstants.backgroundColor);
			return button;
		}
	}
	
	private JFrame frame;
	private XCLConsole console;
	private JTextPane textConsole;
	private XCLTextPane textInput;
	private JTextField textInputPrompt;
	private XCLTextPane textCmd;
	private JTextField textPrompt;
	private JPanel cardPanel = new JPanel();
	private CardLayout cardLayout = new CardLayout();
	private BufferedWriter logWriter;
	private Set<String> keys = new HashSet<String>();
	
	private LinkedBlockingQueue<String> textQueue = new LinkedBlockingQueue<String>();

	public XCLUI(XCLConsole console) {
		this.console = console;
	}

	protected void init() {
		try {
			ImageIcon icon = new ImageIcon(XCLUI.class.getResource(XCLConstants.ICON_IMAGE_PATH));
			getFrame().setIconImage(icon.getImage());  
			getFrame().setSize(1000, 600);
			getFrame().setResizable(true);
			getFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			getFrame().addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					console.exit(0);
				}
				public void windowDeiconified(WindowEvent e) {
					requestFocus(getTextCmd());
				}
			});
			this.cardPanel.setLayout(cardLayout);
			this.cardPanel.add("console", getConsolePanel());
			this.cardPanel.add("input", getInputPanel());
			getFrame().setLayout(new BorderLayout(0, 0));
			getFrame().add(getTextPrompt(), BorderLayout.SOUTH);
			getFrame().add(cardPanel, BorderLayout.CENTER);
			getFrame().setVisible(true);
			console.output(XCLConstants.VERSION_PROMPT);
			initLogger();
			for (String cmdKey : console.commands().keySet()) {
				keys.add(cmdKey);
			}
		} catch (HeadlessException e) {
			e.printStackTrace();
		}
	}
	
	private void initLogger() {
		File logFile = new File(XCLConstants.LOG_FILE);
		try {
			if (logWriter != null) {
				logWriter.close();
				logFile.delete();
			}
			logWriter = new BufferedWriter(new FileWriter(logFile));
			console(XCLConstants.INFO_PROMPT + " - " + logFile.getAbsolutePath() + "\n", XCLConstants.promptColor);
		} catch (IOException e) {
			console("Failed to init the log file: " + e.getMessage(), XCLConstants.errorColor);
		}
	}
	
	private JFrame getFrame() {
		if (this.frame == null) {
			this.frame = new JFrame();
		}
		return this.frame;
	}
	
	private JPanel getInputPanel() {
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout(0, 0));
		inputPanel.add(getTextInputPrompt(), BorderLayout.NORTH);
		inputPanel.add(getScrollInput(), BorderLayout.CENTER);
		return inputPanel;
	}
	
	private JPanel getConsolePanel() {
		JPanel consolePanel = new JPanel();
		consolePanel.setBackground(XCLConstants.backgroundColor);
		consolePanel.setLayout(new BorderLayout(0, 0));
		consolePanel.add(getScrollConsole(), BorderLayout.CENTER);
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBorder(null);
		label.setBackground(XCLConstants.backgroundColor);
		label.setForeground(XCLConstants.foregroundColor);
		label.setFont(getDefaultFont());
		label.setText(" > ");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
//		panel.add(getTextCmd(), BorderLayout.CENTER);
		panel.add(getScrollCmd(), BorderLayout.CENTER);
		panel.add(label, BorderLayout.WEST);
		consolePanel.add(panel, BorderLayout.SOUTH);
		return consolePanel;
	}
	
	private JScrollPane getScrollConsole() {
		JScrollPane pane = new JScrollPane(getTextConsole());
		pane.setBorder(null);
		pane.setBackground(XCLConstants.backgroundColor);
		JScrollBar vb = pane.getVerticalScrollBar();
		JScrollBar hb = pane.getHorizontalScrollBar();
		vb.setUI(new XScrollBarUI());
		hb.setUI(new XScrollBarUI());
		return pane;
	}

	private JScrollPane getScrollInput() {
		JScrollPane pane = new JScrollPane(getTextInput());
		pane.setBorder(null);
		pane.setBackground(XCLConstants.backgroundColor);
		JScrollBar vb = pane.getVerticalScrollBar();
		JScrollBar hb = pane.getHorizontalScrollBar();
		vb.setUI(new XScrollBarUI());
		hb.setUI(new XScrollBarUI());
		return pane;
	}
	
	private JScrollPane getScrollCmd() {
		JScrollPane pane = new JScrollPane(getTextCmd());
		pane.setBorder(null);
		pane.setBackground(XCLConstants.backgroundColor);
		JScrollBar vb = pane.getVerticalScrollBar();
		JScrollBar hb = pane.getHorizontalScrollBar();
		vb.setUI(new XScrollBarUI());
		hb.setUI(new XScrollBarUI());
		return pane;
	}
	
	private XCLTextPane getTextCmd() {
		if (textCmd == null) {
//			textCmd = new JTextField();
			textCmd = new XCLTextPane(keys);
			textCmd.setEditable(false);
			textCmd.setBorder(null);
			textCmd.setBackground(XCLConstants.backgroundColor);
			textCmd.setForeground(XCLConstants.foregroundColor);
			textCmd.setCaretColor(XCLConstants.foregroundColor);
			textCmd.setFont(getDefaultFont());
			textCmd.setPreferredSize(new Dimension(0, 25));
			textCmd.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						String cmdStr = CommonUtils.trim(textCmd.getText());
						if (!CommonUtils.isEmpty(cmdStr)) {
							console.input(cmdStr);
							console.run(cmdStr);
							console.present(null);
						} else {
							// clear
							textCmd.setText(null);
						}
					}
				}
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
						console.cancelCommand();
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (e.getModifiers() == InputEvent.ALT_MASK) {
							if (getTextCmd().isEditable()) {
								int idx = console.getHistoryIndex();
								if (e.getKeyCode() == KeyEvent.VK_UP) {
									idx--;
								} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
									idx++;
								}
								if (idx > -1 && idx < console.getHistorySize()) {
									String cmdStr = console.getHistory(idx);
									console.present(cmdStr);
									console.setHistoryIndex(idx);
								}
							}
						} else {
							if (e.getKeyCode() == KeyEvent.VK_UP) {
								requestFocus(getTextConsole());
							}
						}
					}
				}
			});
		}
		return textCmd;
	}
	
	private JTextField getTextPrompt() {
		if (textPrompt == null) {
			textPrompt = new JTextField();
			textPrompt.setEditable(false);
			textPrompt.setBorder(null);
			textPrompt.setBackground(XCLConstants.backgroundColor);
			textPrompt.setForeground(XCLConstants.promptColor);
			textPrompt.setCaretColor(XCLConstants.foregroundColor);
			textPrompt.setFont(getDefaultFont());
			textPrompt.setPreferredSize(new Dimension(0, 25));
		}
		return textPrompt;
	}
	
	private JTextField getTextInputPrompt() {
		if (textInputPrompt == null) {
			textInputPrompt = new JTextField();
			textInputPrompt.setEditable(false);
			textInputPrompt.setBorder(null);
			textInputPrompt.setBackground(XCLConstants.backgroundColor);
			textInputPrompt.setForeground(XCLConstants.commentColor);
			textInputPrompt.setCaretColor(XCLConstants.foregroundColor);
			textInputPrompt.setFont(getDefaultFont());
			textInputPrompt.setPreferredSize(new Dimension(0, 25));
		}
		return textInputPrompt;
	}
	
	private void console(String str, Color color) {
		try {
			SimpleAttributeSet attr = new SimpleAttributeSet();
			Font font = getDefaultFont();
			StyleConstants.setFontSize(attr, font.getSize());
			StyleConstants.setFontFamily(attr, font.getFamily());
			StyleConstants.setForeground(attr, color);
			Document docs = getTextConsole().getDocument();
			docs.insertString(docs.getLength(), str, attr);
			getTextConsole().setCaretPosition(getTextConsole().getDocument().getLength());
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		if (logWriter != null) {
			try {
				logWriter.append(str);
				logWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private XCLTextPane getTextInput() {
		if (textInput == null) {
			textInput = new XCLTextPane(keys);
			textInput.setBackground(XCLConstants.backgroundColor);
			textInput.setForeground(XCLConstants.foregroundColor);
			textInput.setSelectionColor(XCLConstants.selectionColor);
			textInput.setCaretColor(XCLConstants.foregroundColor);
			textInput.setFont(getDefaultFont());
			textInput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if ((e.isControlDown() || e.isShiftDown()) && e.getKeyChar() == KeyEvent.VK_ENTER) {
						String text = textInput.getText();
						//textQueue.add(text);
						console.setTextInput(text);
					} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
						//textQueue.add(XCLConstants.ESC);
						console.setTextInput(XCLConstants.ESC);
					}
				}
			});
			textInput.setEditable(true);
		}
		return textInput;
	}

	private JTextPane getTextConsole() {
		if (textConsole == null) {
			textConsole = new JTextPane();
			textConsole.setEditable(false);
			textConsole.setBorder(null);
			textConsole.setBackground(XCLConstants.backgroundColor);
			textConsole.setForeground(XCLConstants.foregroundColor);
			textConsole.setSelectionColor(XCLConstants.selectionColor);
			textConsole.setCaretColor(XCLConstants.foregroundColor);
			textConsole.setFont(getDefaultFont());
			textConsole.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					textConsole.getCaret().setVisible(true);
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
			});
			textConsole.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					String selectedText = textConsole.getSelectedText();
					if (selectedText != null && selectedText.length() > 0) {
						Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
						Transferable text = new StringSelection(selectedText);
						clip.setContents(text, null);
					}
					console.prepare();
				}
				@Override
				public void keyPressed(KeyEvent e) {
					/*
					if (e.getModifiers() == InputEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_C) {
						console.cancelCommand();
					}
					*/
					if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
						console.cancelCommand();
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (textConsole.getDocument().getLength() == textConsole.getCaretPosition()) {
							console.prepare();
						}
					}
				}
			});
			// -----------
			textConsole.setTransferHandler(new TransferHandler() {
				private static final long serialVersionUID = 1L;
				@Override
				public boolean importData(JComponent comp, Transferable t) {
					try {
						Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
						String filepath = o.toString();
						if (filepath.startsWith("[")) {
							filepath = filepath.substring(1);
						}
						if (filepath.endsWith("]")) {
							filepath = filepath.substring(0, filepath.length() - 1);
						}
						String runfile = XCLConstants.RUNFILE_COMMAND + " " + filepath;
						console.input(runfile);
						console.run(runfile);
						console.present(null);
						return true;
					} catch (Exception e) {
						console.error(CommonUtils.getStackTrace(e));
					}
					return false;
				}
				@Override
				public boolean canImport(JComponent comp, DataFlavor[] flavors) {
					for (int i = 0; i < flavors.length; i++) {
						if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
							return true;
						}
					}
					return false;
				}
			});
			// -------------
		}
		return textConsole;
	}
	
	private Font getDefaultFont() {
		return XCLConstants.DEFAULT_FONT;
	}
	
	private void requestFocus(JTextComponent text) {
		text.requestFocus();
		text.setCaretPosition(text.getDocument().getLength());
		text.setCaretColor(XCLConstants.foregroundColor);
	}
	
	// ---
	
	public void dispose() {
		this.frame.setVisible(false);
		this.frame.dispose();
	}
	
	public void addKey(String key) {
		this.keys.add(key);
	}
	
	@Override
	public boolean prepareEvent(EventEntity e) {
		if (this.console.getSource().equals(e.getSource())) {
			if (this.console.isConnected() || this.console.isAccepted()) {
				this.console.syncEvent(e); // synchronize local UI events to remote except XCLEvent.present
			}
		}
		return true;
	}
	
	@Override
	public String handleEvent(EventEntity event) {
		EventType type = event.getType();
		String message = event.getMessage();
		String source = !console.getSource().equals(event.getSource()) ? "[" + event.getSource() + "]: " : "";
		System.out.println("[" + console.getSource() + "] XCLUI.handleEvent [type: " + type + ", message: " + message + "]");
		if (XCLEvent.input.equals(type)) {
			console(XCLConstants.IN_PROMPT + source + message + "\n", XCLConstants.foregroundColor); // source
		} else if (XCLEvent.output.equals(type)) {
			console(XCLConstants.OUT_PROMPT + source + message + "\n", XCLConstants.foregroundColor); // source
		} else if (XCLEvent.info.equals(type)) {
			console(XCLConstants.INFO_PROMPT + source + message + "\n", XCLConstants.promptColor); // source
		} else if (XCLEvent.error.equals(type)) {
			console(XCLConstants.ERROR_PROMPT + source + message + "\n", XCLConstants.errorColor); // source
		} else if (XCLEvent.prompt.equals(type)) {
			getTextPrompt().setText(source + message + "  "); // source
		} else if (XCLEvent.title.equals(type)) {
			getFrame().setTitle(source + message); // source
		} else if (XCLEvent.textTitle.equals(type)) {
			getTextInputPrompt().setText(" - " + source + message); // source
		} else if (XCLEvent.prepare.equals(type)) {
			requestFocus(getTextCmd());
			getTextCmd().discardAllEdits();
		} else if (XCLEvent.clear.equals(type)) {
			getTextConsole().setText("");
			getTextConsole().setCaretPosition(getTextConsole().getDocument().getLength());
			initLogger();
		} else if (XCLEvent.editable.equals(type)) {
			boolean editable = Boolean.valueOf(message);
			getTextCmd().setEditable(editable);
			requestFocus(getTextCmd());
		} else if (XCLEvent.present.equals(type)) {
			getTextCmd().setText(message);
			requestFocus(getTextCmd());
		} else if (XCLEvent.textInput.equals(type)) {
			cardLayout.show(cardPanel, "input");
			getTextInput().setText(message);
			getTextInput().discardAllEdits();
			requestFocus(getTextInput());
		} else if (XCLEvent.setTextInput.equals(type)) {
			textQueue.add(message);
		} else if (XCLEvent.getTextInput.equals(type)) {
			try {
				String inputText = textQueue.take();
				cardLayout.show(cardPanel, "console");
				if (!XCLConstants.ESC.equals(inputText)) {
					return inputText;
				}
				return message;
			} catch (InterruptedException e) {
				console.error(CommonUtils.getStackTrace(e));
			}
		}
		return null;
	}

}
