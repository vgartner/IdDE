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

package org.idde.sip.peers.sip.core.useragent.handlers;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.idde.sip.peers.Logger;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.core.useragent.InitialRequestManager;
import org.idde.sip.peers.sip.core.useragent.RequestManager;
import org.idde.sip.peers.sip.core.useragent.SipListener;
import org.idde.sip.peers.sip.core.useragent.UserAgent;
import org.idde.sip.peers.sip.syntaxencoding.NameAddress;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldValue;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderParamName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaders;
import org.idde.sip.peers.sip.syntaxencoding.SipURI;
import org.idde.sip.peers.sip.syntaxencoding.SipUriSyntaxException;
import org.idde.sip.peers.sip.transaction.ClientTransaction;
import org.idde.sip.peers.sip.transaction.ClientTransactionUser;
import org.idde.sip.peers.sip.transaction.NonInviteClientTransaction;
import org.idde.sip.peers.sip.transaction.Transaction;
import org.idde.sip.peers.sip.transaction.TransactionManager;
import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.SipResponse;
import org.idde.sip.peers.sip.transport.TransportManager;

public class RegisterHandler extends MethodHandler
        implements ClientTransactionUser {

    public static final int REFRESH_MARGIN = 10; // seconds

    private InitialRequestManager initialRequestManager;

    private Timer timer;

    private String requestUriStr;
    private String profileUriStr;
    private String callIDStr;
    
    //FIXME should be on a profile based context
    private boolean unregistered;
    
    public RegisterHandler(UserAgent userAgent,
            TransactionManager transactionManager,
            TransportManager transportManager) {
        super(userAgent, transactionManager, transportManager);
        timer = new Timer();
        unregistered = false;
    }

    //TODO factorize common code here and in invitehandler
    public ClientTransaction preProcessRegister(SipRequest sipRequest) {
        SipHeaders sipHeaders = sipRequest.getSipHeaders();
        SipURI destinationUri = RequestManager.getDestinationUri(sipRequest);
        int port = destinationUri.getPort();
        if (port == SipURI.DEFAULT_PORT) {
            port = RFC3261.TRANSPORT_DEFAULT_PORT;
        }
        //TODO if header route is present, addrspec = toproute.nameaddress.addrspec
        String transport = RFC3261.TRANSPORT_UDP;
        Hashtable<String, String> params = destinationUri.getUriParameters();
        if (params != null) {
            String reqUriTransport = params.get(RFC3261.PARAM_TRANSPORT);
            if (reqUriTransport != null) {
                transport = reqUriTransport; 
            }
        }
        ClientTransaction clientTransaction = transactionManager
            .createClientTransaction(sipRequest, destinationUri.getHost(), port,
                    transport, null, this);
        //TODO 10.2
        SipHeaderFieldValue to = sipHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_TO));
        SipHeaderFieldValue from = sipHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_FROM));
        String fromValue = from.getValue();
        to.setValue(fromValue);
        requestUriStr = destinationUri.toString();
        profileUriStr = NameAddress.nameAddressToUri(fromValue);
        callIDStr = sipHeaders.get(new SipHeaderFieldName(RFC3261.HDR_CALLID))
            .toString();
        return clientTransaction;
    }

    public void unregister() {
        if (requestUriStr == null) {
            return;
        }
        SipRequest sipRequest;
        try {
            sipRequest = initialRequestManager.getGenericRequest(requestUriStr,
                    RFC3261.METHOD_REGISTER, profileUriStr);
        } catch (SipUriSyntaxException e) {
            Logger.error("syntax error", e);
            return;
        }
        SipHeaders sipHeaders = sipRequest.getSipHeaders();
        SipHeaderFieldValue callID = sipHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_CALLID));
        callID.setValue(callIDStr);
        ClientTransaction clientTransaction = preProcessRegister(sipRequest);
        initialRequestManager.addContact(sipRequest,
                clientTransaction.getContact(), profileUriStr);
        SipHeaderFieldValue contact = sipHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_CONTACT));
        contact.addParam(new SipHeaderParamName(RFC3261.PARAM_EXPIRES), "0");
        unregistered = true;
        clientTransaction.start();
    }

    //////////////////////////////////////////////////////////
    // ClientTransactionUser methods
    //////////////////////////////////////////////////////////
    
    public void errResponseReceived(SipResponse sipResponse) {
        int statusCode = sipResponse.getStatusCode();
        if ((statusCode == RFC3261.CODE_401_UNAUTHORIZED
                || statusCode == RFC3261.CODE_407_PROXY_AUTHENTICATION_REQUIRED)
                && challengeManager != null) {
            if (!challenged) {
                NonInviteClientTransaction nonInviteClientTransaction =
                    (NonInviteClientTransaction)
                    transactionManager.getClientTransaction(sipResponse);
                SipRequest sipRequest = nonInviteClientTransaction.getRequest();
                challengeManager.handleChallenge(sipRequest, sipResponse);
                challenged = true;
            } else {
                SipListener sipListener = userAgent.getSipListener();
                if (sipListener != null) {
                    sipListener.registerFailed(sipResponse);
                }
            }
        }
    }

    public void provResponseReceived(SipResponse sipResponse,
            Transaction transaction) {
        //meaningless
    }

    public void successResponseReceived(SipResponse sipResponse,
            Transaction transaction) {
        // each contact contains an expires parameter giving the expiration
        // in seconds. Thus the binding must be refreshed before it expires.
        SipHeaders sipHeaders = sipResponse.getSipHeaders();
        SipHeaderFieldValue contact =
            sipHeaders.get(new SipHeaderFieldName(RFC3261.HDR_CONTACT));
        if (contact == null) {
            return;
        }
        String expires =
            contact.getParam(new SipHeaderParamName(RFC3261.PARAM_EXPIRES));
        if (expires == null || "".equals(expires.trim())) {
            return;
        }
        challenged = false;
        if (!unregistered) {
            int delay = Integer.parseInt(expires) - REFRESH_MARGIN;
            timer.schedule(new RefreshTimerTask(), delay * 1000);
        }
        SipListener sipListener = userAgent.getSipListener();
        if (sipListener != null) {
            sipListener.registerSuccessful(sipResponse);
        }
    }

    public void transactionTimeout(ClientTransaction clientTransaction) {
        //TODO alert user
    }

    public void transactionTransportError() {
        //TODO alert user
    }

    //////////////////////////////////////////////////////////
    // TimerTask
    //////////////////////////////////////////////////////////
    
    class RefreshTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                initialRequestManager.createInitialRequest(requestUriStr,
                        RFC3261.METHOD_REGISTER, profileUriStr, callIDStr);
            } catch (SipUriSyntaxException e) {
                Logger.error("syntax error", e);
            }
        }
    }

    public void setInitialRequestManager(InitialRequestManager initialRequestManager) {
        this.initialRequestManager = initialRequestManager;
    }

}
