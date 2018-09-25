/**
 * Description
 *
 * \b Package: \n
 * org.idde.task.model
 *
 * @see org.idde.task.view
 *
 * @since Class created on 01/11/2010
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
package org.idde.task.model;

import java.util.Date;
import java.sql.Timestamp;

/**
 *
 * @author vilson
 */
public class Task
{
    private Integer id;
    private String name;
    private String project;
    private Timestamp dateDue;
    private String priority;
    private String userSent;
    private String description;

    public Task()
    {
    }

    public Task(String name, String project, Timestamp dateDue, String priority, String userSent, String description, Long id)
    {
        this.name = name;
        this.project = project;
        this.dateDue = dateDue;
        this.priority = priority;
        this.userSent = userSent;
        this.description = description;
        this.id = id.intValue();
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the project
     */
    public String getProject()
    {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(String project)
    {
        this.project = project;
    }

    /**
     * @return the dateDue
     */
    public Date getDateDue()
    {
        return dateDue;
    }

    /**
     * @param dateDue the dateDue to set
     */
    public void setDateDue(Timestamp dateDue)
    {
        this.dateDue = dateDue;
    }

    /**
     * @return the priority
     */
    public String getPriority()
    {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getUserSent()
    {
        return userSent;
    }

    public void setUserSent(String userSent)
    {
        this.userSent = userSent;
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

    @Override
    public String toString()
    {
        return "Task{" + "name=" + name + ",project=" + project +
               ",dateDue=" + dateDue + ",priority=" + priority +
               ",description=" + description + '}';
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
