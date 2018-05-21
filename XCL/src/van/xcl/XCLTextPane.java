package van.xcl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.rtf.RTFEditorKit;

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
		String sval = " ";
		String oldStr, str;
		int m_currPosition = 0, m_beginPosition = 0;

		MyStringTokenizer(String str) {
			super(str, " ");
			this.oldStr = str;
			this.str = str;
		}

		public String nextToken() {
			try {
				String s = super.nextToken();
				int pos = -1;
				if (oldStr.equals(s)) {
					return s;
				}
				pos = str.indexOf(s + sval);
				if (pos == -1) {
					pos = str.indexOf(sval + s);
					if (pos == -1)
						return null;
					else
						pos += 1;
				}
				int xBegin = pos + s.length();
				str = str.substring(xBegin);
				m_currPosition = m_beginPosition + pos;
				m_beginPosition = m_beginPosition + xBegin;
				return s;
			} catch (java.util.NoSuchElementException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		// 返回token在字符串中的位置
		public int getCurrPosition() {
			return m_currPosition;
		}
	}
	/**
	 *
	 */
	private static final long serialVersionUID = -66377652770879651L;
	protected StyleContext context;
	protected DefaultStyledDocument document;
	private MutableAttributeSet keyAttr;
	private MutableAttributeSet normalAttr;
	private MutableAttributeSet commentAttr;
	private MutableAttributeSet inputAttributes = new RTFEditorKit().getInputAttributes();
	/**
	 * 所有关键字
	 */
	private String[] keys = new String[] {};
	/**
	 * 所与排除字符集
	 */
	private char[] _character = new char[] { '(', ')', ',', ';', ':', '\t', '\n', '+', '-', '*', '/' };

	/**
	 * 初始化，包括关键字颜色，和非关键字颜色
	 */
	public XCLTextPane(Color normalColor, Color keyColor, String[] keys) {
		super();
		this.keys = keys;
		this.context = new StyleContext();
		this.document = new DefaultStyledDocument(context);
		this.setDocument(document);
		this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				dealSingleRow();
			}
		});
		// 关键字显示属性
		keyAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(keyAttr, keyColor);
		// 一般文本显示属性
		normalAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(normalAttr, normalColor);
		// 注释显示属性
		commentAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(commentAttr, Color.gray);
	}

	/**
	 * 判断字符是不是在排除字符行列
	 */
	private boolean isCharacter(char _ch) {
		for (int i = 0; i < _character.length; i++) {
			if (_ch == _character[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置关键字颜色
	 */
	private int setKeyColor(String _key, int _start, int _length) {
		for (int i = 0; i < keys.length; i++) {
			if ("/".equals(keys[i])) {
				continue;
			}
			int li_index = _key.indexOf(keys[i]);
			if (li_index < 0) {
				continue;
			}
			int li_legnth = li_index + keys[i].length();
			if (li_legnth == _key.length()) {
				if (li_index == 0) {// 处理单独一个关键字的情况，例如：if else 等
					document.setCharacterAttributes(_start, keys[i].length(), keyAttr, false);
				} else {// 处理关键字前面还有字符的情况，例如：)if ;else 等
					char ch_temp = _key.charAt(li_index - 1);
					if (isCharacter(ch_temp)) {
						document.setCharacterAttributes(_start + li_index, keys[i].length(), keyAttr, false);
					}
				}
			} else {
				if (li_index == 0) {// 处理关键字后面还有字符的情况，例如：if( end;等
					char ch_temp = _key.charAt(keys[i].length());
					if (isCharacter(ch_temp)) {
						document.setCharacterAttributes(_start, keys[i].length(), keyAttr, false);
					}
				} else {// 处理关键字前面和后面都有字符的情况，例如：)if( 等
					char ch_temp = _key.charAt(li_index - 1);
					char ch_temp_2 = _key.charAt(li_legnth);
					if (isCharacter(ch_temp) && isCharacter(ch_temp_2)) {
						document.setCharacterAttributes(_start + li_index, keys[i].length(), keyAttr, false);
					}
				}
			}
		}
		return _length + 1;
	}

	/**
	 * 处理一行的数据
	 */
	private void dealText(int _start, int _end) {
		String text = "";
		try {
			text = document.getText(_start, _end - _start).toUpperCase();
		} catch (BadLocationException e) {
			// do nothing.
		}
		if (text == null || text.equals("")) {
			return;
		}
		if (text.trim().startsWith(Constants.COMMONT_PREFIX)) {
			document.setCharacterAttributes(_start, text.length(), commentAttr, false);
		} else {
			int xStart = 0;
			// 析关键字---
			document.setCharacterAttributes(_start, text.length(), normalAttr, false);
			MyStringTokenizer st = new MyStringTokenizer(text);
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (s == null)
					return;
				xStart = st.getCurrPosition();
				setKeyColor(s.toLowerCase(), _start + xStart, s.length());
			}
			inputAttributes.addAttributes(normalAttr);
		}
	}

	/**
	 * 在进行文本修改的时候 获得光标所在行，只对该行进行处理
	 */
	private void dealSingleRow() {
		Element root = document.getDefaultRootElement();
		// 光标当前行
		int cursorPos = this.getCaretPosition(); // 前光标的位置
		int line = root.getElementIndex(cursorPos);// 当前行
		Element para = root.getElement(line);
		int start = para.getStartOffset();
		int end = para.getEndOffset() - 1;// 除\r字符
		dealText(start, end);
	}

	/**
	 * 在初始化面板的时候调用该方法， 查找整个篇幅的关键字
	 */
	public void syntaxParse() {
		Element root = document.getDefaultRootElement();
		int li_count = root.getElementCount();
		for (int i = 0; i < li_count; i++) {
			Element para = root.getElement(i);
			int start = para.getStartOffset();
			int end = para.getEndOffset() - 1;// 除\r字符
			dealText(start, end);
		}
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		syntaxParse();
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

