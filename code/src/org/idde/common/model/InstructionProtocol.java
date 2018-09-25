/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.idde.common.model;

/**
 *
 * @author vilson
 */
public class InstructionProtocol
{
    /**
     * Tags which identifies a IdDE message protocol
     */
    public static String IdDE_ID = "IdDE";

    /**
     * Indicates that a Invalid (or unknown) request was received
     */
    public static String MSG_INVALID_REQUEST = "-1";

    /**
     * Indicates that an error occured when composing response for the request
     */
    public static String MSG_ERROR_PROCESSING = "-2";
    
    /**
     * Used in messages if a user wants to start do share a file
     */
    public static String MSG_START_SHARING_FILE = "1";
    
     /**
     * Accept invitation to share file
     */
    public static String MSG_ACCEPT_START_SHARING_FILE = "1a";

    /**
     * Reject invitation to share file
     */
    public static String MSG_REJECT_START_SHARING_FILE = "1r";

    /**
     * Used do add user to an already existing shared session.
     * Each user in session needs to receive this message and register user in session.
     */
    public static String MSG_JOIN_SHARING_FILE = "1j";

    /**
     * This is used for messages which indicates that text shoud be updated
     */
    public static String MSG_UPDATE_SHARED_TEXT = "2";

    /**
     * Indicates that a text needs to be inserted
     */
    public static String INSERT_TEXT = "2i";

    /**
     * Indicates that a text needs to be deleted
     */
    public static String DELETE_TEXT = "2d";

    /**
     * Request user SIP addres
     */
    public static String MSG_GET_SIP = "4";

    /**
     * Answer user SIP addres
     */
    public static String MSG_REPLY_SIP = "5";

    /**
     * Request user mobile number
     */
    public static String MSG_GET_MOBILE = "6";

    /**
     * Answer user mobile number
     */
    public static String MSG_REPLY_MOBILE = "7";

    /**
     * Request user landline number
     */
    public static String MSG_GET_LANDLINE = "8";

    /**
     * Answer user landline number
     */
    public static String MSG_REPLY_LANDLINE = "9";

    /**
     * Request existing tasks
     */
    public static String MSG_GET_TASKS = "11";

    /**
     * Message indicating that the task was accepted
     */
    public static String MSG_ACCEPT_NEW_TASK = "12";

    /**
     * Message indicating that the task was rejected
     */
    public static String MSG_REJECT_NEW_TASK = "13";
    
    /**
     * Add new task to user
     */
    public static String MSG_ADD_TASK = "14";

    /**
     * Identify caret position 
     */
    public static String MSG_UPDATE_CARET_POSITION = "15";

    /**
     * Exit shared session
     */
    public static String MSG_EXIT_SHARING_FILE = "16";

    /**
     * Update
     */
    public static String MSG_EXIT_UPDATE_FILE_CONTENT = "17";

    /**
     * Add new appointment to user
     */
    public static String MSG_ADD_AGENDA = "18";

    /**
     * Message indicating that the appointment was accepted
     */
    public static String MSG_ACCEPT_NEW_AGENDA = "19";

    /**
     * Message indicating that the appointment was rejected
     */
    public static String MSG_REJECT_NEW_AGENDA = "20";

    /**
     * Request existing appointments
     */
    public static String MSG_GET_AGENDA = "21";

    /**
     * Remote wants to send a file
     */
    public static String MSG_SEND_FILE = "22";

    /**
     * Remote user wants to receive the file
     */
    public static String MSG_ACCEPT_FILE = "23";

    /**
     * Remote user doesn't wants to receive the file
     */
    public static String MSG_REJECT_FILE = "24";

    /**
     * Request files in shared directory
     */
    public static String MSG_VIEW_SHARED_FOLDER = "25";

    /**
     * Respond with file names in shared directory
     */
    public static String MSG_SHOW_FILES_SHARED = "26";

    /**
     * Used to request the Application Name and Version
     */
    public static String MSG_GET_APPVERSION = "30";

    /**
     * Tags which identifies a IdDE message protocol
     */
    public static String STATUS_CHAT_IdDE_ID = "_.=IdDE=._";

}
