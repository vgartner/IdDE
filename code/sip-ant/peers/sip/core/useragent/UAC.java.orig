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

package org.idde.sip.peers.sip.core.useragent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.idde.sip.peers.Logger;
import org.idde.sip.peers.media.CaptureRtpSender;
import org.idde.sip.peers.media.Echo;
import org.idde.sip.peers.media.IncomingRtpReader;
import org.idde.sip.peers.media.SoundManager;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.Utils;
import org.idde.sip.peers.sip.core.useragent.handlers.InviteHandler;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldValue;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaders;
import org.idde.sip.peers.sip.syntaxencoding.SipUriSyntaxException;
import org.idde.sip.peers.sip.transaction.ClientTransaction;
import org.idde.sip.peers.sip.transaction.InviteClientTransaction;
import org.idde.sip.peers.sip.transaction.TransactionManager;
import org.idde.sip.peers.sip.transactionuser.Dialog;
import org.idde.sip.peers.sip.transactionuser.DialogManager;
import org.idde.sip.peers.sip.transactionuser.DialogState;
import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.SipResponse;
import org.idde.sip.peers.sip.transport.TransportManager;

public class UAC {
    
    private InitialRequestManager initialRequestManager;
    private MidDialogRequestManager midDialogRequestManager;

    private String registerCallID;
    private String profileUri;
    
    //FIXME
    private UserAgent userAgent;
    private TransactionManager transactionManager;
    private DialogManager dialogManager;
    private List<String> guiClosedCallIds;
    
    /**
     * should be instanciated only once, it was a singleton.
     */
    public UAC(UserAgent userAgent,
            String profileUri,
            InitialRequestManager initialRequestManager,
            MidDialogRequestManager midDialogRequestManager,
            DialogManager dialogManager,
            TransactionManager transactionManager,
            TransportManager transportManager) {
        this.userAgent = userAgent;
        this.initialRequestManager = initialRequestManager;
        this.midDialogRequestManager = midDialogRequestManager;
        this.dialogManager = dialogManager;
        this.transactionManager = transactionManager;
        this.profileUri = profileUri;
        registerCallID = Utils.generateCallID(userAgent.getMyAddress());
        guiClosedCallIds = Collections.synchronizedList(new ArrayList<String>());
    }

    /**
     * For the moment we consider that only one profile uri is used at a time.
     * @throws SipUriSyntaxException 
     */
    public SipRequest register() throws SipUriSyntaxException {
        String requestUri = RFC3261.SIP_SCHEME + RFC3261.SCHEME_SEPARATOR
            + userAgent.getDomain();
        return initialRequestManager.createInitialRequest(requestUri,
                RFC3261.METHOD_REGISTER, profileUri, registerCallID);
    }
    
    public void unregister() {
        initialRequestManager.registerHandler.unregister();
    }
    
    public SipRequest invite(String requestUri, String callId)
            throws SipUriSyntaxException {
        return initialRequestManager.createInitialRequest(requestUri,
                RFC3261.METHOD_INVITE, profileUri, callId);
        
    }

    private SipRequest getOriginalInvite(String callId) {
        List<ClientTransaction> clientTransactions =
            transactionManager.getClientTransactionsFromCallId(callId,
                    RFC3261.METHOD_INVITE);
        SipRequest sipRequestNoAuth = null;
        for (ClientTransaction clientTransaction: clientTransactions) {
            InviteClientTransaction inviteClientTransaction =
                (InviteClientTransaction)clientTransaction;
            SipRequest sipRequest = inviteClientTransaction.getRequest();
            SipHeaders sipHeaders = sipRequest.getSipHeaders();
            SipHeaderFieldName authorization = new SipHeaderFieldName(
                    RFC3261.HDR_AUTHORIZATION);
            SipHeaderFieldValue value = sipHeaders.get(authorization);
            if (value == null) {
                SipHeaderFieldName proxyAuthorization = new SipHeaderFieldName(
                        RFC3261.HDR_PROXY_AUTHORIZATION);
                value = sipHeaders.get(proxyAuthorization);
            }
            if (value != null) {
                return sipRequest;
            }
            sipRequestNoAuth = sipRequest;
        }
        return sipRequestNoAuth;
    }

    public void terminate(SipRequest sipRequest) {
        String callId = Utils.getMessageCallId(sipRequest);
        if (!guiClosedCallIds.contains(callId)) {
            guiClosedCallIds.add(callId);
        }
        Dialog dialog = dialogManager.getDialogFromCallId(callId);
        SipRequest originalRequest = getOriginalInvite(callId);
        if (dialog != null) {
            DialogState dialogState = dialog.getState();
            if (dialog.EARLY.equals(dialogState)) {
                initialRequestManager.createCancel(originalRequest,
                        midDialogRequestManager, profileUri);
            } else if (dialog.CONFIRMED.equals(dialogState)) {
                midDialogRequestManager.generateMidDialogRequest(
                        dialog, RFC3261.METHOD_BYE);
                guiClosedCallIds.remove(callId);
            }
        } else {
            InviteClientTransaction inviteClientTransaction =
                (InviteClientTransaction)transactionManager
                    .getClientTransaction(originalRequest);
            if (inviteClientTransaction == null) {
                Logger.error("cannot find invite client transaction" +
                        " for call " + callId);
            } else {
                SipResponse sipResponse =
                    inviteClientTransaction.getLastResponse();
                if (sipResponse != null) {
                    int statusCode = sipResponse.getStatusCode();
                    if (statusCode < RFC3261.CODE_200_OK) {
                        initialRequestManager.createCancel(sipRequest,
                                midDialogRequestManager, profileUri);
                    }
                }
            }
        }
        switch (userAgent.getMediaMode()) {
        case captureAndPlayback:
            CaptureRtpSender captureRtpSender = userAgent.getCaptureRtpSender();
            if (captureRtpSender != null) {
                captureRtpSender.stop();
                while (!captureRtpSender.isTerminated()) {
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        Logger.debug("sleep interrupted");
                    }
                }
                userAgent.setCaptureRtpSender(null);
            }
            IncomingRtpReader incomingRtpReader = userAgent.getIncomingRtpReader();
            if (incomingRtpReader != null) {
                incomingRtpReader.stop();
                userAgent.setIncomingRtpReader(null);
            }
            SoundManager soundManager = userAgent.getSoundManager();
            if (soundManager != null) {
                soundManager.closeLines();
            }
            break;
        case echo:
            Echo echo = userAgent.getEcho();
            if (echo != null) {
                echo.stop();
                userAgent.setEcho(null);
            }
            break;
        default:
            break;
        }
    }

    public InviteHandler getInviteHandler() {
        return initialRequestManager.getInviteHandler();
    }

    public List<String> getGuiClosedCallIds() {
        return guiClosedCallIds;
    }

}
