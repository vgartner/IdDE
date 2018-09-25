/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moduleIdDE;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.idde.util.Database;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInstall;

//http://forums.netbeans.org/ntopic23810.html
// dependencies: 
// org.netbeans.core.startup
// org.netbeans.core
//import org.netbeans.core.startup.Splash;
/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall
{

    @Override
    public void restored()
    {
//        Splash splash = Splash.getInstance();
//
//        for (int i = 0; i < 4; i++)
//        {
//            splash.print("===> IdDE module loading . ");
//
//            try
//            {
//                Thread.sleep(200l);
//            }
//            catch (InterruptedException e)
//            {
//            }
//
//            splash.print("===> IdDE module loading o ");
//
//            try
//            {
//                Thread.sleep(200l);
//            }
//            catch (InterruptedException e)
//            {
//            }
//
//            splash.print("===> IdDE module loading O ");
//
//            try
//            {
//                Thread.sleep(200l);
//            }
//            catch (InterruptedException e)
//            {
//            }
//        }
        //old
//        Image loadContent = Splash.loadContent(true);
//        String msg = "[IdDE] Module loaded. I'm alive! :-) ";
//        int msgType = NotifyDescriptor.INFORMATION_MESSAGE;
//        NotifyDescriptor nd = new NotifyDescriptor.Message(msg, msgType);
//        DialogDisplayer.getDefault().notify(nd);
    }

    @Override
    public boolean closing()
    {
        // TODO close sessions where this users is in

//        String msg = "[IdDE] Netbeans is asking to close. Ending all IdDE sessions...";
//        int msgType = NotifyDescriptor.INFORMATION_MESSAGE;
//
//        NotifyDescriptor nd = new NotifyDescriptor.Message(msg, msgType);
//        DialogDisplayer.getDefault().notify(nd);
//
//        // Any error closing IdDE, return false
        return true;
    }

    @Override
    public void close()
    {
//        String msg = "[IdDE] Only to report: Netbeans is closing. Bye.";
//        int msgType = NotifyDescriptor.INFORMATION_MESSAGE;
//
//        NotifyDescriptor nd = new NotifyDescriptor.Message(msg, msgType);
//        DialogDisplayer.getDefault().notify(nd);

        // Close all active connections to database
//        Database.close();
    }
}
