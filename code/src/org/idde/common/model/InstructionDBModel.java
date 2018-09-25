/**
 * Model to be used to insert log record in database
 *
 * \b Package: \n
 * org.idde.common.transport
 *
 * @see org.idde.common.Instruction
 *
 * @since Class created on 03/12/2010
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
package org.idde.common.model;

import java.sql.Timestamp;

public class InstructionDBModel
{
    private String fileName;
    private String instruction;
    private Integer offset;
    private Integer lenght;
    private String text;
    private Timestamp date_time;
    private String user;

    public InstructionDBModel()
    {

    }

    public InstructionDBModel(String fileName, String instruction, int offset, int lenght, String text, Timestamp date_time, String user)
    {
        this.fileName = fileName;
        this.instruction = instruction;
        this.offset = offset;
        this.lenght = lenght;
        this.text = text;
        this.date_time = date_time;
        this.user = user;
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
     * @return the instruction
     */
    public String getInstruction()
    {
        return instruction;
    }

    /**
     * @param instruction the instruction to set
     */
    public void setInstruction(String instruction)
    {
        this.instruction = instruction;
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

    /**
     * @return the date_time
     */
    public Timestamp getDateTime()
    {
        return date_time;
    }

    /**
     * @param date_time the date_time to set
     */
    public void setDateTime(Timestamp date_time)
    {
        this.date_time = date_time;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
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
}
