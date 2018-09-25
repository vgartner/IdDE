/**
 * ChatMessage class
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
package org.idde.chat.model;

import org.idde.common.model.Contact;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * @author vilson
 */
public class ChatMessage
{
    /**
     *
     */
    private Contact from;
    /**
     *
     */
    private Contact to;
    /**
     *
     */
    private String message;
    /**
     *
     */
    private Message smackMessage;

    /**
     * Get contact who sent message
     * @return
     */
    public final Contact getFrom()
    {
        if (this.from == null)
        {
            return new Contact("me");
        }

        return from;
    }

    /**
     * Set messages' contact origin
     * @param from
     */
    public final void setFrom(Contact from)
    {
        this.from = from;
    }

    /**
     * Get the message
     * @return
     */
    public final String getMessage()
    {
        return message;
    }

    /**
     * set the message
     * @param message
     */
    public final void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Get message destination
     * @return
     */
    public final Contact getTo()
    {
        return to;
    }

    /**
     * Set message destinations
     * @param to
     */
    public final void setTo(Contact to)
    {
        this.to = to;
    }
    
    /**
     * Set smack message. This can be used to access original message properties
     * @param message message in smack format
     */
    public void setSmackMessage(Message message)
    {
        this.smackMessage = message;
    }

    /**
     * This can be used to access original message
     * @return message in smack format
     */public Message getSmackMessage()
    {
        return this.smackMessage;
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString()
    {
        return "Message : From " + this.getFrom().getName() + " To " + this.to.getName();
    }
}
