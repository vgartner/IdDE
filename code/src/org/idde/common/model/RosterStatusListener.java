/**
 * Class implementing roster listeners
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

import org.idde.common.model.XMPPManager;
import java.util.Collection;

import org.idde.chat.controller.Properties;
import org.idde.util.Logger;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

public class RosterStatusListener implements RosterListener
{

    private XMPPManager xmppManager = null;

    protected RosterStatusListener(XMPPManager xmppManager)
    {
        this.xmppManager = xmppManager;
    }

    @Override
    public void entriesAdded(Collection<String> addresses)
    {
        //TODO
        for (String addresse : addresses)
        {
            System.out.println(java.util.ResourceBundle.getBundle("org/idde/lang/Bundle").getString("ROSTER_LISTENER_->_") + addresse);
        }
    }

    @Override
    public void entriesDeleted(Collection<String> addresses)
    {
        //TODO
        for (String addresse : addresses)
        {
            System.out.println(java.util.ResourceBundle.getBundle("org/idde/lang/Bundle").getString("ROSTER_LISTENER_->_") + addresse);
        }

    }

    @Override
    public void entriesUpdated(Collection<String> addresses)
    {
        //TODO
        for (String addresse : addresses)
        {
            System.out.println(java.util.ResourceBundle.getBundle("org/idde/lang/Bundle").getString("ROSTER_LISTENER_->_") + addresse);
        }
    }

    @Override
    public void presenceChanged(Presence presence)
    {

        Contact contact = new Contact(presence.getFrom());
        contact.setXMPPPresence(presence);

        //Set contact avaibility
        contact.setAvailable(presence.isAvailable());

        //Set other status
        if (presence.isAway())
        {
            contact.setStatus(Properties.STATUS_AWAY.value());
        }
        else
        {
            if (presence.getMode().equals(Presence.Mode.dnd))
            {
                contact.setStatus(Properties.STATUS_BUSY.value());
            }
            else
            {
                if (presence.getMode().equals(Presence.Mode.available))
                {
                    contact.setStatus(Properties.STATUS_AVAILABLE.value());
                }
                else
                {
                    if (presence.getMode().equals(Presence.Mode.chat))
                    {
                        contact.setStatus(Properties.STATUS_AVAILABLE.value());
                    }
                }
            }
        }

        xmppManager.firePropertyChange(Properties.CONTACT_PRESENCE.toString(), null, contact);
        Logger.getLogger(this).debug(java.util.ResourceBundle.getBundle("org/idde/lang/Bundle").getString("PRESENCE_MESSAGE_:_") + presence.getFrom() + java.util.ResourceBundle.getBundle("org/idde/lang/Bundle").getString("_IS_") + presence);
    }
}
