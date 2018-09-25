/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.idde.editor.controller;

import org.idde.common.model.Contact;
import org.idde.chat.view.ChatFrame;

/**
 *
 * @author vilson
 */
public class Party
{
    private Contact remoteUser;
    private String fileName;
    private ChatFrame chat;

    public Party(Contact remoteUser, String fileName, ChatFrame chat)
    {
        this.remoteUser = remoteUser;
        this.fileName = fileName;
        this.chat = chat;
    }

    /**
     * @return the remoteUser
     */
    public Contact getRemoteUser()
    {
        return remoteUser;
    }

    /**
     * @param remoteUser the remoteUser to set
     */
    public void setRemoteUser(Contact remoteUser)
    {
        this.remoteUser = remoteUser;
    }

    /**
     * @return the fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * @return the chat
     */
    public ChatFrame getChat()
    {
        return chat;
    }

    /**
     * @param chat the chat to set
     */
    public void setChat(ChatFrame chat)
    {
        this.chat = chat;
    }

    @Override
    public String toString()
    {
        return "Party{" + "remoteUser=" + remoteUser + "fileName=" + fileName + '}';
    }
    
}
