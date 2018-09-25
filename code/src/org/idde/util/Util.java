/**
 * Util class.
 *
 * \b Package: \n
 * org.idde.chat.util
 *
 * @see org.idde.chat
 * @see org.idde.chat.util
 *
 * @since Class created on April 10, 2010.
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

import java.io.BufferedWriter;
import java.sql.Timestamp;
import javax.swing.text.Document;
import org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import javax.swing.ImageIcon;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import moduleIdDE.CaretPosAnnotation;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.idde.sip.peers.Config;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.StatusDisplayer;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import moduleIdDE.EditAnnotation;
import org.idde.sip.peers.XmlConfig;
import org.idde.sip.peers.sip.core.useragent.UserAgent;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.loaders.DataObject;
import org.openide.text.Annotation;
import org.openide.text.Line.Part;
import org.openide.text.NbDocument;

/**
 * Class
 * @author vilson
 */
public class Util
{

    private static final String iddeHomeDirectory = System.getProperty("user.home") + "/.idde";
    public static final String logDirectory = System.getProperty("user.home") + "/.idde/logs";
    public static final String configFile = getIddeHomeDirectory() + "/.properties";
    public static final String dbDirectory = getIddeHomeDirectory() + "/db";
    public static final String confDirectory = getIddeHomeDirectory() + "/conf";
    public static final String configFilePeers = confDirectory + "/peers.xml";
    public static String newLine = "\n";

    /**
     * Verify and/or create home directory.
     * @param create Should create the directory if not exists?
     * @return
     */
    public static boolean verifyHomeDirectory(boolean create)
    {
        File file = new File(getIddeHomeDirectory());
        boolean exists = file.exists();
        boolean result;

        if (!exists && create)
        {
            result = (file).mkdir();

            if (result)
            {
                Logger.debugInfo(file, "Created IdDE home directory: " + getIddeHomeDirectory());
            }
            else
            {
                Logger.debugError(file, "ERROR creating IdDE home directory: " + getIddeHomeDirectory());
            }
        }
        else
        {
            if (exists)
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * Verify and/or create log directory.
     * @param create Should create the directory if not exists?
     * @return
     */
    public static boolean verifyLogDirectory(boolean create)
    {
        File file = new File(logDirectory);
        boolean exists = file.exists();
        boolean result;

        if (!exists && create)
        {
            result = (file).mkdirs();

            if (result)
            {
                Logger.debugInfo(file, "Created IdDE LOG directory: " + logDirectory);
            }
            else
            {
                Logger.debugError(file, "ERROR creating IdDE LOG directory: " + logDirectory);
            }
        }
        else
        {
            if (exists)
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * Verify and/or create config directory.
     * @param create Should create the directory if not exists?
     * @return
     */
    public static boolean verifyConfigDirectory(boolean create)
    {
        File file = new File(confDirectory);
        boolean exists = file.exists();
        boolean result;

        if (!exists && create)
        {
            result = (file).mkdirs();

            if (result)
            {
                Logger.debugInfo(file, "Created IdDE CONFIG directory: " + logDirectory);
            }
            else
            {
                Logger.debugError(file, "ERROR creating IdDE LOG directory: " + logDirectory);
            }
        }
        else
        {
            if (exists)
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * Verify/Creates the default config file
     * @param create If the doen't exists, true means create it. False, don't create the file.
     * @return if file exists and/or if it was successful created.
     */
    public static boolean verifyPeersConfigFile(boolean create)
    {
        File file = new File(configFilePeers);
        boolean exists = file.exists();
        boolean result;

        if (!exists && create)
        {
            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configFilePeers)));

                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
                out.write("<peers xmlns=\"http://peers.sourceforge.net\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://peers.sourceforge.net peers.xsd\">");
                out.write("<!-- File created and used by IdDE. DO NOT CHANGE!!! -->");
                out.write("<!-- Doubts? vgartner@gmail.com -->");
                out.write("<network>");
                out.write("    <interfaces>");
                out.write("      <interface id=\"eth1\">");
                out.write("        <name>Ethernet network interface</name>");
                out.write("        <address/>");
                out.write("      </interface>");
                out.write("    </interfaces>");
                out.write("  </network>");
                out.write("  <devices>");
                out.write("    <audio/>");
                out.write("    <video/>");
                out.write("  </devices>");
                out.write("  <sip>");
                out.write("    <profile>");
                out.write("      <userpart>which_username</userpart>");
                out.write("      <domain>sip.server</domain>");
                out.write("      <password>which_password</password>");
                out.write("      <outboundProxy/>");
                out.write("      <interface ref=\"eth1\"/>");
                out.write("      <port>5060</port>");
                out.write("    </profile>");
                out.write("  </sip>");
                out.write("  <codecs>");
                out.write("    <codec>");
                out.write("      <family>audio</family>");
                out.write("      <name>PCMU</name>");
                out.write("      <payloadType>0</payloadType>");
                out.write("    </codec>");
                out.write("    <mediaMode>captureAndPlayback</mediaMode>");
                out.write("    <mediaDebug>false</mediaDebug>");
                out.write("  </codecs>");
                out.write("  <rtp>");
                out.write("    <interface ref=\"eth1\"/>");
                out.write("    <port>8000</port>");
                out.write("  </rtp>");
                out.write("</peers>");
                out.flush();
                out.close();

                result = true;
            }
            catch (IOException ex)
            {
                result = false;
                Exceptions.printStackTrace(ex);
            }


        }
        else
        {
            if (exists)
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * VerifyCreates the default config file
     * @param create If the doen't exists, true means create it. False, don't create the file.
     * @return if file exists and/or if it was successful created.
     */
    public static boolean verifyConfigFile(boolean create)
    {
        File file = new File(configFile);
        boolean exists = file.exists();
        boolean result;

        if (!exists && create)
        {
            result = createConfigFile(configFile);
        }
        else
        {
            if (exists)
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * Creates the default config file
     * @return
     */
    public static boolean createConfigFile(String fileName)
    {
        try
        {
            File file = new File(fileName);

            // Create file if it does not exist
            boolean success = file.createNewFile();

            if (success)
            {
                Logger.debugInfo(file, "Created file: " + fileName);

                PropertiesConfiguration propertiesConfig;

                try
                {
                    propertiesConfig = new PropertiesConfiguration(fileName);

                    propertiesConfig.setProperty("server_xmpp", "server");
                    propertiesConfig.setProperty("login_xmpp", "username");
                    propertiesConfig.setProperty("pass_xmpp", "pass");
                    propertiesConfig.setProperty("port_xmpp", "5222");
                    propertiesConfig.setProperty("resource_xmpp", "IdDE");

                    // Save SIP settings
                    propertiesConfig.setProperty("server_sip", "server");
                    propertiesConfig.setProperty("login_sip", "username");
                    propertiesConfig.setProperty("pass_sip", "pass");
                    propertiesConfig.setProperty("port_sip", "5060");

                    // Save phone numbers
                    propertiesConfig.setProperty("mobile", "0");
                    propertiesConfig.setProperty("landline", "0");

                    //Other settings
                    propertiesConfig.setProperty("compress_msg", "true");
                    propertiesConfig.setProperty("log_db", "false");

                    propertiesConfig.setProperty("encode_charset", "UTF-8");
                    propertiesConfig.setProperty("msg_buffer", "0");

                    propertiesConfig.save();
                }
                catch (ConfigurationException ex)
                {
                    Exceptions.printStackTrace(ex);
                }

                return true;
            }
            else
            {
                Logger.debugInfo(file, "Error creating file: " + fileName);
                return true;
            }
        }
        catch (IOException e)
        {
            System.out.println(ResourceManager.getBundle().getString("ERROR_CREATING_CONFIG_FILE:_") + configFile);
            return false;
        }

    }

    /**
     *
     * @param iconName
     * @return
     */
    public static ImageIcon getIcon(String iconName)
    {
        ImageIcon icon = new ImageIcon(iconName);

        return icon;
    }

    /**
     * Obtain the current time
     * @return Time in format HH:mm:ss
     */
    public static String getCurrentTime()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        return sdf.format(cal.getTime());
    }

    /**
     * Obtain the current time
     * @return Time in format HH:mm:ss
     */
    public static String getCurrentDate()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        return sdf.format(cal.getTime());
    }

    private static String getDateTime(String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();

        return dateFormat.format(date);
    }

    /**
     * Obtain the current date and time
     * @return Date and hour in format: yyyy/MM/dd HH:mm:ss
     */
    public static String getDateTimeYMD()
    {
        return getDateTime("yyyy/MM/dd HH:mm:ss");
    }

    /**
     * Obtain the current date and time
     * @return Date and hour in format: dd/MM/yyyy HH:mm:ss
     */
    public static String getDateTimeDMY()
    {
        return getDateTime("dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Compress message using Deflater
     * http://www.exampledepot.com/egs/java.util.zip/CompArray.html
     * 
     * @param data String to be compressed
     * 
     * @return
     * @throws IOException
     */
    public static String compressString(String data) throws IOException
    {
        byte[] input = data.getBytes("UTF-8");

        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        compressor.setInput(input);
        compressor.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Compress the data
        byte[] buf = new byte[1024];

        while (!compressor.finished())
        {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }

        bos.close();

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();

        return new String(compressedData);
    }

    /**
     * Decompress a message
     * Source: http://www.exampledepot.com/egs/java.util.zip/DecompArray.html
     *
     * @param data Compressed string
     * @return
     */
    public static String uncompressString(String data) throws UnsupportedEncodingException
    {
        // Create the decompressor and give it the data to compress
        byte[] compressed = data.getBytes("UTF-8");

        Inflater decompressor = new Inflater();
        decompressor.setInput(compressed);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);

        // Decompress the data
        byte[] buf = new byte[1024];

        while (!decompressor.finished())
        {
            try
            {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
            catch (DataFormatException e)
            {
            }
        }

        try
        {
            bos.close();
        }
        catch (IOException e)
        {
        }

        // Get the decompressed data
        byte[] decompressedData = bos.toByteArray();

        return new String(decompressedData);
    }

    /**
     * Encoding samples: http://www.rgagnon.com/javadetails/java-0598.html
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeBase64(String str, String charSet) throws UnsupportedEncodingException
    {
        return new String(Base64.encodeBase64(str.getBytes()));
    }

    /**
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decodeBase64(String str, String charSet) throws UnsupportedEncodingException
    {
        return new String(Base64.decodeBase64(str.getBytes()));
    }

    /**
     * This method test if the given string contains only ASCII chars
     * 
     * @param v
     * @return
     */
    public static boolean isValidAsciiChar(String v)
    {
        byte bytearray[] = v.getBytes();
        CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();

        try
        {
            CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
            r.toString();
        }
        catch (CharacterCodingException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Method to show a regular information message
     *
     * @param message Html string mMessage to be shown
     */
    public static void showInformationMessage(String message)
    {
        JOptionPane op = new JOptionPane();
        op.setMessage(message);
        op.setMessageType(JOptionPane.INFORMATION_MESSAGE);

        JDialog dialog = op.createDialog(null, "IdDE - Information");
        dialog.setVisible(true);
    }

    /**
     * Method to show a html error message
     *
     * @param message Html string mMessage to be shown
     */
    public static void showErrorMessage(String message)
    {
        JOptionPane op = new JOptionPane();
        op.setMessage(message);
        op.setMessageType(JOptionPane.ERROR_MESSAGE);

        JDialog dialog = op.createDialog(null, "IdDE - ERROR");
        dialog.setVisible(true);
    }

    /**
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static Date formatDate_ddMMyyyy(String date) throws Exception
    {
        return formatDate(date, "dd/MM/yyyy");
    }

    /**
     * Creates a timestamp based on a date and time
     * @param date Date in format yyyy-mm-dd
     * @param time Time in format hh:mm:ss.SSS
     * @return formated timestamp
     * @throws Exception
     */
    public static Timestamp formatDateTime_ddMMyyyyHHmm(String date, String time) throws Exception
    {
        String datetime = date + " " + time;

        Date date1 = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(datetime);

        String strDateTime1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date1);

        Date resultDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(strDateTime1);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS").format(date1);

//        java.util.Date dateTime = sdf.parse(datetime);
        return new java.sql.Timestamp(resultDate.getTime());
    }

    /**
     * Creates a timestamp based on a date. As time is not passed, 00:00 will be user.
     * @param date Date to be used to create the timestamp
     * @return Timestamp
     * @throws Exception
     */
    public static Timestamp formatDateTime_ddMMyyyy(String date) throws Exception
    {
        return formatDateTime_ddMMyyyyHHmm(date, "00:00:00");
    }

    public static Date ConvertToTime(String time) throws ParseException
    {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = (Date) formatter.parse(time);

        return date;
    }

    /**
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date formatDate(String date, String format) throws Exception
    {
        if (date == null || date.equals(""))
        {
            return null;
        }

        Date dtResult = null;
        try
        {
            DateFormat formatter = new SimpleDateFormat(format);
            dtResult = (java.util.Date) formatter.parse(date);
        }
        catch (ParseException e)
        {
            throw e;
        }
        return dtResult;
    }

    /**
     * Shows a message in IDE's statusbar
     * @param msg
     */
    public static void setStatus(String msg)
    {
        StatusDisplayer.getDefault().setStatusText(msg);
    }

    /**
     * Clear the status bar message
     */
    public static void clearStatus()
    {
        Util.setStatus("");
    }

    /**
     * This methos inserts an annotation into the IDE, showing where a remote user is making changes
     * @param doc Document which was changed
     * @param offset Where the changes where made
     * @param b Booleand
     */
    public static void insertEditAnnotation(Document doc, int offset, int lenght, boolean b, String description)
    {
        Line line = NbEditorUtilities.getLine(doc, offset, b);

        NbEditorDocument document = (NbEditorDocument) doc;
        DataObject dobj = null;
//        StyledDocument docToGo = null;
        dobj = NbEditorUtilities.getDataObject(document);
//        docToGo = document;
        final int col = NbDocument.findLineColumn(document, offset);
        int lin = NbDocument.findLineNumber(document, offset);

//        final Annotation ann = new EditAnnotation(contactName, lin);
        final Annotation ann = EditAnnotation.getAnnotation(description + Integer.toString(col), lin);

        Part lp = line.createPart(col, lenght);
        ann.attach(lp);
    }

    public static void insertCaretPosAnnotation(Document doc, int offset, String name)
    {
//        final Annotation ann = CaretPosAnnotation.getAnnotation(offset, name);
//
//        Line line = NbEditorUtilities.getLine(doc, offset, false);
//        ann.attach(line);
        CaretPosAnnotation.getAnnotation(doc, offset, name);
    }

    /**
     * @return the iddeHomeDirectory
     */
    public static String getIddeHomeDirectory()
    {
        return iddeHomeDirectory;
    }

    public static Config getSIPConfig()
    {
        return new XmlConfig(Util.getIddeHomeDirectory() + File.separator + UserAgent.CONFIG_FILE, Util.getPeersLogger());
    }

    public static org.idde.sip.peers.Logger getPeersLogger()
    {
        return new org.idde.sip.peers.Logger(Util.getIddeHomeDirectory());
    }

    public static String convertTimestampToString(Timestamp t)
    {
        // t.toString() should be: "yyyy-mm-dd hh:mm:ss.nanoseconds"
        String tOld = t.toString();

        if (tOld.length() < 21)
        {
            return "";
        }

        String tNew = tOld.substring(5, 7) + "-"
                + tOld.substring(8, 10) + "-"
                + tOld.substring(0, 4) + " "
                + tOld.substring(11, 13) + ":"
                + tOld.substring(14, 16) + ":"
                + tOld.substring(17, 19);

        return tNew;
    }

    /**
     * Get the name of the user of local machine, based on his XMPP's login name
     * @return User name
     */
    public static String getLocalUserName()
    {
        PropertiesConfiguration propertiesConfig;
        String result = null;
        try
        {
            propertiesConfig = new PropertiesConfiguration(configFile);
//            propertiesConfig.setLogger(Logger.getLogger(propertiesConfig.getClass()));
            // get name from file
            result = propertiesConfig.getString("login_xmpp");
        }
        catch (ConfigurationException ex)
        {
            showErrorMessage("Error getting user name from config file:\n"+ex.getMessage());
        }

        return result;
    }

//    public static String compressString(String str) throws IOException
//    {
//        int size = 1024;
//
//        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(str.getBytes("ISO8859_1")));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        GZIPOutputStream gzip = new GZIPOutputStream(baos);
//
//        byte[] buffer = new byte[size];
//
//        int len;
//
//        while ( (len = bis.read(buffer, 0, size) ) != -1)
//        {
//            gzip.write(buffer, 0, len);
//        }
//
//        gzip.finish();
//        bis.close();
//        gzip.close();
//
//        return new String( baos.toByteArray() );
//    }
//
//    public static String uncompressString(String message) throws IOException
//    {
//        int size = 1024;
//
//        byte[] data = message.getBytes("ISO8859_1");
//
//        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        byte[] buffer = new byte[size];
//        int len;
//
//        while ((len = gzip.read(buffer, 0, size)) != -1)
//        {
//            baos.write(buffer, 0, len);
//        }
//        gzip.close();
//        baos.close();
//
//        return new String(baos.toByteArray());
//    }
}
