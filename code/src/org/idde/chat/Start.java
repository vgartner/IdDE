/**
 * Start
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

//nimrodlf-1.0e.jar
//import com.nilo.plaf.nimrod.NimRODLookAndFeel;
//import com.nilo.plaf.nimrod.NimRODTheme;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.idde.chat.controller.DefaultController;
import org.idde.common.model.XMPPManager;
import org.idde.common.view.iddeMainFrame;
import org.jivesoftware.smack.XMPPConnection;

/**
 *
 *
 */
public class Start
{

    /** Creates a new instance of Main
     * @param arg
     */
    @SuppressWarnings("static-access")
    static public void main(String[] arg)
    {
//        nimrodlf-1.0e.jar
//        NimRODTheme nt = new NimRODTheme();

        /*	nt.setPrimary1( Color.decode("0x1F2E3A"));
        nt.setPrimary2( Color.decode("0x293844"));
        nt.setPrimary3( Color.decode("0x33424E"));
        nt.setSecondary1(Color.decode("0x3B3B3B"));
        nt.setSecondary2(Color.decode("0x454545"));
        nt.setSecondary3(Color.decode("0x4F4F4F"));
        nt.setWhite(Color.WHITE);
        nt.setBlack(Color.black);*/
        //nt.setMenuOpacity(80);
        //nt.setFrameOpacity(180);

//		NimRODLookAndFeel NimRODLF = new NimRODLookAndFeel();
//		NimRODLF.setCurrentTheme( nt);
//        try
//        {
//            UIManager.setLookAndFeel(NimRODLF);
//        }
//        catch ( UnsupportedLookAndFeelException ex )
//        {
//            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
//        }


        XMPPManager connectionManager = new XMPPManager();
        XMPPConnection.DEBUG_ENABLED = true;

        DefaultController controller = new DefaultController();
        iddeMainFrame chatView = new iddeMainFrame(controller);

        controller.addModel(connectionManager);

        Runtime.getRuntime().addShutdownHook(new Shutdown(connectionManager));

        chatView.setVisible(true);

    }
}
