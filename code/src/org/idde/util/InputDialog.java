/**
 * Description
 *
 * \b Package: \n
 * org.idde.util
 *
 * @see ???.???.???
 *
 * @since Class created on 10/11/2010
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
package org.idde.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author vilson
 */
public class InputDialog extends JFrame
{

    JLabel lbl = new JLabel("Text entered = ");

    public InputDialog()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocation(200, 100);
        JPanel main = new JPanel(new BorderLayout());
        main.add(lbl, BorderLayout.CENTER);
        JButton btn = new JButton("Get some text");
        btn.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                lbl.setText("<html>Text entered = <br>"
                        + new MyJOptionPane().showInputDialog("Enter some text").replaceAll("\\n", "<br>")
                        + "</html>");
            }
        });
        main.add(btn, BorderLayout.SOUTH);
        getContentPane().add(main);
    }

    public static void main(String[] args)
    {
        new InputDialog().setVisible(true);
    }
}

class MyJOptionPane extends JOptionPane
{

    public static String showInputDialog(final String message)
    {
        String data = null;
        class GetData extends JDialog implements ActionListener
        {

            JTextArea ta = new JTextArea(5, 10);
            JButton btnOK = new JButton("   OK   ");
            JButton btnCancel = new JButton("Cancel");
            String str = null;

            public GetData()
            {
                setModal(true);
                getContentPane().setLayout(new BorderLayout());
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                setLocation(400, 300);
                getContentPane().add(new JLabel(message), BorderLayout.NORTH);
                getContentPane().add(ta, BorderLayout.CENTER);
                JPanel jp = new JPanel();
                btnOK.addActionListener(this);
                btnCancel.addActionListener(this);
                jp.add(btnOK);
                jp.add(btnCancel);
                getContentPane().add(jp, BorderLayout.SOUTH);
                pack();
                setVisible(true);
            }

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                if (ae.getSource() == btnOK)
                {
                    str = ta.getText();
                }
                dispose();
            }

            public String getData()
            {
                return str;
            }
        }
        data = new GetData().getData();
        return data;
    }
}
