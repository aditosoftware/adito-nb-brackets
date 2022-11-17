package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.openide.text.CloneableEditorSupport;

import javax.swing.*;
import javax.swing.text.*;
import java.util.List;

/**
 * Listener that surrounds the selection by the given char
 *
 * @author w.glanzer, 15.11.2022
 */
class SurroundWithTagsListener extends InsertClosingTagListener
{

  @Override
  protected boolean handleCharInserted0(@NotNull JTextComponent pTextComponent, char pChar) throws Exception
  {
    int selectionEnd = pTextComponent.getSelectionEnd();
    int selectionStart = pTextComponent.getSelectionStart();

    // check if something was selected
    if (selectionEnd - selectionStart > 0)
    {
      // check if multiselection
      //noinspection unchecked
      List<Position> positions = (List<Position>) pTextComponent.getClientProperty("rectangular-selection-regions");
      if (positions == null || positions.size() == 2)
        _doSurround(pTextComponent, selectionStart, selectionEnd, pChar, 1);
      else
        for (int i = 0; i < positions.size(); i = i + 2)
          _doSurround(pTextComponent, positions.get(i).getOffset(), positions.get(i + 1).getOffset(), pChar, 1 + i);

      return true;
    }

    return false;
  }

  @Override
  protected boolean isEnabled()
  {
    return !isLegacyKeyListenerAvailable() && super.isEnabled();
  }

  /**
   * Surrounds the text at the given position with the given char
   *
   * @param pTextComponent  TextComponent to get the text from
   * @param pSelectionStart Selection Start offset
   * @param pSelectionEnd   Selection End offset
   * @param pChar           Char to surround
   * @param pOffset         The offset, mainly used for multiselection
   */
  private void _doSurround(@NotNull JTextComponent pTextComponent, int pSelectionStart, int pSelectionEnd, char pChar, int pOffset)
      throws BadLocationException
  {
    Document document = pTextComponent.getDocument();
    String text = document.getText(pSelectionStart, pSelectionEnd - pSelectionStart);
    int caretPosition = pTextComponent.getCaretPosition();
    sendUndoableEdit(document, CloneableEditorSupport.BEGIN_COMMIT_GROUP);

    SwingUtilities.invokeLater(() -> {
      try
      {
        // insert the text and the "closing" character
        // the "opening" character is handled by the component. Unfortunately, the selected text gets overridden => selected text must
        // be inserted again
        document.insertString(pSelectionStart + pOffset, text + TAG_MAPPING.get(pChar), null);

        // set initial caret position again
        pTextComponent.setCaretPosition(caretPosition + pOffset);

        // set initial selection again
        pTextComponent.setSelectionStart(pSelectionStart + pOffset);
        pTextComponent.setSelectionEnd(pSelectionEnd + pOffset);
        sendUndoableEdit(document, CloneableEditorSupport.END_COMMIT_GROUP);
      }
      catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    });
  }
}
