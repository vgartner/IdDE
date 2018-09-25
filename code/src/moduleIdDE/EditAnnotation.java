package moduleIdDE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openide.text.Annotation;
import org.openide.text.Line;

public final class EditAnnotation extends Annotation
{
    private final String strAnn;
    private final int lineNum;
    private static Set annotations = null;
//    private static EditAnnotation ann = null;

    public static EditAnnotation getAnnotation(String str, int lin)
    {
        String s2 = str;

        if ( annotations == null )
        {
            annotations = new HashSet();
            annotations.add(new AnnotationCl(lin, str));
        }
        else
        {
            Iterator i = annotations.iterator();
            Boolean found = false;

            while (i.hasNext())
            {
                AnnotationCl a = (AnnotationCl) i.next();

                if ( a.getLine() == lin  )
                {
                    found = true;
                    
                    String s1 = a.getStrAnn();
                    s2 = s1 + "\n" + str;
                    a.setStrAnn(s2);
                }
            }

            if ( ! found )
            {
                annotations.add(new AnnotationCl(lin, str));
            }
        }

        EditAnnotation ann = new EditAnnotation(s2, lin);
        return ann;
    }

    public static String getDescription(int line)
    {
        Iterator i = annotations.iterator();

        String rs = null;

        while (i.hasNext())
        {
            AnnotationCl a = (AnnotationCl) i.next();

            if ( a.getLine() == line  )
            {
                rs = a.getStrAnn();
            }
        }

        return rs;
    }

    /**
     *
     * @param author
     */
    public EditAnnotation(String author, int lineNum)
    {
        this.strAnn = author;
        this.lineNum = lineNum;
    }

    @Override
    public String getAnnotationType()
    {
        return "moduleIdDE";
    }

    @Override
    public String getShortDescription()
    {
//        return "IdDE change by " + strAnn;

        String descr = EditAnnotation.getDescription(this.lineNum);
        
        return descr;
    }
}
