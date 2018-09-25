/**
 * Abstract view panel.
 *
 * \b Package: \n
 * org.idde.chat.view
 *
 * @see org.idde.chat
 * @see org.idde.chat.view
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

package org.idde.chat.view;

import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;

/**
 * This class provides the base level abstraction for views. All
 * views that extend this class also extend JPanel as well as providing the
 * modelPropertyChange() method that controllers can use to propogate model
 * property changes.
 *
 * @author Robert Eckstein
 */
public abstract class AbstractViewPanel extends JPanel {

    /**
     * Called by the controller when it needs to pass along a property change
     * from a model.
     *
     * @param evt The property change event from the model
     */
    public abstract void modelPropertyChange(PropertyChangeEvent evt);


}
