/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.idde.common.model;

/**
 *
 * @author vilson
 */
public class MenuItemModel
{
    private String caption;
    private Contact contact;

    public void MenuModel(String caption, Contact contact)
    {
        this.caption = caption;
        this.contact = contact;
    }

    @Override
    public String toString()
    {
        return caption;
    }
}


