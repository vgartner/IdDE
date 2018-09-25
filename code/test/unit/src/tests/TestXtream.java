/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.thoughtworks.xstream.XStream;
import org.idde.common.transport.Instruction;
import org.idde.common.model.InstructionProtocol;

/**
 *
 * @author vilson
 */
public class TestXtream
{
    public static void main(String[] args)
    {
        String message = "<IdDE><code>2</code><arg0>File.java</arg0><arg1>2i</arg1><arg2>125</arg2><arg3>6</arg3><arg4>cabelo</arg4></IdDE>";
        XStream xstream = new XStream();

        xstream.alias("IdDE", Instruction.class);
        Instruction ins = (Instruction)xstream.fromXML(message);

        System.out.println("Code: "+ins.getCode());
        System.out.println("Objeto: "+ins.toString());

        Instruction instruc = new Instruction(InstructionProtocol.MSG_UPDATE_SHARED_TEXT, "File.java", "2i", "125", "6", "cabelo");

        String xml = xstream.toXML(instruc);
        System.out.println("xml: "+xml);

    }
}
