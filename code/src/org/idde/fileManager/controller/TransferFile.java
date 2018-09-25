/**
 * Description
 *
 * \b Package: \n
 * fileManager.controller
 *
 * @see ???.???.???
 * @see http://www.igniterealtime.org/builds/smack/docs/latest/documentation/extensions/filetransfer.html
 *
 * @since Class created on 01/02/2011
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
package org.idde.fileManager.controller;

//import org.jivesoftware.smackx.f
import java.io.File;
import javax.swing.JFileChooser;
import org.idde.common.model.Contact;
import org.idde.common.view.ContactListPanel;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.netbeans.editor.view.spi.FlyView.Parent;

/**
 *
 * @author vilson
 */
public class TransferFile
{

    private XMPPConnection connection;
    private FileTransferManager manager;
    private String fileName;
    private Contact contact;

    public void setConnection(XMPPConnection conn)
    {
        connection = conn;
    }

    public void fileTransfer(XMPPConnection connection, String fileName, Contact contact)
    {
        this.connection = connection;
        this.fileName = fileName;
        this.contact = contact;
    }

    public static File openFile()
    {
        JFileChooser jfc = new JFileChooser();
        File rs = null;

        int result = jfc.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            System.out.println(jfc.getSelectedFile());
            rs = jfc.getSelectedFile();
        }
        else
        {
            System.out.println("Open file cancelled, or error!");
        }
        
        return rs;
    }

    public void sendFile() throws XMPPException
    {
        // Create the file transfer manager
//        final FileTransferManager manager = new FileTransferManager(connection);
        manager = new FileTransferManager(connection);

        // Create the outgoing file transfer
        OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(contact.getName());

        // Send the file
        transfer.sendFile(new File("shakespeare_complete_works.txt"), "You won't believe this!");
    }

    public void receiveFile(String file)
    {
        // Create the file transfer manager
        manager = new FileTransferManager(connection);

        // Create the listener
        manager.addFileTransferListener(new FileTransferListener()
        {

            @Override
            public void fileTransferRequest(FileTransferRequest request)
            {
                // Check to see if the request should be accepted
//                if (shouldAccept(request))
//                {
//                    // Accept it
//                    IncomingFileTransfer transfer = request.accept();
//                    transfer.recieveFile(new File("shakespeare_complete_works.txt"));
//                }
//                else
//                {
//                    // Reject it
//                    request.reject();
//                }
            }
        });
    }

    public void monitorTransfer(TransferFile transfer)
    {
//        while (!transfer.isDone())
        {
//            if (transfer.getStatus().equals(Status.ERROR))
//            {
//                System.out.println("ERROR!!! " + transfer.getError());
//            }
//            else
//            {
//                System.out.println(transfer.getStatus());
//                System.out.println(transfer.getProgress());
//            }
//            sleep(1000);
        }
    }

    @Override
    public String toString()
    {
        return fileName;
    }


}
