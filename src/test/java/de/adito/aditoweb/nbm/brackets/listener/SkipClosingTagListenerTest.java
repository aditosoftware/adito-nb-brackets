package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for deleting close tags, to mimic the "skip character" behaviour
 *
 * @author w.glanzer, 17.11.2022
 * @see InsertClosingTagListener
 */
class SkipClosingTagListenerTest extends AbstractTagListenerTest
{

  /**
   * Tests, if the tags are skipped
   *
   * @param pComponent  Editor Pane
   * @param pOpeningTag Opening tag
   * @param pClosingTag Closing tag
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(SkipClosingTagListener.class)
  void shouldSkipTag(@NotNull JEditorPane pComponent, char pOpeningTag, char pClosingTag)
  {
    pComponent.setCaretPosition(7);

    typeChar(pComponent, pOpeningTag);
    typeChar(pComponent, 'a');
    typeChar(pComponent, pClosingTag);

    pComponent.setCaretPosition(9);
    typeChar(pComponent, pClosingTag);

    assertEquals("This is" + pOpeningTag + "a" + pClosingTag + " my text.\r\nThis is line number two", pComponent.getText());
    assertEquals(10, pComponent.getCaretPosition());
  }

}
