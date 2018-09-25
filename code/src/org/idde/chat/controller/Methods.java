/**
 * Methods
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

public enum Methods {

	//Connection methods
        CONNECT("connect"),
	LOGIN("login"),
	DISCONNECT("disconnect"),

	//Roaster methods
	LOAD_CONTACT_LIST("loadContactList"),
	MESSAGE_OUT("sendMessage");

	Methods(String name) {
		this.name = name;
	}

	private String name;

	public String value()
	  {
	    return this.name;
	  }
}
