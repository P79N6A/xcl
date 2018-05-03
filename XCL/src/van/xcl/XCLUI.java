package van.xcl;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import van.util.CommonUtils;
import van.util.evt.EventHandler;

public class XCLUI extends EventHandler {

	class XScrollBarUI extends BasicScrollBarUI {
		@Override
		protected void configureScrollBarColors() {
//			trackColor = backgroundColor;
			trackColor = Color.gray;
		}
		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			super.paintTrack(g, c, trackBounds);
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			g.translate(thumbBounds.x, thumbBounds.y);
			g.setColor(Color.white);
			g.drawRoundRect(5, 0, 6, thumbBounds.height - 1, 5, 5);
			Graphics2D g2 = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.addRenderingHints(rh);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
			g2.fillRoundRect(5, 0, 6, thumbBounds.height - 1, 5, 5);
		}
		@Override
		protected JButton createIncreaseButton(int orientation) {
			JButton button = new JButton();
			button.setBorder(null);
			button.setPreferredSize(new Dimension(0, 0));
			return button;
		}
		@Override
		protected JButton createDecreaseButton(int orientation) {
			JButton button = new JButton();
			button.setBorder(null);
			button.setPreferredSize(new Dimension(0, 0));
			return button;
		}
	}
	
	class KeyAssist extends KeyAdapter {
		private Map<Character, Character> map = new HashMap<Character, Character>();
		private JTextComponent t;
		public KeyAssist(JTextComponent t) {
			this.t = t;
			this.map.put('"', '"');
			this.map.put('{', '}');
			this.map.put('[', ']');
			this.map.put('\'', '\'');
			this.t.addKeyListener(this);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if (map.containsKey(e.getKeyChar())) {
				char c = map.get(e.getKeyChar());
				String text = t.getText();
				int pos = t.getCaretPosition();
				String str1 = text.substring(0, pos);
				String str2 = text.substring(pos);
				t.setText(str1 + c + str2);
				t.setCaretPosition(pos);
			}
		}
		
	}
	
	private static final Color backgroundColor = new Color(39, 40, 34);
	private static final Color foregroundColor = new Color(248, 248, 242);
	private static final Color selectionColor = new Color(73, 72, 62);
	private static final Color promptColor = new Color(128, 128, 128);
	private static final Color errorColor = new Color(255, 0, 0);

	private JFrame frame;
	private XCLConsole console;
	private JTextPane textConsole;
	private JTextArea textInput;
	private JTextField textCmd;
	private JTextField textPrompt;
	private TitledBorder titledBorder;
	private JPanel cardPanel = new JPanel();
	private CardLayout cardLayout = new CardLayout();
	private BufferedWriter logWriter;
	
	private LinkedBlockingQueue<String> textQueue = new LinkedBlockingQueue<String>();

	public XCLUI(XCLConsole console) {
		this.console = console;
	}

	protected void init() {
		ImageIcon icon = new ImageIcon(XCLUI.class.getResource(Constants.ICON_IMAGE_PATH));
		getFrame().setIconImage(icon.getImage());  
		getFrame().setSize(1000, 600);
		getFrame().setResizable(true);
		getFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getFrame().addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				console.exit(0);
			}
		});
		this.cardPanel.setLayout(cardLayout);
		this.cardPanel.add("console", getConsolePanel());
		this.cardPanel.add("input", getScrollInput());
		getFrame().setLayout(new BorderLayout());
		getFrame().add(getTextPrompt(), BorderLayout.SOUTH);
		getFrame().add(cardPanel, BorderLayout.CENTER);
		getFrame().setVisible(true);
		console.info(Constants.VERSION_PROMPT);
		initLogger();
	}
	
	private void initLogger() {
		File logFile = new File(Constants.LOG_FILE);
		try {
			if (logWriter != null) {
				logWriter.close();
				logFile.delete();
			}
			logWriter = new BufferedWriter(new FileWriter(logFile));
			console(Constants.INFO_PROMPT + " - " + logFile.getAbsolutePath() + "\n", promptColor);
		} catch (IOException e) {
			console("Failed to init the log file: " + e.getMessage(), errorColor);
		}
	}
	
	private JFrame getFrame() {
		if (this.frame == null) {
			this.frame = new JFrame();
		}
		return this.frame;
	}
	
	private JPanel getConsolePanel() {
		JPanel consolePanel = new JPanel();
		consolePanel.setLayout(new BorderLayout(0, 0));
		consolePanel.add(getScrollConsole(), BorderLayout.CENTER);
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBorder(null);
		label.setBackground(backgroundColor);
		label.setForeground(foregroundColor);
		label.setFont(getDefaultFont());
		label.setText(" > ");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(label, BorderLayout.WEST);
		panel.add(getTextCmd(), BorderLayout.CENTER);
		consolePanel.add(panel, BorderLayout.SOUTH);
		return consolePanel;
	}
	
	private JScrollPane getScrollConsole() {
		JScrollPane pane = new JScrollPane(getTextConsole());
		JScrollBar vb = pane.getVerticalScrollBar();
		JScrollBar hb = pane.getHorizontalScrollBar();
		vb.setUI(new XScrollBarUI());
		hb.setUI(new XScrollBarUI());
//		vb.setPreferredSize(new Dimension(10, 10));
//		hb.setPreferredSize(new Dimension(10, 10));
		return pane;
	}

	private JScrollPane getScrollInput() {
		JScrollPane pane = new JScrollPane(getTextInput());
		JScrollBar vb = pane.getVerticalScrollBar();
		JScrollBar hb = pane.getHorizontalScrollBar();
		vb.setUI(new XScrollBarUI());
		hb.setUI(new XScrollBarUI());
//		vb.setPreferredSize(new Dimension(10, 10));
//		hb.setPreferredSize(new Dimension(10, 10));
		pane.setBorder(getTextInputBorder());
		return pane;
	}
	
	private JTextField getTextCmd() {
		if (textCmd == null) {
			textCmd = new JTextField();
			textCmd.setEditable(false);
			textCmd.setBorder(null);
			textCmd.setBackground(backgroundColor);
			textCmd.setForeground(foregroundColor);
			textCmd.setCaretColor(Color.white);
			textCmd.setFont(getDefaultFont());
			textCmd.setPreferredSize(new Dimension(0, 25));
			textCmd.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						String cmdStr = textCmd.getText();
						if (!CommonUtils.isEmpty(cmdStr)) {
							console.input(cmdStr);
							console.run(cmdStr);
							console.present(null);
						}
					}
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
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (e.getModifiers() == InputEvent.ALT_MASK) {
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
						} else {
							if (e.getKeyCode() == KeyEvent.VK_UP) {
								getTextConsole().requestFocus();
								getTextConsole().setCaretPosition(getTextConsole().getDocument().getLength());
							}
						}
					}
				}
			});
			new KeyAssist(textCmd);
		}
		return textCmd;
	}
	
	private JTextField getTextPrompt() {
		if (textPrompt == null) {
			textPrompt = new JTextField();
			textPrompt.setEditable(false);
			textPrompt.setBorder(null);
			textPrompt.setBackground(backgroundColor);
			textPrompt.setForeground(promptColor);
			textPrompt.setCaretColor(foregroundColor);
			textPrompt.setFont(getDefaultFont());
			textPrompt.setPreferredSize(new Dimension(0, 25));
		}
		return textPrompt;
	}
	
	private void console(String str, Color color) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		Font font = getDefaultFont();
		StyleConstants.setFontSize(attr, font.getSize());
		StyleConstants.setFontFamily(attr, font.getFamily());
		StyleConstants.setForeground(attr, color);
		Document docs = getTextConsole().getDocument();
		try {
			docs.insertString(docs.getLength(), str, attr);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		getTextConsole().setCaretPosition(getTextConsole().getDocument().getLength());
		if (logWriter != null) {
			try {
				logWriter.append(str);
				logWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private TitledBorder getTextInputBorder() {
		if (titledBorder == null) {
			titledBorder = new TitledBorder("Input");
			titledBorder.setBorder(null);
			titledBorder.setTitleColor(Color.black);
		}
		return titledBorder;
	}

	private JTextArea getTextInput() {
		if (textInput == null) {
			textInput = new JTextArea();
			textInput.setBackground(backgroundColor);
			textInput.setForeground(foregroundColor);
			textInput.setSelectionColor(selectionColor);
			textInput.setCaretColor(foregroundColor);
			textInput.setFont(getDefaultFont());
			textInput.setWrapStyleWord(false);
			textInput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if ((e.isControlDown() || e.isShiftDown()) && e.getKeyChar() == KeyEvent.VK_ENTER) {
						String text = textInput.getText();
						textQueue.add(text);
					} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
						textQueue.add(Constants.ESC);
					}
				}
			});
			textInput.setEditable(true);
			new KeyAssist(textInput);
		}
		return textInput;
	}

	private JTextPane getTextConsole() {
		if (textConsole == null) {
			textConsole = new JTextPane();
			textConsole.setEditable(false);
			textConsole.setBorder(null);
			textConsole.setBackground(backgroundColor);
			textConsole.setForeground(foregroundColor);
			textConsole.setSelectionColor(selectionColor);
			textConsole.setCaretColor(foregroundColor);
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
						String runfile = Constants.RUNFILE_COMMAND + " " + filepath;
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
		return Constants.DEFAULT_FONT;
	}
	
	// ---
	
	@Override
	public String handle(String type, String message) {
		System.out.println("XCLUI --> handle [type: " + type + ", message: " + message + "]");
		if (XCLEvent.prepare.name().equals(type)) {
			getTextCmd().requestFocus();
			getTextCmd().setCaretPosition(getTextCmd().getDocument().getLength());
		} else if (XCLEvent.input.name().equals(type)) {
			console(Constants.IN_PROMPT + message + "\n", foregroundColor);
		} else if (XCLEvent.output.name().equals(type)) {
			console(Constants.OUT_PROMPT + message + "\n", foregroundColor);
		} else if (XCLEvent.info.name().equals(type)) {
			console(Constants.INFO_PROMPT + message + "\n", promptColor);
		} else if (XCLEvent.error.name().equals(type)) {
			console(Constants.ERROR_PROMPT + message + "\n", errorColor);
		} else if (XCLEvent.prompt.name().equals(type)) {
			getTextPrompt().setText(message + "  ");
		} else if (XCLEvent.title.name().equals(type)) {
			getFrame().setTitle(message);
		} else if (XCLEvent.clear.name().equals(type)) {
			getTextConsole().setText("");
			getTextConsole().setCaretPosition(getTextConsole().getDocument().getLength());
			initLogger();
		} else if (XCLEvent.editable.name().equals(type)) {
			boolean editable = Boolean.valueOf(message);
			getTextCmd().setEditable(editable);
		} else if (XCLEvent.present.name().equals(type)) {
			getTextCmd().setText(message);
			getTextCmd().setCaretPosition(getTextCmd().getDocument().getLength());
		} else if (XCLEvent.textTitle.name().equals(type)) {
			getTextInputBorder().setTitle("[" + message + "]");
		} else if (XCLEvent.textInput.name().equals(type)) {
			cardLayout.show(cardPanel, "input");
			getTextInput().requestFocus();
			getTextInput().setText(message);
			getTextInput().setCaretPosition(textInput.getDocument().getLength());
		} else if (XCLEvent.getTextInput.name().equals(type)) {
			try {
				String inputText = textQueue.take();
				cardLayout.show(cardPanel, "console");
				if (!Constants.ESC.equals(inputText)) {
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
