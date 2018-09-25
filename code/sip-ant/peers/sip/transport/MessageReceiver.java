/*
    This file is part of Peers, a java SIP softphone.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright 2007, 2008, 2009, 2010 Yohann Martineau 
*/

package org.idde.sip.peers.sip.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import org.idde.sip.peers.Logger;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.Utils;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldValue;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderParamName;
import org.idde.sip.peers.sip.syntaxencoding.SipParserException;
import org.idde.sip.peers.sip.transaction.ClientTransaction;
import org.idde.sip.peers.sip.transaction.ServerTransaction;
import org.idde.sip.peers.sip.transaction.TransactionManager;

public abstract class MessageReceiver implements Runnable {

    public static final int BUFFER_SIZE = 2048;//FIXME should correspond to MTU 1024;
    public static final String CHARACTER_ENCODING = "US-ASCII";
    
    protected int port;
    private boolean isListening;
    
    //private UAS uas;
    private SipServerTransportUser sipServerTransportUser;
    private TransactionManager transactionManager;
    private TransportManager transportManager;

    public MessageReceiver(int port, TransactionManager transactionManager,
            TransportManager transportManager) {
        super();
        this.port = port;
        this.transactionManager = transactionManager;
        this.transportManager = transportManager;
        isListening = true;
    }
    
    public void run() {
        while (isListening) {
            try {
                listen();
            } catch (IOException e) {
                Logger.error("input/output error", e);
            }
        }
    }

    protected abstract void listen() throws IOException;
    
    protected boolean isRequest(byte[] message) {
        String beginning = null;
        try {
            beginning = new String(message, 0,
                    RFC3261.DEFAULT_SIP_VERSION.length(), CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            Logger.error("unsupported encoding", e);
        }
        if (RFC3261.DEFAULT_SIP_VERSION.equals(beginning)) {
            return false;
        }
        return true;
    }
    
    protected void processMessage(byte[] message, InetAddress sourceIp)
            throws IOException {
        SipMessage sipMessage = null;
        try {
            sipMessage = transportManager.sipParser.parse(
                    new ByteArrayInputStream(message));
        } catch (IOException e) {
            Logger.error("input/output error", e);
        } catch (SipParserException e) {
            Logger.error("SIP parser error", e);
        }
        if (sipMessage == null) {
            return;
        }
        if (sipMessage instanceof SipRequest) {
            SipRequest sipRequest = (SipRequest)sipMessage;
            
            
            SipHeaderFieldValue topVia = Utils.getTopVia(sipRequest);
            String sentBy =
                topVia.getParam(new SipHeaderParamName(RFC3261.PARAM_SENTBY));
            if (sentBy != null) {
                int colonPos = sentBy.indexOf(RFC3261.TRANSPORT_PORT_SEP);
                if (colonPos < 0) {
                    colonPos = sentBy.length();
                }
                sentBy = sentBy.substring(0, colonPos);
            }
            if (InetAddress.getByName(sentBy).equals(sourceIp)) {
                topVia.addParam(new SipHeaderParamName(RFC3261.PARAM_RECEIVED),
                        sourceIp.getHostAddress());
            }
            
            
            
            ServerTransaction serverTransaction =
                transactionManager.getServerTransaction(sipRequest);
            if (serverTransaction == null) {
                //uas.messageReceived(sipMessage);
                sipServerTransportUser.messageReceived(sipMessage);
            } else {
                serverTransaction.receivedRequest(sipRequest);
            }
        } else {
            SipResponse sipResponse = (SipResponse)sipMessage;
            ClientTransaction clientTransaction =
                transactionManager.getClientTransaction(sipResponse);
            Logger.debug("ClientTransaction = " + clientTransaction);
            if (clientTransaction == null) {
                //uas.messageReceived(sipMessage);
                sipServerTransportUser.messageReceived(sipMessage);
            } else {
                clientTransaction.receivedResponse(sipResponse);
            }
        }
    }
    
    public synchronized void setListening(boolean isListening) {
        this.isListening = isListening;
    }

    public synchronized boolean isListening() {
        return isListening;
    }

    public void setSipServerTransportUser(
            SipServerTransportUser sipServerTransportUser) {
        this.sipServerTransportUser = sipServerTransportUser;
    }

//    public void setUas(UAS uas) {
//        this.uas = uas;
//    }
    
}
