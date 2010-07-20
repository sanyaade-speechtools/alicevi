/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package  edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.response.PoseAnimation;
import edu.cmu.cs.stage3.alice.core.response.Print;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.SoundResponse;
import edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * Title: CompositeComponentResponsePanel
 * Description: 
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author 
 * @version 1.0
 * 
 * CompositeComponentResponsePanel is a response panel made up of different components that can have items dragged and items
 * dropped onto it, depending on the types of objects being dragged and dropped.
 */

@SuppressWarnings("serial")
public class CompositeComponentResponsePanel extends  CompositeComponentElementPanel{

    public CompositeComponentResponsePanel(){
        super();
    }

    public void set(ObjectArrayProperty elements, CompositeResponsePanel owner, AuthoringTool authoringToolIn) {
        super.set(elements, owner, authoringToolIn);
    }

    /**
     * Makes the GUI of this component
     * @param Element - the current element to add to this element panel (should be a response panel
     * @return returns the GUI created from the current element to be added to the main application
     */
    protected Component makeGUI(Element currentElement){
        JComponent toAdd = null;
        if (currentElement instanceof Response){
            if (currentElement instanceof CompositeResponse){
                toAdd = GUIFactory.getGUI(currentElement);
            }
            else{
                if (currentElement != null){
                    toAdd = new ComponentResponsePanel();
                    ((ComponentResponsePanel)toAdd).set(currentElement);
                }
            }
            
        }
        return toAdd;
    }

    /**
     * Handles the dragging of objects onto the ResponsePanel, handling different types of objects that can
     * be dragged over this panel in a different manner.
     * 
     * @param dtde - the drop target drag event, or the object the user drags onto this panel
     */
    public void dragOver( DropTargetDragEvent dtde ) {
        int action = dtde.getDropAction();
        boolean isCopy = ((action & DnDConstants.ACTION_COPY) > 0);
        boolean isMove = ((action & DnDConstants.ACTION_MOVE) > 0);
        if (!m_owner.isExpanded()){
            if (m_owner.getParent() instanceof CompositeComponentResponsePanel){
                ((CompositeComponentResponsePanel)m_owner.getParent()).dragOver(dtde);
                return;
            }
        }
        if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.responseReferenceFlavor ) ) {
            try{
                Transferable transferable = DnDManager.getCurrentTransferable();
                Response response = (Response)transferable.getTransferData( ElementReferenceTransferable.responseReferenceFlavor );
                boolean isValid = checkLoop(response);
                if (isValid){
                    if (isMove){
                        dtde.acceptDrag( DnDConstants.ACTION_MOVE);
                    }
                    else if (isCopy){
                        dtde.acceptDrag( DnDConstants.ACTION_COPY );
                    }
                    insertDropPanel(dtde);
                }
                else{
                    dtde.rejectDrag();
                }
            } catch( UnsupportedFlavorException e ) {
                dtde.rejectDrag();
            } catch( IOException e ) {
                dtde.rejectDrag();
            } catch( Throwable t ) {
                dtde.rejectDrag();
            }
        }
        else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CopyFactoryTransferable.copyFactoryFlavor )){
            try {
                Transferable transferable = DnDManager.getCurrentTransferable();
                CopyFactory copyFactory = (CopyFactory)transferable.getTransferData( CopyFactoryTransferable.copyFactoryFlavor );
                Class<?> valueClass = copyFactory.getValueClass();
                if (Response.class.isAssignableFrom(valueClass)){
                    dtde.acceptDrag( DnDConstants.ACTION_MOVE);  //looks nicer
                    insertDropPanel(dtde);
                }
                else{
                    dtde.rejectDrag();
                }
            } catch( UnsupportedFlavorException e ) {
                dtde.rejectDrag();
            } catch( IOException e ) {
                dtde.rejectDrag();
            } catch( Throwable t ) {
                dtde.rejectDrag();
            }
        }
        else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor ) ) {
            try {
                Transferable transferable = DnDManager.getCurrentTransferable();
                Element element = (Element)transferable.getTransferData( ElementReferenceTransferable.elementReferenceFlavor );
                if (!((element instanceof Behavior) || (element instanceof World) || (element instanceof TextureMap))){
                    if (checkLoop(element)){
                        dtde.acceptDrag( DnDConstants.ACTION_MOVE);  //looks nicer
                        insertDropPanel(dtde);
                    }
                    else{
                        dtde.rejectDrag();
                    }
                }
                else{
                    dtde.rejectDrag();
                }
            } catch( UnsupportedFlavorException e ) {
                dtde.rejectDrag();
            } catch( IOException e ) {
                dtde.rejectDrag();
            } catch( Throwable t ) {
                dtde.rejectDrag();
            }
        }else if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor )
                  || AuthoringToolResources.safeIsDataFlavorSupported(dtde, PropertyReferenceTransferable.propertyReferenceFlavor )
                  || AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor )){
            if (isMove){
                dtde.acceptDrag( DnDConstants.ACTION_MOVE);
                insertDropPanel(dtde);
            }
            else if (isCopy){
                dtde.rejectDrag();
            }
        }else {
            dtde.rejectDrag();
        }
    }

    /**
     * Handles the dropping of objects onto the ResponsePanel, handling different types of objects that can
     * be dropped onto this panel in a different manner.
     * 
     * @param dtde - the drop target drop event, or the object the user drops onto this panel
     */
    public void drop( final DropTargetDropEvent dtde ) {
        HACK_started = false;
        boolean successful = true;
        int action = dtde.getDropAction();
        boolean isCopy = ((action & DnDConstants.ACTION_COPY) > 0 );
        boolean isMove = ((action & DnDConstants.ACTION_MOVE) > 0);
        if (!m_owner.isExpanded()){
            if (m_owner.getParent() instanceof  CompositeComponentElementPanel){
                (( CompositeComponentElementPanel)m_owner.getParent()).drop(dtde);
                return;
            }
        }
        
        Transferable transferable = dtde.getTransferable();

        if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, CopyFactoryTransferable.copyFactoryFlavor ) ) {
            try {
                CopyFactory copyFactory = (CopyFactory)transferable.getTransferData( CopyFactoryTransferable.copyFactoryFlavor );
                Class<?> valueClass = copyFactory.getValueClass();
                if (Response.class.isAssignableFrom(valueClass)){
                    dtde.acceptDrop( DnDConstants.ACTION_COPY);
                    successful = true;
					Response response = (Response)copyFactory.manufactureCopy(m_owner.getElement().getRoot(), null, null, m_owner.getElement() );
                    if (response != null){
                        performDrop(response, dtde);
                    }
                }
                else{
                    successful = false;
                    dtde.rejectDrop();
                }
            } catch( UnsupportedFlavorException e ) {
                AuthoringTool.showErrorDialog( "The drop failed because of a bad flavor.", e );
                successful = false;
            } catch( IOException e ) {
                AuthoringTool.showErrorDialog( "The drop failed because of an IO error.", e );
                successful = false;
            } catch( Throwable t ) {
                AuthoringTool.showErrorDialog( "The drop failed.", t );
                successful = false;
            }
        }else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.responseReferenceFlavor ) ) {
            try {
                Response response = (Response)transferable.getTransferData( ElementReferenceTransferable.responseReferenceFlavor );
                if (response instanceof CompositeResponse){
                    if (!isCopy && !isValidDrop( s_currentComponentPanel.getElement(), (CompositeResponse)response)){
                        successful = false;
                    }
                }
                if (successful){
                    if (isMove){
                        dtde.acceptDrop( DnDConstants.ACTION_MOVE );
                    }
                    else if (isCopy){
                        dtde.acceptDrop( DnDConstants.ACTION_COPY);
                    }
                    performDrop(response, dtde);
                    successful = true;
                }
            } catch( UnsupportedFlavorException e ) {
                AuthoringTool.showErrorDialog( "The drop failed because of a bad flavor.", e );
                successful = false;
            } catch( IOException e ) {
                AuthoringTool.showErrorDialog( "The drop failed because of an IO error.", e );
                successful = false;
            } catch( Throwable t ) {
                AuthoringTool.showErrorDialog( "The drop failed.", t );
                successful = false;
            }
        }
        else if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor ) ) {
            if (isMove){
                dtde.acceptDrop( DnDConstants.ACTION_MOVE );
                successful = true;
            }
            else if (isCopy){
                dtde.rejectDrop();
                successful = false;
            }
            if (successful){
                try {
                    ResponsePrototype responsePrototype = (ResponsePrototype)transferable.getTransferData( ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor );
                    if ((responsePrototype.getDesiredProperties() == null || responsePrototype.getDesiredProperties().length < 1) &&
                        !Print.class.isAssignableFrom(responsePrototype.getResponseClass())){
                        performDrop(responsePrototype.createNewResponse(), dtde);
                    } else if (responsePrototype.getDesiredProperties().length > 3){ //Bypass the popup menu and just put in defaults if it wants more than 3 parameters
						performDrop(responsePrototype.createNewResponse(), dtde);
                    }
                    else{
                        PopupItemFactory factory = new PopupItemFactory() {
                            public Object createItem( final Object object ) {
                                return new Runnable() {
                                    public void run() {
                                        if (object instanceof ResponsePrototype){
                                            performDrop(((ResponsePrototype)object).createNewResponse(), dtde);
                                        }
                                        else if (object instanceof ElementPrototype){
                                            Element newResponse = ((ElementPrototype)object).createNewElement();
                                            if (newResponse instanceof Response){
                                                performDrop((Response)newResponse, dtde);
                                            }
                                        }
                                    }
                                };
                            }
                        };
                        Vector<?> structure = null;
                        if (Print.class.isAssignableFrom(responsePrototype.getResponseClass())){
                            structure = PopupMenuUtilities.makeResponsePrintStructure(factory, componentElements.getOwner());
                        }
                        else{
                            structure= PopupMenuUtilities.makePrototypeStructure( responsePrototype, factory, componentElements.getOwner() );
                        }
                        JPopupMenu popup = PopupMenuUtilities.makePopupMenu( structure );
                        popup.addPopupMenuListener(this);
                        inserting = true;
                        popup.show( dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY() );
                        PopupMenuUtilities.ensurePopupIsOnScreen( popup );
                    }
                } catch( UnsupportedFlavorException e ) {
                    AuthoringTool.showErrorDialog( "The drop failed because of a bad flavor.", e );
                    successful = false;
                } catch( IOException e ) {
                    AuthoringTool.showErrorDialog( "The drop failed because of an IO error.", e );
                    successful = false;
                } catch( Throwable t ) {
                    AuthoringTool.showErrorDialog( "The drop failed.", t );
                    successful = false;
                }
            }
        }else if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, PropertyReferenceTransferable.propertyReferenceFlavor ) ) {
            if (isMove){
                dtde.acceptDrop( DnDConstants.ACTION_MOVE );
                successful = true;
            }
            else if (isCopy){
                dtde.rejectDrop();
                successful = false;
            }
            if (successful){
                try {
                    StringObjectPair[] known;
                    Class<?> animationClass;
                    Property property = (Property)transferable.getTransferData( PropertyReferenceTransferable.propertyReferenceFlavor );
                    if (property instanceof VehicleProperty){
                        StringObjectPair[] newKnown = {new StringObjectPair("element", property.getOwner()), new StringObjectPair("propertyName", property.getName()), new StringObjectPair("duration", new Double(0))};
                        known = newKnown;
                        animationClass = VehiclePropertyAnimation.class;
                    }
                    else{
                        StringObjectPair[] newKnown = {new StringObjectPair("element", property.getOwner()), new StringObjectPair("propertyName", property.getName())};
                        known = newKnown;
                        animationClass = PropertyAnimation.class;
                    }
                    PopupItemFactory factory = new PopupItemFactory() {
                        public Object createItem( final Object object ) {
                            return new Runnable() {
                                public void run() {
                                    if (object instanceof ResponsePrototype){
                                        performDrop(((ResponsePrototype)object).createNewResponse(), dtde);
                                    }
                                    else if (object instanceof ElementPrototype){
                                        Element newResponse = ((ElementPrototype)object).createNewElement();
                                        if (newResponse instanceof Response){
                                            performDrop((Response)newResponse, dtde);
                                        }
                                    }
                                }
                            };
                        }
                    };
                    String[] desired = {"value"};
                    ResponsePrototype rp = new ResponsePrototype(animationClass, known, desired);
                    Vector<?> structure = PopupMenuUtilities.makePrototypeStructure( rp, factory, componentElements.getOwner()  );
                    javax.swing.JPopupMenu popup = PopupMenuUtilities.makePopupMenu( structure );
                    popup.addPopupMenuListener(this);
                    inserting = true;
                    popup.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY() );
                    PopupMenuUtilities.ensurePopupIsOnScreen( popup );
                } catch( UnsupportedFlavorException e ) {
                    AuthoringTool.showErrorDialog( "The drop failed because of a bad flavor.", e );
                    successful = false;
                } catch( IOException e ) {
                    AuthoringTool.showErrorDialog( "The drop failed because of an IO error.", e );
                    successful = false;
                } catch( Throwable t ) {
                    AuthoringTool.showErrorDialog( "The drop failed.", t );
                    successful = false;
                }
            }
        }else if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor ) ) {
            if (isMove){
                dtde.acceptDrop( DnDConstants.ACTION_MOVE );
                successful = true;
            }
            else if (isCopy){
                dtde.rejectDrop();
                successful = false;
            }
            if (successful){
                try {
                    Variable variable = (Variable)transferable.getTransferData( ElementReferenceTransferable.variableReferenceFlavor );
                    if (!checkLoop(variable)){
                        dtde.rejectDrop();
                        successful = false;
                    }
                    else{
                        PopupItemFactory factory = new PopupItemFactory() {
                            public Object createItem( final Object object ) {
                                return new Runnable() {
                                    public void run() {
                                        if (object instanceof ResponsePrototype){
                                            performDrop(((ResponsePrototype)object).createNewResponse(), dtde);
                                        }
                                        else if (object instanceof ElementPrototype){
                                            Element newResponse = ((ElementPrototype)object).createNewElement();
                                            if (newResponse instanceof Response){
                                                performDrop((Response)newResponse, dtde);
                                            }
                                        }
                                    }
                                };
                            }
                        };
                        Vector<?> structure = PopupMenuUtilities.makeExpressionResponseStructure( variable, factory, componentElements.getOwner()  );
                        javax.swing.JPopupMenu popup = PopupMenuUtilities.makePopupMenu( structure );
                        popup.addPopupMenuListener(this);
                        inserting = true;
                        popup.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY() );
                        PopupMenuUtilities.ensurePopupIsOnScreen( popup );
                    }
                } catch( UnsupportedFlavorException e ) {
                    AuthoringTool.showErrorDialog( "The drop failed because of a bad flavor.", e );
                    successful = false;
                } catch( IOException e ) {
                    AuthoringTool.showErrorDialog( "The drop failed because of an IO error.", e );
                    successful = false;
                } catch( Throwable t ) {
                    AuthoringTool.showErrorDialog( "The drop failed.", t );
                    successful = false;
                }
            }
        }
        else if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor ) ) {

            try {
                final Element element = (Element)transferable.getTransferData( ElementReferenceTransferable.elementReferenceFlavor );
                if ((element instanceof Behavior) || (element instanceof World) || (element instanceof TextureMap)){
                    dtde.rejectDrop();
                    successful = false;
                }
                if (isMove){
                    dtde.acceptDrop( DnDConstants.ACTION_MOVE );
                    successful = true;
                }
                else if (isCopy){
                    dtde.rejectDrop();
                    successful = false;
                }
                if (successful){
                    if (element instanceof Sound){
                        SoundResponse r = new SoundResponse();
                        r.sound.set(element);
                        r.subject.set(element.getParent());
                        //r.addSoundListener(new movieMaker.SoundHandler(authoringTool.getSoundStorage()));
                        performDrop(r, dtde);
                    }
                    else if (element instanceof Pose){
                        PoseAnimation r = new PoseAnimation();
                        r.pose.set(element);
                        r.subject.set(element.getParent());
                        performDrop(r, dtde);
                    }
                    else{
                        final PopupItemFactory factory = new PopupItemFactory() {
                            public Object createItem( final Object object ) {
                                return new Runnable() {
                                    public void run() {
                                        if (object instanceof ResponsePrototype){
                                            performDrop(((ResponsePrototype)object).createNewResponse(), dtde);
                                        }
                                        else if (object instanceof ElementPrototype){
                                            Element newResponse = ((ElementPrototype)object).createNewElement();
                                            if (newResponse instanceof Response){
                                                performDrop((Response)newResponse, dtde);
                                            }
                                        }
                                    }
                                };
                            }
                        };
                        Vector<?> structure = PopupMenuUtilities.makeResponseStructure( element, factory, componentElements.getOwner()  );
                        javax.swing.JPopupMenu popup = PopupMenuUtilities.makePopupMenu( structure );
                        popup.addPopupMenuListener(this);
                        inserting = true;
                        popup.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY() );
                        PopupMenuUtilities.ensurePopupIsOnScreen( popup );
                    }
                }
            } catch( UnsupportedFlavorException e ) {
                AuthoringTool.showErrorDialog( "The drop failed because of a bad flavor.", e );
                successful = false;
            } catch( IOException e ) {
                AuthoringTool.showErrorDialog( "The drop failed because of an IO error.", e );
                successful = false;
            } catch( Throwable t ) {
                AuthoringTool.showErrorDialog( "The drop failed.", t );
                successful = false;
            }
        }else{
            dtde.rejectDrop();
            successful = false;
        }
        dtde.dropComplete(successful);
    }
}