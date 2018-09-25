/**
 * Resource manager
 *
 * \b Package: \n
 * org.idde.chat.view
 *
 * @see org.idde.chat
 * @see org.idde.chat.view
 *
 * @since Class created on April 11, 2010.
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

import javax.swing.ImageIcon;
import java.util.ResourceBundle;

/**
 *
 * @author vilson
 */
public class  ResourceManager {

    /**
     *
     * @param iconName
     * @return
     */
    public ImageIcon getChatIcon( String iconName ) {

        ImageIcon icon = getIcon("chat/icons/"+iconName);
        return icon;
    }

    public ImageIcon getSIPIcon( String iconName ) {

        ImageIcon icon = getIcon("sip/icons/"+iconName);
        return icon;
    }

    private ImageIcon getIcon(String iconName) {

        ClassLoader cldr = this.getClass().getClassLoader();
        java.net.URL imageURL = cldr.getResource("resources/" + iconName);
        ImageIcon icon = new ImageIcon(imageURL);
        return icon;
    }

    public static ResourceBundle getBundle() {

        String i18n = "org/idde/lang/Bundle";
        ResourceBundle bundle = java.util.ResourceBundle.getBundle(i18n);

        return bundle;
    }

}
