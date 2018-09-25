/**
 * Description
 *
 * \b Package: \n
 * moduleIdDE
 *
 * @see moduleIdDE.EditAnnotation
 *
 * @since Class created on 04/12/2010
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
package moduleIdDE;

/**
 *
 * @author vilson
 */
public class AnnotationCl
{
    private int line;
    private String strAnn;

    public AnnotationCl(int line, String strAnn)
    {
        this.line = line;
        this.strAnn = strAnn;
    }

    /**
     * @return the line
     */
    public int getLine()
    {
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line)
    {
        this.line = line;
    }

    /**
     * @return the strAnn
     */
    public String getStrAnn()
    {
        return strAnn;
    }

    /**
     * @param strAnn the strAnn to set
     */
    public void setStrAnn(String strAnn)
    {
        this.strAnn = strAnn;
    }

}
