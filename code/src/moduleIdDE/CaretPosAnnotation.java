package moduleIdDE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.text.Annotation;
import org.openide.text.Line;

public final class CaretPosAnnotation extends Annotation
{

    /*
     * 
     */
    private final String remoteUser;
    private final int offSet;
    private static Set annotations = null;

    public static Annotation getAnnotation(Document doc, int offset, String remoteName)
    {
//        CaretPosAnnotation ann = new CaretPosAnnotation(offset, userName);
//        return ann;
        CaretPosAnnotation ann = new CaretPosAnnotation(offset, remoteName);

        Line line = NbEditorUtilities.getLine(doc, offset, false);

        if ( annotations == null )
        {
            annotations = new HashSet();
//            annotations.add(ann);
        }
        else
        {
            Iterator i = annotations.iterator();
            Boolean found = false;

            while (i.hasNext())
            {
                CaretPosAnnotation cpa = (CaretPosAnnotation) i.next();

                // if the remote is the same, detach existing annotation
                if (cpa.getRemoteUserName().equalsIgnoreCase(remoteName) )
                {
                    found = true;
                    annotations.remove(cpa);
                    cpa.detach();
                    cpa.notifyDetached(line);
                }
            }

            // if not found, then it's a new user annotation
            if (! found)
            {
//                //add annotation
//                ann.attach(line);
//                annotations.add(ann);
            }
        }
                //add annotation
                ann.attach(line);
                ann.moveToFront();
                annotations.add(ann);

        return ann;
    }

    /**
     *
     * @param author
     * @param lineNum
     */
    public CaretPosAnnotation(int offset, String userName)
    {
        this.remoteUser = userName;
        this.offSet = offset;
    }

    public String getRemoteUserName()
    {
        return this.remoteUser;
    }

    @Override
    public String getAnnotationType()
    {
        return "moduleIdDECaretPos";
    }

    @Override
    public String getShortDescription()
    {
        return "Remote user: " + remoteUser;
    }
}
