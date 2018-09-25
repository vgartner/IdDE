/**
 * Database related functions.
 *
 * \b Package: \n
 * org.idde.util
 *
 * @see org.idde.util
 *
 * @since Class created on 02/12/2010
 *
 * @author Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b Maintainers: \n
 * Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b License: \n
 * Licensed under BSD License {@link http://www.opensource.org/licenses/bsd-license.php}
 * Many ideas and code are based on shortalk {@link http://code.google.com/p/shortalk/}
 *
 * @version $Id$
 */
// Exemplos SQLJet:
// http://svn.sqljet.com/repos/sqljet/trunk/sqljet-examples/inventory/src/org/tmatesoft/sqljet/examples/inventory/
//
// @TODO Avaliar o projeto H2: http://www.h2database.com/html/main.html
package org.idde.util;

import java.io.File;
import java.util.HashSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.idde.agenda.model.Appointment;
import org.idde.common.model.InstructionDBModel;
import org.idde.task.model.Task;
import org.openide.util.Exceptions;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 *
 * @author vilson
 */
public class Database
{

    private static Boolean disableLog = false;
    private static String DB_NAME = Util.dbDirectory + File.separator + "idde.sqlite";
    private static String TABLE_TASKS = "tasks";
    private static String TABLE_SESSIONS = "sessions";
    private static String TABLE_AGENDA = "agenda";
    private static SqlJetDb dbConnection = null;

    public static void initables() throws SqlJetException
    {
        Boolean create = false;

        if (!new File(DB_NAME).exists())
        {
            create = true;
        }

        dbConnection = getConnection(DB_NAME, true, create);

        System.out.println(dbConnection.getSchema().toString());

        if (dbConnection.getSchema().getTable(TABLE_SESSIONS) == null)
        {
            System.out.println("IdDE: Creating table '" + TABLE_SESSIONS + "'...");
            Util.setStatus("IdDE: Creating table '" + TABLE_SESSIONS + "'...");

            dbConnection.beginTransaction(SqlJetTransactionMode.WRITE);
            try
            {
                String stmt = "CREATE TABLE " + TABLE_SESSIONS + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "filename VARCHAR(50), "
                        + "instruction VARCHAR(3), "
                        + "off_set VARCHAR(7), "
                        + "lenght VARCHAR(7), "
                        + "string text, "
                        //                        + "date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "date_time VARCHAR(15), "
                        + "author VARCHAR(15)"
                        + ")";

                dbConnection.createTable(stmt);
                dbConnection.createIndex("CREATE INDEX idx_" + TABLE_SESSIONS + "_filename ON " + TABLE_SESSIONS + "(filename)");
                dbConnection.createIndex("CREATE INDEX idx_" + TABLE_SESSIONS + "_date_time ON " + TABLE_SESSIONS + "(date_time)");
            }
//            catch (SqlJetException ex1)
            finally
            {
                dbConnection.commit();
//                Util.showErrorMessage("Error creating '" + TABLE_SESSIONS + "' table:" + ex1.getMessage() + "\n" + ex.toString());
            }
        }

        if (dbConnection.getSchema().getTable(TABLE_TASKS) == null)
        {
            System.out.println("Creating table '" + TABLE_TASKS + "'...");
            Util.setStatus("Creating table '" + TABLE_TASKS + "'...");

            dbConnection.beginTransaction(SqlJetTransactionMode.WRITE);
            try
            {
                String stmt = "CREATE TABLE " + TABLE_TASKS + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "name VARCHAR(50), "
                        + "project VARCHAR(50), "
                        + "date_due VARCHAR(25), "
                        + "priority VARCHAR(15), "
                        + "user_sent VARCHAR(25), "
                        + "description text"
                        + ")";

                dbConnection.createTable(stmt);
                dbConnection.createIndex("CREATE INDEX idx_" + TABLE_TASKS + "_name ON " + TABLE_TASKS + "(name)");
                dbConnection.createIndex("CREATE INDEX idx_" + TABLE_TASKS + "_date_due ON " + TABLE_TASKS + "(date_due)");
            }
//            catch (SqlJetException ex1)
            finally
            {
                dbConnection.commit();

                //Util.showErrorMessage("Error creating '" + TABLE_TASKS + "' table:" + ex1.getMessage() + "\n" + ex.toString());
            }

        }

        if (dbConnection.getSchema().getTable(TABLE_AGENDA) == null)
        {
            System.out.println("Creating table '" + TABLE_AGENDA + "'...");
            Util.setStatus("Creating table '" + TABLE_AGENDA + "'...");

            dbConnection.beginTransaction(SqlJetTransactionMode.WRITE);
            try
            {
                String stmt = "CREATE TABLE " + TABLE_AGENDA + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "what VARCHAR(50), "
                        + "where_ VARCHAR(50), "
                        + "date_from VARCHAR(25), "
                        + "date_to VARCHAR(25), "
                        + "user_sent VARCHAR(25), "
                        + "description text"
                        + ")";

                dbConnection.createTable(stmt);
                dbConnection.createIndex("CREATE INDEX idx_" + TABLE_AGENDA + "_what ON " + TABLE_AGENDA + "(what)");
                dbConnection.createIndex("CREATE INDEX idx_" + TABLE_AGENDA + "_date_from ON " + TABLE_AGENDA + "(date_from)");
            }
//            catch (SqlJetException ex1)
            finally
            {
                dbConnection.commit();

                //Util.showErrorMessage("Error creating '" + TABLE_TASKS + "' table:" + ex1.getMessage() + "\n" + ex.toString());
            }
        }

        //dbConnection.close();
    }

    public static void insertSessionInstruction(InstructionDBModel instruction) throws SqlJetException
    {
        if (isDisabledLogInstruction())
        {
            return;
        }

        SqlJetDb db = getConnection(DB_NAME, true, false);
        db.beginTransaction(SqlJetTransactionMode.WRITE);

        try
        {
            ISqlJetTable table = db.getTable(TABLE_SESSIONS);

//            String sql = "insert into sessions (filename, instruction, offset, lenght, string, date_time, author) "
//                       + "values (?,?,?,?,?,?,?)";
            Hashtable vals = new Hashtable();

            vals.put("filename", instruction.getFileName());
            vals.put("instruction", instruction.getInstruction());
            vals.put("off_set", instruction.getOffset());
            vals.put("lenght", instruction.getLenght());
            vals.put("string", instruction.getText());
//            vals.put("date_time", Util.convertTimestampToString(instruction.getDateTime()) );
            vals.put("date_time", instruction.getDateTime().toString());
            vals.put("author", instruction.getUser());

            table.insertByFieldNames(vals);

//            table.insert(instruction.getFileName(), instruction.getInstruction(), instruction.getOffset(),
//                    instruction.getLenght(), instruction.getText(), instruction.getDateTime(), instruction.getUser());
        }
        finally
        {
            db.commit();
        }

//        db.close();
    }

    public static HashSet<InstructionDBModel> getSessionInstructions(String fileName) throws SqlJetException
    {
        HashSet<InstructionDBModel> instructions = new HashSet<InstructionDBModel>();

        SqlJetDb db = getConnection(DB_NAME, true, false);

        try
        {
            ISqlJetTable table = db.getTable(TABLE_SESSIONS);
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);

            ISqlJetCursor cursor = table.lookup("idx_sessions_filename", fileName);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));
            table.lookup("idx_sessions_date_time", new Object[]
                    {
                        calendar.getTimeInMillis()
                    });
            table.order("idx_sessions_date_time");

//select *
//  from sessions
// where date(date_time) = date('now')

            try
            {
                if (!cursor.eof())
                {
                    // sql = "SELECT filename, instruction, offset, lenght, string, date_time, author FROM sessions where filename=?";
                    Timestamp timestamp = null;

                    do
                    {
                        try
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            java.util.Date date = sdf.parse(cursor.getString("date_time"));
                            timestamp = new java.sql.Timestamp(date.getTime());
                        }
                        catch (Exception ex)
                        {
                            Exceptions.printStackTrace(ex);
                        }

                        instructions.add(new InstructionDBModel(cursor.getString("filename"),
                                cursor.getString("instruction"),
                                Integer.parseInt(cursor.getString("off_set").trim()),
                                Integer.parseInt(cursor.getString("lenght").trim()),
                                cursor.getString("string"),
                                timestamp,
                                cursor.getString("author")));
                    }
                    while (cursor.next());
                }
            }
            finally
            {
                cursor.close();
            }

        }
        finally
        {
            db.commit();
        }

//        db.close();

        return instructions;
    }

    public static void insertTask(Task task) throws SqlJetException
    {
        SqlJetDb db = getConnection(DB_NAME, true, false);
        db.beginTransaction(SqlJetTransactionMode.WRITE);

        try
        {
            ISqlJetTable table = db.getTable(TABLE_TASKS);

//          String sql = "insert into tasks (name, project, date_due, priority, user_sent, description) "
//                     + "values (?,?,?,?,?,?)";

            Hashtable vals = new Hashtable();

            vals.put("name", task.getName());
            vals.put("project", task.getProject());
            vals.put("date_due", task.getDateDue().toString());
            vals.put("priority", task.getPriority());
            vals.put("user_sent", task.getUserSent());
//            vals.put("date_time", Util.convertTimestampToString(instruction.getDateTime()) );
            vals.put("description", task.getDescription());

            table.insertByFieldNames(vals);

//            table.insert(task.getName(), task.getProject(), new Date(task.getDateDue().getTime()),
//                    task.getPriority(), task.getUserSent(), task.getDescription());
        }
        finally
        {
            db.commit();
        }

//        db.close();
    }

    public static HashSet<Task> getTasks() throws SqlJetException
    {
        HashSet<Task> tasks = new HashSet<Task>();
        SqlJetDb db = getConnection(DB_NAME, true, false);

        try
        {
            ISqlJetTable table = db.getTable(TABLE_TASKS);
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            table.order("idx_tasks_date_due");

            ISqlJetCursor cursor = table.lookup(table.getPrimaryKeyIndexName());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));

//            table.order("idx_tasks_date_time");

            try
            {
                if (!cursor.eof())
                {
                    // sql = "SELECT name, project, date_due, priority, user_sent, description FROM tasks");
                    Timestamp dateDue = null;

                    do
                    {
                        try
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            java.util.Date date = sdf.parse(cursor.getString("date_due"));
                            dateDue = new java.sql.Timestamp(date.getTime());
                        }
                        catch (Exception ex)
                        {
                            Exceptions.printStackTrace(ex);
                        }

                        tasks.add(new Task(cursor.getString("name"),
                                cursor.getString("project"),
                                dateDue,
                                cursor.getString("priority"),
                                cursor.getString("user_sent"),
                                cursor.getString("description"),
                                cursor.getInteger("id")));
                    }
                    while (cursor.next());
                }
            }
            finally
            {
                cursor.close();
            }
        }
        finally
        {
            db.commit();
        }

//        db.close();

        return tasks;
    }

    /**
     * Insert appointment into database
     * 
     * @param app Appointment to be added
     * 
     * @throws SqlJetException
     */
    public static void insertAppointment(Appointment app) throws SqlJetException
    {
        SqlJetDb db = getConnection(DB_NAME, true, false);
        db.beginTransaction(SqlJetTransactionMode.WRITE);

        try
        {
            ISqlJetTable table = db.getTable(TABLE_AGENDA);

            Hashtable vals = new Hashtable();

            vals.put("what", app.getWhat());
            vals.put("where_", app.getWhere());
            vals.put("date_from", app.getDateFrom().toString());
            vals.put("date_to", app.getDateTo().toString());
            vals.put("user_sent", app.getUserAdded());
            vals.put("description", app.getDescription());

            table.insertByFieldNames(vals);
        }
        finally
        {
            db.commit();
        }

//        db.close();
    }

    public static void updateAppointment(Appointment app) throws SqlJetException
    {
        final Map<String, Object> vals = new HashMap<String, Object>();
        final Integer id = app.getId();

        vals.put("what", app.getWhat());
        vals.put("where_", app.getWhere());
        vals.put("date_from", app.getDateFrom().toString());
        vals.put("date_to", app.getDateTo().toString());
        vals.put("user_sent", app.getUserAdded());
        vals.put("description", app.getDescription());

        SqlJetDb db = getConnection(DB_NAME, true, false);
        db.beginTransaction(SqlJetTransactionMode.WRITE);

        db.runWriteTransaction(new ISqlJetTransaction()
        {
            @Override
            public Object run(SqlJetDb db) throws SqlJetException
            {
                ISqlJetTable users = db.getTable(TABLE_AGENDA);
                ISqlJetCursor cursor = users.lookup(users.getPrimaryKeyIndexName(), id);
                try
                {
                    if ( ! cursor.eof() )
                    {
                        cursor.updateByFieldNames(vals);
                    }
                }
                finally
                {
                    cursor.close();
                }
                return null;
            }
        });

        db.commit();

//        SqlJetDb db = getConnection(DB_NAME, true, false);
//
//        ISqlJetTable table = db.getTable(TABLE_AGENDA);
//        db.beginTransaction(SqlJetTransactionMode.WRITE);
//
//        try
//        {
//            table.lookup(table.getPrimaryKeyIndexName(), new Object[]
//                    {
//                        a.getId()
//                    });
//
//            ISqlJetCursor updateCursor = table.open();
//
//            do
//            {
//                updateCursor.update(
//                        updateCursor.getValue("id"),
//                        a.getWhat(),
//                        a.getWhere(),
//                        a.getDateFrom(),
//                        a.getDateTo(),
//                        updateCursor.getValue("user_sent"),
//                        a.getDescription());
//            }
//            while (updateCursor.next());
//
//            updateCursor.close();
//        }
//        finally
//        {
//            db.commit();
//        }
    }

    public static void deleteAppointment(Integer id) throws SqlJetException
    {
        SqlJetDb db = getConnection(DB_NAME, true, false);
        db.beginTransaction(SqlJetTransactionMode.WRITE);

        ISqlJetTable table = db.getTable(TABLE_AGENDA);
        ISqlJetCursor cursor = table.lookup(table.getPrimaryKeyIndexName(), id);

        try
        {
            if (!cursor.eof())
            {
                cursor.delete();
            }
        }
        finally
        {
            cursor.close();
            db.commit();
        }

        return;

//        ISqlJetCursor cursor = db.getTable(TABLE_AGENDA).open();
//        db.beginTransaction(SqlJetTransactionMode.WRITE);
//
//        try
//        {
//            if (cursor.goTo(id))
//            {
//                cursor.delete();
//            }
//        }
//        finally
//        {
//            cursor.close();
//            db.commit();
//        }
//
//        return;

//        try
//        {
//            ISqlJetCursor deleteCursor = table.scope(table.getPrimaryKeyIndexName(),
//                    new Object[]
//                    {
//                        Long.MIN_VALUE
//                    }, new Object[]
//                    {
//                        id
//                    });
//            while (!deleteCursor.eof())
//            {
//                deleteCursor.delete();
//            }
//            deleteCursor.close();
//        }
//        finally
//        {
//            db.commit();
//        }

    }

    /**
     * Gel all appointments
     *
     * @return HashSet of appointments
     * @throws SqlJetException
     */
    public static HashSet<Appointment> getAppointments() throws SqlJetException
    {
        HashSet<Appointment> appointments = new HashSet<Appointment>();

        SqlJetDb db = getConnection(DB_NAME, true, false);

        try
        {
            ISqlJetTable table = db.getTable(TABLE_AGENDA);
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            table.order("idx_agenda_date_from");

            ISqlJetCursor cursor = table.lookup(table.getPrimaryKeyIndexName());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));

//            table.order("idx_tasks_date_time");

            try
            {
                if (!cursor.eof())
                {
                    Timestamp dateFrom = null;
                    Timestamp dateTo = null;

                    do
                    {
                        try
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                            java.util.Date date = sdf.parse(cursor.getString("date_from"));
                            dateFrom = new java.sql.Timestamp(date.getTime());

                            date = sdf.parse(cursor.getString("date_to"));
                            dateTo = new java.sql.Timestamp(date.getTime());
                        }
                        catch (Exception ex)
                        {
                            Exceptions.printStackTrace(ex);
                        }

                        appointments.add(new Appointment(cursor.getString("what"),
                                cursor.getString("where_"),
                                dateFrom,
                                dateTo,
                                cursor.getString("description"),
                                cursor.getString("user_sent"),
                                cursor.getInteger("id")));
                    }
                    while (cursor.next());
                }
            }
            finally
            {
                cursor.close();
            }
        }
        finally
        {
            db.commit();
        }

//        db.close();

        return appointments;
    }

    /**
     * Returns a connnection to given SQLite dbFile
     * @param dbFile SQLite file
     *
     * @return A object which can be used to execute SQL instructions
     */
    public static SqlJetDb getConnection(String dbFile, Boolean writeMode, Boolean create) throws SqlJetException
    {
        if (dbConnection != null)
        {
            return dbConnection;
        }

        File file = new File(dbFile);
        SqlJetDb db = SqlJetDb.open(file, writeMode);

        if (create)
        {
            // set DB option that have to be set before running any transactions:
            db.getOptions().setAutovacuum(true);
        }

        // set DB option that have to be set in a transaction:
        db.runTransaction(new ISqlJetTransaction()
        {

            @Override
            public Object run(SqlJetDb db) throws SqlJetException
            {
                db.getOptions().setUserVersion(1);
                return true;
            }
        }, SqlJetTransactionMode.WRITE);

        // Set the static connection var
        dbConnection = db;

        return db;
    }

    /**
     * @return the disableLog
     */
    public static Boolean isDisabledLogInstruction()
    {
        return disableLog;
    }

    /**
     * @param aDisableLog the disableLog to set
     */
    public static void setDisableLogInstruction(Boolean aDisableLog)
    {
        disableLog = aDisableLog;
    }

    private static void verifyTasksTable() throws SqlJetException
    {
        final SqlJetDb db = SqlJetDb.open(new File(DB_NAME), true);

        if (db.getSchema().getTable(TABLE_TASKS) == null)
        {
            System.out.println("Creating table '" + TABLE_TASKS + "'...");

            db.runWriteTransaction(new ISqlJetTransaction()
            {

                public Object run(SqlJetDb arg0) throws SqlJetException
                {
                    String table = "CREATE TABLE " + TABLE_TASKS + " ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "name VARCHAR(50), "
                            + "project VARCHAR(50), "
                            + "date_due DATE, "
                            + "priority VARCHAR(15), "
                            + "description text"
                            + ")";

                    String index = "CREATE INDEX idx_name ON " + TABLE_SESSIONS + "(name)";

                    db.createTable(table);
                    db.createIndex(index);

                    return null;
                }
            });
        }

//        db.close();

//        SqlJetDb db = getConnection(DB_NAME, true);
//        ISqlJetTable table;
//
//        try
//        {
//            db.getTable(TABLE_TASKS);
//
//        }
//        catch (SqlJetException ex)
//        {
//            System.out.println("Creating table '" + TABLE_TASKS + "'...");
//
//            db.beginTransaction(SqlJetTransactionMode.WRITE);
//            try
//            {
//                String stmt = "CREATE TABLE " + TABLE_TASKS + " ("
//                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
//                        + "name VARCHAR(50), "
//                        + "project VARCHAR(50), "
//                        + "date_due DATE, "
//                        + "priority VARCHAR(15), "
//                        + "description text"
//                        + ")";
//
//                db.createTable(stmt);
//                db.createIndex("CREATE INDEX idx_name ON " + TABLE_SESSIONS + "(name)");
//            }
////            catch (SqlJetException ex1)
//            finally
//            {
//                db.commit();
//
//                //Util.showErrorMessage("Error creating '" + TABLE_TASKS + "' table:" + ex1.getMessage() + "\n" + ex.toString());
//            }
//        }
//
//        db.close();

    }

    private static void verifySessionsTable() throws SqlJetException
    {
        SqlJetDb db = getConnection(DB_NAME, true, false);
        ISqlJetTable table;

        try
        {
            table = db.getTable(TABLE_SESSIONS);
        }
        catch (SqlJetException ex)
        {
            System.out.println("Creating tables '" + TABLE_SESSIONS + "'...");

            db.beginTransaction(SqlJetTransactionMode.WRITE);
            try
            {
                String stmt = "CREATE TABLE " + TABLE_SESSIONS + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "filename VARCHAR(50), "
                        + "instruction VARCHAR(3), "
                        + "off_set VARCHAR(7), "
                        + "lenght VARCHAR(7), "
                        + "string text, "
                        //                        + "date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "date_time TEXT, "
                        + "author VARCHAR(15)"
                        + ")";

                db.createTable(stmt);
                db.createIndex("CREATE INDEX idx_filename ON " + TABLE_SESSIONS + "(filename)");
            }
//            catch (SqlJetException ex1)
            finally
            {
                db.commit();
//                Util.showErrorMessage("Error creating '" + TABLE_SESSIONS + "' table:" + ex1.getMessage() + "\n" + ex.toString());
            }
        }

        if (db.getSchema().getTable(TABLE_TASKS) == null)
        {
            System.out.println("Creating table '" + TABLE_TASKS + "'...");

            db.beginTransaction(SqlJetTransactionMode.WRITE);
            try
            {
                String stmt = "CREATE TABLE " + TABLE_TASKS + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "name VARCHAR(50), "
                        + "project VARCHAR(50), "
                        + "date_due DATE, "
                        + "priority VARCHAR(15), "
                        + "description text"
                        + ")";

                db.createTable(stmt);
                db.createIndex("CREATE INDEX idx_name ON " + TABLE_TASKS + "(name)");
            }
//            catch (SqlJetException ex1)
            finally
            {
                db.commit();

                //Util.showErrorMessage("Error creating '" + TABLE_TASKS + "' table:" + ex1.getMessage() + "\n" + ex.toString());
            }

        }


//        db.close();

    }

    public static void updateTask(Task task) throws SqlJetException
    {
        final Map<String, Object> vals = new HashMap<String, Object>();

        final Integer id = task.getId();
        
        vals.put("id", task.getId());
        vals.put("name", task.getName());
        vals.put("project", task.getProject());
        vals.put("date_due", task.getDateDue().toString());
        vals.put("priority", task.getPriority());
        vals.put("user_sent", task.getUserSent());
        vals.put("description", task.getDescription());

        SqlJetDb db = getConnection(DB_NAME, true, false);
        db.beginTransaction(SqlJetTransactionMode.WRITE);

        db.runWriteTransaction(new ISqlJetTransaction()
        {
            @Override
            public Object run(SqlJetDb db) throws SqlJetException
            {
                ISqlJetTable users = db.getTable(TABLE_TASKS);
                ISqlJetCursor cursor = users.lookup(users.getPrimaryKeyIndexName(), id);
                try
                {
                    if ( ! cursor.eof() )
                    {
                        cursor.updateByFieldNames(vals);
                    }
                }
                finally
                {
                    cursor.close();
                }
                return null;
            }
        });
        db.commit();
    }

    public static void deleteTask(int id) throws SqlJetException
    {
        SqlJetDb db = getConnection(DB_NAME, true, false);
        db.beginTransaction(SqlJetTransactionMode.WRITE);

        ISqlJetTable table = db.getTable(TABLE_TASKS);
        ISqlJetCursor cursor = table.lookup(table.getPrimaryKeyIndexName(), id);

        try
        {
            if (!cursor.eof())
            {
                cursor.delete();
            }
        }
        finally
        {
            cursor.close();
            db.commit();
        }

        return;
    }
}
