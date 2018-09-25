/**
 * Contact
 *
 * \b Package: \n
 * org.idde.chat.model
 *
 * @see org.idde.chat
 * @see org.idde.chat.model
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

import org.idde.chat.controller.Properties;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

public class Contact implements Comparable
{

    private String name;
    private String group;
    private String nickName;
    private boolean available;
    private String status;
    private Presence presence;
    private RosterEntry rosterEntry;

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public Contact(String name)
    {
        this.name = name;
        this.nickName = name.replaceFirst("@.*$", "");
        this.group = "null";
        this.available = false;
        this.status = Properties.STATUS_AVAILABLE.value();
    }

    public String getGroup()
    {
        return group;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( o.getClass() == Contact.class )
        {
            if ( ((Contact) o).getName().replaceFirst("/.*$", "").equals(this.getName()) )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        String result;

        if ( this.rosterEntry == null )
        {
            result = this.getNickName();
        }
        else
        {
            result = this.rosterEntry.getName();
        }

        if ( result == null )
        {
            result = this.getNickName();
        }
        
        return result;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public final String getStatus()
    {
        return status;
    }

    public final void setStatus(String status)
    {
        this.status = status;
    }

    public final void setGroup(String group)
    {
        this.group = group;
    }

    @Override
    public int compareTo(Object o)
    {
        return getName().compareTo(( (Contact) o).getName() );
    }

    public void setXMPPPresence(Presence presence)
    {
        this.presence = presence;
    }

    public Presence getXMPPPresence()
    {
        return this.presence;
    }

    /**
     * @return the rosterEntry
     */
    public RosterEntry getRosterEntry()
    {
        return rosterEntry;
    }

    /**
     * @param rosterEntry the rosterEntry to set
     */
    public void setRosterEntry(RosterEntry rosterEntry)
    {
        this.rosterEntry = rosterEntry;
    }


}
