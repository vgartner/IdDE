/**
 * Task manager.
 *
 * \b Package: \n
 * org.idde.chat.view
 *
 * @see org.idde.task
 * @see org.idde.TaskFrame.java
 * @see org.idde.task.view
 *
 * @since Class created on 27/10/2010
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
package org.idde.task.view;

import java.util.Iterator;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.idde.task.model.Task;
import org.idde.util.Database;
import org.idde.util.Util;
import org.jivesoftware.smack.XMPPConnection;
import org.openide.util.Exceptions;
import org.tmatesoft.sqljet.core.SqlJetException;

public class TaskManagerFrame extends javax.swing.JFrame
{

    /**
     *
     */
    private static final long serialVersionUID = -7266858009982252487L;
    private XMPPConnection connection;

    public TaskManagerFrame()
    {
        initComponents();

        updateTableData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tasksTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnInsert = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setTitle("IdDE - Task Management");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("My Tasks");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(511, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        tasksTable.setAutoCreateRowSorter(true);
        tasksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Project", "Date Due", "Priority", "Description", "Author", "Id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tasksTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tasksTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tasksTable);
        tasksTable.getColumnModel().getColumn(4).setResizable(false);
        tasksTable.getColumnModel().getColumn(5).setResizable(false);

        btnInsert.setText("Insert");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(291, Short.MAX_VALUE)
                .addComponent(btnInsert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemove)
                .addGap(18, 18, 18)
                .addComponent(btnExit)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit)
                    .addComponent(btnRemove)
                    .addComponent(btnEdit)
                    .addComponent(btnInsert))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tasksTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tasksTableMouseClicked
    {//GEN-HEADEREND:event_tasksTableMouseClicked
        if (evt.getClickCount() == 2)
        {
            editTask();
        }
    }//GEN-LAST:event_tasksTableMouseClicked

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnInsertActionPerformed
    {//GEN-HEADEREND:event_btnInsertActionPerformed
        insertNewTask();
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnExitActionPerformed
    {//GEN-HEADEREND:event_btnExitActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnEditActionPerformed
    {//GEN-HEADEREND:event_btnEditActionPerformed
        editTask();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnRemoveActionPerformed
    {//GEN-HEADEREND:event_btnRemoveActionPerformed
        deleteTask();
    }//GEN-LAST:event_btnRemoveActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tasksTable;
    // End of variables declaration//GEN-END:variables

    private void editTask()
    {
        int row = tasksTable.getSelectedRow();

        // If no item was selected
        if (row == -1)
        {
            Util.showInformationMessage("You need to select a task in order to edit it.");
            return;
        }

        final String name     = tasksTable.getValueAt(row, 0).toString();
        final String project  = tasksTable.getValueAt(row, 1).toString();
        final String dateDue  = tasksTable.getValueAt(row, 2).toString();
        final String priority = tasksTable.getValueAt(row, 3).toString();
        final String descr    = tasksTable.getValueAt(row, 4).toString();
        final String author   = tasksTable.getValueAt(row, 5).toString();
        final String id       = tasksTable.getValueAt(row, 6).toString();

        TaskFrame tf = new TaskFrame(name, project, dateDue, priority, descr, author, Integer.parseInt(id), this);
        tf.setVisible(true);
    }

    private void insertNewTask()
    {
        TaskFrame tf = new TaskFrame(this);
        tf.setFunction(TaskFrame.INSERT);
        tf.setVisible(true);
    }

    public void updateTableData()
    {
        Set changes = null;
        try
        {
            changes = Database.getTasks();
        }
        catch (SqlJetException ex)
        {
            Exceptions.printStackTrace(ex);
        }

        Object[] objArray = null;
        Iterator i = null;

        if ( changes != null )
        {
            objArray = changes.toArray();
            i = changes.iterator();
        }

        int row = 0;

        String data[][] = {};
        String[] columnNames = {"Name", "Project", "Date Due", "Priority", "Description", "Author", "Id"};

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        tasksTable.setModel(model);

        TableColumn col;
        col = tasksTable.getColumnModel().getColumn(0);
        col.setPreferredWidth(110);
        col = tasksTable.getColumnModel().getColumn(4);
        col.setPreferredWidth(90);
        col = tasksTable.getColumnModel().getColumn(6);
        col.setPreferredWidth(5);

        for(int index=0; index < objArray.length ; index++)
        {
            System.out.println(objArray[index]);

            String name     = ((Task) (objArray[index])).getName();
            String project  = ((Task) (objArray[index])).getProject();
            String dateDue  = ((Task) (objArray[index])).getDateDue().toString();
            String priority = ((Task) (objArray[index])).getPriority();
            String descrip  = ((Task) (objArray[index])).getDescription();
            String author   = ((Task) (objArray[index])).getUserSent();
            String id       = ((Task) (objArray[index])).getId().toString();

            model.insertRow(row, new Object[] {name, project, dateDue, priority, descrip, author, id});
        }
    }

    private void deleteTask()
    {
        int row = tasksTable.getSelectedRow();

        // If no item was selected
        if (row == -1)
        {
            Util.showInformationMessage("You need to select a task in order to delete it.");
            return;
        }

        final String id = tasksTable.getValueAt(row, 6).toString();

        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record "+"("+id+")?", "IdDE - Delete Task", JOptionPane.YES_NO_OPTION) == 1)
        {
            return;
        }

        try
        {
            Database.deleteTask(Integer.parseInt(id));
            Util.showInformationMessage("Record deleted.");
            updateTableData();
        }
        catch (SqlJetException ex)
        {
            Util.showErrorMessage("Error deleting record ("+ id +"): " + ex.getMessage());
        }
    }
}