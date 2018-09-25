/**
 * Used to define Request model
 *
 * \b Package: \n
 * org.idde.editor.model
 *
 * @see org.idde.editor.tranport.OperationalTransformation.java
 *
 * @since Class created on 16/03/2011
 *
 * @author Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b Maintainers: \n
 * Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b License: \n
 * Contact the author
 *
 * @version $Id$
 */
package org.idde.editor.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author vilson
 */
public class Request
{
    private String user;
    private String type;
    private Integer offset;
    private Integer lenght;
    private String text;
    private Map<String, Integer> siteState;

    public Request(String user, String type, Integer offset, String text, Integer lenght, Map<String, Integer> siteState)
    {
        this.user = user;
        this.type = type;
        this.offset = offset;
        this.text   = text;
        this.lenght = lenght;
        this.siteState = siteState;
    }

    /**
     * @return The user name
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param Username to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the offset
     */
    public Integer getOffset()
    {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }

    /**
     * @return the lenght
     */
    public Integer getLenght()
    {
        return lenght;
    }

    /**
     * @param lenght the lenght to set
     */
    public void setLenght(Integer lenght)
    {
        this.lenght = lenght;
    }

    /**
     * @return the siteState
     */
    public Map<String, Integer> getSiteState()
    {
        return siteState;
    }

    /**
     * @param siteState the siteState to set
     */
    public void setSiteState(Map<String, Integer> siteState)
    {
        this.setSiteState(siteState);
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return "<" + user + ", "+ type + ", "+ offset + ", "+ text + ", "+ lenght + ", "+ siteState.toString() + ">" ;
    }


    /**
     * Returns a clone from the Request
     * @return clone with values
     */
    public Request getClone()
    {
        Map<String, Integer> cloneState = new HashMap<String, Integer>();

        Iterator it = siteState.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry pairs = (Map.Entry) it.next();
            cloneState.put(pairs.getKey().toString(), Integer.parseInt(pairs.getValue().toString()) );
        }

        Request clone = new Request(user, type, offset, text, lenght, cloneState);

        return clone;
    }

}
