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
    this(new SurroundWithTagsListener(), new InsertClosingTagListener(), new SkipClosingTagListener());
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

    try
    {
      for (ITagListener listener : listeners)
        if (listener.handleCharInserted((JTextComponent) source, e.getKeyChar()))
          break;
    }
    catch (Exception ex)
    {
      Logger.getLogger(BracketsKeyListener.class.getName()).log(Level.WARNING, "Failed to handle brackets event", ex);
    }
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
  }

}
