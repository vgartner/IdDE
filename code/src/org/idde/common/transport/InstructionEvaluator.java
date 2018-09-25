/**
 * Coded message evaluator. Verifies which type of message was received.
 *
 * \b Package: \n
 * org.idde.common.transport
 *
 * @see org.idde.chat
 * @see org.idde.chat.view
 *
 * @since Class created on 04/07/2010
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
package org.idde.common.transport;

import org.idde.common.model.InstructionProtocol;
import org.idde.common.model.InstructionDBModel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import moduleIdDE.DocumentChangesListener;
import org.idde.common.model.Contact;
import org.idde.chat.model.ChatMessage;
import org.idde.chat.view.ChatFrame;
import org.idde.common.model.XMPPManager;
import org.idde.common.view.ContactListPanel;
import org.idde.editor.controller.OperationalTransformation;
import org.idde.editor.controller.SessionControl;
import org.idde.editor.model.Request;
import org.idde.fileManager.view.ProgressFrame;
import org.idde.task.model.Task;
import org.idde.util.Database;
import org.idde.util.Logger;
import org.idde.util.Util;
import org.netbeans.api.actions.Openable;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.tmatesoft.sqljet.core.SqlJetException;

/**
 * This class provides function to evaluate coded messages.
 */
public class InstructionEvaluator
{

    ChatMessage msg;
    ContactListPanel clp;
    Instruction instruction;

    public InstructionEvaluator()
    {
    }

    /**
     * Example of a message:
     * [code]||share||FileName||FileContentNowHere
     *
     * @param message
     * @return
     */
    public boolean processMessage(ChatMessage msg, ContactListPanel clp)
    {
        Logger.getLogger(this).debug("[Debug] Processing message... ");

        this.msg = msg;
        this.clp = clp;

        // Convert received xml message to object
        XStream xstream = new XStream();
        xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

        String xmlMessage = msg.getMessage();

        this.instruction = (Instruction) xstream.fromXML(xmlMessage);

        Logger.getLogger(this).debug("[Debug] Verifying message type " + this.instruction.getCode().toString());

        // Verify which message and send return
        if (this.instruction.getCode().equals(InstructionProtocol.MSG_GET_LANDLINE))
        {
            InstructionComposer.sendLandLineToRemote(clp, msg.getFrom());
        }
        else
        {
            if (this.instruction.getCode().equals(InstructionProtocol.MSG_GET_MOBILE))
            {
                InstructionComposer.sendMobileToRemote(clp, msg.getFrom());
            }
            else
            {
                if (this.instruction.getCode().equals(InstructionProtocol.MSG_GET_SIP))
                {
                    InstructionComposer.sendSIPToRemote(clp, msg.getFrom());
                }
                else
                {
                    if (this.instruction.getCode().equals(InstructionProtocol.MSG_GET_APPVERSION))
                    {
                        InstructionComposer.sendAppVersionToRemote(clp, msg.getFrom());
                    }
                    // Remote has requested to see which files are in shared directory
                    else
                    {
                        if (this.instruction.getCode().equals(InstructionProtocol.MSG_VIEW_SHARED_FOLDER))
                        {
                            // TODO: ler arquivos da pasta
                            String files = "file1, file2, file3, file4, file5";

                            Instruction i = new Instruction(InstructionProtocol.MSG_SHOW_FILES_SHARED, files);

                            Logger.getLogger(this).debug("[Debug] Sending file list in shared folder to remote: " + files);

                            clp.sendInstructionMessage(xstream.toXML(i), msg.getFrom());
                        }
                        // Remote requested to see my tasks
                        else
                        {
                            if (this.instruction.getCode().equals(InstructionProtocol.MSG_GET_TASKS))
                            {
                                InstructionComposer.sendTasksToRemote(msg, clp);
                            }
                            // Remote requested to see my appointments
                            else
                            {
                                if (this.instruction.getCode().equals(InstructionProtocol.MSG_GET_AGENDA))
                                {
                                    InstructionComposer.sendAppointmentsToRemote(msg, clp);
                                }
                                // Remote requested to add a new tasks
                                else
                                {
                                    if (this.instruction.getCode().equals(InstructionProtocol.MSG_ADD_TASK))
                                    {
                                        addNewTask();
                                    }
                                    // Remote requested to add a new agenda
                                    else
                                    {
                                        if (this.instruction.getCode().equals(InstructionProtocol.MSG_ADD_AGENDA))
                                        {
                                            InstructionComposer.newAgenda(clp, msg.getFrom(), this.instruction);
                                        }
                                        // this message is received when remote asks to share a file
                                        else
                                        {
                                            if (this.instruction.getCode().equals(InstructionProtocol.MSG_START_SHARING_FILE))
                                            {
                                                startEditingSharedFile();
                                            }
                                            // This message is received when remote hasn't accepted your invitation
                                            else
                                            {
                                                if (this.instruction.getCode().equals(InstructionProtocol.MSG_REJECT_START_SHARING_FILE))
                                                {
                                                    JOptionPane.showMessageDialog(null, "Remote user \"" + msg.getFrom().getNickName()
                                                            + "\" has declined you invitation to share \"" + this.instruction.getArg0() + "\".",
                                                            "IdDE - Shared editing", 1);
                                                }
                                                // This message is received when remote hasn't accepted new task
                                                else
                                                {
                                                    if (this.instruction.getCode().equals(InstructionProtocol.MSG_REJECT_NEW_TASK))
                                                    {
                                                        JOptionPane.showMessageDialog(null, "Remote user \"" + msg.getFrom().getNickName()
                                                                + "\" hasn't accepted your task.",
                                                                "IdDE - New Task", 1);
                                                    }
                                                    // This message is received when remote has accepted new task
                                                    else
                                                    {
                                                        if (this.instruction.getCode().equals(InstructionProtocol.MSG_ACCEPT_NEW_TASK))
                                                        {
                                                            JOptionPane.showMessageDialog(null, "Remote user \"" + msg.getFrom().getNickName()
                                                                    + "\" has accepted your task.",
                                                                    "IdDE - New Task", 1);
                                                        }
                                                        // This message is received when remote has accepted new appointment
                                                        else
                                                        {
                                                            if (this.instruction.getCode().equals(InstructionProtocol.MSG_ACCEPT_NEW_AGENDA))
                                                            {
                                                                JOptionPane.showMessageDialog(null, "Remote user \"" + msg.getFrom().getNickName()
                                                                        + "\" has accepted new appointment.",
                                                                        "IdDE - New Appointment", 1);
                                                            }
                                                            // This message is received when remote has accepted an invitation
                                                            else
                                                            {
                                                                if (this.instruction.getCode().equals(InstructionProtocol.MSG_ACCEPT_START_SHARING_FILE))
                                                                {
                                                                    // Add share to session. If a session already already exists for this file, propagate this join to another users
                                                                    addToSharedSession(msg.getFrom(), this.instruction.getArg0(), Boolean.TRUE);
//            addToSharedSession(msg.getFrom(), this.instruction.getArg0(), Boolean.FALSE);

                                                                    JOptionPane.showMessageDialog(null, "You are now sharing file '" + this.instruction.getArg0() + "' with remote user '" + msg.getFrom().getNickName() + "'.",
                                                                            "IdDE - Shared editing", JOptionPane.INFORMATION_MESSAGE);
                                                                }
                                                                // this message is received when a new remote user joins a shared editing session on a file
                                                                else
                                                                {
                                                                    if (this.instruction.getCode().equals(InstructionProtocol.MSG_JOIN_SHARING_FILE))
                                                                    {
                                                                        // convert xml to object
                                                                        XStream xContact = new XStream();
                                                                        xContact.alias("contact", Contact.class);

                                                                        Contact c = (Contact) xContact.fromXML(this.instruction.getArg1());

                                                                        JOptionPane.showMessageDialog(null, "Remote user \"" + c.getNickName() + "\" has just joined shared editing session on file \""
                                                                                + this.instruction.getArg0() + "\".",
                                                                                "IdDE - Shared editing", JOptionPane.INFORMATION_MESSAGE);

                                                                        // Join to session
                                                                        addToSharedSession(c, this.instruction.getArg0(), Boolean.FALSE);
                                                                    }
                                                                    else
                                                                    {
                                                                        if (this.instruction.getCode().equals(InstructionProtocol.MSG_UPDATE_CARET_POSITION))
                                                                        {
                                                                            updateCaretPositionAnnotation();
                                                                        }
                                                                        else
                                                                        {
                                                                            if (this.instruction.getCode().equals(InstructionProtocol.MSG_SEND_FILE))
                                                                            {
                                                                                Contact remoteUser = msg.getFrom();
                                                                                String fileName = this.instruction.getArg0();
                                                                                String taskMessage = "Remote user \"" + remoteUser.toString() + "\" wants to send you a file:\n"
                                                                                        + fileName + "\n\nDo you accept this file? ";

                                                                                //        Object[] options = {"Yes, Create Task", "No, thanks"};

                                                                                if (JOptionPane.showConfirmDialog(null, taskMessage, "IdDE - File Transfer", JOptionPane.YES_NO_OPTION) == 0)
                                                                                {
                                                                                    // ACCEPTED
                                                                                    xstream = new XStream();
                                                                                    xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

                                                                                    Instruction i = new Instruction(InstructionProtocol.MSG_ACCEPT_FILE, this.instruction.getArg0(), this.instruction.getArg1());
                                                                                    clp.sendInstructionMessage(xstream.toXML(i), msg.getFrom());

                                                                                    ProgressFrame progress = new ProgressFrame(XMPPManager.getConnection(), msg.getFrom(), this.instruction.getArg0());
                                                                                    progress.receiveFile();
                                                                                }
                                                                                else
                                                                                {
                                                                                    xstream = new XStream();
                                                                                    xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

                                                                                    Instruction i = new Instruction(InstructionProtocol.MSG_REJECT_FILE, this.instruction.getArg0(), this.instruction.getArg1());
                                                                                    clp.sendInstructionMessage(xstream.toXML(i), msg.getFrom());
                                                                                }
                                                                            }
                                                                            // Remote user hasn't accepted file transfer
                                                                            else
                                                                            {
                                                                                if (this.instruction.getCode().equals(InstructionProtocol.MSG_REJECT_FILE))
                                                                                {
                                                                                    JOptionPane.showMessageDialog(null, "Remote user \"" + msg.getFrom() + "\" hasn't accepted the file \""
                                                                                            + this.instruction.getArg0() + "\".",
                                                                                            "IdDE - File transfer", JOptionPane.INFORMATION_MESSAGE);
                                                                                }
                                                                                // Remote user HAS accepted file transfer
                                                                                else
                                                                                {
                                                                                    if (this.instruction.getCode().equals(InstructionProtocol.MSG_ACCEPT_FILE))
                                                                                    {
                                                                                        String file = this.instruction.getArg1();
                                                                                        ProgressFrame progress = new ProgressFrame(XMPPManager.getConnection(), msg.getFrom(), file);
                                                                                        progress.sendFile();
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        if (this.instruction.getCode().equals("teste_criacao_novo_doc"))
                                                                                        {
                                                                                            JTextComponent editor = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
                                                                                            Document doc = editor.getDocument();

                                                                                            MutableAttributeSet mas = (MutableAttributeSet) new SimpleAttributeSet();
                                                                                            StyleConstants.setForeground(mas, Color.red);

                                                                                            Integer col = Integer.parseInt(this.instruction.getArg1());
                                                                                            try
                                                                                            {
                                                                                                doc.insertString(col, "TEXTO", mas);
                                                                                            }
                                                                                            catch (BadLocationException ex)
                                                                                            {
                                                                                                Exceptions.printStackTrace(ex);
                                                                                            }
                                                                                            editor.select(col, col + 5);
                                                                                            editor.setCaretPosition(col + 5);

                                                                                            StyledDocument doc1 = (StyledDocument) editor.getDocument();

                                                                                            BaseDocument bd = (BaseDocument) editor.getDocument();
                                                                                            try
                                                                                            {
                                                                                                bd.replace(20, 20, "usando replace", mas);
                                                                                            }
                                                                                            catch (BadLocationException ex)
                                                                                            {
                                                                                                Exceptions.printStackTrace(ex);
                                                                                            }

                                                                                            int currentLine = NbDocument.findLineNumber(doc1, editor.getCaretPosition());
                                                                                            int nextLineOffset = NbDocument.findLineOffset(doc1, currentLine + 1);

                                                                                            try
                                                                                            {
                                                                                                doc1.insertString(nextLineOffset, "texto na nova linha", mas);
                                                                                            }
                                                                                            catch (BadLocationException ex)
                                                                                            {
                                                                                                Exceptions.printStackTrace(ex);
                                                                                            }
//            try
//            {
//                int reformat = bd.getFormatter().reformat(bd, 0, bd.getLength() - 1);
//                //Reformat.get(bd).unlock();
//                //bd.insertString(nextLineOffset, message, null)
//            }
//            catch (BadLocationException ex)
//            {
//                Exceptions.printStackTrace(ex);
//            }
                                                                                            //Reformat.get(bd).unlock();

                                                                                            //bd.insertString(nextLineOffset, message, null)


                                                                                            //org.netbeans.api.editor.EditorRegistry.lastFocusedComponent().getd

                                                                                            Position position = new MyPosition(col + 10);
                                                                                            Annotation annotation = new MyAnnotation(position.getOffset());
                                                                                            //NbDocument.addAnnotation((StyledDocument)doc, position, -1, annotation);

                                                                                            NbDocument.addAnnotation(doc1, position, col + 10, annotation);

//            Highlighter.HighlightPainter highlightpainter = new MyHighlightPainters(Color.yellow);
//            Highlighter highlighters = ed.getHighlighter();
//            highlighters.removeAllHighlights();
//            try
//            {
//                highlighters.addHighlight(10, 20, highlightpainter);
//        AbstractDocument doc1 = (AbstractDocument) ed.getDocument();
//        MutableAttributeSet mas = (MutableAttributeSet)new SimpleAttributeSet ();
//        StyleConstants.setForeground(mas, Color.red);
//        doc.insertString(10, "ViLsOn", mas);
//        doc1.insertString(50, "CrIsTiAnO", mas);

                                                                                            //ed.setDocument(doc1);

//
//        JEditorPane editorPane;
//
//

//		Element e = (Element) doc.getDefaultRootElement();
//		// Copy attribute Set
//		AttributeSet attr = e.getAttributes().copyAttributes();
//		doc.insertString(doc.getLength(), "Naam", attr);
//
//            }
//            catch (BadLocationException ex)
//            {
//                Exceptions.printStackTrace(ex);
//            }

                                                                                            Logger.getLogger(this).debug("[Debug] receiving file");
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                            if (this.instruction.getCode().equals(InstructionProtocol.MSG_EXIT_SHARING_FILE.toString()))
                                                                                            {
                                                                                                SessionControl.removeUseFromSession(msg.getFrom().getNickName(), this.instruction.getArg0());
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                                if (this.instruction.getCode().equals(InstructionProtocol.MSG_UPDATE_SHARED_TEXT.toString()))
                                                                                                {
                                                                                                    updateEditingText();
                                                                                                }
                                                                                                // Unknown or invalid request
                                                                                                else
                                                                                                {
                                                                                                    Instruction i = new Instruction(InstructionProtocol.MSG_INVALID_REQUEST, this.instruction.getCode());

                                                                                                    Logger.getLogger(this).debug("[Debug] Invalid request received: " + this.instruction.getCode());

                                                                                                    clp.sendInstructionMessage(xstream.toXML(i), msg.getFrom());
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private void updateCaretPositionAnnotation()
    {
        Iterator i = org.netbeans.api.editor.EditorRegistry.componentList().listIterator();

        JTextComponent ed1, ed = null;
        Document doc1, doc = null;
        String filePath;
        String fileName;
        Boolean continueLoop = true;

        while (i.hasNext() && continueLoop)
        {
            ed1 = (JTextComponent) i.next();
            doc1 = ed1.getDocument();

            filePath = (String) doc1.getProperty(Document.TitleProperty);
            fileName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

            if (fileName.indexOf(" ") > 0)
            {
                fileName = fileName.substring(0, fileName.indexOf(" "));
            }

            if (fileName.equals(instruction.getArg0()))
            {
//                System.out.print("[InstructionEvaluator] File is opened in Netbeans: " + fileName + "\n");
                ed = ed1;
                doc = doc1;

                continueLoop = false;
            }
        }

        // If document wasn't found in Netbeans, do nothing.
        if (doc == null)
        {
            String errorMesg = "[ERROR] IdDE::updateEditingText InstructionEvaluator - File " + instruction.getArg0() + " not opened/found in Netbeans!";
            Util.showErrorMessage(errorMesg);
            Util.setStatus(errorMesg);

            return;
        }

        Util.insertCaretPosAnnotation(doc, Integer.parseInt(this.instruction.getArg1()), msg.getFrom().getNickName());
    }

    public static class MyPosition implements Position
    {

        private int offset;

        public MyPosition(int incl)
        {
            offset = incl;
        }

        public int getOffset()
        {
            return offset;
        }
    }

    public static class MyAnnotation extends Annotation
    {

        private int offset;

        public MyAnnotation(int offset)
        {
            this.offset = offset;
        }

        public int getOffset()
        {
            return offset;
        }

        public String getAnnotationType()
        {
            return "org-netbeans-modules-cnd-cpp-parser_annotation_err";
        }

        public String getShortDescription()
        {
            return "CppParserAnnotationInclude";
        }
    }

    // http://wiki.netbeans.org/DevFaqFileObjectInMemory
    // http://tdamir.blogspot.com/2008/02/file-opening-editorcookie-or-opencookie.html
    // http://wiki.netbeans.org/EditorCookieOrOpenCookie
    private void startEditingSharedFile()
    {
        String editMsg = "Remote user \"" + msg.getFrom() + "\" is inviting you to join and edit a shared file \""
                + this.instruction.getArg0()
                + "\". \nDo you want to join? ";

//        Object[] options = {"Yes, Create Task", "No, thanks"};

        if (JOptionPane.showConfirmDialog(null, editMsg, "IdDE - Join Shared Editing", JOptionPane.YES_NO_OPTION) == 0)
        {
            String fileName = System.getProperty("user.home") + System.getProperty("file.separator") + this.instruction.getArg0();

            JOptionPane.showMessageDialog(null, "You clicked \"Ok\". Joining shared editing with " + msg.getFrom() + ". \nCreating new file: " + fileName, "IdDE", 1);

            // Creating new document in Netbeans
            try
            {
                PrintWriter out = new PrintWriter(new FileWriter(fileName));
                out.println(this.instruction.getArg1());
                out.close();

                DataObject file = DataObject.find(FileUtil.toFileObject(new File(fileName)));
                Openable fileOpener = file.getLookup().lookup(Openable.class);
                fileOpener.open();


////                // Works, but problem with content putting content
////                FileSystem fs = FileUtil.createMemoryFileSystem();
////                FileObject fob = fs.getRoot().createData(fileName, fileType);
////                try
////                {
////                    DataObject data = DataObject.find((FileObject) fob);
////                    OpenCookie cookie = (OpenCookie) data.getCookie(OpenCookie.class);
////                    cookie.open();
////                }
////                catch (DataObjectNotFoundException ex)
////                {
////                    Exceptions.printStackTrace(ex);
////                }
//
//                JTextComponent ed = org.netbeans.api.editor.EditorRegistry.focusedComponent();
//                Document doc = ed.getDocument();
//
//                try
//                {
//                    doc.insertString(0, this.instruction.getArg1(), null);
//                }
//                catch (BadLocationException ex)
//                {
//                    Exceptions.printStackTrace(ex);
//                }

                // Add share to session. If a session already already exists for this file, spread do other users
//                addToSharedSession( msg.getFrom(), this.instruction.getArg0(), Boolean.TRUE );
                addToSharedSession(msg.getFrom(), this.instruction.getArg0(), Boolean.FALSE);
            }
            catch (IOException ex)
            {
                Exceptions.printStackTrace(ex);
            }

            Logger.getLogger(this).debug("[Debug] Sending my answer to remote: I'm joining shared editing session!!!");

            // Accepted. Send reply.

            // Used to convert object to xml string
            XStream xstream = new XStream();
            xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

            Instruction i = new Instruction(InstructionProtocol.MSG_ACCEPT_START_SHARING_FILE, this.instruction.getArg0());

            //sendInstruction(msg.getFrom(), xstream.toXML(i));

            clp.sendInstructionMessage(xstream.toXML(i), msg.getFrom());
        }
        else
        {
            JOptionPane.showMessageDialog(null, "You clicked \"Cancel\". Not joining editing session.", "IdDE", 1);

            // Not accepted. Reply to user.
            String msgResponse = "IDDE Daemon: " + msg.getTo() + " has declined your invitation! :-/";

            Logger.getLogger(this).debug("[Debug] Sending my answer to remote: no sharing this time.");

            clp.sendInstructionMessage(msgResponse, msg.getFrom());
        }

    }

    /**
     * Sends a join message to users that already are sharing this file, informing to register
     * this new user in this session. And also sends a message for the new user, so that he register
     * this existing users.
     * If this isn't done, when the new user changes his file, existing users won't
     * receive an update message. The same occurs when existing an user changes his file, the new user
     * won't receive a message.
     *
     * @param contactSet
     * @param fileName
     * @param newUser
     */
    private void sendJoinMessage(Set contactSet, String fileName, Contact newUser)
    {
        // Used to convert object to xml string
        XStream xstream = new XStream();
        xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

        XStream xContact = new XStream();
        xContact.alias("contact", Contact.class);

        // Message for existing users in session
        Instruction inst4Exist = new Instruction(InstructionProtocol.MSG_JOIN_SHARING_FILE, fileName, xContact.toXML(newUser));

        Iterator it = contactSet.iterator();
        while (it.hasNext())
        {
            // Send a message to the new user, to register other existing users
            Contact cnt = (Contact) it.next();
            Instruction inst4New = new Instruction(InstructionProtocol.MSG_JOIN_SHARING_FILE, fileName, xContact.toXML(cnt));
            clp.sendInstructionMessage(xstream.toXML(inst4New), newUser);

            // Send message to existing users, to register new user join
            clp.sendInstructionMessage(xstream.toXML(inst4Exist), cnt);
        }
    }

    /**
     *
     * @param contact
     * @param fileName 
     * @param propagate Propagate this joining to other users in session
     */
    public void addToSharedSession(Contact contact, String fileName, Boolean propagate)
    {
        // If it should be spreaded, first verify if this file has a session stablished
        Set exists = SessionControl.getSharedUsers(fileName);

        // Chat frame used to send messages
        ChatFrame chatWindow = this.clp.getChat(contact);

        // If a session exists and should be propagated, first send the propagate message,
        // otherwise te new added user will also receive a join message
        if ((exists != null) && exists.size() > 0 && (propagate))
        {
            Logger.getLogger(this).debug("[Debug] File already in session. Communicating other users to Join...");

            // Spread Join message to other users
            sendJoinMessage(exists, fileName, contact);
        }
        else
        {
            Logger.getLogger(this).debug("[Debug] Session is still empty/null. ");
        }

        // Add the user to the session
        SessionControl.addSharedEditing(contact, fileName, chatWindow);
    }

    /**
     *
     */
    public void updateEditingText()
    {
        // arg0 = filename
        // arg1 = insert / delete
        // arg2 = offset
        // arg3 = lenght
        // arg4 = text (if edit)

//        Logger.getLogger(this).debug("[Debug] Updating the text");
//        String fileName = instruction.getArg0();
        String fileName = null;
        String type = instruction.getArg1();
        Integer offset = Integer.parseInt(instruction.getArg2());
        Integer lenght = Integer.parseInt(instruction.getArg3());
        String textChanged = instruction.getArg4();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Create a object to write do database
        InstructionDBModel im = new InstructionDBModel(fileName, type, offset, lenght, null, now, this.msg.getFrom().getNickName());

        Logger.getLogger(this).debug("[Debug] Received update text message. Type: " + type
                + ", offset: " + offset.toString() + ", Text: " + textChanged);

        // Locate file tab
//        List lst = org.netbeans.api.editor.EditorRegistry.componentList();

//        Iterator i = org.netbeans.api.editor.EditorRegistry.componentList().iterator();

//        System.out.print("Iniciando verificação arquivos abertos..." + "\n");

        Iterator i = org.netbeans.api.editor.EditorRegistry.componentList().listIterator();

        JTextComponent ed1, ed = null;
        Document doc1, doc = null;
        String filePath;
        Boolean continueLoop = true;

        while (i.hasNext() && continueLoop)
        {
            ed1 = (JTextComponent) i.next();
            doc1 = ed1.getDocument();

            filePath = (String) doc1.getProperty(Document.TitleProperty);
            fileName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

            if (fileName.indexOf(" ") > 0)
            {
                fileName = fileName.substring(0, fileName.indexOf(" "));
            }

            if (fileName.equals(instruction.getArg0()))
            {
//                System.out.print("[InstructionEvaluator] File is opened in Netbeans: " + fileName + "\n");
                ed = ed1;
                doc = doc1;

                im.setFileName(fileName);
                continueLoop = false;
            }
        }

        // If document wasn't found in Netbeans, do nothing.
        if (doc == null)
        {
            Util.setStatus("[ERROR] IdDE::updateEditingText InstructionEvaluator - File " + instruction.getArg0() + " not opened/found in Netbeans!");
            Util.showErrorMessage("Instruction::updateEditingText - File "+ instruction.getArg0() + "\n wasn't not found or isn 't opened in Netbeans!");

            return;
        }

//        JTextComponent ed = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
//        Document doc = ed.getDocument();

        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setBold(attributes, true);
        StyleConstants.setItalic(attributes, true);

        if (type.equals(InstructionProtocol.INSERT_TEXT))
        {
            final int len = textChanged.length();

            im.setText(textChanged);
            im.setLenght(len);

            // <OT>
            // verify if it was made on the left of on the right (Operational Transformation
            // Convert map from XML, convert back to Map
            String xmlSiteState = instruction.getArg5();

            XStream xMStream = new XStream(new DomDriver());
            xMStream.alias("map", java.util.Map.class);
            Map<String, Integer> siteStateInfo = (Map<String, Integer>) xMStream.fromXML(xmlSiteState);

            String remoteUser = msg.getFrom().getNickName();
            Request r = new Request(remoteUser, DocumentChangesListener.INSERT, offset, textChanged, lenght, siteStateInfo);

//            Set requests = new HashSet();

            Queue <Request> workQueue = new LinkedList();

            OperationalTransformation ot = SessionControl.getFileSessionSiteStates(fileName);
            // Left side must always be executed
            if (ot.verifyIfLeftToOffset(offset))
            {
                Logger.getLogger(this).debug("[Debug] Change was made on the left (OT). APPLY it:  " + offset);
                ot.incrementSiteState(remoteUser);
                ot.addToLog(r.getClone());

                // Add request to set, to be processed
//                requests.add(r);
                workQueue.add(r.getClone());
            }
            // Right side depends on the site states
            else
            {
                // Can it be added to document immediately (less state values)?
                Logger.getLogger(this).debug("[Debug] Verifying the right side condition for: "+r);
                String rightSide = ot.verifyRightSide(r);

                // without transformation
                if ( rightSide.equalsIgnoreCase(OperationalTransformation.RIGHT_NOW) )
                {
                    Logger.getLogger(this).debug("[Debug] Apply right now (no transformation).");
                    ot.incrementSiteState(remoteUser);
                    ot.addToLog(r.getClone());

                    // Add request to be processed and inserted
                    workQueue.add(r.getClone());

                    // If there is a queue, process it making necessary transformations
                    workQueue = ot.processQueue(workQueue);
                }
                else if ( rightSide.equalsIgnoreCase(OperationalTransformation.TRANSFORM) )
                {
                    Logger.getLogger(this).debug("[Debug] Need to transform!");
                    Request r1 = ot.transform(r.getClone());
                    ot.incrementSiteState(remoteUser);
                    ot.addToLog(r1.getClone());

                    // Add request to set, to be processed and inserted later
                    workQueue.add(r1.getClone());

                    // If there is a queue, process it making necessary transformations
                    workQueue = ot.processQueue(workQueue);
                }
                // Otherways, go to queue
                else
                {
                    Logger.getLogger(this).debug("[Debug] Change on the RIGHT (OT) -> ADD TO QUEUE: " + offset);
                    ot.enqueue(r);
                }
            }

            Request req;
//            while (it.hasNext())
            while ( ! workQueue.isEmpty() )
            {
                if ( workQueue.size() > 1 )
                {
                    Logger.getLogger(this).debug("[Debug] Queue size = " + workQueue.size());
                }

//                req = (Request) i.next();
                // Remove the first element from the queue
                req = workQueue.remove();

                try
                {
                    offset = req.getOffset();
                    lenght = req.getLenght();
                    textChanged = req.getText();

                    // Remove the listeners, otherways local IdDE will catch the changes
                    // and send it to parties. Result: a infinite loop  :-)
                    doc.removeDocumentListener(DocumentChangesListener.getDefaultListener());

                    // Add string to doc
                    doc.insertString(offset, textChanged, attributes);

                    // Put the listener back
                    doc.addDocumentListener(DocumentChangesListener.getDefaultListener());
                    StatusDisplayer.getDefault().setStatusText("IdDE: File '" + fileName + "' changed at offset '" + offset.toString() + "', by user '" + this.msg.getFrom().getNickName() + "': " + textChanged);

                    // Mark changes
                    Util.insertEditAnnotation(doc, offset, lenght, false, "'" + textChanged + "' inserted by " + req.getUser() + " @ column ");
                }
                catch (BadLocationException ex)
                {
                    Exceptions.printStackTrace(ex);
                }
            }
//            ed.setCaretPosition(offset+len);
//            ed.select(offset, offset+len);
        }
        else
        {
            if (type.equals(InstructionProtocol.DELETE_TEXT))
            {
                try
                {
                    doc.removeDocumentListener(DocumentChangesListener.getDefaultListener());
                    doc.remove(offset, lenght);
                    doc.addDocumentListener(DocumentChangesListener.getDefaultListener());
                    StatusDisplayer.getDefault().setStatusText("IdDE: File '" + fileName + "' changed at offset '" + offset.toString() + "', " + lenght.toString() + " chars removed by user '" + this.msg.getFrom().getNickName() + "'");

                    // Mark changes
                    Util.insertEditAnnotation(doc, offset, lenght, false, lenght.toString() + " chars removed by " + this.msg.getFrom().getNickName() + " @ column ");
                }
                catch (BadLocationException ex)
                {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        try
        {
            // Insert log record in database
            Database.insertSessionInstruction(im);
        }
        catch (SqlJetException ex)
        {
            Exceptions.printStackTrace(ex);

            JOptionPane.showMessageDialog(null, "Error inserting log in database:\n" + ex.getMessage(), "IdDE - Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     *
     */
    private void addNewTask()
    {
        // convert xml to object
        XStream xTask = new XStream();
        xTask.alias("task", Task.class);

        Task t = (Task) xTask.fromXML(this.instruction.getArg0());

        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = out.format(t.getDateDue());

        String taskStr = "Task Name: " + t.getName() + "\n"
                + "Date Due: " + strDate + "\n"
                + "Project: " + t.getProject() + "\n"
                + "Details: " + t.getDescription() + "\n";

        String taskMessage = "Remote user \"" + msg.getFrom() + "\" wants to add a new task for you:\n\n"
                + taskStr + "\nDo you accept this new task? ";

//        Object[] options = {"Yes, Create Task", "No, thanks"};

        if (JOptionPane.showConfirmDialog(null, taskMessage, "IdDE - New Task", JOptionPane.YES_NO_OPTION) == 0)
        {
            JOptionPane.showMessageDialog(null, "You clicked \"Ok\". Task will be created.",
                    "IdDE", 1);
            try
            {
                Database.insertTask(t);
            }
            catch (SqlJetException ex)
            {
                Util.showErrorMessage(ex.getMessage());
//                Exceptions.printStackTrace(ex);
            }

            // Not accepted. Warn user.
            XStream xstream = new XStream();
            xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

            Instruction i = new Instruction(InstructionProtocol.MSG_ACCEPT_NEW_TASK, this.instruction.getArg0());
            clp.sendInstructionMessage(xstream.toXML(i), msg.getFrom());

            Logger.getLogger(this).debug("[Debug] Sending my answer to remote: task accepted!!!");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "You clicked \"Cancel\". Task hasn't been created.", "IdDE", 1);

            // Not accepted. Warn user.
            XStream xstream = new XStream();
            xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

            Instruction i = new Instruction(InstructionProtocol.MSG_REJECT_NEW_TASK, this.instruction.getArg0());
            clp.sendInstructionMessage(xstream.toXML(i), msg.getFrom());
        }
    }
}
//class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
//{
//  public MyHighlightPainter(Color color)
//  {
//    super(color);
//  }
//}
//
//class UnderlineHighlighter extends DefaultHighlighter
//{
//
//    public UnderlineHighlighter(Color c)
//    {
//
//        painter = (c == null ? sharedPainter : new UnderlineHighlightPainter(c));
//
//    }
//
//    // Convenience method to add a highlight with
//    // the default painter.
//    public Object addHighlight(int p0, int p1) throws BadLocationException
//    {
//
//        return addHighlight(p0, p1, painter);
//
//    }
//
//    public void setDrawsLayeredHighlights(boolean newValue)
//    {
//
//        // Illegal if false - we only support layered highlights
//
//        if (newValue == false)
//        {
//
//            throw new IllegalArgumentException(
//                    "UnderlineHighlighter only draws layered highlights");
//
//        }
//
//        super.setDrawsLayeredHighlights(true);
//
//    }
//
//    // Painter for underlined highlights
//    public static class UnderlineHighlightPainter extends LayeredHighlighter.LayerPainter
//    {
//
//        public UnderlineHighlightPainter(Color c)
//        {
//
//            color = c;
//
//        }
//
//        public void paint(Graphics g, int offs0, int offs1, Shape bounds,
//                JTextComponent c)
//        {
//            // Do nothing: this method will never be called
//        }
//
//        public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
//                JTextComponent c, View view)
//        {
//
//            g.setColor(color == null ? c.getSelectionColor() : color);
//
//
//
//            Rectangle alloc = null;
//
//            if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset())
//            {
//
//                if (bounds instanceof Rectangle)
//                {
//
//                    alloc = (Rectangle) bounds;
//
//                }
//                else
//                {
//
//                    alloc = bounds.getBounds();
//
//                }
//
//            }
//            else
//            {
//
//                try
//                {
//
//                    Shape shape = view.modelToView(offs0,
//                            Position.Bias.Forward, offs1,
//                            Position.Bias.Backward, bounds);
//
//                    alloc = (shape instanceof Rectangle) ? (Rectangle) shape
//                            : shape.getBounds();
//
//                }
//                catch (BadLocationException e)
//                {
//
//                    return null;
//
//                }
//
//            }
//
//
//
//            FontMetrics fm = c.getFontMetrics(c.getFont());
//
//            int baseline = alloc.y + alloc.height - fm.getDescent() + 1;
//
//            g.drawLine(alloc.x, baseline, alloc.x + alloc.width, baseline);
//
//            g.drawLine(alloc.x, baseline + 1, alloc.x + alloc.width,
//                    baseline + 1);
//
//
//
//            return alloc;
//
//        }
//        protected Color color; // The color for the underline
//    }
//    // Shared painter used for default highlighting
//    protected static final Highlighter.HighlightPainter sharedPainter = new UnderlineHighlightPainter(
//            null);
//    // Painter used for this highlighter
//    protected Highlighter.HighlightPainter painter;
//}
//
//class MyHighlightPainters extends DefaultHighlighter.DefaultHighlightPainter
//{
//        public MyHighlightPainters(Color clr)
//{
//            super(clr);
//        }
//    }
