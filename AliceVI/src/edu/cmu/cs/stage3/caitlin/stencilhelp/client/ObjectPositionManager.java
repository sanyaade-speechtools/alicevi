package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

/**
 * <p>Title: ObjectPositionManager</p>
 * <p>Description: This is a light-weight class designed for objects to ask where
 * they should be, something like a stage manager.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.awt.Rectangle;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException;

public class ObjectPositionManager {
  StencilApplication stencilApp = null;

  public ObjectPositionManager(StencilApplication stencilApp) {
    this.stencilApp = stencilApp;
  }
  public double getScreenHeight() {
    return stencilApp.getScreenSize().getHeight();
  }
  public double getScreenWidth() {
    return stencilApp.getScreenSize().getWidth();
  }
  public Rectangle getInitialBox(String ID) {
    try {
      if (stencilApp.isIDVisible(ID)) {
        return stencilApp.getBoxForID(ID);
      }
    } catch (IDDoesNotExistException idne) {
    }
    return null;
  }
  public Rectangle getBoxForID(String ID) {
    try {
      if (!(stencilApp.isIDVisible(ID))) {
        stencilApp.makeIDVisible(ID);
      }
      return stencilApp.getBoxForID(ID);
    } catch (IDDoesNotExistException idne) {
    }
    return null;
  }
}