/**
 * Contact list cell render.
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
package org.idde.common.view;

import org.idde.common.view.StatusIcons;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.idde.chat.controller.Properties;
import org.idde.common.model.Contact;
import org.idde.common.model.InstructionProtocol;

/**
 *
 */
public class ContactListCellRenderer extends DefaultTreeCellRenderer
{

    /**
     *
     */
    private static final long serialVersionUID = 1879867084212242772L;

    /** Creates a new instance of ContactListCellRenderer */
    public ContactListCellRenderer()
    {
        //Set defaults icons
        this.setOpenIcon(StatusIcons.ICON_EXPAND);
        this.setClosedIcon(StatusIcons.ICON_COLLAPSE);
    }

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        if (leaf)
        {
            setIcon(getStatusIcon(value));
        }
        return this;
    }

    protected ImageIcon getStatusIcon(Object value)
    {
        try
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            Contact contact = (Contact) (node.getUserObject());

            if (contact.isAvailable())
            {
                if ( contact.getStatus().equals(InstructionProtocol.STATUS_CHAT_IdDE_ID) ) // is the other side using IdDE?!
                {
                    return StatusIcons.ICON_AVAILABLE_IdDE;
                }
                else if(contact.getStatus().equals(Properties.STATUS_AWAY.value()))
                {
                    return StatusIcons.ICON_AWAY;
                }
                else
                {
                    if (contact.getStatus().equals(Properties.STATUS_BUSY.value()))
                    {
                        return StatusIcons.ICON_BUSY;
                    }
                    else
                    {
                        return StatusIcons.ICON_AVAILABLE;
                    }
                }
            }
            else
            {
                if (contact.getStatus() != null)
                {
                    // Probably a different message
                    // Gtalk's portuguese available message is "Disponivel"
                    if (contact.getStatus().equals("Disponível"))
                    {
                        return StatusIcons.ICON_AVAILABLE;
                    }
                    else if ( contact.getStatus().equals(InstructionProtocol.STATUS_CHAT_IdDE_ID) ) // is the other side using IdDE?!
                    {
                        return StatusIcons.ICON_AVAILABLE_IdDE;
                    }
                    else
                    {
                        return StatusIcons.ICON_AWAY;
                    }
                }

                return StatusIcons.ICON_UNAVAILABLE;
            }

        }
        catch (ClassCastException e)
        {
        }

        return new ImageIcon();

    }
}
