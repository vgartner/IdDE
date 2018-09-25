/**
 * Thread to evaluate document change.
 *
 * \b Package: \n
 * moduleIdDE
 *
 * @see org.idde.
 * @see org.idde.chat.view
 *
 * @since Class created on 20/10/2010
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
import java.io.File;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.idde.common.transport.Instruction;
import org.idde.common.model.InstructionProtocol;
import org.idde.editor.controller.SessionControl;
import org.openide.util.Exceptions;

/**
 * Thread to evaluate document change.
 */
public class DocumentChangeThread implements Runnable
{

    /**
     * Used to indicate that a text was inserted
     */
    public static final String INSERT = "insert";

    /**
     * Used to indicate that a text was deleted
     */
    public static final String DELETE = "delete";

    private DocumentEvent docEvent;
    private String changeType;

    DocumentChangeThread(DocumentEvent e, String ct)
    {
        this.docEvent = e;
        this.changeType = ct;
    }

    @Override
    public void run()
    {
        // Used to convert object to xml string
        XStream xstream = new XStream();
        xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

        Document doc = docEvent.getDocument();
        String newContent;

        System.out.println("Pegando nome do documento...");
        String filePath = (String) doc.getProperty(Document.TitleProperty);
        String fileName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

//        if (fileName.indexOf(" ") > 0)
//        {
//            System.out.println("Parece ser um arquivo de memoria: " + fileName);
//            fileName = fileName.substring(0, fileName.indexOf(" "));
//        }

        System.out.println("O nome é: " + fileName);
        Instruction i = null;

        if (this.changeType.equals(INSERT)) // String was inserted
        {
            try
            {
                newContent = doc.getText(docEvent.getOffset(), docEvent.getLength());

                Segment text = new Segment();
                doc.getText(docEvent.getOffset(), docEvent.getLength(), text);

                System.out.println("text: " + text);

                //newContent = newContent.toLowerCase();
                System.out.println("Foi Inserido '" + newContent + "' no offset: " + docEvent.getOffset());

                // Compose instruction message

                // arg0 = filename
                // arg1 = insert 2i
                // arg2 = offset
                // arg3 = lenght
                // arg4 = text

                i = new Instruction(InstructionProtocol.MSG_UPDATE_SHARED_TEXT, fileName,
                        InstructionProtocol.INSERT_TEXT, Integer.toString(docEvent.getOffset()),
                        Integer.toString(docEvent.getLength()), newContent);

//                String cm = "Inserir " + newContent +" offset " +docEvent.getOffset();
//                SessionControl.sendChangesToParties(fileName, cm);
            }
            catch (BadLocationException ex)
            {
                Exceptions.printStackTrace(ex);
            }
        }
        else if (this.changeType.equals(DELETE))
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

//            String cm = "Remover "+ docEvent.getLength() +" caracteres no offset: " + docEvent.getOffset();
//            SessionControl.sendChangesToParties(fileName, cm);
        }
        else
        {
            System.out.println("*** Outro evento não tratado ocorreu no documento" + this.changeType );
        }
        
        // Add message to buffer. SessionControl takes care to send it tor other parties
        SessionControl.addMessageToBuffer(fileName, xstream.toXML(i));
    }

}
