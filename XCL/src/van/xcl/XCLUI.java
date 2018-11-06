package van.xcl;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;

import van.util.CommonUtils;
import van.util.evt.EventEntity;
import van.util.evt.EventHandler;
import van.util.evt.EventType;

public class XCLUI implements EventHandler {

	private Logger logger = Logger.getLogger(getClass());
	
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
	
	enum ConsoleType {
		input, output, info, error
	}
	
	enum PaneType {
		input,
		console
	}
	
	private static final String KEY_SEARCH_TEXT = "search_text";
	private static final String KEY_SEARCH_ROW = "search_row";
	private static final String KEY_SEARCH_ROW_OFFSET = "search_row_offset";
	
	private JFrame frame;
	private XCLConsole console;
	private XCLTextPane textConsole;
	private XCLTextInputPane textInput;
	private JTextField textInputPrompt;
	private XCLTextInputPane textCmd;
	private JTextField textPrompt;
	private JPanel cardPanel;
	private CardLayout cardLayout;
	private BufferedWriter logWriter;
	private XCLTextKeys keys = new XCLTextKeys();
	private Map<Integer, Integer> fixedRows = new ConcurrentHashMap<Integer, Integer>();
	
	private LinkedBlockingQueue<String> textQueue = new LinkedBlockingQueue<String>();

	public XCLUI(XCLConsole console) {
		this.console = console;
	}

	protected void init() {
		try {
			// inits frame components
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
			getCardPanel().setLayout(getCardLayout());
			getCardPanel().add(PaneType.console.name(), getConsolePanel());
			getCardPanel().add(PaneType.input.name(), getInputPanel());
			getFrame().setLayout(new BorderLayout(0, 0));
			getFrame().add(getTextPrompt(), BorderLayout.SOUTH);
			getFrame().add(getCardPanel(), BorderLayout.CENTER);
			getFrame().setVisible(true);
			// prints version prompt
			console.output(XCLConstants.VERSION_PROMPT);
			// inits out file
			File outFile = initOutFile();
			console(ConsoleType.info, -1, " - " + outFile.getAbsolutePath());
			// loads command keys
			for (String cmdKey : console.commands().keySet()) {
				keys.getKeys().add(cmdKey);
			}
		} catch (HeadlessException e) {
			console.output("HeadlessException found");
		}
	}
	
	private File initOutFile() {
		File outFile = new File(XCLConstants.OUT_FILE);
		try {
			if (logWriter != null) {
				logWriter.close();
				outFile.delete();
			}
			logWriter = new BufferedWriter(new FileWriter(outFile));
			return outFile;
		} catch (IOException e) {
			console(ConsoleType.error, -1, "Failed to init the log file: " + e.getMessage());
		}
		return null;
	}
	
	private JFrame getFrame() {
		if (this.frame == null) {
			this.frame = new JFrame();
		}
		return this.frame;
	}
	
	private JPanel getCardPanel() {
		if (this.cardPanel == null) {
			this.cardPanel = new JPanel();
		}
		return this.cardPanel;
	}
	
	private CardLayout getCardLayout() {
		if (this.cardLayout == null) {
			this.cardLayout = new CardLayout();
		}
		return this.cardLayout;
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
		panel.add(getScrollCmd(), BorderLayout.CENTER);
		panel.add(label, BorderLayout.WEST);
		consolePanel.add(panel, BorderLayout.SOUTH);
		return consolePanel;
	}
	
	private JScrollPane getScrollConsole() {
		JScrollPane pane = new JScrollPane(getTextConsole());
		pane.setBorder(null);
		pane.setBackground(XCLConstants.backgroundColor);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollBar vb = pane.getVerticalScrollBar();
		// JScrollBar hb = pane.getHorizontalScrollBar();
		vb.setUI(new XScrollBarUI());
		// hb.setUI(new XScrollBarUI());
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
	
	private JTextField getTextPrompt() {
		if (textPrompt == null) {
			textPrompt = new JTextField();
			textPrompt.setEditable(false);
			textPrompt.setBorder(null);
			textPrompt.setBackground(XCLConstants.backgroundColor);
			textPrompt.setForeground(XCLConstants.promptColor);
			textPrompt.setCaretColor(XCLConstants.caretColor);
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
			textInputPrompt.setCaretColor(XCLConstants.caretColor);
			textInputPrompt.setFont(getDefaultFont());
			textInputPrompt.setPreferredSize(new Dimension(0, 25));
		}
		return textInputPrompt;
	}
	
	private SimpleAttributeSet getConsoleAttr(Color color, Font font) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, font.getSize());
		StyleConstants.setFontFamily(attr, font.getFamily());
		StyleConstants.setForeground(attr, color);
		return attr;
	}
	
	private synchronized void console(ConsoleType type, int traceId, String str) {
		Color color = XCLConstants.foregroundColor;
		String prefix = XCLConstants.OUT_PROMPT;
		if (ConsoleType.input.equals(type)) {
			color = XCLConstants.foregroundColor;
			prefix = XCLConstants.IN_PROMPT;
		} else if (ConsoleType.output.equals(type)) {
			color = XCLConstants.foregroundColor;
			prefix = XCLConstants.OUT_PROMPT;
		} else if (ConsoleType.info.equals(type)) {
			color = XCLConstants.promptColor;
			prefix = XCLConstants.INFO_PROMPT;
			str = CommonUtils.resolveString(str, 150); // trim length for INFO
		} else if (ConsoleType.error.equals(type)) {
			color = XCLConstants.errorColor;
			prefix = XCLConstants.ERROR_PROMPT;
		}
		str = prefix + str + "\n";
		try {
			SimpleAttributeSet attr = getConsoleAttr(color, getDefaultFont());
			Document document = getTextConsole().getDocument();
			int offset = -1;
			int textLength = str.length();
			if (fixedRows.containsKey(traceId)) {
				offset = fixedRows.get(traceId);
				document.remove(offset, document.getLength() - offset);
				document.insertString(offset, str, attr);
			} else {
				offset = document.getLength();
				document.insertString(offset, str, attr);
			}
			int length = document.getLength();
			getTextConsole().setCaretPosition(length);
			if (ConsoleType.input.equals(type)) {
				getTextConsole().handleRow(offset, textLength);
			}
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, CommonUtils.getStackTrace(e));
		}
		if (logWriter != null) {
			try {
				logWriter.append(str);
				logWriter.flush();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, CommonUtils.getStackTrace(e));
			}
		}
	}
	
	private void clearFixedRows() {
		fixedRows.clear();
	}
	
	private synchronized void fixedRow(int traceId, boolean fixRow) {
		if (fixRow) {
			int position = getTextConsole().getDocument().getLength();
			fixedRows.put(traceId, position);
		} else {
			fixedRows.remove(traceId);
		}
	}
	
	private XCLTextInputPane getTextCmd() {
		if (textCmd == null) {
//			textCmd = new JTextField();
			textCmd = new XCLTextInputPane(keys);
			textCmd.setCaret(new XCLCaret());
			textCmd.setEditable(true);
			textCmd.setBorder(null);
			textCmd.setBackground(XCLConstants.backgroundColor);
			textCmd.setForeground(XCLConstants.foregroundColor);
			textCmd.setCaretColor(XCLConstants.caretColor);
			textCmd.setFont(getDefaultFont());
			textCmd.setPreferredSize(new Dimension(0, 25));
			textCmd.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
				}
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
						e.consume();
						clearFixedRows(); // fixed position error
						console.cancelCommand();
					} else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						e.consume();
						String cmd = CommonUtils.trim(textCmd.getText());
						if (!CommonUtils.isEmpty(cmd)) {
							console.input(cmd); // save historic command
							console.run(cmd);
							console.present(null);
						} else {
							// clear
							textCmd.setText(null);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_F1) {
						String cmd = CommonUtils.trim(textCmd.getText());
						if (!CommonUtils.isEmpty(cmd)) {
							textCmd.setAttribute(KEY_SEARCH_TEXT, cmd);
							textCmd.setAttribute(KEY_SEARCH_ROW, 0);
							textCmd.setAttribute(KEY_SEARCH_ROW_OFFSET, 0);
							console.prompt(cmd);
							requestFocus(getTextConsole());
						}
					} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
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
	
	private XCLTextInputPane getTextInput() {
		if (textInput == null) {
			textInput = new XCLTextInputPane(keys);
			textInput.setCaret(new XCLCaret());
			textInput.setBackground(XCLConstants.backgroundColor);
			textInput.setForeground(XCLConstants.foregroundColor);
			textInput.setSelectionColor(XCLConstants.selectionColor);
			textInput.setCaretColor(XCLConstants.caretColor);
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

	private XCLTextPane getTextConsole() {
		if (textConsole == null) {
			textConsole = new XCLTextPane(keys);
			textConsole.setCaret(new XCLCaret());
			textConsole.setEditable(false);
			textConsole.setBorder(null);
			textConsole.setBackground(XCLConstants.backgroundColor);
			textConsole.setForeground(XCLConstants.foregroundColor);
			textConsole.setSelectionColor(XCLConstants.selectionColor);
			textConsole.setCaretColor(XCLConstants.caretColor);
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
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						String selectedText = textConsole.getSelectedText();
						if (selectedText != null && selectedText.length() > 0) {
							Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
							Transferable text = new StringSelection(selectedText);
							clip.setContents(text, null);
							console.prompt("The text copied to clipboard: \"" + selectedText + "\"");
						}
					}
				}
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
						clearFixedRows(); // fixed position error
						console.cancelCommand();
					} else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						console.prepare();
					} else if (e.getKeyCode() == KeyEvent.VK_F1) {
						String searchText = (String) getTextCmd().getAttribute(KEY_SEARCH_TEXT);
						Integer searchRow = (Integer) getTextCmd().getAttribute(KEY_SEARCH_ROW);
						Integer searchRowOffset = (Integer) getTextCmd().getAttribute(KEY_SEARCH_ROW_OFFSET);
						console.prompt("Search text: " + searchText);
						if (!CommonUtils.isEmpty(searchText) && searchRow != null && searchRowOffset != null) {
							Document document = textConsole.getDocument();
							Element root = document.getDefaultRootElement();
							int rowCount = root.getElementCount();
							boolean isSuccess = false;
							for (int i = searchRow; i < rowCount ; i++) {
								System.out.println("row: " + i);
								Element row = root.getElement(i);
								int start = row.getStartOffset();
								int end = row.getEndOffset() - 1;
								try {
									String line = document.getText(start, end - start);
									int index = line.toLowerCase().indexOf(searchText.toLowerCase(), 
											(searchRow == i) ? searchRowOffset : 0);
									if (index > -1) {
										int startOffset = start + index;
										int endOffset = startOffset + searchText.length();
										getTextConsole().setSelectionStart(startOffset);
										getTextConsole().setSelectionEnd(endOffset);
										getTextCmd().setAttribute(KEY_SEARCH_ROW, i);
										getTextCmd().setAttribute(KEY_SEARCH_ROW_OFFSET, index + searchText.length());
										console.prompt("Search text: " + searchText + " - 1 matches in Console, row: " + i + ", offset: " + index);
										isSuccess = true;
										break;
									}
								} catch (BadLocationException e1) {
									e1.printStackTrace();
								}
							}
							if (!isSuccess) {
								getTextCmd().setAttribute(KEY_SEARCH_ROW, 0);
								getTextCmd().setAttribute(KEY_SEARCH_ROW_OFFSET, 0);
								console.prompt("Search text: " + searchText + " - 0 matches in Console");
							}
						}
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
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
		text.setCaretColor(XCLConstants.caretColor);
	}
	
	private synchronized void showPane(PaneType panelType) {
		getCardLayout().show(getCardPanel(), panelType.name());
	}
	
	// ---
	
	public void dispose() {
		this.frame.setVisible(false);
		this.frame.dispose();
	}
	
	public void addDynamicKey(String key) {
		this.keys.getDynamicKeys().add(key);
	}
	
	public void removeDynamicKey(String key) {
		this.keys.getDynamicKeys().remove(key);
	}
	
	@Override
	public boolean prepareEvent(EventEntity e) {
		if (this.console.getSource().equals(e.getSource())) {
			if (this.console.hasConnectors() || this.console.hasAcceptors()) {
				this.console.syncEvent(e); // synchronize local UI events to remote except XCLEvent.present
			}
		}
		return true;
	}
	
	@Override
	public String handleEvent(EventEntity event) {
		if (GraphicsEnvironment.isHeadless()) {
			// skipped as headless
			return null;
		}
		int traceId = event.getTraceId();
		EventType type = event.getType();
		String message = event.getMessage();
		String source = !console.getSource().equals(event.getSource()) ? "[" + event.getSource() + "]: " : "";
		logger.info("[" + console.getSource() + "] XCLUI.handleEvent [type: " + type + ", message: " + CommonUtils.trim(message) + "]");
		if (XCLEvent.input.equals(type)) {
			console(ConsoleType.input, traceId, source + message); // source
		} else if (XCLEvent.output.equals(type)) {
			console(ConsoleType.output, traceId, source + message); // source
		} else if (XCLEvent.info.equals(type)) {
			console(ConsoleType.info, traceId, source + message); // source
		} else if (XCLEvent.error.equals(type)) {
			console(ConsoleType.error, traceId, source + message); // source
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
			clearFixedRows(); // fixed the position error
			initOutFile();
		} else if (XCLEvent.editable.equals(type)) {
			requestFocus(getTextCmd());
		} else if (XCLEvent.present.equals(type)) {
			getTextCmd().setText(message);
			requestFocus(getTextCmd());
		} else if (XCLEvent.fixedRow.equals(type)) {
			boolean b = Boolean.valueOf(message);
			fixedRow(traceId, b);
		} else if (XCLEvent.textInput.equals(type)) {
			showPane(PaneType.input);
			getTextInput().setText(message);
			getTextInput().discardAllEdits();
			requestFocus(getTextInput());
		} else if (XCLEvent.setTextInput.equals(type)) {
			textQueue.add(message);
		} else if (XCLEvent.getTextInput.equals(type)) {
			try {
				String inputText = textQueue.take();
				showPane(PaneType.console);
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
