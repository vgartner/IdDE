/**
 * Shutdown
 *
 * \b Package: \n
 * org.idde.chat
 *
 * @see org.idde.chat
 *
 * @since Class created on 04/10/2010
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

package org.idde.chat;

import org.idde.util.Logger;
import org.idde.common.model.XMPPManager;

public class Shutdown extends Thread {

    private XMPPManager xmppManager;

    protected Shutdown(XMPPManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    public void run() {
        Logger.getLogger(this).info("Shuting down ...");
        this.xmppManager.disconnect();
        Logger.getLogger(this).info("Exit");
    }

}