/**
 * Properties
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

import org.idde.common.model.Contact;
import org.idde.chat.model.ChatMessage;

public enum Properties {


	//Connection properties
	CONNECTED(Boolean.class.toString()),
	AUTHENTICATED(Boolean.class.toString()),
	SERVER("Server"),

	//Roaster properties
	CONTACT_ADD(Contact.class.toString()),
	CONTACT_DELETE(Contact.class.toString()),
	CONTACT_PRESENCE(Contact.class.toString()),

	//Presence Status
	STATUS_AVAILABLE("available"),
	STATUS_BUSY("busy"),
	STATUS_AWAY("away"),

	//Messages
	MESSAGE_IN(ChatMessage.class.toString());


	Properties(String value) {
		this.value = value;
	}

	private String value;

	public String value()
	  {
	    return this.value;
	  }

}
