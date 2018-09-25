/**
 * Document listener.
 *
 * \b Package: \n
 * org.idde.common.transport
 *
 * @see org.idde.chat
 * @see org.idde.chat.view
 *
 * @since Class created on 10/08/2010
 *
 * @author Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b Maintainers: \n
 * Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b License: \n
 * Licensed under BSD License {@link http://www.opensource.org/licenses/bsd-license.php}
 *
 * @version $Id$
 */
package moduleIdDE;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.Timestamp;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.idde.common.model.XMPPManager;
import org.idde.common.transport.Instruction;
import org.idde.common.model.InstructionDBModel;
import org.idde.common.model.InstructionProtocol;
import org.idde.editor.controller.OperationalTransformation;
import org.idde.editor.controller.SessionControl;
import org.idde.editor.model.Request;
import org.idde.util.Database;
import org.idde.util.Util;
import org.netbeans.editor.DocumentUtilities;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.loaders.DataObject;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.text.Line.Part;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

public class DocumentChangesListener implements DocumentListener, KeyListener
{
    private static DocumentChangesListener DefaultListener = null;

    /**
     * This method returns a instance of DocumentChangesListener, which controls the changes on a document.
     * If the instance if null (not yet initiated), this methods initiates it.
     * 
     * @return DocumentChangesListener DefaultListener
     */
    public static DocumentChangesListener getDefaultListener()
    {
        if ( DocumentChangesListener.DefaultListener == null )
        {
            DocumentChangesListener.DefaultListener = new DocumentChangesListener();
        }

        return DocumentChangesListener.DefaultListener;
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
//        Runnable r = new DocumentChangeThread(e, DocumentChangeThread.INSERT);
//        new Thread(r).start();
        run(e, INSERT);
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
//        Runnable r = new DocumentChangeThread(e, DocumentChangeThread.DELETE);
//        new Thread(r).start();

        run(e, DELETE);
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        System.out.println( "Modificado no offset: " +e.getOffset());

        run(e, "outro");
//        Document doc = e.getDocument();
//        String newContent;
//
//        try
//        {
//            newContent = doc.getText(e.getOffset(), e.getLength());
//            newContent = newContent.toLowerCase();
//            System.out.println( "Modificado '" + newContent +"' no offset: " +e.getOffset());
//        }
//        catch (BadLocationException ex)
//        {
//            Exceptions.printStackTrace(ex);
//        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
//        System.out.println( "Dentro do DocumentChangesListener" );
//        System.out.println( "keyPressed char -> " + e.getKeyChar() );
//        System.out.println( "keyPressed code -> " + e.getKeyCode() );
//        System.out.println( "keyPressed text -> " + e.getKeyText(e.getKeyCode()) );
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Used to indicate that a text was inserted
     */
    public static final String INSERT = "insert";

    /**
     * Used to indicate that a text was deleted
     */
    public static final String DELETE = "delete";

    public void run(DocumentEvent docEvent, String changeType)
    {
        // Used to convert object to xml string
        XStream xstream = new XStream();
        xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

        Document doc = docEvent.getDocument();
        String newContent;

        // Get document name
        String filePath = (String) doc.getProperty(Document.TitleProperty);
        String fileName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

        // temp
        NbEditorDocument document = (NbEditorDocument) docEvent.getDocument();
        DataObject dobj = null;
//        StyledDocument docToGo = null;
        dobj = NbEditorUtilities.getDataObject(document);
//        docToGo = document;
        final int col = NbDocument.findLineColumn(document, docEvent.getOffset());
        int lin = NbDocument.findLineNumber(document, docEvent.getOffset());

        // /temp

        final Annotation ann = new EditAnnotation("local user", lin);
        Line line = NbEditorUtilities.getLine(doc, docEvent.getOffset(), false);

        // funcionando
        Part lp = line.createPart(col, docEvent.getLength());
//        ann.attach(lp);

        // Mark changes
//        Util.insertEditAnnotation(doc, docEvent.getOffset(), docEvent.getLength(), false, "local user - changed "+docEvent.getLength() + " chars at column " );


        // outra forma de adicionar o annotation
//        document.addAnnotation(new Position() {
//
//            @Override
//            public int getOffset()
//            {
//                return col;
//            }
//        }, docEvent.getLength() , ann);


        // if nobody is sharing this file, stop this
        if ( SessionControl.getSharedUsers(fileName).isEmpty() )
        {
            return;
        }

        Instruction i = null;

        // Create a object to write do database
        InstructionDBModel im = new InstructionDBModel();

        if (changeType.equals(INSERT)) // String was inserted
        {
            try
            {
                newContent = doc.getText(docEvent.getOffset(), docEvent.getLength());

                Segment text = new Segment();
                doc.getText(docEvent.getOffset(), docEvent.getLength(), text);

//                System.out.println("text: " + text);

                // Verify if it's a non valid char
                if (( XMPPManager.allowOnlyAscii() ) && ( ! Util.isValidAsciiChar(newContent) ))
                {
                    //JOptionPane.showMessageDialog(null, "<html>\"<b>" + newContent + "</b>\" is not a valid char!\nOnly ASCII chars are alowed at this time.", "IdDE - ERROR", JOptionPane.ERROR_MESSAGE);

                    Util.showErrorMessage("<html>Sorry, <b><font size='+1'>" + newContent +
                                          "</font></b> is not a valid char! <br> Only ASCII chars are allowed at this time.");

                    // remove char from document, but first, remove listener
                    doc.removeDocumentListener( DocumentChangesListener.getDefaultListener() );
                    doc.remove(docEvent.getOffset(), docEvent.getLength());
                    doc.addDocumentListener( DocumentChangesListener.getDefaultListener() );

                    return;
                }

                //newContent = newContent.toLowerCase();
                System.out.println("LOCAL CHANGE: '" + newContent + "' at offset: " + docEvent.getOffset());

                // Compose instruction message

                // arg0 = filename
                // arg1 = insert 2i
                // arg2 = offset
                // arg3 = lenght
                // arg4 = text
                // mapSiteState = Map

                //<Operational Transformation>
                // update the Operational Transformation state
                OperationalTransformation ot = SessionControl.getFileSessionSiteStates(fileName);
                // Increment local user's site state info
                ot.incrementSiteState(SessionControl.LOCAL_USER);

                Request r = new Request(SessionControl.LOCAL_USER, INSERT, docEvent.getOffset(), newContent, docEvent.getLength(), ot.getSiteStateClone());
                ot.setLastLocalChangeOffSet(r.getClone());

                Request r1 = r.getClone();
                ot.addToLog(r1);

                // Create a XML for the map
                XStream xStream = new XStream(new DomDriver());
                xStream.alias("map", java.util.Map.class);
                String xmlSiteState = xStream.toXML(ot.getSiteState());
                //</Operational Transformation>

                i = new Instruction(InstructionProtocol.MSG_UPDATE_SHARED_TEXT, fileName,
                        InstructionProtocol.INSERT_TEXT, Integer.toString(docEvent.getOffset()),
                        Integer.toString(docEvent.getLength()), newContent, xmlSiteState );

                im.setFileName(fileName);
                im.setText(newContent);
                im.setOffset(docEvent.getOffset());
                im.setLenght(docEvent.getLength());
//                im.setDateTime( new Timestamp(System.currentTimeMillis()) );

                im.setDateTime( new java.sql.Timestamp( (new java.util.Date()).getTime() ) );
                im.setInstruction(InstructionProtocol.INSERT_TEXT);
                im.setUser("local_user");

//                String cm = "Inserir " + newContent +" offset " +docEvent.getOffset();
//                SessionControl.sendChangesToParties(fileName, cm);
            }
            catch (BadLocationException ex)
            {
                Exceptions.printStackTrace(ex);
            }
        }
        else if (changeType.equals(DELETE))
        {
            System.out.println("Removidos " + docEvent.getLength() + " caracteres no offset: " + docEvent.getOffset());

            // Compose instruction message

            // arg0 = filename
            // arg1 = 2d
            // arg2 = offset
            // arg3 = lenght

            i = new Instruction(InstructionProtocol.MSG_UPDATE_SHARED_TEXT, fileName,
                    InstructionProtocol.DELETE_TEXT, Integer.toString(docEvent.getOffset()),
                    Integer.toString(docEvent.getLength()));

            im.setFileName(fileName);
            im.setText("");
            im.setOffset(docEvent.getOffset());
            im.setLenght(docEvent.getLength());
            im.setDateTime( new Timestamp(System.currentTimeMillis()) );
            im.setInstruction(InstructionProtocol.DELETE_TEXT);
            im.setUser("local_user");
            
//            String cm = "Remover "+ docEvent.getLength() +" caracteres no offset: " + docEvent.getOffset();
//            SessionControl.sendChangesToParties(fileName, cm);
        }
        else
        {
            Util.showErrorMessage("Unknown event occurred on document (DocumentChangesListener)!");
            System.out.println("*** Outro evento não tratado ocorreu no documento" + changeType );
        }

        // Add message to buffer. SessionControl takes care to send it tor other parties
        SessionControl.addMessageToBuffer(fileName, xstream.toXML(i));
        try
        {
            // Insert log record in database
            Database.insertSessionInstruction(im);
        }
        catch (Exception ex)
        {
            if (JOptionPane.showConfirmDialog(null, "Error registering log in database:\n"+ex.getMessage()+"\nDo you want to disable logging to database?", "IdDE - Error", JOptionPane.YES_NO_OPTION) == 0)
            {
                Database.setDisableLogInstruction(Boolean.TRUE);
            }
        }

    }

}
