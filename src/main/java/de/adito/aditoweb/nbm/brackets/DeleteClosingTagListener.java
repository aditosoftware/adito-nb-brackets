package de.adito.aditoweb.nbm.brackets;

import javax.swing.text.JTextComponent;
import java.awt.event.*;
import java.util.List;

/**
 * Listener that deletes all closing tags, if they are already present.
 * The user will notice it like "skipping the closing tag"
 *
 * @author w.glanzer, 26.10.2022
 */
class DeleteClosingTagListener extends KeyAdapter
{

  private static final List<Character> _AUTOCLOSING_CHARS = List.of(')', ']', '}', '\'', '\"', ';');

  @Override
  public void keyTyped(KeyEvent e)
  {
    Object source = e.getSource();
    if (source instanceof JTextComponent)
    {
      try
      {
        JTextComponent pane = (JTextComponent) source;
        char typedChar = e.getKeyChar();
        if (_AUTOCLOSING_CHARS.contains(typedChar))
        {
          int caretPosition = pane.getCaretPosition();
          String nextChar = pane.getText(caretPosition, 1);
          if (nextChar.toCharArray()[0] == typedChar)
            pane.getDocument().remove(caretPosition, 1);
        }
      }
      catch (Exception ex)
      {
        // ignore
      }
    }
  }

}
