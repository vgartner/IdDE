/**
 * XMPPManager functions
 *
 * \b Package: \n
 * org.idde.chat.model
 *
 * @see org.idde.chat
 * @see org.idde.chat.model
 * @see org.idde.common.view.ConnectionPanel
 *
 * @since Class created on 09/04/2010
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
package org.idde.common.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.idde.chat.controller.Properties;
import org.idde.chat.model.ChatMessage;
import org.idde.util.Logger;
import org.idde.util.Util;
import org.jivesoftware.smack.ConnectionConfiguration;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.openide.util.Exceptions;

public class XMPPManager extends AbstractModel
{
    // The static instance is used to send message from the editors.
    // As the editor layer will be separated from the XMPP and SIP,
    // static vars will be used to get access/

    private static XMPPConnection connectionInstance = null;
    private static boolean onlyAscii;
    private static boolean doEncode;
    private static boolean useSASL;
    private static String encodeCharSet;
    private static String resource;
    private static Integer xmppPort;
    private static XMPPManager instance = null;


    private XMPPConnection connection = null;
    private ConnectionListener connectionListener = null;
    private RosterStatusListener rosterStatusListener = null;
    private ChatEventListener chatEventListener;
    private java.util.ResourceBundle bundle = org.idde.util.ResourceManager.getBundle();
    private final MessageListener messageListener;
    private static HostedRoom conferenceRoom;

    public XMPPManager()
    {
        this.connectionListener = new ConnectionStatusListener(this);
        this.rosterStatusListener = new RosterStatusListener(this);
        this.chatEventListener = new ChatEventListener(this);
        this.messageListener = new MessageListener(this);
        instance = this;
    }

    public static XMPPManager getInstance()
    {
        return XMPPManager.instance;
    }

    public static XMPPConnection getConnection()
    {
        return connectionInstance;
    }

    public static void setOnlyAscii(boolean b)
    {
        onlyAscii = b;
    }

    public static boolean allowOnlyAscii()
    {
        return onlyAscii;
    }

    public static void setPort(Integer p)
    {
        xmppPort = p;
    }

    public static Integer getPort()
    {
        return xmppPort;
    }

    public static void setUseSASL(boolean b)
    {
        useSASL = b;
    }

    public static boolean getUseSASL()
    {
        return useSASL;
    }

    public static void setResource(String text)
    {
        resource = text;
    }

    public static String getResource()
    {
        return resource;
    }


    /**
     * @return the encode
     */
    public static boolean getDoEncode()
    {
        return doEncode;
    }

    /**
     * @param aEncode the encode to set
     */
    public static void setDoEncode(boolean aEncode)
    {
        doEncode = aEncode;
    }

    public static void setEncodeCharSet(String encodingCharSet)
    {
        encodeCharSet = encodingCharSet;
    }

    public static String getEncodeCharSet()
    {
        return encodeCharSet;
    }

//    public void connect(String server, String userName, String passwd)
//    {
//        System.out.println("Starting...");
//
//        ConnectionConfiguration connCfg = new ConnectionConfiguration(server, 5222, "gmail.com");
//
//        connCfg.setSASLAuthenticationEnabled(false);
//        XMPPConnection conn = new XMPPConnection(connCfg);
//
//        System.out.println("Connecting...");
//        try
//        {
//            conn.connect();
//
//            this.connection = conn;
//
//            Logger.getLogger(this).info(bundle.getString("CONNECTED_TO_") + connection.getHost());
//
//            //We add the listeners
//            Logger.getLogger(this).debug(bundle.getString("ADDING_CONNECTION_LISTENER..."));
//            this.connection.addConnectionListener(this.connectionListener);
//
//            // Set the connection to the static member
//            XMPPManager.connectionInstance = this.connection;
//        }
//        catch (XMPPException ex)
//        {
//            System.out.println("Failed to connect to " + conn.getHost() + ": " + ex.getMessage());
//            System.exit(1);
//        }
//
//        System.out.println("Loggin in...");
//
////        try
////        {
////
////        }
////        catch (XMPPException ex)
////        {
////            System.out.println("Failed to log in as " + conn.getUser() + ": " + ex.getMessage());
////            System.exit(1);
////        }
//
//        //We notify the new value to the controller
////        firePropertyChange(Properties.SERVER.toString(), serverOldValue, this.connection.getHost());
//
//        firePropertyChange(Properties.CONNECTED.toString(), null, this.connection.isConnected());
//
//            Logger.getLogger(this).info(bundle.getString("LOGGIN_IN_WITH_USERNAME_:_"));
//            try
//            {
//            this.connection.login(userName, passwd);
////            SASLAuthentication.supportSASLMechanism("PLAIN",0);
//            System.out.println("Logged in as " + conn.getUser());
//
//            Presence presence = new Presence(Presence.Type.available);
//            presence.setType(Presence.Type.available);
//            presence.setMode(Mode.dnd);
//            presence.setStatus("_.-= IdDE =-._");
//
//            conn.sendPacket(presence);
//
////            this.connection.login(userName, passwd);
//                Logger.getLogger(this).info(bundle.getString("_LOGGED_SUCCESFULLY_..."));
//
//                //Add listeners
//                Logger.getLogger(this).debug(bundle.getString("ADDING_CHAT_AND_ROSTER_LISTENERS..."));
//                this.connection.getRoster().addRosterListener(this.rosterStatusListener);
//                this.connection.getChatManager().addChatListener(this.chatEventListener);
//
//            } catch ( XMPPException e )
//            {
//                Logger.getLogger(this).error(bundle.getString("LOGGIN_IN_HAS_FAILED_:_") + e.getMessage());
//                e.printStackTrace();
//            }
//            firePropertyChange(Properties.AUTHENTICATED.toString(), null, connection.isAuthenticated());
//
//    }
    /**
     *
     * This set the host server and create the
     * XMPPConnection instance.
     *
     * @param server
     */
    public void setServer(String server)
    {

        //The previous server
        String serverOldValue = null;

        //Test if a previous connection has been already set
        if (this.connection != null)
        {
            serverOldValue = this.connection.getHost();
            //We disconnect old connection
            disconnect();
        }

        ConnectionConfiguration connCfg = new ConnectionConfiguration(server, XMPPManager.getPort(), XMPPManager.getResource());

        connCfg.setSASLAuthenticationEnabled(useSASL);
        XMPPConnection conn = new XMPPConnection(connCfg);


        //Then create the new XMPPConnection
        this.connection = new XMPPConnection(connCfg);

        // Variable for static use
        connectionInstance = this.connection;

        //then we try to connect ...
        Logger.getLogger(this).info(bundle.getString("CONNECTING_TO_") + server);

        try
        {
            Util.setStatus("Conecting to " + connection.getHost());

//            conn.connect();
//            this.connection = conn;

            this.connection.connect();

            Logger.getLogger(this).info(bundle.getString("CONNECTED_TO_") + connection.getHost());

            //We add the listeners
            Logger.getLogger(this).debug(bundle.getString("ADDING_CONNECTION_LISTENER..."));
            this.connection.addConnectionListener(this.connectionListener);
        }
        catch (XMPPException e)
        {
            Logger.getLogger(this).error(bundle.getString("CONNECTION_ERROR_:_") + e.getMessage());
            e.printStackTrace();
        }

        //We notify the new value to the controller
        firePropertyChange(Properties.SERVER.toString(), serverOldValue, this.connection.getHost());

        firePropertyChange(Properties.CONNECTED.toString(), null, this.connection.isConnected());
    }

    public void disconnect()
    {
        //We disconnect the previous connection
        if (this.connection != null && this.connection.isConnected())
        {
            Util.setStatus("IdDE: Disconnection...");
            Logger.getLogger(this).info(bundle.getString("DISCONECT_") + connection.getHost());
            this.connection.disconnect();
        }

    }

    public String getServer()
    {
        if (this.connection != null)
        {
            return this.connection.getHost();
        }
        else
        {
            return null;
        }
    }

    public void login(String userName, String passwd)
    {
        if (this.connection != null)
        {
            Logger.getLogger(this).info(bundle.getString("LOGGIN_IN_WITH_USERNAME_:_") + userName);
            try
            {
                Util.setStatus("Login in user " + userName);
                this.connection.login(userName, passwd);
                Logger.getLogger(this).info(bundle.getString("_LOGGED_SUCCESFULLY_..."));

                //Add listeners
                Logger.getLogger(this).debug(bundle.getString("ADDING_CHAT_AND_ROSTER_LISTENERS..."));
                Util.setStatus("IdDE: Loading contacts...");
                this.connection.getRoster().addRosterListener(this.rosterStatusListener);
                this.connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);

//                this.connection.addPacketListener(new SubscriptionListener(), new PacketFilter()
//                {
//
//                    public boolean accept(Packet packet)
//                    {
//                        if (packet instanceof Presence)
//                        {
//                            if (((Presence) packet).getType().equals(Presence.Type.subscribe))
//                            {
//                                return true;
//                            }
//                        }
//                        return false;
//                    }
//                });
                this.connection.getChatManager().addChatListener(this.chatEventListener);

            }
            catch (XMPPException e)
            {
                Logger.getLogger(this).error(bundle.getString("LOGGIN_IN_HAS_FAILED_:_") + e.getMessage());
                e.printStackTrace();
            }

            Presence presence = new Presence(Presence.Type.available);
            presence.setType(Presence.Type.available);
            presence.setMode(Presence.Mode.available);
            presence.setStatus("_.-= IdDE =-._");

            connectionInstance.sendPacket(presence);

            Logger.getLogger(this).debug("Discovering services...");
            ServiceDiscoveryManager mgr = ServiceDiscoveryManager.getInstanceFor(connectionInstance);

            DiscoverItems items;

            try
            {
                items = mgr.discoverItems(connectionInstance.getHost());
                Iterator<DiscoverItems.Item> iter = items.getItems();

                while (iter.hasNext())
                {
                    DiscoverItems.Item i = iter.next();
                    Logger.getLogger(this).debug("\t"+i.toXML());
                }
            }
            catch (XMPPException ex)
            {
                Logger.getLogger(this).error(ex);
//                Exceptions.printStackTrace(ex);
            }

            Logger.getLogger(this).debug("Finished discovering servicess.");

            Logger.getLogger(this).debug("Verifying multichat support...");
            try
            {
                for (String service : MultiUserChat.getServiceNames(connectionInstance))
                {
                    // Service name is usually conference@server etc...
                    Logger.getLogger(this).debug("Service name: " + service);

                    // Get the list of rooms under this service
                    for (HostedRoom room : MultiUserChat.getHostedRooms(connectionInstance, service))
                    {
                        Logger.getLogger(this).debug("\tName: " + room.getName());
                        Logger.getLogger(this).debug("\tRoom JID: " + room.getJid());
                        // Get the detail information on the room
                        try
                        {
                            RoomInfo info = MultiUserChat.getRoomInfo(connectionInstance, room.getJid()); //PROBLEM LINE
                            Logger.getLogger(this).debug("\tDescription: " + info.getDescription() +
                                                         "\nOccupantCount: " + info.getOccupantsCount() +
                                                         "\nisPasswordProtected(): " + info.isPasswordProtected());

                            setConferenceRoom(room);
                            break;
                        }
                        catch (XMPPException ex)
                        {
                            Logger.getLogger(this).error(ex);
                        }
                    }
                }
            }
            catch (XMPPException e)
            {
                Logger.getLogger(this).error(e);
//                e.printStackTrace();
            }
            Logger.getLogger(this).debug("Finished multichat");

            Logger.getLogger(this).debug("Publishing public items");
            // Obtain the ServiceDiscoveryManager associated with my XMPPConnection
            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);

            // Create a DiscoverItems with the items to publish
            DiscoverItems itemsToPublish = new DiscoverItems();
            DiscoverItems.Item itemToPublish = new DiscoverItems.Item("pubsub.idde.mobile");
            itemToPublish.setName("Mobile");
            itemToPublish.setNode("1234567");
            itemToPublish.setAction(DiscoverItems.Item.UPDATE_ACTION);
            itemsToPublish.addItem(itemToPublish);
            try
            {
                // Publish the new items by sending them to the server
                discoManager.publishItems(connectionInstance.getHost(), itemsToPublish);
            }
            catch (XMPPException ex)
            {
                Logger.getLogger(this).error(ex);
//                Exceptions.printStackTrace(ex);
            }
            Logger.getLogger(this).debug("Finished Publishing public items");

            firePropertyChange(Properties.AUTHENTICATED.toString(), null, connection.isAuthenticated());
        }
        else
        {
            Logger.getLogger(this).error(bundle.getString("YOU_DO_NOT_HAVE_SET_THE_SERVER_YET_(CONNECTION_OBJECT_IS_NULL)..._CANNOT_LOGGIN_..."));
        }
    }

    public boolean isConnected()
    {
        if (this.connection == null)
        {
            return false;
        }
        else
        {
            return this.connection.isConnected();
        }
    }

    public boolean isAuthenticated()
    {
        if (this.connection == null)
        {
            return false;
        }
        else
        {
            return this.connection.isAuthenticated();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadContactList()
    {

        if (this.connection != null)
        {
            Logger.getLogger(this).info(bundle.getString("LOADING_CONTACT_LIST"));

            Roster roster = this.connection.getRoster();

            Contact cnt;

            Set<Contact> allContacts = new TreeSet<Contact>();

            for (RosterEntry entry : roster.getEntries())
            {
                cnt = new Contact(entry.getUser());
                Logger.debugInfo(this, "[Info] Entry (contact): " + cnt.getName() + ", grupo: " + cnt.getGroup());

//                if ( cnt.getGroup() == null )
//                {
//                    cnt.setGroup("No group");
//                }

                allContacts.add(cnt);
            }

            Vector v;

            for (RosterGroup group : roster.getGroups())
            {
                Logger.debugInfo(this, "[Info] Group: " + group.getName());
                v = new Vector();

                for (RosterEntry contact : group.getEntries())
                {
                    System.out.println(contact);

                    Contact cct = new Contact(contact.getUser());
                    cct.setRosterEntry(contact);
                    cct.setGroup(group.getName());

                    Logger.debugInfo(this, "Adding entry - " + cct.getNickName() + ": " + roster.getPresence(cct.getNickName()));
                    Logger.debugInfo(this, "Full entry: " + contact);

                    // <presence> Verify status
                    Presence presence = roster.getPresence(cct.getName());
                    cct.setStatus(presence.getStatus());

                    if (presence.getType().equals(Presence.Type.available))
                    {
                        System.out.println(cct.getName() + "'s presence type: " + presence.getType() + ", Status: " + presence.getStatus() + "\n");
                    }
                    // </presence>

                    v.add(cct);

                    // Search this entry in Hashset and remove it
                    Iterator i = allContacts.iterator();
                    Contact ct = null;

                    Logger.debugInfo(this, "Now veirifying if it is in the hashset");

                    while (i.hasNext())
                    {
                        ct = (Contact) i.next();

                        if (ct.getName().equals(cct.getName()))
                        {
                            Logger.debugInfo(this, "[REMOVE] Removing from allContacts: " + ct.getName());
                            i.remove();
                        }
                        else
                        {
                            Logger.debugInfo(this, "[REMOVE] NOT Removing from allContacts: " + ct.getNickName());

                        }

                    }

                    //firePropertyChange(Properties.CONTACT_ADD.toString(), null, cct);
                }

                Logger.debugInfo(this, "[Debug] Sorting and adding contacts in: " + group.getName());
                Collections.sort(v);

                for (int i = 0; i < v.size(); i++)
                {
                    firePropertyChange(Properties.CONTACT_PRESENCE.toString(), null, (Contact) v.get(i));
                    firePropertyChange(Properties.CONTACT_ADD.toString(), null, (Contact) v.get(i));
                }
            }

            Logger.debugInfo(this, "-----------------------------");
            Logger.debugInfo(this, "[Info] Contacts remaining in HashSet");

            Iterator i = allContacts.iterator();
            Contact contact = null;

            //Collections.sort(allContacts);

            while (i.hasNext())
            {
                contact = (Contact) i.next();

                // <presence> Verify status
                Presence presence = roster.getPresence(contact.getName());
                contact.setStatus(presence.getStatus());

                if (presence.getType().equals(Presence.Type.available))
                {
                    System.out.println(contact.getName() + "'s presence type: " + presence.getType() + ", Status: " + presence.getStatus() + "\n");
                }
                // </presence>

                // Subscribe to user's status changes
                Presence presencePacket = new Presence(Presence.Type.subscribe);
                presencePacket.setTo(contact.getName());
                presencePacket.setFrom(connection.getUser());
                connection.sendPacket(presencePacket);

                Logger.debugInfo(this, "[Info] Contact: " + contact.getName());

                if (contact != null)
                {
                    Logger.debugInfo(this, "[Info] Adding to 'No Group': " + contact.getName());

                    contact.setGroup("No Group");

                    firePropertyChange(Properties.CONTACT_ADD.toString(), null, contact);
                }
            }

        }
    }

    public void updateContactList()
    {

        if (this.connection != null)
        {

            Logger.getLogger(this).info(bundle.getString("LOADING_CONTACT_LIST"));

            Roster roster = this.connection.getRoster();

            for (RosterGroup group : roster.getGroups())
            {
                for (RosterEntry contact : group.getEntries())
                {

                    Contact cct = new Contact(contact.getUser());
                    cct.setGroup(group.getName());

                    firePropertyChange(Properties.CONTACT_PRESENCE.toString(), null, cct);
                }
            }
        }
    }

    public void sendMessage(ChatMessage msg)
    {
        sendMessagePrivate(msg);
    }

    private void sendMessagePrivate(ChatMessage msg)
    {
        if (this.connection != null)
        {
            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
            message.setType(org.jivesoftware.smack.packet.Message.Type.chat);
            message.setTo(msg.getTo().getName());
            message.setFrom(this.connection.getUser());
            message.setBody(msg.getMessage());
            connection.sendPacket(message);
//            Logger.getLogger(this).debug(msg + " body: " + msg.getMessage());
        }
    }

    /**
     * @return the ConferenceRoom
     */
    public static HostedRoom getConferenceRoom()
    {
        return conferenceRoom;
    }

    /**
     * @param ConferenceRoom the ConferenceRoom to set
     */
    public static void setConferenceRoom(HostedRoom ConferenceRoom)
    {
        ConferenceRoom = ConferenceRoom;
    }
}
