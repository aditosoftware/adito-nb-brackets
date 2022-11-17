package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for surrounding strings
 *
 * @author w.glanzer, 17.11.2022
 * @see SurroundWithTagsListener
 */
class SurroundWithTagListenerTest extends AbstractTagListenerTest
{

  /**
   * Tests, if the selection inside pComponent gets surrounded by the opening tag
   *
   * @param pComponent  Editor Pane
   * @param pOpeningTag Opening Tag to test
   * @param pClosingTag Closing Tag to test
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(SurroundWithTagsListener.class)
  void shouldSurroundSelectedText(@NotNull JEditorPane pComponent, char pOpeningTag, char pClosingTag)
  {
    pComponent.setCaretPosition(7);
    pComponent.setSelectionStart(5);
    pComponent.setSelectionEnd(7);

    typeChar(pComponent, pOpeningTag);

    assertEquals("This " + pOpeningTag + "is" + pClosingTag + " my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(6, pComponent.getSelectionStart());
    assertEquals(8, pComponent.getSelectionEnd());
    assertEquals(8, pComponent.getCaretPosition());
  }

  /**
   * Tests, if the selection inside pComponent gets replaced if something typed
   *
   * @param pComponent Editor Pane
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(SurroundWithTagsListener.class)
  void shouldOverrideSelectedText(@NotNull JEditorPane pComponent)
  {
    pComponent.setCaretPosition(7);
    pComponent.setSelectionStart(5);
    pComponent.setSelectionEnd(7);

    typeChar(pComponent, 'a');

    assertEquals("This a my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(6, pComponent.getSelectionStart());
    assertEquals(6, pComponent.getSelectionEnd());
    assertEquals(6, pComponent.getCaretPosition());
  }

}
