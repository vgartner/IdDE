/**
 * Appointment frame.
 *
 * \b Package: \n
 * org.idde.agenda.view
 *
 * @see org.idde.agenda
 * @see org.idde.agenda.view
 *
 * @since Class created on 27/10/2010
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
package org.idde.agenda.view;

import com.thoughtworks.xstream.XStream;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.idde.agenda.model.Appointment;
import org.idde.common.model.Contact;
import org.idde.common.model.XMPPManager;
import org.idde.common.transport.Instruction;
import org.idde.common.model.InstructionProtocol;
import org.idde.util.Database;
import org.idde.util.DateUtils;
import org.idde.util.Util;
import org.jivesoftware.smack.XMPPConnection;
import org.openide.util.Exceptions;
import org.tmatesoft.sqljet.core.SqlJetException;

/**
 *
 * @author  vilson
 */
public class AppointmentFrame extends javax.swing.JFrame
{
    static String EDIT   = "edit";
    static String INSERT = "insert";
    static String REMOTE = "add_remote";

    private Contact contact = null;
    private static final long serialVersionUID = -7266858009982252487L;
    private XMPPConnection connection;
    private String function = REMOTE;
    private Integer id;
    private AppointmentManagerFrame amf;
    private String userAdded;

    public AppointmentFrame(AppointmentManagerFrame amf)
    {
        initComponents();

        // Set function, to do the correct operation on post
        function = INSERT;
        this.amf = amf;
        this.userAdded = "Myself";

        lblTitle.setText("Insert New Appointment");
        lblRemoteUser.setText(null);
        btnPost.setText("Save New Appointment");
    }

    public AppointmentFrame(Contact selectedContact)
    {
        contact = selectedContact;
        initComponents();
        lblRemoteUser.setText(contact.getNickName());
        this.userAdded = contact.getNickName();
        lblRemoteUser.setToolTipText(contact.getName() +" - "+contact.getStatus());
        function = REMOTE;
    }

    /**
     * Constructor used to edit data
     * @param what
     * @param where
     * @param from
     * @param to
     * @param descr
     * @param author
     * @param id
     * @param amf
     */
    public AppointmentFrame(String what, String where, String from, String to, String descr, String author, Integer id, AppointmentManagerFrame amf)
    {
        initComponents();

        // Set function, to do the correct operation on post
        function = EDIT;
        this.id  = id;
        this.amf = amf;
        this.userAdded = author;
        
        lblTitle.setText("Edit Appointment");
        lblRemoteUser.setText(null);
        btnPost.setText("Save Changes");

        java.util.Date begin;
        java.util.Date end;

        String dateFrom = null;
        String timeFrom = null;
        String dateTo = null;
        String timeTo = null;

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS");
            begin = sdf.parse(from);
            end   = sdf.parse(to);

//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd");
//            sdf1.format(begin);

            dateFrom = DateUtils.dateToString(begin, DateUtils.FORMAT_DDMMYYYY_SLASHES);
            timeFrom = DateUtils.getTimeFromDate(begin);

            dateTo = DateUtils.dateToString(end, DateUtils.FORMAT_DDMMYYYY_SLASHES);
            timeTo = DateUtils.getTimeFromDate(end);
        }
        catch (ParseException ex)
        {
            Exceptions.printStackTrace(ex);
        }

  //      date.getTime();

        tfWhat.setText(what);
        tfWhere.setText(where);
        tfWhenFromDate.setText(dateFrom);
        tfWhenFromTime.setText(timeFrom);
        tfWhenToDate.setText(dateTo);
        tfWhenToTime.setText(timeTo);
        taDescription.setText(descr);
//        contact.setNickName();

    }

    /**
     * Create a appointment object based on form inputs
     * @return Appointment
     */
    private Appointment createAppointmentObject()
    {
        Appointment a = new Appointment();
        a.setWhat(tfWhat.getText());
        a.setWhere(tfWhere.getText());
        a.setDescription(taDescription.getText());
        a.setUserAdded(this.userAdded);

        try
        {
            a.setDateFrom(Util.formatDateTime_ddMMyyyyHHmm(tfWhenFromDate.getText(), tfWhenFromTime.getText()));
        }
        catch (Exception ex)
        {
            Util.showErrorMessage(ex.getMessage());
            a = null;
//            Exceptions.printStackTrace(ex);
        }
        try
        {
            a.setDateTo(Util.formatDateTime_ddMMyyyyHHmm(tfWhenToDate.getText(), tfWhenToTime.getText()));
        }
        catch (Exception ex)
        {
            Util.showErrorMessage(ex.getMessage());
            a = null;
//            Exceptions.printStackTrace(ex);
        }

        return a;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfWhat = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblRemoteUser = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        tfWhenFromDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        tfWhenFromTime = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        tfWhenToDate = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        tfWhenToTime = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        tfWhere = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnPost = new javax.swing.JButton();

        setTitle("IdDE - Agenda");

        jLabel4.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.jLabel4.text")); // NOI18N

        jLabel6.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.jLabel6.text")); // NOI18N

        tfWhat.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.tfTaskName.text")); // NOI18N
        tfWhat.setToolTipText("What is the appointment about?");

        taDescription.setColumns(20);
        taDescription.setRows(5);
        taDescription.setToolTipText("Enter the full description of the task");
        jScrollPane1.setViewportView(taDescription);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.jLabel3.text")); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lblTitle.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTitle.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.jLabel1.text")); // NOI18N

        lblRemoteUser.setText("User Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 301, Short.MAX_VALUE)
                .addComponent(lblRemoteUser)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(lblRemoteUser))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("When"));

        tfWhenFromDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        tfWhenFromDate.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.tfDueDate.text")); // NOI18N
        tfWhenFromDate.setToolTipText("Format: dd/mm/yyyy");

        jLabel2.setText("Start Time");

        tfWhenFromTime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        tfWhenFromTime.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.tfDueDate.text")); // NOI18N
        tfWhenFromTime.setToolTipText("Format: dd/mm/yyyy");

        jLabel9.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.jLabel5.text")); // NOI18N

        tfWhenToDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        tfWhenToDate.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.tfDueDate.text")); // NOI18N
        tfWhenToDate.setToolTipText("Format: dd/mm/yyyy");

        jLabel10.setText("End Time");

        tfWhenToTime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        tfWhenToTime.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.tfDueDate.text")); // NOI18N
        tfWhenToTime.setToolTipText("Format: dd/mm/yyyy");

        jLabel5.setText("Start Date");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(tfWhenToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfWhenToTime, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(tfWhenFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfWhenFromTime, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfWhenFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tfWhenFromTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfWhenToTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addComponent(tfWhenToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tfWhere.setText(org.openide.util.NbBundle.getMessage(AppointmentFrame.class, "TaskFrame.tfTaskName.text")); // NOI18N
        tfWhere.setToolTipText("Task name");

        jLabel7.setText("What");

        btnPost.setText("Send Appointment do User");
        btnPost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPostActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(tfWhere, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tfWhat, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGap(188, 188, 188)))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(174, 174, 174)
                .addComponent(btnPost)
                .addContainerGap(205, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfWhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfWhere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPost)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPostActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPostActionPerformed
    {//GEN-HEADEREND:event_btnPostActionPerformed
        if ( function.equalsIgnoreCase(EDIT))
        {
            updateAppoinment();
            return;
        }
        else if ( function.equalsIgnoreCase(INSERT))
        {
            insertAppoinment();
            return;
        }

        sendAppointmentToRemote();
        Util.showInformationMessage("Appointment was sent to remote user. \nYou will be notified if it was accepted or rejected.");
        this.setVisible(false);
    }//GEN-LAST:event_btnPostActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPost;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblRemoteUser;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField tfWhat;
    private javax.swing.JFormattedTextField tfWhenFromDate;
    private javax.swing.JFormattedTextField tfWhenFromTime;
    private javax.swing.JFormattedTextField tfWhenToDate;
    private javax.swing.JFormattedTextField tfWhenToTime;
    private javax.swing.JTextField tfWhere;
    // End of variables declaration//GEN-END:variables

    private void sendAppointmentToRemote()
    {
        Appointment a = createAppointmentObject();

        // Used to convert object to xml string
        XStream xstream = new XStream();
        xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);
        xstream.alias("appointment", Appointment.class);

        Instruction i = new Instruction(InstructionProtocol.MSG_ADD_AGENDA, xstream.toXML(a));

        String mesg =  xstream.toXML(i);

        // Verify if message should be encoded
        if (XMPPManager.getDoEncode())
        {
            try
            {
                mesg = Util.encodeBase64(mesg, XMPPManager.getEncodeCharSet());
            }
            catch (UnsupportedEncodingException ex)
            {
                Exceptions.printStackTrace(ex);
            }
        }

        connection = XMPPManager.getConnection();

        org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
        message.setType(org.jivesoftware.smack.packet.Message.Type.chat);
        message.setFrom( connection.getUser());
//        message.addBody("en", xstream.toXML(i) );
//        message.setBody(xstream.toXML(i));
        message.setBody(mesg);
        message.setTo(this.contact.getName());

        connection.sendPacket(message);
    }

    void setFunction(String func)
    {
        function = func;
    }

    private void updateAppoinment()
    {
        Appointment a = createAppointmentObject();

        if ( a == null )
        {
            return;
        }

        a.setId(id);

        try
        {
            Database.updateAppointment(a);
            Util.showInformationMessage("Appointment was updated.");
            amf.updateTableData();
        }
        catch (SqlJetException ex)
        {
            Util.showErrorMessage("Error updating record ("+ a.getId().toString() +"): " + ex.getMessage());
        }
    }

    private void insertAppoinment()
    {
        Appointment a = createAppointmentObject();

        if ( a == null )
        {
            return;
        }
        try
        {
            Database.insertAppointment(a);
            Util.showInformationMessage("Appointment created.");
            amf.updateTableData();
        }
        catch (SqlJetException ex)
        {
            Util.showErrorMessage(ex.getMessage());
        }
    }


}
