/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.io.IOException;
import java.util.Iterator;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;

/**
 *
 * @author vilson
 */
public class OpenTabsNetbeans
{
    public static void main(String[] args)
    {
        System.out.print("Iniciando... pressione algo..." + "\n");
        try
        {
            System.in.read();
        }
        catch (IOException ex)
        {
            Exceptions.printStackTrace(ex);
        }

        Iterator i = org.netbeans.api.editor.EditorRegistry.componentList().listIterator();

        while (i.hasNext())
        {
            JTextComponent ed = (JTextComponent) i.next();
            Document doc = ed.getDocument();

            String filePath = (String) doc.getProperty(Document.TitleProperty);

            System.out.print("Arquivo: " + filePath + "\n");
        }
    }
}
