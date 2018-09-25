/**
 * Description
 *
 * \b Package: \n
 * org.idde.agenda.model
 *
 * @see org.idde.agenda.view
 *
 * @since Class created on 31/01/2011
 *
 * @author Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b Maintainers: \n
 * Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b License: \n
 * Licensed under BSD License {@link http://www.opensource.org/licenses/bsd-license.php}
 *
 * @version $Id$
 */

package org.idde.agenda.model;

import java.sql.Timestamp;

/**
 *
 * @author vilson
 */
public class Appointment
{
    private Integer id;
    private String what;
    private String where;
    private Timestamp dateFrom;
    private Timestamp timeFrom;
    private Timestamp dateTo;
    private Timestamp timeTo;
    private String description;
    private String userSent;

    public Appointment(String what, String where, Timestamp date_from, Timestamp date_to, String description, String author, Long id)
    {
        this.what = what;
        this.where = where;
        this.dateFrom = date_from;
        this.dateTo   = date_to;
        this.description = description;
        this.userSent = author;
        this.id = id.intValue();
    }

    public Appointment()
    {
    }

    /**
     * @return the what
     */
    public String getWhat()
    {
        return what;
    }

    /**
     * @param what the what to set
     */
    public void setWhat(String what)
    {
        this.what = what;
    }

    /**
     * @return the where
     */
    public String getWhere()
    {
        return where;
    }

    /**
     * @param where the where to set
     */
    public void setWhere(String where)
    {
        this.where = where;
    }

    /**
     * @return the dateFrom
     */
    public Timestamp getDateFrom()
    {
        return dateFrom;
    }

    /**
     * @param dateFrom the dateFrom to set
     */
    public void setDateFrom(Timestamp dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    /**
     * @return the timeFrom
     */
    public Timestamp getTimeFrom()
    {
        return timeFrom;
    }

    /**
     * @param timeFrom the timeFrom to set
     */
    public void setTimeFrom(Timestamp timeFrom)
    {
        this.timeFrom = timeFrom;
    }

    /**
     * @return the dateTo
     */
    public Timestamp getDateTo()
    {
        return dateTo;
    }

    /**
     * @param dateTo the dateTo to set
     */
    public void setDateTo(Timestamp dateTo)
    {
        this.dateTo = dateTo;
    }

    /**
     * @return the timeTo
     */
    public Timestamp getTimeTo()
    {
        return timeTo;
    }

    /**
     * @param timeTo the timeTo to set
     */
    public void setTimeTo(Timestamp timeTo)
    {
        this.timeTo = timeTo;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the userAdded
     */
    public String getUserAdded()
    {
        return userSent;
    }

    /**
     * @param userAdded the userAdded to set
     */
    public void setUserAdded(String userAdded)
    {
        this.userSent = userAdded;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id)
    {
        this.id = id;
    }
}
