/**
 * Chat frame.
 *
 * \b Package: \n
 * org.idde.chat.view
 *
 * @see org.idde.chat
 * @see org.idde.chat.view
 *
 * @since Class created on 04/10/2010
 *
 * @author Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b Maintainers: \n
 * Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b License: \n
 * Licensed under BSD License {@link http://www.opensource.org/licenses/bsd-license.php}
 * Many ideas and code are based on shortalk {@link http://code.google.com/p/shortalk/}
 *
 * @version $Id$
 */
package org.idde.chat.view;

import com.thoughtworks.xstream.XStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.io.UnsupportedEncodingException;
import javax.swing.text.BadLocationException;
import org.idde.chat.controller.DefaultController;
import org.idde.common.model.Contact;
import org.idde.chat.model.ChatMessage;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.idde.common.model.XMPPManager;
import org.idde.common.transport.Instruction;
import org.idde.common.model.InstructionProtocol;
import org.idde.util.ResourceManager;
import org.idde.util.Util;
import org.openide.util.Exceptions;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author  remy
 */
public class ChatFrame extends javax.swing.JFrame
{

    /**
     *
     */
    private static final long serialVersionUID = -7266858009982252487L;
    static private SimpleAttributeSet BLACK = new SimpleAttributeSet();
    static private SimpleAttributeSet BLUE_BOLD = new SimpleAttributeSet();
    static private SimpleAttributeSet RED_BOLD = new SimpleAttributeSet();

    static
    {
        StyleConstants.setForeground(BLACK, Color.black);
        StyleConstants.setForeground(BLUE_BOLD, Color.blue);
        StyleConstants.setBold(BLUE_BOLD, true);
        StyleConstants.setForeground(RED_BOLD, Color.red);
        StyleConstants.setBold(RED_BOLD, true);
    }

    /** Creates new form ChatFrame
     * @param participant
     * @param controller
     */
    public ChatFrame(Contact participant, DefaultController controller)
    {
        this.participant = participant;
        this.controller = controller;
        initComponents();
        sendTextArea.requestFocus();
        pnlTranslate.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sendButton = new javax.swing.JButton();
        sendScrollPane = new javax.swing.JScrollPane();
        sendTextArea = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatPane = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        btnTranslate = new javax.swing.JToggleButton();
        pnlTranslate = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbTrFrom = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        cbTrTo = new javax.swing.JComboBox();
        btnDoTranslate = new javax.swing.JButton();
        chkTranslateRec = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setTitle(this.participant.getNickName());

        sendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/chat/icons/sendmessage.png"))); // NOI18N
        sendButton.setText("Send Message");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        sendScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        sendTextArea.setColumns(20);
        sendTextArea.setLineWrap(true);
        sendTextArea.setRows(3);
        sendTextArea.setTabSize(4);
        sendTextArea.setWrapStyleWord(true);
        sendTextArea.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        sendTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sendTextAreaKeyTyped(evt);
            }
        });
        sendScrollPane.setViewportView(sendTextArea);

        chatPane.setEditable(false);
        chatPane.setFocusable(false);
        jScrollPane1.setViewportView(chatPane);

        jLabel1.setText("Your Message:");

        btnTranslate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/chat/icons/translate.png"))); // NOI18N
        btnTranslate.setText("Translate");
        btnTranslate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTranslateActionPerformed(evt);
            }
        });

        pnlTranslate.setBorder(javax.swing.BorderFactory.createTitledBorder("Translate Messages:"));

        jLabel2.setText("from");

        cbTrFrom.setModel( new DefaultComboBoxModel(Language.values()));
        cbTrFrom.setToolTipText("Select your language");

        jLabel3.setText("to");

        cbTrTo.setModel(new DefaultComboBoxModel(Language.values()));
        cbTrTo.setToolTipText("Select remote's language");

        btnDoTranslate.setText("Go");
        btnDoTranslate.setToolTipText("Translate your message");
        btnDoTranslate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoTranslateActionPerformed(evt);
            }
        });

        chkTranslateRec.setSelected(true);
        chkTranslateRec.setText("Translate incoming messages (to -> from)");

        javax.swing.GroupLayout pnlTranslateLayout = new javax.swing.GroupLayout(pnlTranslate);
        pnlTranslate.setLayout(pnlTranslateLayout);
        pnlTranslateLayout.setHorizontalGroup(
            pnlTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTranslateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTranslateLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbTrFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cbTrTo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addComponent(btnDoTranslate))
                    .addComponent(chkTranslateRec))
                .addContainerGap())
        );
        pnlTranslateLayout.setVerticalGroup(
            pnlTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTranslateLayout.createSequentialGroup()
                .addGroup(pnlTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbTrFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cbTrTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDoTranslate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTranslateRec)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("History:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnTranslate)
                        .addGap(111, 111, 111)
                        .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                    .addComponent(sendScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addComponent(pnlTranslate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTranslate)
                    .addComponent(sendButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTranslate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public DefaultController getController()
    {
        return this.controller;
    }

    private void sendTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sendTextAreaKeyTyped
        if (evt.getKeyChar() == '\n')
        {
            sendMessage();
        }
    }//GEN-LAST:event_sendTextAreaKeyTyped

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        sendTextArea.append("\n");
        sendMessage();
    }//GEN-LAST:event_sendButtonActionPerformed

    private void btnTranslateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnTranslateActionPerformed
    {//GEN-HEADEREND:event_btnTranslateActionPerformed
        pnlTranslate.setVisible(btnTranslate.isSelected());
    }//GEN-LAST:event_btnTranslateActionPerformed

    private void btnDoTranslateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDoTranslateActionPerformed
    {//GEN-HEADEREND:event_btnDoTranslateActionPerformed
        String originalText = sendTextArea.getText();
        String translatedText;

        if (originalText.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "No message was entered!",
                    "IdDE - Translate", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else
        {
            if (cbTrFrom.getSelectedItem().toString().equals(""))
            {
                JOptionPane.showMessageDialog(null, "You need to select the source language!",
                        "IdDE - Translate", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else
            {
                if (cbTrTo.getSelectedItem().toString().equals(""))
                {
                    JOptionPane.showMessageDialog(null, "You need to select the destination language!",
                            "IdDE - Translate", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        btnDoTranslate.setText("Wait");

        try
        {
            Translate.setHttpReferrer("http://www.vgdata.net");
            translatedText = Translate.execute(originalText, (Language) cbTrFrom.getSelectedItem(), (Language) cbTrTo.getSelectedItem());
            sendTextArea.setText(translatedText);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, "Error translating message: \n" + ex.getMessage(),
                    "IdDE - Translate", JOptionPane.ERROR_MESSAGE);
            Exceptions.printStackTrace(ex);
        }

        btnDoTranslate.setText("Go");

    }//GEN-LAST:event_btnDoTranslateActionPerformed

    /**
     * Send message for remote contact of this chat.
     * 
     * @param message Message to send
     */
    public void doSendInstruction(String msg)
    {
        sendInstructionMessage(msg);


        /*
        ChatMessage msg = new ChatMessage();
        msg.setTo(this.participant);
        msg.setMessage(message);
        controller.sendMessage(msg);

        try
        {
        chatPane.getDocument().insertString(chatPane.getDocument().getLength(), "Code string sent:\n"+message+"\n", RED_BOLD);
        }
        catch (BadLocationException ex)
        {
        Exceptions.printStackTrace(ex);
        }
         */
    }

    /**
     * Send a text message to remote user. This method should be called by other classes.
     * @param message Message to be sent
     */
    public void sendTextMessage(String message)
    {
        ChatMessage msg = new ChatMessage();
        msg.setTo(this.participant);
        msg.setMessage(sendTextArea.getText());
        controller.sendMessage(msg);
    }

    private void sendMessage()
    {
        if (!sendTextArea.getText().matches("^[\n\t ]*$"))
        {
            ChatMessage msg = new ChatMessage();
            msg.setTo(this.participant);
            msg.setMessage(sendTextArea.getText());
            controller.sendMessage(msg);
            sendTextArea.setText("");

            try
            {
                String date = Util.getCurrentDate();
                String time = Util.getCurrentTime();
                chatPane.getDocument().insertString(chatPane.getDocument().getLength(), time, BLACK);
                chatPane.getDocument().insertString(chatPane.getDocument().getLength(), " me ", BLUE_BOLD);
                chatPane.getDocument().insertString(chatPane.getDocument().getLength(), msg.getMessage(), BLACK);
                insertNewLine();
                chatPane.setCaretPosition(chatPane.getDocument().getLength());

                org.idde.util.Logger.logConversation(participant, date + " " +time + " - me: " + msg.getMessage());
            }
            catch (BadLocationException ex)
            {
                Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            sendTextArea.setText("");
        }
    }

    /**
     * Process and show received message
     * @param msg Message which was received
     */
    private void processReceivedMessage(ChatMessage msg)
    {
        try
        {
            String date = Util.getCurrentDate();
            String time = Util.getCurrentTime();

            chatPane.getDocument().insertString(chatPane.getDocument().getLength(), time, BLACK);
            chatPane.getDocument().insertString(chatPane.getDocument().getLength(), " " + msg.getFrom().getNickName(), RED_BOLD);
            chatPane.getDocument().insertString(chatPane.getDocument().getLength(), " " + msg.getMessage(), BLACK);
            insertNewLine();

            org.idde.util.Logger.logConversation(msg.getFrom(), date + " " +time + " - " + msg.getFrom().getNickName() +": " + msg.getMessage());

            // if translation is enabled, translate the message
            if (btnTranslate.isSelected()
                    && (!cbTrFrom.getSelectedItem().toString().equals(""))
                    && (!cbTrTo.getSelectedItem().toString().equals(""))
                    && (chkTranslateRec.isSelected()))
            {
                chatPane.getDocument().insertString(chatPane.getDocument().getLength(), " Translated: ", RED_BOLD);

                Translate.setHttpReferrer("http://www.vgdata.net");
                String translatedText;

                try
                {
                    translatedText = Translate.execute(msg.getMessage(), (Language) cbTrTo.getSelectedItem(), (Language) cbTrFrom.getSelectedItem());
                    chatPane.getDocument().insertString(chatPane.getDocument().getLength(), " " + translatedText, BLACK);
                    insertNewLine();

                    org.idde.util.Logger.logConversation(msg.getFrom(), "Translated: "+translatedText);
                }
                catch (Exception ex)
                {
                    Exceptions.printStackTrace(ex);
                }
            }

            chatPane.setCaretPosition(chatPane.getDocument().getLength());
        }
        catch (BadLocationException ex)
        {
            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param msg
     */
    public void receivedMessage(ChatMessage msg)
    {
        processReceivedMessage(msg);
    }

    private void insertNewLine()
    {
        if (!chatPane.getText().endsWith("\n"))
        {
            try
            {
                chatPane.getDocument().insertString(chatPane.getDocument().getLength(), "\n", BLACK);
            }
            catch (BadLocationException ex)
            {
                Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDoTranslate;
    private javax.swing.JToggleButton btnTranslate;
    private javax.swing.JComboBox cbTrFrom;
    private javax.swing.JComboBox cbTrTo;
    private javax.swing.JTextPane chatPane;
    private javax.swing.JCheckBox chkTranslateRec;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlTranslate;
    private javax.swing.JButton sendButton;
    private javax.swing.JScrollPane sendScrollPane;
    private javax.swing.JTextArea sendTextArea;
    // End of variables declaration//GEN-END:variables
    //Other declarations
    private Contact participant;
    private DefaultController controller;
    //End

    public final Contact getParticipant()
    {
        return participant;
    }

    private void sendInstructionMessage(String msg)
    {
        String message = msg;

        // Verify if message should be encoded
        if (XMPPManager.getDoEncode())
        {
            try
            {
                message = Util.encodeBase64(msg, XMPPManager.getEncodeCharSet());
            }
            catch (UnsupportedEncodingException ex)
            {
                Exceptions.printStackTrace(ex);
                message = msg;
            }
        }

        ChatMessage cmsg = new ChatMessage();
        cmsg.setTo(this.participant);
        cmsg.setMessage(message);
        controller.sendMessage(cmsg);
    }

}
