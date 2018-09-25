/**
 * Class used to compose instructions. Acts similar to InstructionEvaluator, but is the oposite.
 *
 * \b Package: \n
 * org.idde.common.transport
 *
 * @see InstructionEvaluator
 *
 * @since Class created on 09/03/2011
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

import com.thoughtworks.xstream.XStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.idde.agenda.model.Appointment;
import org.idde.chat.model.ChatMessage;
import org.idde.common.model.Contact;
import org.idde.common.model.InstructionProtocol;
import org.idde.common.view.ContactListPanel;
import org.idde.task.model.Task;
import org.idde.util.Database;
import org.idde.util.Logger;
import org.idde.util.Util;
import org.tmatesoft.sqljet.core.SqlJetException;

/**
 *
 * @author vilson
 */
public class InstructionComposer
{

    static void sendTasksToRemote(ChatMessage msg, ContactListPanel clp)
    {
        String newLine = Util.newLine;
        String tasks = "*** Here are my Tasks: ***" + newLine;
        Set changes = null;

        try
        {
            changes = Database.getTasks();
        }
        catch (SqlJetException ex)
        {
            Util.showErrorMessage(ex.getMessage());
//            Exceptions.printStackTrace(ex);
        }

        Object[] objArray = null;

        if (changes != null)
        {
            objArray = changes.toArray();
        }

        for (int index = 0; index < objArray.length; index++)
        {
            System.out.println(objArray[index]);

            String name = ((Task) (objArray[index])).getName();
            String priority = ((Task) (objArray[index])).getPriority();
            String date = ((Task) (objArray[index])).getDateDue().toString();
            String project = ((Task) (objArray[index])).getProject();
            String descr = ((Task) (objArray[index])).getDescription();
            String author = ((Task) (objArray[index])).getUserSent();

            tasks += "Task Name " + name + newLine
                    + "Project: " + project + newLine
                    + "Date Due: " + date + newLine
                    + "Priority: " + priority + newLine
                    + "Description: " + descr + newLine
                    + "Created by: " + author + newLine
                    + "-----------------------------------------------------------------------" + newLine;
        }

        clp.sendTextMessage(tasks, msg.getFrom());
    }

    /**
     * Send appointments of local user to a remote user.
     * @param msg ChatMessage which will be used to send the message
     * @param clp
     */
    static void sendAppointmentsToRemote(ChatMessage msg, ContactListPanel clp)
    {
        String newLine = Util.newLine;
        String appointments = "*** Here are my Appointments: ***" + Util.newLine;
        Set changes = null;

        try
        {
            changes = Database.getAppointments();
        }
        catch (SqlJetException ex)
        {
            Util.showErrorMessage(ex.getMessage());
        }

        Object[] objArray = null;

        if (changes != null)
        {
            objArray = changes.toArray();
        }

        for (int index = 0; index < objArray.length; index++)
        {
            System.out.println(objArray[index]);

            String what = ((Appointment) (objArray[index])).getWhat();
            String where = ((Appointment) (objArray[index])).getWhere();
            String from = ((Appointment) (objArray[index])).getDateFrom().toString();
            String to = ((Appointment) (objArray[index])).getDateTo().toString();
            String descr = ((Appointment) (objArray[index])).getDescription();
            String author = ((Appointment) (objArray[index])).getUserAdded();

            appointments += "What: " + what + newLine
                    + "Where: " + where + newLine
                    + "Begin: " + from + newLine
                    + "End: " + to + newLine
                    + "Description: " + descr + newLine
                    + "Created by: " + author + newLine
                    + "-----------------------------------------------------------------------" + newLine;
        }

        clp.sendTextMessage(appointments, msg.getFrom());
    }

    static void sendMobileToRemote(ContactListPanel clp, Contact user)
    {
        PropertiesConfiguration conf;
        String mobile = "";

        try
        {
            conf = new PropertiesConfiguration(Util.configFile);
            mobile = conf.getString("mobile");
        }
        catch (ConfigurationException ex)
        {
//                java.util.logging.Logger.getLogger(InstructionEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger("InstructionComposer").error("[Error] Error reading Mobile from properties file: " + ex.getMessage());
        }

        Logger.getLogger("InstructionComposer").debug("[Debug] Sending my Mobile to remote: " + mobile);
        clp.sendTextMessage("My Mobile: " + mobile, user);
    }

    static void sendSIPToRemote(ContactListPanel clp, Contact user)
    {
        PropertiesConfiguration conf;
        String sip = "";

        try
        {
            conf = new PropertiesConfiguration(Util.configFile);
            sip = conf.getString("login_sip");
        }
        catch (ConfigurationException ex)
        {
            //                java.util.logging.Logger.getLogger(InstructionEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger("InstructionComposer").error("[Error] Error reading SIP from properties file: " + ex.getMessage());
        }

        Logger.getLogger("InstructionComposer").debug("[Debug] Sending my SIP to remote: " + sip);

        clp.sendTextMessage("My SIP: " + sip, user);
    }

    static void sendLandLineToRemote(ContactListPanel clp, Contact user)
    {
        PropertiesConfiguration conf;
        String landline = "";

        try
        {
            conf = new PropertiesConfiguration(Util.configFile);
            landline = conf.getString("landline");
        }
        catch (ConfigurationException ex)
        {
            Logger.getLogger("InstructionComposer").error("[Error] Error reading Landline phone from properties file: " + ex.getMessage());
        }

        Logger.getLogger("InstructionComposer").debug("[Debug] Sending my Landline to remote: " + landline);

        clp.sendTextMessage("My LandLine: " + landline, user);
    }

    static void sendAppVersionToRemote(ContactListPanel clp, Contact user)
    {
        Logger.getLogger("InstructionComposer").debug("[Debug] Sending my app name and author.");

        String version = moduleIdDE.iddeTopComponent.getVersionInfo("moduleIdDE");
        String rs = version+ "Author: Vilson C. Gärtner <vgartner@gmail.com>\n";
        
        clp.sendTextMessage("I'm running on: \n" + rs , user);
    }

    static void newAgenda(ContactListPanel clp, Contact from, Instruction instruction)
    {
        // convert xml to object
        XStream xAppointm = new XStream();
        xAppointm.alias("appointment", Appointment.class);

        String instr = instruction.getArg0();
        Appointment app = (Appointment) xAppointm.fromXML(instr);

        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String strDateFrom   = out.format( app.getDateFrom() );
        String strDateTo     = out.format( app.getDateTo() );

        String taskStr = "What: " + app.getWhat() + "\n" +
                         "Where: " + app.getWhere() + "\n" +
                         "Begin: " + strDateFrom + "\n" +
                         "End: " + strDateTo + "\n" +
                         "Description: " + app.getDescription() + "\n" ;

        String fromName = from.toString();
        String taskMessage = "Remote user \"" + fromName + "\" wants to add a new appointment for you:\n\n"+
                             taskStr + "\nDo you accept this new appointment? ";

        if (JOptionPane.showConfirmDialog(null, taskMessage, "IdDE - New Appointment", JOptionPane.YES_NO_OPTION) == 0)
        {
            JOptionPane.showMessageDialog(null, "You clicked \"Ok\". Appointment will be created.",
                    "IdDE", 1);
            try
            {
                Database.insertAppointment(app);
            }
            catch (SqlJetException ex)
            {
                Util.showErrorMessage(ex.getMessage());
//                Exceptions.printStackTrace(ex);
            }

            // Not accepted. Warn user.
            XStream xstream = new XStream();
            xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

            Instruction i = new Instruction(InstructionProtocol.MSG_ACCEPT_NEW_AGENDA, instruction.getArg0() );
            clp.sendInstructionMessage(xstream.toXML(i), from);

            Logger.getLogger("InstructionComposer").debug("[Debug] Sending my answer to remote: appointment accepted!!!");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "You clicked \"Cancel\". Appointment hasn't been created.", "IdDE", 1);

            // Not accepted. Warn user.
            XStream xstream = new XStream();
            xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);

            Instruction i = new Instruction(InstructionProtocol.MSG_REJECT_NEW_AGENDA, instruction.getArg0() );
            clp.sendInstructionMessage(xstream.toXML(i), from);
        }


    }
}
