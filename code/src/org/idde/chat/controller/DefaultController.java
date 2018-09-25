/**
 * Default Controller
 *
 * \b Package: \n
 * org.idde.chat.controller
 *
 * @see org.idde.chat
 * @see org.idde.chat.controller
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
package org.idde.chat.controller;

import org.idde.chat.model.ChatMessage;
import org.idde.util.Util;

public class DefaultController extends AbstractController
{

    /**
     * Change the server in the model.
     * The model will try to connect to the new server
     * automatically.
     *
     * @param newServer
     */
    public void changeServer(String newServer)
    {
        setModelProperty(Properties.SERVER.value(), newServer);
    }

    /**
     *
     * Call the login method in the model
     * whitch try authenticate to the server
     * with the given login and password.
     *
     * @param login
     * @param passwd
     */
    public void login(String login, String passwd)
    {

        callModelMethod(Methods.LOGIN.value(), new Object[]
                {
                    login,
                    passwd
                });
    }

    /**
     * Disconnect the current connection
     *
     */
    public void disconnect()
    {
        callModelMethod(Methods.DISCONNECT.value(), new Object[]
                {
                });
    }

    /**
     * Load the contact list sending evt properties
     *
     */
    public void loadContactList()
    {
        Util.setStatus("IdDE: Loading contacts...");
        callModelMethod(Methods.LOAD_CONTACT_LIST.value(), new Object[]
                {
                });
    }

    /**
     * Send a message
     *
     */
    public void sendMessage(ChatMessage message)
    {
        callModelMethod(Methods.MESSAGE_OUT.value(), new Object[]
                {
                    message
                });
    }

}
