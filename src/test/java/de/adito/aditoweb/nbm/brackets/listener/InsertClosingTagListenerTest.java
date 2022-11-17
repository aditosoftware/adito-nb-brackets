package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for inserting close tags
 *
 * @author w.glanzer, 17.11.2022
 * @see InsertClosingTagListener
 */
class InsertClosingTagListenerTest extends AbstractTagListenerTest
{

  /**
   * Tests, if the closing tag gets inserted
   *
   * @param pComponent  Editor Pane
   * @param pOpeningTag Opening tag
   * @param pClosingTag Closing tag
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(InsertClosingTagListener.class)
  void shouldInsertClosingTag(@NotNull JEditorPane pComponent, char pOpeningTag, char pClosingTag)
  {
    pComponent.setCaretPosition(7);

    typeChar(pComponent, pOpeningTag);

    assertEquals("This is" + pOpeningTag + pClosingTag + " my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(8, pComponent.getCaretPosition());
  }

  /**
   * Tests, if the closing tag is ignored if there was already such a sign on the left
   *
   * @param pComponent  Editor Pane
   * @param pOpeningTag Opening tag
   * @param pClosingTag Closing tag
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(InsertClosingTagListener.class)
  void shouldIgnoreClosingTag(@NotNull JEditorPane pComponent, char pOpeningTag, char pClosingTag)
  {
    // Only test on identical signs
    if (pOpeningTag != pClosingTag)
      return;

    pComponent.setCaretPosition(7);
    typeChar(pComponent, pOpeningTag);
    pComponent.setCaretPosition(9);
    typeChar(pComponent, pOpeningTag);

    assertEquals("This is" + pOpeningTag + pClosingTag + pClosingTag + " my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(10, pComponent.getCaretPosition());

    typeChar(pComponent, pOpeningTag);
    assertEquals("This is" + pOpeningTag + pClosingTag + pOpeningTag + pClosingTag + " my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(11, pComponent.getCaretPosition());

    typeChar(pComponent, pOpeningTag);
    assertEquals("This is" + pOpeningTag + pClosingTag + pOpeningTag + pClosingTag + pOpeningTag + " my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(12, pComponent.getCaretPosition());
  }

  /**
   * Tests, if normal characters gets inserted normally
   *
   * @param pComponent Editor Pane
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(InsertClosingTagListener.class)
  void shouldInsertNormalCharacter(@NotNull JEditorPane pComponent)
  {
    pComponent.setCaretPosition(7);

    typeChar(pComponent, 'a');

    assertEquals("This isa my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(8, pComponent.getCaretPosition());
  }

}
