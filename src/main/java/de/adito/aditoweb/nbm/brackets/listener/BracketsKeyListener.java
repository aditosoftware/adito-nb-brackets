package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;

import javax.swing.text.JTextComponent;
import java.awt.event.*;
import java.util.List;
import java.util.logging.*;

/**
 * "Root"-KeyListener for all ITagListeners
 *
 * @author w.glanzer, 15.11.2022
 * @see ITagListener
 */
public class BracketsKeyListener extends KeyAdapter
{

  private final List<ITagListener> listeners;

  public BracketsKeyListener()
  {
    this(new SurroundWithTagsListener(), new InsertClosingTagListener(), new SkipClosingTagListener(), new DeleteClosingTagListener());
  }

  protected BracketsKeyListener(ITagListener... pListeners)
  {
    listeners = List.of(pListeners);
  }

  @Override
  public void keyPressed(KeyEvent e)
  {
    Object source = e.getSource();
    if (!(source instanceof JTextComponent))
      return;

    JTextComponent comp = (JTextComponent) source;

    try
    {
      // Deletion Events: Backspace
      // Ignore Deletion by "Delete"-Key, because IntelliJ/VSCode ignores it too
      if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
      {
        int caretPosition = comp.getCaretPosition();
        if (caretPosition > 0)
        {
          char deletedChar = comp.getText(caretPosition - 1, 1).charAt(0);
          _fire(pListener -> pListener.handleCharDeleted(comp, deletedChar));
        }
      }

      // any other char
      else
        _fire(pListener -> pListener.handleCharInserted(comp, e.getKeyChar()));
    }
    catch (Exception ex)
    {
      Logger.getLogger(BracketsKeyListener.class.getName()).log(Level.WARNING, "Failed to handle brackets event", ex);
    }
  }

  /**
   * Executors the given function on all listeners.
   * If the function returns true, then the propagation will be stopped, because the event got handled
   *
   * @param pOnListenerFn function to execute
   */
  private void _fire(@NotNull IOnListenerFn pOnListenerFn) throws Exception
  {
    for (ITagListener listener : listeners)
      if (pOnListenerFn.apply(listener))
        break;
  }

  /**
   * Function to trigger a listener
   */
  private interface IOnListenerFn
  {
    /**
     * Gets called if the listener should fire an event
     *
     * @param pListener Listener to fire the event
     * @return true, if the listener handled the event and the propagation should be stopped
     */
    boolean apply(@NotNull ITagListener pListener) throws Exception;
  }

  public interface ITagListener
  {
    /**
     * Gets called if a char was inserted into the text component
     *
     * @param pTextComponent TextComponent that received the event
     * @param pChar          char that was inserted
     * @return true, if the event was handled
     */
    boolean handleCharInserted(@NotNull JTextComponent pTextComponent, char pChar) throws Exception;

    /**
     * Gets called if a char was deleted (by backspace) from the text component
     *
     * @param pTextComponent text component that received the event
     * @param pChar          char that was deleted
     * @return true, if the event was handled
     */
    boolean handleCharDeleted(@NotNull JTextComponent pTextComponent, char pChar) throws Exception;
  }

}
