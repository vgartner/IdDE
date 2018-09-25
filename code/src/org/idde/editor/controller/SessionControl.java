/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.idde.editor.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import moduleIdDE.SessionTopComponent;
import org.idde.common.model.Contact;
import org.idde.common.model.XMPPManager;
import org.idde.chat.view.ChatFrame;
import org.idde.util.Logger;
import org.idde.util.Util;
import org.jivesoftware.smack.XMPPConnection;

/**
 *
 * @author vilson
 */
public class SessionControl
{
    // This variable holds the shared editing sessions

    private static Set editSessions = null;
    private static Map<String, OperationalTransformation> fileSessionSiteStates;
    public static String LOCAL_USER = Util.getLocalUserName();


    public static OperationalTransformation getFileSessionSiteStates(String fileName)
    {
        OperationalTransformation result = null;

        if ( fileSessionSiteStates.containsKey(fileName) )
        {
            result = fileSessionSiteStates.get(fileName) ;
        }

        return result;
    }

    /**
     * Get the editing session. If it isn't initialized (= null), then it will be done too.
     * The session control is a set of Paty objects.
     * @return the editing session control
     */
    public static Set getEditSession()
    {
        if (editSessions == null)
        {
            editSessions = new HashSet();
        }

        return editSessions;
    }

    /**
     * Adds a new shared edition session. This sessions are used to control for
     * what users
     *
     * @param userNickName The destination user.
     * @param fileName The file name which is shared
     */
    public static Boolean addSharedEditing(Contact remoteParty, String fileName, ChatFrame chat)
    {
        return newSharedEditing(remoteParty, fileName, chat);
    }

    private static Set getFilesBeingShared()
    {
        Set result = new HashSet();
        Iterator i = getEditSession().iterator();
        Party p;

        String fileName = null;
        String fileNameLast = null;

        while (i.hasNext())
        {
            p = (Party) i.next();

//            fileName = p.getFileName();
//
//            if ( ! p.getFileName().equals(fileNameLast))
//            {
            result.add(p.getFileName());
//            }
        }

        return result;
    }

    private static Set getSharedUsersOnFile(String fileName)
    {
        Set result = new HashSet();
        Iterator i = getEditSession().iterator();
        Party p;

        while (i.hasNext())
        {
            p = (Party) i.next();

            if (p.getFileName().equals(fileName))
            {
                result.add(p.getRemoteUser());
            }
        }

        return result;
    }

    private static Boolean newSharedEditing(Contact remoteParty, String fileName, ChatFrame chat)
    {
        Logger.getLogger(SessionControl.getEditSession()).debug("[Debug] Sharing file " + fileName + " with " + remoteParty);
        //        Contact contact = new Contact("vilson@vgdata.net");
        //        contact.setNickName("vilson@vgdata.net");
        //        Contact contact = new Contact(remoteParty);
        //        contact.setNickName(remoteParty);
        Party p = new Party(remoteParty, fileName, chat);
        boolean rs = true;
        // Add party to session
        // If not added, it already exists.
        if (!SessionControl.getEditSession().add(p))
        {
            rs = false;
        }
        else
        {
            rs = true;
            
            createSiteStatesForSession(fileName, LOCAL_USER, remoteParty.getNickName());
        }
        // update session table
        updateSessionTableData();

        return rs;
    }

    /**
     * Get all users who are sharing the same file.
     *
     * @param fileName File name to look at
     * @return Set of remote users () who sharing this file
     */
    public static Set getSharedUsers(String fileName)
    {
        return getSharedUsersOnFile(fileName);
    }

    public static void addMessageToBuffer(String fileName, String msg)
    {
        //if ( qtdBuffer > = config )
        //{
        //  compose message to send
        //  call sendChangesToParties

        // queue (linked list)

        sendChangesToParties(fileName, msg);
    }

    /**
     * This method sends messages to all users registered in session and who are
     * sharing the same files.
     * 
     * @param fileName Name of the file which was changed
     * @param text String to be sent do remote parties
     */
    private static void sendChangesToParties(String fileName, String msg)
    {
        Party p;
        Iterator i = getEditSession().iterator();

//        String text = null;
//
//        // Verify if we nedd to encode this message
//        if ( XMPPManager.getDoEncode() )
//        {
//            try
//            {
//                text = Util.encodeBase64(msg);
//            }
//            catch (UnsupportedEncodingException ex)
//            {
//                Exceptions.printStackTrace(ex);
//                text = msg;
//            }
//        }
//        else
//        {
//            text = msg;
//        }

        XMPPConnection connection = XMPPManager.getConnection();

        if (connection == null)
        {
            System.out.print("[SessionControl.sendChangestoParties] XMPPConnection is still null, maybe not connected?!\n");
            return;
        }

        org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
        message.setType(org.jivesoftware.smack.packet.Message.Type.chat);
        message.setFrom(connection.getUser());
        message.addBody("en", msg);

        System.out.print("[SessionControl] Looking who is sharing " + fileName + " with me\n");

        // Process each Party and, if the file is the same, send a message (Instruction)
        while (i.hasNext())
        {
            p = (Party) i.next();

            System.out.print("[SessionControl] Registered file: " + p.getFileName() + ", shared with: " + p.getRemoteUser() + "\n");

            if (p.getFileName().equals(fileName))
            {
                ChatFrame cf = p.getChat();

                // Send message
                System.out.print("[SessionControl] Sending instruction to: " + p.getRemoteUser().getName() + "\n");
                cf.doSendInstruction(msg);

//                message.setTo(cf.getParticipant().getName());
//                connection.sendPacket(message);
            }
        }
    }

    /**
     * This method returns which files are being shared in session
     *
     * @return Set containig files which are being shared
     */
    public static Set getSharedFiles()
    {
        return getFilesBeingShared();
    }

    /**
     * Update the Session window' table
     */
    private static void updateSessionTableData()
    {
        SessionTopComponent.updateTableData();
    }

    /**
     * 
     */
    public static void pingAlive()
    {
        // TODO verify alive users on sessions
    }

    public static void exitSharedEditing(String fileName)
    {
        Iterator i = getEditSession().iterator();
        Party p;

        while (i.hasNext())
        {
            p = (Party) i.next();

            if (p.getFileName().equals(fileName))
            {
                // remove item from hashset
                i.remove();

                // update session table
                updateSessionTableData();
            }
        }

        // destroy the Operation Control as well
        if ( fileSessionSiteStates.containsKey(fileName) )
        {
            Logger.getLogger("SessionControl").debug("[Debug] Removing from 'fileSessionSiteStates': "+ fileName);
            fileSessionSiteStates.remove(fileName);
        }

    }

    public static void removeUseFromSession(String nickName, String fileName)
    {
        Iterator i = getEditSession().iterator();
        Party p;

        while (i.hasNext())
        {
            p = (Party) i.next();

            if (p.getFileName().equals(fileName) && p.getRemoteUser().getNickName().equals(nickName))
            {
                // remove item from hashset
                i.remove();
                
                // remove it from Operational Transformation control
                if ( fileSessionSiteStates.get(fileName).containsSiteState(nickName) )
                {
                    fileSessionSiteStates.get(fileName).removeStateFromSiteStates(nickName);
                }

                // update session table
                updateSessionTableData();
            }
        }
    }

    /**
     * This method creates the OperationsTransformation() used to control this stuff
     * @param fileName
     */
    private static void createSiteStatesForSession(String fileName, String localUser, String remoteUser)
    {
        // First time the method is invoked, object will be instanciated
        if ( fileSessionSiteStates == null )
        {
            fileSessionSiteStates = new HashMap<String, OperationalTransformation>();
        }

        // verify if this Operational Transformation already exists
        // if local user already exists on this file's OT, it's already created.
        // So, maybe only is needed to add remote user (was a join command)
        OperationalTransformation ot;
        ot = fileSessionSiteStates.get(fileName);

        // if it was created 
        if ( ot != null && ot.containsSiteState(localUser) )
        {
            if ( ! fileSessionSiteStates.get(fileName).containsSiteState(remoteUser) )
            {
                fileSessionSiteStates.get(fileName).addStateToSiteStates(remoteUser);
            }
        }
        else
        {
            // Create Operational Transformation and add 2 key
            ot = new OperationalTransformation();
            ot.addStateToSiteStates(localUser);
            ot.addStateToSiteStates(remoteUser);

            fileSessionSiteStates.put (fileName, ot);
        }
    }


// exemplo HashSet
//    Set s = new HashSet();
//for (int i = 0; i < args.length; i++){
//if (!s.add(args[i]))
//System.out.println("Duplicate detected: " +
//args[i]);
//}
//System.out.println(s.size() +
//" distinct words detected: " +
//s);
}
