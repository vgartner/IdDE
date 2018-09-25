/**
 * Description
 *
 * \b Package: \n
 * tests
 *
 * @see ???.???.???
 *
 * @since Class created on 03/02/2011
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
package tests;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.openide.util.Exceptions;

/**
 *
 * @author vilson
 */
public class write_to_file
{
    public static void main(String[] args)
    {
                FileWriter outFile;
        try
        {
            outFile = new FileWriter("c:\temp\teste_grava_arq.txt");
                PrintWriter out1 = new PrintWriter(outFile);


                BufferedWriter out = new BufferedWriter(new FileWriter("outfilename"));

                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
                out.write("<peers xmlns=\"http://peers.sourceforge.net\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://peers.sourceforge.net peers.xsd\">");
                out.write("<!-- File created and used by IdDE. DO NOT CHANGE!!! -->");
                out.write("<!-- Doubts? vgartner@gmail.com -->");
                out.write("<network>");
                out.write("    <interfaces>");
                out.write("      <interface id=\"eth1\">");
                out.write("        <name>Ethernet network interface</name>");
                out.write("        <address/>");
                out.write("      </interface>");
                out.write("    </interfaces>");
                out.write("  </network>");
                out.write("  <devices>");
                out.write("    <audio/>");
                out.write("    <video/>");
                out.write("  </devices>");
                out.write("  <sip>");
                out.write("    <profile>");
                out.write("      <userpart>which_username</userpart>");
                out.write("      <domain>sip.server</domain>");
                out.write("      <password>which_password</password>");
                out.write("      <outboundProxy/>");
                out.write("      <interface ref=\"eth1\"/>");
                out.write("      <port>6060</port>");
                out.write("    </profile>");
                out.write("  </sip>");
                out.write("  <codecs>");
                out.write("    <codec>");
                out.write("      <family>audio</family>");
                out.write("      <name>PCMU</name>");
                out.write("      <payloadType>0</payloadType>");
                out.write("    </codec>");
                out.write("    <mediaMode>echo</mediaMode>");
                out.write("    <mediaDebug>false</mediaDebug>");
                out.write("  </codecs>");
                out.write("  <rtp>");
                out.write("    <interface ref=\"eth1\"/>");
                out.write("    <port>8000</port>");
                out.write("  </rtp>");
                out.write("</peers>");
                out.flush();
                out.close();
        }
        catch (IOException ex)
        {
            Exceptions.printStackTrace(ex);
        }
                
    }
}
