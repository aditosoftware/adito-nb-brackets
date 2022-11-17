package de.adito.aditoweb.nbm.brackets;

import de.adito.aditoweb.nbm.brackets.listener.BracketsKeyListener;
import org.openide.modules.OnStart;
import org.openide.text.CloneableEditorSupport;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.beans.*;

/**
 * Listener on all TopComponents to register the BracketsEditorListener
 *
 * @author w.glanzer, 26.10.2022
 * @see de.adito.aditoweb.nbm.brackets.listener.BracketsKeyListener
 */
@OnStart
public class TopComponentListener implements Runnable, PropertyChangeListener
{

  private final BracketsKeyListener editorListener = new BracketsKeyListener();

  @Override
  public void run()
  {
    TopComponent.getRegistry().addPropertyChangeListener(this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    if (TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName()))
    {
      Object newValue = evt.getNewValue();
      if (newValue instanceof CloneableEditorSupport.Pane)
      {
        JEditorPane pane = ((CloneableEditorSupport.Pane) newValue).getEditorPane();
        if (pane != null)
        {
          pane.removeKeyListener(editorListener);
          pane.addKeyListener(editorListener);
        }
      }
    }
  }

}
