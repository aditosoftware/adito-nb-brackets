package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.openide.modules.Modules;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoableEdit;
import java.util.Map;

/**
 * Listener, that combines all of our known ITagListeners
 *
 * @author w.glanzer, 15.11.2022
 * @see de.adito.aditoweb.nbm.brackets.listener.BracketsKeyListener.ITagListener
 */
abstract class AbstractTagListener implements BracketsKeyListener.ITagListener
{
  private static Boolean staticKeyListenerAvailable = null;

  /**
   * Mapping of the starting tag to closing tag
   */
  protected static final Map<Character, Character> TAG_MAPPING = Map.of(
      '"', '"',
      '\'', '\'',
      '(', ')',
      '[', ']',
      '{', '}'
  );

  @Override
  public final boolean handleCharInserted(@NotNull JTextComponent pTextComponent, char pChar) throws Exception
  {
    if (isEnabled() && isApplicableForChar(pChar))
      return handleCharInserted0(pTextComponent, pChar);
    return false;
  }

  /**
   * Gets called if a char was inserted into the text
   * component, that matches our "applicable chars"
   *
   * @param pTextComponent TextComponent that received the event
   * @param pChar          char that was inserted
   * @return true, if the event was handled
   */
  protected abstract boolean handleCharInserted0(@NotNull JTextComponent pTextComponent, char pChar) throws Exception;

  /**
   * Determines if this tag listener is applicable for the char that was entered
   *
   * @param pChar char that was entered
   * @return true, if this listener is applicable for the given char
   */
  protected boolean isApplicableForChar(char pChar)
  {
    return TAG_MAPPING.containsKey(pChar);
  }

  /**
   * Determines if this tag listener is enabled or not
   *
   * @return true, if enabled and ready for processing events
   */
  protected boolean isEnabled()
  {
    return true;
  }

  /**
   * Sends an edit event to all listeners of the document
   *
   * @param pDocument document to send the events to
   * @param pEdit     event to send
   */
  protected void sendUndoableEdit(@NotNull Document pDocument, @NotNull UndoableEdit pEdit)
  {
    if (pDocument instanceof AbstractDocument)
    {
      UndoableEditListener[] uels = ((AbstractDocument) pDocument).getUndoableEditListeners();
      UndoableEditEvent ev = new UndoableEditEvent(pDocument, pEdit);
      for (UndoableEditListener uel : uels)
        uel.undoableEditHappened(ev);
    }
  }

  /**
   * Determines, if the legacy KeyListener is available.
   * This key listener was added approx. in 2022.1.0 and was removed in 2022.2.1.
   * But, regardless of the listener, we want to be downwards compatible with all versions up to 2022.0.0.
   * So we have to check, if the legacy key listener is available and disable a bunch of our listeners here.
   *
   * @return true, if the legacy key listener is available
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted") // readability
  protected boolean isLegacyKeyListenerAvailable()
  {
    if (staticKeyListenerAvailable == null)
    {
      try
      {
        Class.forName("org.netbeans.modules.lsp.client.bindings.TextDocumentSyncServerCapabilityHandler$KeyListener", false,
                      Modules.getDefault().findCodeNameBase("org.netbeans.modules.lsp.client").getClassLoader());
        staticKeyListenerAvailable = true;
      }
      catch (Throwable t)
      {
        staticKeyListenerAvailable = false;
      }
    }

    return staticKeyListenerAvailable;
  }

}
