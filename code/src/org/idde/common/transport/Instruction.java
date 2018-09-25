/**
 * Description
 *
 * \b Package: \n
 * org.idde.common.transport
 *
 * @see ???.???.???
 *
 * @since Class created on 15/06/2010
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

package org.idde.common.transport;

/**
 * This class is used for the instructions which are sent from one user to another.
 *
 * @author vilson
 */
public class Instruction
{
    private String code;
    private String arg0;
    private String arg1;
    private String arg2;
    private String arg3;
    private String arg4;
    private String arg5;
    
    /**
     *
     * @param code
     * @param args
     */
    public Instruction(String code, String... args)
    {
        this.code = code;

        if ( args.length > 0 )
        {
            this.arg0 = args[0];
        }

//        System.out.println("Comprimento "+args.length);

        if ( args.length >= 2 )
        {
            this.arg1 = args[1];
        }

        if ( args.length >= 3 )
        {
            this.arg2 = args[2];
        }

        if ( args.length >= 4 )
        {
            this.arg3 = args[3];
        }

        if ( args.length >= 5 )
        {
            this.arg4 = args[4];
        }

        if ( args.length >= 6 )
        {
            this.arg5 = args[5];
        }
    }

    /**
     * Get the first argument instruction. This value changes depending the instruction.
     * 
     * @return arg0
     */
    public String getArg0()
    {
        return arg0;
    }

    public void setArg0(String arg0)
    {
        this.arg0 = arg0;
    }

    public String getArg1()
    {
        return arg1;
    }

    public void setArg1(String arg1)
    {
        this.arg1 = arg1;
    }

    public String getArg2()
    {
        return arg2;
    }

    public void setArg2(String arg2)
    {
        this.arg2 = arg2;
    }

    public String getArg3()
    {
        return arg3;
    }

    public void setArg3(String arg3)
    {
        this.arg3 = arg3;
    }

    public String getArg4()
    {
        return arg4;
    }

    public void setArg4(String arg4)
    {
        this.arg4 = arg4;
    }

    public String getArg5()
    {
        return arg5;
    }

    public void setArg5(String arg5)
    {
        this.arg5 = arg5;
    }

    /**
     * Get the instruction's code. This code is defined in Insctruction class
     *
     * @return String code of the instruction
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Set the code of the instruction
     *
     * @param code
     */
    public void setCode(String code)
    {
        this.code = code;
    }

//    public String getAsXML()
//    {
//        String result;
//
//        result = "<" + InstructionProtocol.IdDE_ID + ">" +
//                 "<code>" + this.code + "</code>"+
//                 "<arg0>" + this.arg0 + "</arg0>";
//
//        if ( this.arg1 != null )
//        {
//            result += "<arg1>" + this.arg1 + "</arg1>";
//        }
//
//        if ( this.arg2 != null )
//        {
//            result += "<arg2>" + this.arg2 + "</arg2>";
//        }
//
//        if ( this.arg3 != null )
//        {
//            result += "<arg3>" + this.arg3 + "</arg3>";
//        }
//
//        if ( this.arg4 != null )
//        {
//            result += "<arg4>" + this.arg4 + "</arg4>";
//        }
//
//        if ( this.arg5 != null )
//        {
//            result += "<arg5>" + this.arg5 + "</arg5>";
//        }
//
//        result += "</" + InstructionProtocol.IdDE_ID + ">";
//
//        return result;
//    }

    @Override
    public String toString()
    {
        return "Message{" + "code=" + code + "arg0=" + arg0 + "arg1=" + arg1 + "arg2=" + arg2 + "arg3=" + arg3 + "arg4=" + arg4 + "arg5=" + arg5 + '}';
    }



}
