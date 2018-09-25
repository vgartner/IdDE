/**
 * Logger class.
 *
 * \b Package: \n
 * org.idde.util
 *
 * @see org.idde
 * @see org.idde.util
 *
 * @since Class created on April 10, 2010.
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

package org.idde.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.idde.common.model.Contact;
import org.openide.util.Exceptions;

public class Logger {

	//private static final Log LOGGER = LogFactory.getLog(Logger.class);

	public static Log getLogger(Object o) {
		return LogFactory.getLog(o.getClass());
	}

    /**
     *
     * @param obj
     * @param message
     */
    public static void debugError(Object obj, String message)
    {
            getLogger(obj).error(message);
    }

    /**
     *
     * @param obj
     * @param message
     */
    public static void debugWarning(Object obj, String message)
    {
            getLogger(obj).warn(message);
    }

    /**
     *
     * @param obj
     * @param message
     */
    public static void debugInfo(Object obj, String message)
    {
            getLogger(obj).info(message);
    }

    /**
     *
     * @param obj
     * @param message
     */
    public static void debug(Object obj, String message)
    {
            debugInfo(obj, message);
    }

    public static void logConversation(Contact contact, String message)
    {
        String logFile = Util.logDirectory + File.separator +contact.getNickName()+ "-chat"+".log";
        PrintWriter out;

        try
        {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
            out.write(message+"\n");
            out.flush();
            out.close();
        }
        catch (IOException ex)
        {
            Exceptions.printStackTrace(ex);
        }

    }
}
