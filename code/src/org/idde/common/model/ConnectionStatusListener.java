/**
 * Connection status listener
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
import org.idde.chat.controller.Properties;
import org.idde.util.Logger;

public class ConnectionStatusListener implements org.jivesoftware.smack.ConnectionListener
{

    private XMPPManager xmppManager = null;

    protected ConnectionStatusListener(XMPPManager xmppManager)
    {
        this.xmppManager = xmppManager;
    }

    public void connectionClosed()
    {
        xmppManager.firePropertyChange(Properties.AUTHENTICATED.toString(), null, false);
        xmppManager.firePropertyChange(Properties.CONNECTED.toString(), null, false);
        Logger.getLogger(this).info("Connection has been properly closed...");
    }

    public void connectionClosedOnError(Exception e)
    {
        xmppManager.firePropertyChange(Properties.AUTHENTICATED.toString(), null, false);
        xmppManager.firePropertyChange(Properties.CONNECTED.toString(), null, false);
        Logger.getLogger(this).error("Connection closed on error : " + e.getMessage());
    }

    public void reconnectingIn(int seconds)
    {
        xmppManager.firePropertyChange(Properties.AUTHENTICATED.toString(), null, false);
        xmppManager.firePropertyChange(Properties.CONNECTED.toString(), null, false);
        Logger.getLogger(this).info("Connection closed, try to reconnect for " + seconds + " seconds");
    }

    public void reconnectionFailed(Exception e)
    {
        xmppManager.firePropertyChange(Properties.AUTHENTICATED.toString(), null, false);
        xmppManager.firePropertyChange(Properties.CONNECTED.toString(), null, false);
        Logger.getLogger(this).error("Connection reconnection has failed : " + e.getMessage());
    }

    public void reconnectionSuccessful()
    {
        xmppManager.firePropertyChange(Properties.CONNECTED.toString(), null, true);

        if (xmppManager.isAuthenticated())
        {
            xmppManager.firePropertyChange(Properties.AUTHENTICATED.toString(), null, true);
        }
        else
        {
            xmppManager.firePropertyChange(Properties.AUTHENTICATED.toString(), null, false);
        }

        Logger.getLogger(this).info("Connection has been successfully reconnected ...");
    }
}
