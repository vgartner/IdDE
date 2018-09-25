/**
 * Send file to remote.
 *
 * \b Package: \n
 * org.idde.fileManager.view
 *
 * @see org.idde.fileManager
 * @see org.idde.fileManager.controller
 *
 * @since Class created on 15/03/2011
 *
 * @author Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b Maintainers: \n
 * Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * @version $Id$
 */
package org.idde.fileManager.view;

import java.io.File;
import org.idde.common.model.Contact;
import org.idde.util.Util;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

/**
 *
 * @author  vilson
 */
public class ProgressFrame extends javax.swing.JFrame
{
//    private static final long serialVersionUID = -7266858009982252487L;

    private XMPPConnection connection;
    private Contact contact;
    private String fileName;

    /**
     * Constructor to send a file to remote user
     * @param connection
     * @param contact
     * @param fileName
     */
    public ProgressFrame(XMPPConnection connection, Contact contact, String fileName)
    {
        this.connection = connection;
        this.contact = contact;
        this.fileName = fileName;

        initComponents();
    }

    public void sendFile()
    {
        lblTitle.setText("Sending file to: " + contact.toString());
        startSendingFile();
    }

    public void receiveFile()
    {
        lblTitle.setText("Receiving file: " + contact.toString());
        startReceivingFile();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        pbTransfer = new javax.swing.JProgressBar();
        lblStatus = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();

        setTitle("File transfer progress");

        lblTitle.setText("Transfering file...");

        lblStatus.setText("Please wait...");

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lblStatus))
                            .addComponent(pbTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(lblTitle)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addComponent(pbTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCancelActionPerformed
    {//GEN-HEADEREND:event_btnCancelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JProgressBar pbTransfer;
    // End of variables declaration//GEN-END:variables

    private void startSendingFile()
    {
        // Create the file transfer manager
        FileTransferManager manager = new FileTransferManager(connection);
        final String remote = contact.getName();

        // Create the outgoing file transfer

        OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer("vgartner@gmail.com");
        try
        {
            // Send the file
            final File file = new File(fileName);
            transfer.sendFile(file, "IdDE: Sending file...");
        }
        catch (XMPPException ex)
        {
            Util.showErrorMessage("Error sending file: " + ex.getMessage());
//            Exceptions.printStackTrace(ex);
        }

    }

    private void startReceivingFile()
    {
        // Create the file transfer manager
        final FileTransferManager manager = new FileTransferManager(connection);

        // Create the listener
        manager.addFileTransferListener(new FileTransferListener()
        {

            @Override
            public void fileTransferRequest(FileTransferRequest request)
            {
                // Check to see if the request should be accepted
//                  if(shouldAccept(request)) {
                // Accept it
                IncomingFileTransfer transfer = request.accept();
                try
                {
                    transfer.recieveFile(new File("/temp/"+fileName));
                    while (! transfer.isDone())
                    {
                        try
                        {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException ex)
                        {
                            Util.showErrorMessage(ex.getMessage());
                        }

                        lblStatus.setText(transfer.getStatus() + " - " + transfer.getProgress());
                    }
                }
                catch (XMPPException ex)
                {
                    Util.showErrorMessage("Error receiving file: " + ex.getMessage());
//                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }
}
