/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Description
 *
 * \b Package: \n
 * tests
 *
 * @see ???.???.???
 *
 * @since Class created on 18/03/2011
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

import java.util.HashMap;

/**
 *
 * @author vilson
 */
public class HashMapClone
{

    public static void main(String[] a)
    {
        HashMap map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        HashMap map2 = (HashMap) map.clone();
        System.out.println(map2);
    }
}
