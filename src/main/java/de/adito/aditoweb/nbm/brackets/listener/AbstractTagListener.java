package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.openide.modules.Modules;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoableEdit;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * Listener, that combines all of our known ITagListeners
 *
 * @author w.glanzer, 15.11.2022
 * @see de.adito.aditoweb.nbm.brackets.listener.BracketsKeyListener.ITagListener
 */
abstract class AbstractTagListener implements BracketsKeyListener.ITagListener
{
  private static Boolean staticKeyListenerAvailable = null;
  private static Class<? extends KeyListener> staticKeyListener = null;

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
    // Remove the legacy key listener, if available
    // -> has to be as early as possible to prevent incorrect behaviour in combination with the legacy listener
    removeLegacyKeyListener(pTextComponent);

    // Propagate event
    if (isApplicableForChar(pChar))
      return handleCharInserted0(pTextComponent, pChar);
    return false;
  }

  @Override
  public final boolean handleCharDeleted(@NotNull JTextComponent pTextComponent, char pChar) throws Exception
  {
    // Remove the legacy key listener, if available
    // -> has to be as early as possible to prevent incorrect behaviour in combination with the legacy listener
    removeLegacyKeyListener(pTextComponent);

    // Propagate event
    if (isApplicableForChar(pChar))
      return handleCharDeleted0(pTextComponent, pChar);
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
  protected boolean handleCharInserted0(@NotNull JTextComponent pTextComponent, char pChar) throws Exception
  {
    return false;
  }

  /**
   * Gets called if a char was deleted (by backspace) from the text
   * component, that matches our "applicable chars"
   *
   * @param pTextComponent TextComponent that received the event
   * @param pChar          char that was deleted
   * @return true, if the event was handled
   */
  protected boolean handleCharDeleted0(@NotNull JTextComponent pTextComponent, char pChar) throws Exception
  {
    return false;
  }

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
   * Removes the legacy key listener from the given text component
   *
   * @param pComponent Component to remove the listener from
   */
  protected void removeLegacyKeyListener(@NotNull JTextComponent pComponent)
  {
    if (isLegacyKeyListenerAvailable() && staticKeyListener != null)
    {
      for (KeyListener keyListener : List.of(pComponent.getKeyListeners()))
      {
        if (staticKeyListener.isInstance(keyListener))
        {
          pComponent.removeKeyListener(keyListener);
          break;
        }
      }
    }
  }

  /**
   * Determines, if the legacy KeyListener is available.
   * This key listener was added approx. in 2022.1.0 and was removed in 2022.2.1.
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
        //noinspection unchecked
        staticKeyListener = (Class<? extends KeyListener>) Class.forName("org.netbeans.modules.lsp.client.bindings.TextDocumentSyncServerCapabilityHandler$KeyListener", false,
                                                                         Modules.getDefault().findCodeNameBase("org.netbeans.modules.lsp.client").getClassLoader());
        staticKeyListenerAvailable = true;
      }
      catch (Throwable t)
      {
        staticKeyListener = null;
        staticKeyListenerAvailable = false;
      }
    }

    return staticKeyListenerAvailable;
  }

}
