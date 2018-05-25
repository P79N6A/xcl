package van.xcl;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.UndoManager;

/**
 * 关键字特殊处理面板
 * 
 * @author Administrator
 *
 */
public class XCLTextPane extends JTextPane {
	
	/**
	 * 在分析字符串的同时，记录每个token所在的位置
	 *
	 */
	class MyStringTokenizer extends StringTokenizer {
		private static final String delimeter = " ";
		private String original;
		private String text;
		private int currPos = 0;
		private int startPos = 0;

		private MyStringTokenizer(String text) {
			super(text, delimeter);
			this.original = text;
			this.text = text;
		}

		public String nextToken() {
			try {
				String s = super.nextToken();
				int pos = -1;
				if (original.equals(s)) {
					return s;
				}
				pos = text.indexOf(s + delimeter);
				if (pos == -1) {
					pos = text.indexOf(delimeter + s);
					if (pos == -1) {
						return null;
					} else {
						pos += 1;
					}
				}
				int xBegin = pos + s.length();
				text = text.substring(xBegin);
				currPos = startPos + pos;
				startPos = startPos + xBegin;
				return s;
			} catch (NoSuchElementException ex) {
				ex.printStackTrace();
				return null;
			}
		}
		public int getCurrPosition() {
			return currPos;
		}
	}
	
	class KeyAssist extends KeyAdapter {
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
				char c = map.get(e.getKeyChar());
				String text = t.getText();
				int pos = t.getCaretPosition();
				String str1 = text.substring(0, pos);
				String str2 = text.substring(pos);
				t.setText(str1 + c + str2);
				t.setCaretPosition(pos);
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
	
	private static MutableAttributeSet keyAttr;
	private static MutableAttributeSet normalAttr;
	private static MutableAttributeSet commentAttr;
	private static MutableAttributeSet inputAttributes = new RTFEditorKit().getInputAttributes();
	
	/**
	 * 所与排除字符集
	 */
	private static char[] exceptionCharacters = new char[] { '(', ')', ',', ';', ':', '\t', '\n', '+', '-', '*', '/' };
	
	static {
		// 关键字显示属性
		keyAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(keyAttr, XCLConstants.keyColor);
		// 一般文本显示属性
		normalAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(normalAttr, XCLConstants.normalColor);
		// 注释显示属性
		commentAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(commentAttr, XCLConstants.commentColor);
	}
	
	protected StyleContext context;
	protected DefaultStyledDocument document;
	
	/**
	 * 所有关键字
	 */
	private Set<String> keys = null;
	private KeyAssist keyAssist = null;

	/**
	 * 初始化，包括关键字颜色，和非关键字颜色
	 */
	public XCLTextPane(Set<String> keys) {
		super();
		this.keys = keys;
		this.context = new StyleContext();
		this.document = new DefaultStyledDocument(context);
		this.setDocument(document);
		this.keyAssist = new KeyAssist(this);
	}
	
	public void discardAllEdits() {
		this.keyAssist.discardAllEdits();
	}

	/**
	 * 判断字符是不是在排除字符行列
	 */
	private boolean isExceptionCharacter(char _ch) {
		for (int i = 0; i < exceptionCharacters.length; i++) {
			if (_ch == exceptionCharacters[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置关键字颜色
	 */
	private int setKeyColor(String text, int startIndex, int textLength) {
		for (String key : keys) {
			int index = text.indexOf(key);
			if (index < 0) {
				continue;
			}
			int length = index + key.length();
			if (length == text.length()) {
				if (index == 0) {// 处理单独一个关键字的情况，例如：if else 等
					document.setCharacterAttributes(startIndex, key.length(), keyAttr, false);
				} else {// 处理关键字前面还有字符的情况，例如：)if ;else 等
					char ch_temp = text.charAt(index - 1);
					if (isExceptionCharacter(ch_temp)) {
						document.setCharacterAttributes(startIndex + index, key.length(), keyAttr, false);
					}
				}
			} else {
				if (index == 0) {// 处理关键字后面还有字符的情况，例如：if( end;等
					char ch_temp = text.charAt(key.length());
					if (isExceptionCharacter(ch_temp)) {
						document.setCharacterAttributes(startIndex, key.length(), keyAttr, false);
					}
				} else {// 处理关键字前面和后面都有字符的情况，例如：)if( 等
					char ch_temp = text.charAt(index - 1);
					char ch_temp_2 = text.charAt(length);
					if (isExceptionCharacter(ch_temp) && isExceptionCharacter(ch_temp_2)) {
						document.setCharacterAttributes(startIndex + index, key.length(), keyAttr, false);
					}
				}
			}
		}
		return textLength + 1;
	}

	/**
	 * 处理一行的数据
	 */
	private void handleRowText(int startIndex, int endIndex) {
		String text = "";
		try {
			text = document.getText(startIndex, endIndex - startIndex).toUpperCase();
		} catch (BadLocationException e) {
			// do nothing.
		}
		if (text == null || text.equals("")) {
			return;
		}
		if (text.trim().startsWith(XCLConstants.COMMONT_PREFIX)) {
			document.setCharacterAttributes(startIndex, text.length(), commentAttr, false);
		} else {
			int lastPosition = 0;
			document.setCharacterAttributes(startIndex, text.length(), normalAttr, false);
			MyStringTokenizer st = new MyStringTokenizer(text);
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (s == null)
					return;
				lastPosition = st.getCurrPosition();
				setKeyColor(s.toLowerCase(), startIndex + lastPosition, s.length());
			}
			inputAttributes.addAttributes(normalAttr);
		}
	}

	/**
	 * 在进行文本修改的时候 获得光标所在行，只对该行进行处理
	 */
	private void handleCurrentRow() {
		Element root = document.getDefaultRootElement();
		// 光标当前行
		int cursorPos = this.getCaretPosition(); // 前光标的位置
		int line = root.getElementIndex(cursorPos);// 当前行
		Element para = root.getElement(line);
		int start = para.getStartOffset();
		int end = para.getEndOffset() - 1;// 除\r字符
		handleRowText(start, end);
	}

	/**
	 * 在初始化面板的时候调用该方法， 查找整个篇幅的关键字
	 */
	public void handleAllRows() {
		Element root = document.getDefaultRootElement();
		int li_count = root.getElementCount();
		for (int i = 0; i < li_count; i++) {
			Element para = root.getElement(i);
			int start = para.getStartOffset();
			int end = para.getEndOffset() - 1;// 除\r字符
			handleRowText(start, end);
		}
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		handleAllRows();
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public void setSize(Dimension d) {
		if (d.width < getParent().getSize().width) {
			d.width = getParent().getSize().width;
		}
		d.width += 100;
		super.setSize(d);
	}

}

