package task;

import java.io.*;
import java.util.*;

class ReadTasks
{

    public static void main(String[] args)
    {
        ReadTasks rt = new ReadTasks();
        rt.dbTest();
    }

    void dbTest()
    {
        DataInputStream dataInputS = null;
        String record = null;

        try
        {
            File file = new File(".." + File.separatorChar + "test" + 
                                 File.separatorChar + "task" +
                                 File.separatorChar +"tasks.db");

            String absolutePath = file.getAbsolutePath();
            System.out.println(" Absolute path is "  + absolutePath);

            FileInputStream fileInputS = new FileInputStream(file);
            BufferedInputStream bufferInputS = new BufferedInputStream(fileInputS);
            dataInputS = new DataInputStream(bufferInputS);

            // read record of the database
            while ((record = dataInputS.readLine()) != null)
            {
                StringTokenizer st = new StringTokenizer(record, "\\|\\|");
                String task  = st.nextToken();
                String descr = st.nextToken();
                String author = st.nextToken();
                String date = st.nextToken();
                String hour = st.nextToken();

                System.out.println("Task: " + task);
                System.out.println("Description: " + descr);
                System.out.println("Author: " + author);
                System.out.println("Date: " + date);
                System.out.println("Tame: " + hour + "\n");
            }

        }
        catch (IOException e)
        {
            // errors from FileInputStream or readLine()
            System.out.println("IOException error: " + e.getMessage());

        }
        finally
        {
            // if okay, close the file
            if (dataInputS != null)
            {
                try
                {
                    dataInputS.close();
                }
                catch (IOException e)
                {
                    System.out.println("IOException closing the file: "
                            + e.getMessage());
                }
            }
        }
    }
}
