/**
 * This class implements the dOPT Algorithm.
 * Original paper can be found at
 * {@link http://www.itu.dk/stud/speciale/bepjea/xwebtex/litt/concurrency-control-in-groupware-systems.pdf}
 *
 * \b Package: \n
 * moduleIdDE
 *
 * @see DocumentChangesListener.java
 *
 * @since Class created on 16/03/2011
 *
 * @author Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b Maintainers: \n
 * Vilson Cristiano Gartner [vgartner@gmail.com]
 *
 * \b License: \n
 * Contact the author.
 *
 * @version $Id$
 */
package org.idde.editor.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import org.idde.editor.model.Request;
import org.idde.util.Logger;

/**
 * @author vilson
 */
public class OperationalTransformation
{
    public static final String RIGHT_NOW = "without_transf";
    public static final String TRANSFORM = "transform";

    /**
     * This member stores the site's states
     */
    private Map<String, Integer> siteState;

    /**
     * Attribute to controle the queue
     */
    private Queue<Request> siteQueue;

    /**
     * Attribute to control the log
     */
    private LinkedList<Request> siteLog;

    /**
     * This attribute is used to control which was the last change by the local user. 
     * It is necessary to be possible to find out if changes from other user where made
     * on the left or on the right.
     */
    private Request lastLocalChangeOffSet;

    public OperationalTransformation()
    {
        System.out.println("Constructor: creating new instance of 'OperationalTransformation'");
        siteState = new HashMap<String, Integer>();
        siteLog   = new LinkedList();
        siteQueue = new LinkedList();
    }

    /**
     * Get the site's state
     * @return
     */
    public Map<String, Integer> getSiteState()
    {
        return siteState;
    }

    /**
     * Get the site's state
     * @return
     */
    public Map<String, Integer> getSiteStateClone()
    {
        Map<String, Integer> clone = new HashMap<String, Integer>();

        Iterator it = siteState.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry pairs = (Map.Entry) it.next();
            clone.put(pairs.getKey().toString(), Integer.parseInt(pairs.getValue().toString()) );
        }

        return clone;
    }


    /**
     * Used to add a new state key to the site states
     * @param userName Name of the user who is the key
     */
    public void addStateToSiteStates(String userName)
    {
        Logger.getLogger(this).debug("[Debug] Adding " + userName + " to Site States");

        // Only add if it doesn't already contain this key
        if ( ! siteState.containsKey(userName))
        {
            siteState.put(userName, 0);
        }
    }

    /**
     * Removes a key from the site states
     * @param userName Key to be removed
     */
    public void removeStateFromSiteStates(String userName)
    {
        Logger.getLogger(this).debug("[Debug] Removing " + userName + " to Site States");

        // Only add if it doesn't already contain this key
        if (siteState.containsKey(userName))
        {
            siteState.remove(userName);
        }
    }

    /**
     * This method verifies if the site state contains certain key
     * @param userName name of the user to verify
     * @return True if it is present
     */
    public Boolean containsSiteState(String userName)
    {
        return siteState.containsKey(userName);
    }

    /**
     * Compares an received request's states with the local site state.
     * If all states are lower it means that the request can be processed but must be transformed.
     * If all states are the same, process but without transformations.
     * <example>
     * (1,0,1) is the local state and (1,1,2) arrives
     * This indicates that the received request must wait in queue, because his 
     * elements are greater than the site state. There are some messages (requests) 
     * on the way to arrive... ;-)
     * Note: we are using Map to simulate this behavior, so the order of the elements
     * aren't actually importante. For instance:
     * (vilson=>1,simone=>0,alann=>1)
     * </example>
     *
     * @param r Request to be verified
     * @return String indicating if the state values are the same or lower.
     */
    private String hasLowerOrSameStates(Request r)
    {
        Boolean lowerStates = false;
        Boolean sameStates  = false;
        String result = "QUEUE" ;

        Logger.getLogger(this).debug("[Debug] Comparing states against: "+siteState);

        Iterator it = siteState.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry pairs = (Map.Entry) it.next();
//            System.out.println("Verifying all sitestate map: " + pairs.getKey() + " = " + pairs.getValue());

            if (pairs.getKey().toString().equals(r.getUser()))
            {
                // whe are not interested in the user we are testing
                // whe want to test it against the other states, to see if it is lower
//                System.out.println("Not interested in this user: " + r.getUser());

                //****** TODO  NOT SURE IF IT IS NEEDED
                String userName = pairs.getKey().toString();
                Integer local   = Integer.parseInt(pairs.getValue().toString());
                Integer remote  = Integer.parseInt(r.getSiteState().get(userName).toString());
//                System.out.println("Local site state: " + local.toString() + "Remote user state: "+ remote.toString());

                // Remote can only be i greater, otherways are messages missing
                Integer diff = remote - local;
                System.out.println("Remote Diff: " + diff + " (expected: 1)");
                if (diff == 1)
                {
                    sameStates  = true;
                    lowerStates = true;
                }
                else
                {
                    System.out.println("####################### RECEIVED MESSAGE NOT IN ORDER ###########");
                    sameStates = false;
                }
                //****** TODO  NOT SURE IF IT IS NEEDED... PROBABLY IS!!
            }
            else
            {
                String userName = pairs.getKey().toString();
                Integer local   = Integer.parseInt(pairs.getValue().toString());
                Integer remote  = Integer.parseInt(r.getSiteState().get(userName).toString());
                
                if (remote < local)
                {
                    lowerStates = true;
                }
                else if(remote == local)
                {
                    sameStates = true;
                }
                else
                {
                    System.out.println("!!! REMOTE > LOCATE:  " + remote.toString() + " - "+ local.toString());
                    lowerStates = true;
                    sameStates  = true;
                }
            }
        }

        if ( lowerStates )
        {
            result = TRANSFORM;
        }
        else if ( sameStates )
        {
            result = RIGHT_NOW;
        }

        return result;
    }

    /**
     * Returns the Request which has the lowest states comparing with the actuall site's states.
     * @return Request with lowest states
     */
    public Request getLowestStatesFromQueue()
    {
        Request result = null;
        // Create a temporary copy of site's queue to iterate with
        Queue<Request> tempQueue = siteQueue;

        // If there is no queue or it is empty, result will be null
        if ( siteQueue == null || siteQueue.isEmpty() )
        {
            return result;
        }

        // Process while not empty || until lowest is found
        while ( (! tempQueue.isEmpty()) && (result == null) )
        {
            Request r = tempQueue.remove();

            // Verify if this Request has the lowest states
            Iterator it = siteState.entrySet().iterator();
            Boolean isLowest = false;

            while (it.hasNext())
            {
                Map.Entry pairs = (Map.Entry) it.next();
                System.out.println("Verifying all sitestate map: " + pairs.getKey() + " = " + pairs.getValue());

                if (pairs.getKey().toString().equals(r.getUser()))
                {
                    // whe are not interested in the value of the user we are testing
                    // whe want to test it against the other states, to see if it is lower

                //****** TODO  NOT SURE IF IT IS NEEDED
                String userName = pairs.getKey().toString();
                Integer local   = Integer.parseInt(pairs.getValue().toString());
                Integer remote  = Integer.parseInt(r.getSiteState().get(userName).toString());
                System.out.println("Local site state: " + local.toString() + "Remote user state: "+ remote.toString());

                // Remote can only be i greater, otherways are messages missing
                Integer diff = remote - local;
                System.out.println("Diff: " + diff);
                if (diff == 1)
                {
                    isLowest = true;
                }
                else
                {
                    System.out.println("#################################################################");
                    isLowest = false;
                }
                //****** TODO  NOT SURE IF IT IS NEEDED


                }
                else
                {
                    String userName = pairs.getKey().toString();
                    if (Integer.parseInt(pairs.getValue().toString()) <= Integer.parseInt(r.getSiteState().get(userName).toString()))
                    {
                        isLowest = true;
                    }
                    else
                    {
                        isLowest = false;
                    }
                }
            }

            // we have found the element with lowest states
            if ( isLowest )
            {
                // remove this element from queue
                siteQueue.remove(r);
                // set as result element
                result = r;
            }
        }

        return result;
    }

    /**
     * This method updates the site state which contains the value of the remote user being processed
     * @param Name of the user to be updated.
     */
    public void incrementSiteState(String userName)
    {
        Logger.getLogger(this).debug("[Debug] Incrementing Site State for " + userName);

        Iterator it = siteState.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry pairs = (Map.Entry) it.next();
//            System.out.println("Verifying all sitestate map: " + pairs.getKey() + " = " + pairs.getValue());

            if (pairs.getKey().toString().equals(userName))
            {
                Integer value = Integer.parseInt(pairs.getValue().toString());
                System.out.println(pairs.getKey() + " before increment: "+value.toString());
                pairs.setValue(value + 1);
                System.out.println(pairs.getKey() + " after increment: "+pairs.getValue());
            }
        }
    }

    /**
     * This is a importat method in OT: it transforms request's offset to it's new values
     * @param r Request to be transformed
     * @return Transformed request
     */
    public Request transform(Request r)
    {
        Request result = r;

        Integer missCount = sumNotCountedLocalChanges(r);

        // Sum the last local change's lenght to the Request's offset
//        Integer lc = getLastLocalChangeOffSet().getLenght();
        result.setOffset(r.getOffset()+missCount);
        
        return result;
    }

    /**
     * Add an executed element to the log
     * @param r
     */
    public void addToLog(Request r)
    {
        Logger.getLogger(this).debug("[Debug] Adding to log: " + r);
        siteLog.addFirst(r);
    }

    /**
     * Add an elemento to site queue. This elemens are waiting to be added to the document.
     * @param r Request to be enqueued
     */
    public void enqueue(Request r)
    {
        Logger.getLogger(this).debug("[Debug] Adding to queue: " + r);
        siteQueue.add(r);
    }

    /**
     * This method returns the next element form the queue which can be
     * transformed and applied on the document
     * @return Request to be tranformed and applied to doc
     */
    public Request getNextFromQueue()
    {
        Request result = null;

        return result;
    }


    public void setLastLocalChangeOffSet(Request r)
    {
        Request last = r;
        Logger.getLogger(this).debug("[Debug] Setting last changeOffset to: " + last);

        this.lastLocalChangeOffSet = last;
    }

    public Request getLastLocalChangeOffSet()
    {
        return this.lastLocalChangeOffSet;
    }

    /**
     * This method verifies if the received request's offset is at the left of local offset.
     * It if is at left, the request can be applied to document immediately,
     * no right side and/or queue check is needed.
     * @param offset Received request's offset
     * @return True if it located at the left
     */
    public boolean verifyIfLeftToOffset(Integer offset)
    {
        Boolean result = false;

        if (getLastLocalChangeOffSet() == null)
        {
            result = true;
        }
        else
        {
            result = offset < lastLocalChangeOffSet.getOffset();
        }

        return result;
    }

    /**
     * Verifies if this request can be placed immediately on the document. This will be possible
     * if all the request's state info are lower than the
     * @param r Received request
     * @return True if it can be added to document. False indicates that it should be placed on queue
     */
    public String verifyRightSide(Request r)
    {
        //Verify if this request has lower os equal state's values
        String result = hasLowerOrSameStates(r);

        return result;
    }

    /**
     * This method processes all values of the site queue, to see if the values can be applied now.
     * @param requestsQueue Queue to receive the values in order to be applied
     * @return @param0 populated whith the values
     */
    public Queue<Request> processQueue(Queue<Request> requestsQueue)
    {
        Queue<Request> result = requestsQueue;
        Boolean doWork = true;

        while ( doWork )
        {
            Request req = getLowestStatesFromQueue();

            // Element was found: process it
            if ( req != null )
            {
                Logger.getLogger(this).debug("[Debug] OT Queue - Adding: " + req.getUser() + "states: "+req.getSiteState());

                // now, make necessary transformation to obtain req'
                req = transform(req);
                // process it
                result.add(req);
                incrementSiteState(req.getUser());
                addToLog(req.getClone());
            }
            else
            {
                doWork = false;
            }
        }
        
        return result;
    }

    @Override
    public String toString()
    {
        return siteState.toString();
    }

    /**
     * This method sums the lenght of all the missing elements which wheren't present on remote, when he sent his message.
     * For instance, let's say that remote's Request arrives containing a site state like (2,5) where 2 is local site state.
     * If local site info is (6,5) means that there whe need look the lenght of 3, 4 and 5. Otherways the offset will be wrong.
     * @param r Request receiver
     * @return Sum of the missing changes on remote site
     */
    private Integer sumNotCountedLocalChanges(Request r)
    {
        Integer resultSum = 0;
        Integer request;
        Integer local;
        String user = SessionControl.LOCAL_USER;

        for( Integer i=0; i<siteLog.size(); i++)
        {
            request = r.getSiteState().get(user);
            local   = siteLog.get(i).getSiteState().get(user);

            if ( request < local )
            {
                resultSum = resultSum + siteLog.get(i).getLenght();
            }
        }

        System.out.println("*****  Adjusting: "+resultSum.toString());
        return resultSum;
    }
}
