/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.util.Collection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

/**
 *
 * @author vilson
 */
public class XMPPTest
{

    public static void main(String[] args)
    {

        System.out.println("Starting...");

        ConnectionConfiguration connCfg = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");

        connCfg.setSASLAuthenticationEnabled(false);
        XMPPConnection connection = new XMPPConnection(connCfg);

        System.out.println("Connecting...");
        try
        {
            connection.connect();
            System.out.println("Connected to " + connection.getHost());
        }
        catch (XMPPException ex)
        {
            System.out.println("Failed to connect to " + connection.getHost() + ": " + ex.getMessage());
            System.exit(1);
        }

        System.out.println("Loggin in...");

        try
        {
            connection.login("vgartner", "#$CxFm(6KD");
//            SASLAuthentication.supportSASLMechanism("PLAIN",0);
            System.out.println("Logged in as " + connection.getUser());

            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);

        }
        catch (XMPPException ex)
        {
            System.out.println("Failed to log in as " + connection.getUser() + ": " + ex.getMessage());
            System.exit(1);
        }

        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();

        System.out.println("\n\n" + entries.size() + " buddy(ies):");
        for (RosterEntry r : entries)
        {
            System.out.println(r.getUser());
        }

        connection.disconnect();
    }
}
