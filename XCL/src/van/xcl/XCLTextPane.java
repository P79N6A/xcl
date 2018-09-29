package van.xcl;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class XCLTextPane extends JTextPane {
	
	class XCLStringTokenizer extends StringTokenizer {
		private static final String delimeter = " ";
		private String original;
		private String text;
		private int currPos = 0;
		private int startPos = 0;

		private XCLStringTokenizer(String text) {
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
	
	/**
	 *
	 */
	private static final long serialVersionUID = -66377652770879651L;
	
	private static MutableAttributeSet keyAttr;
	private static MutableAttributeSet normalAttr;
	private static MutableAttributeSet commentAttr;
	
	private static char[] exceptionCharacters = new char[] { '(', ')', ',', ';', ':', '\t', '\n', '+', '-', '*', '/' };
	
	static {
		keyAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(keyAttr, XCLConstants.keyColor);
		normalAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(normalAttr, XCLConstants.normalColor);
		commentAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(commentAttr, XCLConstants.commentColor);
	}
	
	protected StyleContext context;
	protected DefaultStyledDocument document;
	
	private Set<String> keys = null;
	private Map<String, Object> attrs = new HashMap<String, Object>();

	public XCLTextPane(Set<String> keys) {
		super();
		this.keys = keys;
		this.context = new StyleContext();
		this.document = new DefaultStyledDocument(context);
		this.setDocument(document);
	}
	
	private boolean isExceptionCharacter(char _ch) {
		for (int i = 0; i < exceptionCharacters.length; i++) {
			if (_ch == exceptionCharacters[i]) {
				return true;
			}
		}
		return false;
	}
	
	private void setAttributes(int offset, int length, MutableAttributeSet attr) {
		document.setCharacterAttributes(offset, length, attr, false);
	}

	private int setKeyColor(String text, int startIndex, int textLength) {
		for (String key : keys) {
			int index = text.indexOf(key);
			if (index < 0) {
				continue;
			}
			int length = index + key.length();
			if (length == text.length()) {
				if (index == 0) {
					setAttributes(startIndex, key.length(), keyAttr);
				} else {
					char ch_temp = text.charAt(index - 1);
					if (isExceptionCharacter(ch_temp)) {
						setAttributes(startIndex + index, key.length(), keyAttr);
					}
				}
			} else {
				if (index == 0) {
					char ch_temp = text.charAt(key.length());
					if (isExceptionCharacter(ch_temp)) {
						setAttributes(startIndex, key.length(), keyAttr);
					}
				} else {
					char ch_temp = text.charAt(index - 1);
					char ch_temp_2 = text.charAt(length);
					if (isExceptionCharacter(ch_temp) && isExceptionCharacter(ch_temp_2)) {
						setAttributes(startIndex + index, key.length(), keyAttr);
					}
				}
			}
		}
		return textLength + 1;
	}

	private void handleRowText(int startIndex, int endIndex) {
		try {
			String text = document.getText(startIndex, endIndex - startIndex);
			if (text != null && !"".equals(text)) {
				if (text.trim().startsWith(XCLConstants.COMMONT_PREFIX)) {
					setAttributes(startIndex, text.length(), commentAttr);
				} else {
					int lastPosition = 0;
					setAttributes(startIndex, text.length(), normalAttr);
					XCLStringTokenizer st = new XCLStringTokenizer(text);
					while (st.hasMoreTokens()) {
						String s = st.nextToken();
						if (s != null) {
							lastPosition = st.getCurrPosition();
							setKeyColor(s, startIndex + lastPosition, s.length());
						}
					}
				}
			}
		} catch (BadLocationException e) {
			// do nothing.
		}
	}

	public void handleCurrentRow() {
		Element root = document.getDefaultRootElement();
		int rowLine = root.getElementIndex(getCaretPosition());
		Element rowElement = root.getElement(rowLine);
		int start = rowElement.getStartOffset();
		int end = rowElement.getEndOffset() - 1;
		handleRowText(start, end);
	}
	
	public void handlePerviousRow() {
		Element root = document.getDefaultRootElement();
		int rowLine = root.getElementIndex(getCaretPosition());
		if (rowLine >= 1) {
			Element rowElement = root.getElement(rowLine - 1);
			int start = rowElement.getStartOffset();
			int end = rowElement.getEndOffset() - 1;
			handleRowText(start, end);
		}
	}

	public void handleAllRows() {
		int position = this.getCaretPosition();
		Element root = document.getDefaultRootElement();
		int rowCount = root.getElementCount();
		for (int i = 0; i < rowCount; i++) {
			Element para = root.getElement(i);
			int start = para.getStartOffset();
			int end = para.getEndOffset() - 1;
			handleRowText(start, end);
		}
		this.setCaretPosition(position);
	}
	
	public void setAttribute(String key, Object value) {
		attrs.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return attrs.get(key);
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

	public static MutableAttributeSet getKeyAttr() {
		return keyAttr;
	}

	public static MutableAttributeSet getNormalAttr() {
		return normalAttr;
	}

	public static MutableAttributeSet getCommentAttr() {
		return commentAttr;
	}
	
}

