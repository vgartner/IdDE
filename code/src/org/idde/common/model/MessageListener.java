/**
 * Description
 *
 * \b Package: \n
 * org.idde.chat.model
 *
 * @see ???.???.???
 *
 * @since Class created on 26/10/2010
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
package org.idde.common.model;

import org.idde.common.model.XMPPManager;
import org.idde.chat.controller.Properties;
import org.idde.util.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * @author vilson
 */
class MessageListener implements org.jivesoftware.smack.MessageListener
{

    private XMPPManager xmppManager;

    public MessageListener(XMPPManager xmppManager)
    {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processMessage(Chat chat, Message message)
    {
        if (message.getBody() != null && !message.getBody().equals(""))
        {
            Contact from = new Contact(message.getFrom());
            Contact to = new Contact(message.getTo());

            org.idde.chat.model.ChatMessage msg = new org.idde.chat.model.ChatMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setMessage(message.getBody());
            msg.setSmackMessage(message);

            // Fire property change to be caught by apropriate listener
            xmppManager.firePropertyChange(Properties.MESSAGE_IN.toString(), null, msg);

//            Logger.getLogger(this).debug(msg + " body : " + msg.getMessage());
        }

    }
}
