package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;

import javax.swing.text.JTextComponent;
import java.util.List;

/**
 * Listener that deletes all closing tags, if they are already present.
 * The user will notice it like "skipping the closing tag"
 *
 * @author w.glanzer, 26.10.2022
 */
class SkipClosingTagListener extends AbstractTagListener
{

  private static final List<Character> _ADDITIONAL_SKIPPABLE_CHARS = List.of(';');

  @Override
  protected boolean handleCharInserted0(@NotNull JTextComponent pTextComponent, char pChar) throws Exception
  {
    // check, if nothing is selected
    if (pTextComponent.getSelectionEnd() - pTextComponent.getSelectionStart() <= 0)
    {
      int caretPosition = pTextComponent.getCaretPosition();
      String nextChar = pTextComponent.getText(caretPosition, 1);
      if (nextChar.toCharArray()[0] == pChar)
      {
        pTextComponent.getDocument().remove(caretPosition, 1);
        return true;
      }
    }

    return false;
  }

  @Override
  protected boolean isApplicableForChar(char pChar)
  {
    return TAG_MAPPING.containsValue(pChar) || _ADDITIONAL_SKIPPABLE_CHARS.contains(pChar);
  }

}
