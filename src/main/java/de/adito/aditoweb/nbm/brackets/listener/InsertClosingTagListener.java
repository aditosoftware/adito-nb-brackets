package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.netbeans.api.editor.document.*;

import javax.swing.text.*;

/**
 * Inserts the closing tag, if an opening tag was inserted.
 * Does nothing, if something is selected
 *
 * @author w.glanzer, 15.11.2022
 */
class InsertClosingTagListener extends AbstractTagListener
{

  @Override
  protected boolean handleCharInserted0(@NotNull JTextComponent pTextComponent, char pChar) throws Exception
  {
    // check, if nothing is selected
    if (pTextComponent.getSelectionEnd() - pTextComponent.getSelectionStart() <= 0)
    {
      String toInsert = Character.toString(TAG_MAPPING.get(pChar));
      int caretPosition = pTextComponent.getCaretPosition();
      Document document = pTextComponent.getDocument();

      // If an identical character should be inserted check, if the count is even
      if (document instanceof LineDocument && TAG_MAPPING.get(pChar) == pChar)
      {
        // check if already an even count of characters in this line is present
        // only if not, then insert the second one
        int lineStart = LineDocumentUtils.getLineStart((LineDocument) document, caretPosition);
        String line = document.getText(lineStart, caretPosition - lineStart);
        int count = line.length() - line.replace(Character.toString(pChar), "").length();

        // increase count by one, because we know that pChar will be inserted afterwards
        count = count + 1;

        if (count % 2 == 0)
          return false;

        // Ignore, if there is already such a sign on the left
        if (line.endsWith(String.valueOf(pChar)))
          return false;
      }

      document.insertString(caretPosition, toInsert, null);
      pTextComponent.setCaretPosition(caretPosition);

      return true;
    }

    return false;
  }

  @Override
  protected boolean isEnabled()
  {
    return !isLegacyKeyListenerAvailable() && super.isEnabled();
  }

}
