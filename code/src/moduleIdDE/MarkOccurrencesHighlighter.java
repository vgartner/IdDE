package moduleIdDE;

import com.thoughtworks.xstream.XStream;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import org.idde.common.transport.Instruction;
import org.idde.common.model.InstructionProtocol;
import org.idde.editor.controller.SessionControl;
import org.idde.util.Util;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;

public class MarkOccurrencesHighlighter implements CaretListener, KeyListener
{

    private static final AttributeSet defaultColors =
            AttributesUtilities.createImmutable(StyleConstants.Background,
            new Color(236, 235, 163));
    private final OffsetsBag bag;
    private JTextComponent comp;
    private final WeakReference<Document> weakDoc;

    /**
     * Used to control caret position on each doc
     */
    private static Set caretPositions = null;

    public static Set getCaretPositions()
    {
        if (caretPositions == null)
        {
            caretPositions = new HashSet();
        }

        return caretPositions;
    }


    public MarkOccurrencesHighlighter(Document doc)
    {
        bag = new OffsetsBag(doc);
        weakDoc = new WeakReference<Document>((Document) doc);
        DataObject dobj = NbEditorUtilities.getDataObject(weakDoc.get());

        try
        {
            EditorCookie pane = dobj.getCookie(EditorCookie.class);
            JEditorPane[] panes = pane.getOpenedPanes();

            if (panes != null && panes.length > 0)
            {
                comp = panes[0];

                // KeyListener is working, but is comented out
                // cause we don't need them now...
                // Future plans... ;-)
//                comp.addKeyListener(this);

                // Caret listener to show remote user annotation
                comp.addCaretListener(this);
                // Document listener, which takes care to notify remote users
                doc.addDocumentListener(DocumentChangesListener.getDefaultListener());

                System.out.println("[IdDE] Adding listener to: " + comp.toString());
            }
        }
        catch (Exception e)
        {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    /**
     * TODO: create class DocumentCaretListener
     * @param e
     */
    @Override
    public void caretUpdate(CaretEvent e)
    {
//        bag.clear();
//        scheduleUpdate();

        // Here, we update the position anotation on remote IdDEs
        String filePath = (String) comp.getDocument().getProperty(Document.TitleProperty);
        String fileName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

        int caretPosition = comp.getCaretPosition();
        Element root = comp.getDocument().getDefaultRootElement();
        int line = root.getElementIndex( caretPosition ) + 1;

//        System.out.println("New Caret position: " + Integer.toString(line));

//        Util.insertCaretPosAnnotation(comp.getDocument(), comp.getCaretPosition(), "local_user");

        // if nobody is sharing this file, no message needs to be send
        if ( SessionControl.getSharedUsers(fileName).isEmpty() )
        {
            return;
        }

        // verify which was the las line. If it's the same, ignore the caret change
        // this occurs, for example, when the user types in a char, col changes and so do the caret position
        if ( ! checkLastLine(line, fileName) )
        {
            return;
        }

        // Send message to remote
        // Used to convert object to xml string
        XStream xstream = new XStream();
        xstream.alias(InstructionProtocol.IdDE_ID, Instruction.class);
        
        Instruction i = new Instruction(InstructionProtocol.MSG_UPDATE_CARET_POSITION, fileName,
                                        Integer.toString(comp.getCaretPosition()));

        // Add message to buffer. SessionControl takes care to send it to other parties
        SessionControl.addMessageToBuffer(fileName, xstream.toXML(i));
    }

    private RequestProcessor.Task task = null;
    private final static int DELAY = 100;

    public void scheduleUpdate()
    {
        if (task == null)
        {
            task = RequestProcessor.getDefault().create(new Runnable()
            {

                public void run()
                {
                    String selection = comp.getSelectedText();
                    System.out.println("Executando: scheduleUpdate.run na selecao: " + selection);
                    
                    if (selection != null)
                    {
                        Pattern p = Pattern.compile(selection);
                        Matcher m = p.matcher(comp.getText());
                        
                        while (m.find() == true)
                        {
                            int startOffset = m.start();
                            int endOffset = m.end();
                            bag.addHighlight(startOffset, endOffset, defaultColors);
                        }
                    }
                }
            }, true);
            task.setPriority(Thread.MIN_PRIORITY);
        }
        task.cancel();
        task.schedule(DELAY);
    }

    public OffsetsBag getHighlightsBag()
    {
        return bag;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        System.out.println("keyPressed char -> " + e.getKeyChar());
        System.out.println("keyPressed code -> " + e.getKeyCode());
        System.out.println("keyPressed text -> " + e.getKeyText(e.getKeyCode()));
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
//        System.out.println( "keyPressed char -> " + e.getKeyChar() );
//        System.out.println( "keyPressed code -> " + e.getKeyCode() );
//        System.out.println( "keyPressed text -> " + e.getKeyText(e.getKeyCode()) );
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
//        System.out.println( "keyReleased -> " + e.getKeyChar() );
    }

    /**
     * Verify the last caret's line to see if it is the same
     * @param line line to check
     * @param fileName filename to check
     * @return true if it's a new line (last line was another one)
     */
    private boolean checkLastLine(int line, String fileName)
    {
        Iterator i = getCaretPositions().iterator();
        CaretPositionObj cpo;
        boolean found = false;
        boolean result = true;

        while (i.hasNext())
        {
            cpo = (CaretPositionObj) i.next();

            // if the file was found, let's check the las line number
            if ( cpo.getFileName().equals(fileName) )
            {
                // we have found the file in set
                found = true;

                if ( cpo.getLine() == line )
                {
                    // it's the same line
                    result = false;
                }
                else
                {
                    // set the new line
                    cpo.setLine(line);
                }
            }
        }

        if ( ! found )
        {
            getCaretPositions().add(new CaretPositionObj(fileName, line));
        }

        return result;
    }
}

class CaretPositionObj
{
    private String fileName;
    private Integer line;

    public CaretPositionObj(String fileName, Integer line)
    {
        this.fileName = fileName;
        this.line = line;
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
     * @return the line
     */
    public Integer getLine()
    {
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(Integer line)
    {
        this.line = line;
    }

    @Override
    public String toString()
    {
        return "CaretPositionObj{" + "fileName=" + fileName + "line=" + line + '}';
    }


}
