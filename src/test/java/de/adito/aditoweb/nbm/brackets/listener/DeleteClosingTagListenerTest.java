package de.adito.aditoweb.nbm.brackets.listener;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.swing.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for deleting close tags on backspace
 *
 * @author w.glanzer, 17.11.2022
 * @see InsertClosingTagListener
 */
class DeleteClosingTagListenerTest extends AbstractTagListenerTest
{

  /**
   * Tests, if the tags are deleted
   *
   * @param pComponent  Editor Pane
   * @param pOpeningTag Opening tag
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(DeleteClosingTagListener.class)
  void shouldSkipTagStart(@NotNull JEditorPane pComponent, char pOpeningTag)
  {
    String text = pComponent.getText();
    pComponent.setCaretPosition(0);
    typeChar(pComponent, pOpeningTag);
    typeChar(pComponent, '\b', KeyEvent.VK_BACK_SPACE);
    assertEquals(text, pComponent.getText());
    assertEquals(0, pComponent.getCaretPosition());
  }

  /**
   * Tests, if the tags are deleted
   *
   * @param pComponent  Editor Pane
   * @param pOpeningTag Opening tag
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(DeleteClosingTagListener.class)
  void shouldSkipTagMiddle(@NotNull JEditorPane pComponent, char pOpeningTag)
  {
    String text = pComponent.getText();
    pComponent.setCaretPosition(7);
    typeChar(pComponent, pOpeningTag);
    typeChar(pComponent, '\b', KeyEvent.VK_BACK_SPACE);
    assertEquals(text, pComponent.getText());
    assertEquals(7, pComponent.getCaretPosition());
  }

  /**
   * Tests, if the tags are deleted
   *
   * @param pComponent  Editor Pane
   * @param pOpeningTag Opening tag
   */
  @ParameterizedTest
  @ArgumentsSource(ArgProv.class)
  @ArgProv.Listener(DeleteClosingTagListener.class)
  void shouldSkipTagEnd(@NotNull JEditorPane pComponent, char pOpeningTag)
  {
    String text = pComponent.getText();
    pComponent.setCaretPosition(pComponent.getText().length() - 1);
    typeChar(pComponent, pOpeningTag);
    typeChar(pComponent, '\b', KeyEvent.VK_BACK_SPACE);
    assertEquals(text, pComponent.getText());
    assertEquals(pComponent.getText().length() - 1, pComponent.getCaretPosition());
  }

}
