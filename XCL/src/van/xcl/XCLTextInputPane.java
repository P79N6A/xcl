package van.xcl;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import van.util.CommonUtils;

public class XCLTextInputPane extends XCLTextPane {
	
	public class KeyAssist extends KeyAdapter {
		private Map<Character, Character> map = new HashMap<Character, Character>();
		private JTextComponent t;
		private UndoManager und;
		private AtomicBoolean isUndDown = new AtomicBoolean(false);
		private String styleChangeText = UIManager.getString("AbstractDocument.styleChangeText");
		private String associateText = null;
		private int associateIndex = -1;
		public KeyAssist(JTextComponent t) {
			this.und = new UndoManager();
			this.t = t;
			this.t.getDocument().addUndoableEditListener(und);
			this.map.put('"', '"');
			this.map.put('{', '}');
			this.map.put('[', ']');
			this.map.put('\'', '\'');
			this.t.addKeyListener(this);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if (map.containsKey(e.getKeyChar())) {
				try {
					char c = map.get(e.getKeyChar());
					int pos = t.getCaretPosition();
					t.getDocument().insertString(pos, String.valueOf(c), XCLTextPane.getNormalAttr());
					// t.document.insertString(pos, String.valueOf(c), normalAttr);
					t.setCaretPosition(pos);
				} catch (BadLocationException e1) {
					// Do noting
				}
			}
			if (!isUndDown.compareAndSet(true, false)) {
				handleCaretRow();
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown()) {
				int count = 0;
				if (e.getKeyCode() == KeyEvent.VK_Y) { // redo
					isUndDown.set(true);
					while (und.canRedo() && count == 0) {
						if (!und.getRedoPresentationName().contains(styleChangeText)) {
							count++;
						}
						und.redo();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_Z) { // undo
					isUndDown.set(true);
					while (und.canUndo() && count == 0) {
						if (!und.getUndoPresentationName().contains(styleChangeText)) {
							count++;
						}	
						und.undo();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_SLASH) {
					try {
						Element root = t.getDocument().getDefaultRootElement();
						int startRow = root.getElementIndex(t.getSelectionStart());
						int endRow = root.getElementIndex(t.getSelectionEnd());
						for (int i = startRow ; i <= endRow ; i++) {
							Element rowElement = root.getElement(i);
							int start = rowElement.getStartOffset();
							int end = rowElement.getEndOffset() - 1;
							String rowText = t.getText(start, end - start);
							if (!CommonUtils.isEmpty(CommonUtils.trim(rowText))) {
								if (rowText.startsWith(XCLConstants.COMMONT_PREFIX)) {
									t.getDocument().remove(start, XCLConstants.COMMONT_PREFIX.length());
								} else {
									t.getDocument().insertString(start, XCLConstants.COMMONT_PREFIX, XCLTextPane.getNormalAttr());
								}
							}
						}
						handleAllRows();
					} catch (BadLocationException ex) {
						System.out.println(ex.getMessage());
					}
				}
				if (isUndDown.get()) {
					t.setCaretPosition(t.getDocument().getLength());
				}
			} else {
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					try {
						e.consume();
						Element root = t.getDocument().getDefaultRootElement();
						int rowLine = root.getElementIndex(getCaretPosition());
						Element rowElement = root.getElement(rowLine);
						int start = rowElement.getStartOffset();
						int end = rowElement.getEndOffset() - 1;
						String rowText = t.getDocument().getText(start, end - start);
						int idx = rowText.lastIndexOf(" ");
						String text = idx == -1 ? rowText : rowText.substring(idx + 1);
						if (!CommonUtils.isEmpty(text)) {
							if (getAssociateText() == null) {
								setAssociateText(text);
								resetAssociateIndex();
							}
							String findText = getAssociateText();
							String associateText = findAssociateText(findText);
							if (!CommonUtils.isEmpty(associateText)) {
								if (!associateText.equals(findText)) {
									if (idx == -1) {
										t.getDocument().remove(start, end - start);
										t.getDocument().insertString(start, associateText, XCLTextPane.getNormalAttr());
									} else {
										t.getDocument().remove(start + idx + 1, end - start - idx - 1);
										t.getDocument().insertString(start + idx + 1, associateText, XCLTextPane.getNormalAttr());
									}
								} else {
									resetAssociateIndex();
								}
							} else {
								resetAssociateIndex();
							}
						}
					} catch (Throwable ex) {
						System.out.println(ex.getMessage());
					}
				} else {
					setAssociateText(null);
					resetAssociateIndex();
				}
			}
		}
		public void discardAllEdits() {
			this.und.discardAllEdits();
		}
		
		public String findAssociateText(String text) {
			int associateIndex = -1;
			for (String key : getKeys().getKeys()) {
				if (key.startsWith(text)) {
					associateIndex++;
					if (associateIndex > this.associateIndex) {
						this.associateIndex = associateIndex;
						return key;
					}
				}
			}
			for (String key : getKeys().getDynamicKeys()) {
				if (key.startsWith(text)) {
					associateIndex++;
					if (associateIndex > this.associateIndex) {
						this.associateIndex = associateIndex;
						return key;
					}
				}
			}
			return null;
		}
		
		public void setAssociateText(String associateText) {
			this.associateText = associateText;
		}
		
		public String getAssociateText() {
			return this.associateText;
		}
		
		public void resetAssociateIndex() {
			this.associateIndex = -1;
		}
		
	}
	/**
	 *
	 */
	private static final long serialVersionUID = -66377652770879651L;
	
	private KeyAssist keyAssist = null;

	public XCLTextInputPane(XCLTextKeys keys) {
		super(keys);
		this.keyAssist = new KeyAssist(this);
	}
	
	public void discardAllEdits() {
		this.keyAssist.discardAllEdits();
	}
	
}

