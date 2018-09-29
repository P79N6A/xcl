package van.xcl;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class XCLTextInputPane extends XCLTextPane {
	
	public class KeyAssist extends KeyAdapter {
		private Map<Character, Character> map = new HashMap<Character, Character>();
		private JTextComponent t;
		private UndoManager und;
		private AtomicBoolean isUndDown = new AtomicBoolean(false);
		private String styleChangeText = UIManager.getString("AbstractDocument.styleChangeText");
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
				handleCurrentRow();
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
				}
				if (isUndDown.get()) {
					t.setCaretPosition(t.getDocument().getLength());
				}
			} 
		}
		public void discardAllEdits() {
			this.und.discardAllEdits();
		}
		
	}
	/**
	 *
	 */
	private static final long serialVersionUID = -66377652770879651L;
	
	private KeyAssist keyAssist = null;

	public XCLTextInputPane(Set<String> keys) {
		super(keys);
		this.keyAssist = new KeyAssist(this);
	}
	
	public void discardAllEdits() {
		this.keyAssist.discardAllEdits();
	}

}

