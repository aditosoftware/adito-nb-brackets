package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.*;
import org.netbeans.modules.editor.NbEditorDocument;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.lang.annotation.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Abstract test for the AbstractTagListener tests
 *
 * @author w.glanzer, 17.11.2022
 */
abstract class AbstractTagListenerTest
{

  protected static class ArgProv implements ArgumentsProvider
  {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context)
    {
      System.setProperty("java.awt.headless", "true");

      Class<? extends BracketsKeyListener.ITagListener> listenerToTest = context.getTestMethod()
          .map(pMethod -> pMethod.getAnnotation(Listener.class).value())
          .orElseThrow();

      return Stream.concat(
          AbstractTagListener.TAG_MAPPING.entrySet().stream()
              .map(pEntry -> {
                try
                {
                  _JEditorPane singleListenerPane = new _JEditorPane();
                  singleListenerPane.setDocument(new NbEditorDocument("text/javascript"));
                  singleListenerPane.setText("This is my text.\r\nThis is line number two");
                  singleListenerPane.addKeyListener(new BracketsKeyListener(listenerToTest.getDeclaredConstructor().newInstance()));
                  return Arguments.of(singleListenerPane, pEntry.getKey(), pEntry.getValue());
                }
                catch (Exception e)
                {
                  throw new RuntimeException(e);
                }
              }),
          AbstractTagListener.TAG_MAPPING.entrySet().stream()
              .map(pEntry -> {
                _JEditorPane allListenersPane = new _JEditorPane();
                allListenersPane.setDocument(new NbEditorDocument("text/javascript"));
                allListenersPane.setText("This is my text.\r\nThis is line number two");
                allListenersPane.addKeyListener(new BracketsKeyListener());
                return Arguments.of(allListenersPane, pEntry.getKey(), pEntry.getValue());
              }));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Listener
    {
      Class<? extends BracketsKeyListener.ITagListener> value();
    }
  }

  /**
   * Types the given char in the component - like the user will do.
   * Blocks until the event got propagated to the component
   *
   * @param pComponent Component to type into
   * @param pChar      char to type
   */
  protected void typeChar(@NotNull JTextComponent pComponent, char pChar)
  {
    SwingUtilities.invokeLater(() -> {
      try
      {
        KeyEvent event = new KeyEvent(pComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, pChar);
        ((_JEditorPane) pComponent).processKeyEvent(event);
        // invoke this Action "manually". This simulates Netbeans
        new DefaultEditorKit.DefaultKeyTypedAction().actionPerformed(new ActionEvent(pComponent,
                                                                                     ActionEvent.ACTION_PERFORMED,
                                                                                     Character.toString(event.getKeyChar()),
                                                                                     event.getWhen(),
                                                                                     event.getModifiersEx()));
      }
      catch (Throwable pE)
      {
        throw new RuntimeException(pE);
      }
    });

    // Wait for EDT to complete
    AtomicBoolean wait = new AtomicBoolean(false);
    SwingUtilities.invokeLater(() -> wait.set(true));

    //noinspection ConditionalBreakInInfiniteLoop
    while (true)
    {
      if (wait.get())
        break;

      try
      {
        //noinspection BusyWait
        Thread.sleep(100);
      }
      catch (InterruptedException pE)
      {
        throw new RuntimeException(pE);
      }
    }
  }

  /**
   * EditorPane to make "processKeyEvent()" publicly available
   */
  private static class _JEditorPane extends JEditorPane
  {
    @Override
    public void processKeyEvent(KeyEvent e)
    {
      super.processKeyEvent(e);
    }

    @Override
    public String toString()
    {
      return "_JEditorPane";
    }
  }

}
