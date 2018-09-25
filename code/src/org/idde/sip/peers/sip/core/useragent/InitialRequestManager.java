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

import org.idde.sip.peers.Logger;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.Utils;
import org.idde.sip.peers.sip.core.useragent.handlers.ByeHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.CancelHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.InviteHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.OptionsHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.RegisterHandler;
import org.idde.sip.peers.sip.syntaxencoding.NameAddress;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldValue;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderParamName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaders;
import org.idde.sip.peers.sip.syntaxencoding.SipURI;
import org.idde.sip.peers.sip.syntaxencoding.SipUriSyntaxException;
import org.idde.sip.peers.sip.transaction.ClientTransaction;
import org.idde.sip.peers.sip.transaction.TransactionManager;
import org.idde.sip.peers.sip.transactionuser.DialogManager;
import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.TransportManager;

public class InitialRequestManager extends RequestManager {

    public InitialRequestManager(UserAgent userAgent,
            InviteHandler inviteHandler,
            CancelHandler cancelHandler,
            ByeHandler byeHandler,
            OptionsHandler optionsHandler,
            RegisterHandler registerHandler,
            DialogManager dialogManager,
            TransactionManager transactionManager,
            TransportManager transportManager,
            Logger logger) {
        super(userAgent,
                inviteHandler,
                cancelHandler,
                byeHandler,
                optionsHandler,
                registerHandler,
                dialogManager,
                transactionManager,
                transportManager,
                logger);
        registerHandler.setInitialRequestManager(this);
    }

    /**
     * gives a new request outside of a dialog
     * 
     * @param requestUri
     * @param method
     * @return
     * @throws SipUriSyntaxException 
     */
    public SipRequest getGenericRequest(String requestUri, String method,
            String profileUri) throws SipUriSyntaxException {
        //8.1.1
        SipRequest request = new SipRequest(method, new SipURI(requestUri));
        SipHeaders headers = request.getSipHeaders();
        //String hostAddress = utils.getMyAddress().getHostAddress();
        
        //Via
        
        //TODO no Via should be added directly by UAC, Via is normally added by Transaction layer
        
//        StringBuffer viaBuf = new StringBuffer();
//        viaBuf.append(RFC3261.DEFAULT_SIP_VERSION);
//        // TODO choose real transport
//        viaBuf.append("/UDP ");
//        viaBuf.append(hostAddress);
//        SipHeaderFieldValue via = new SipHeaderFieldValue(viaBuf.toString());
//        via.addParam(new SipHeaderParamName(RFC3261.PARAM_BRANCHID),
//                utils.generateBranchId());
//        headers.add(new SipHeaderFieldName(RFC3261.HDR_VIA), via);
        
        Utils.addCommonHeaders(headers);
        
        //To
        
        NameAddress to = new NameAddress(requestUri);
        headers.add(new SipHeaderFieldName(RFC3261.HDR_TO),
                new SipHeaderFieldValue(to.toString()));
        
        //From
        
        NameAddress fromNA = new NameAddress(profileUri);
        SipHeaderFieldValue from = new SipHeaderFieldValue(fromNA.toString());
        from.addParam(new SipHeaderParamName(RFC3261.PARAM_TAG),
                Utils.generateTag());
        headers.add(new SipHeaderFieldName(RFC3261.HDR_FROM), from);
        
        //Call-ID
        
        headers.add(new SipHeaderFieldName(RFC3261.HDR_CALLID),
                new SipHeaderFieldValue(Utils.generateCallID(
                        userAgent.getConfig().getLocalInetAddress())));
        
        //CSeq
        
        headers.add(new SipHeaderFieldName(RFC3261.HDR_CSEQ),
                new SipHeaderFieldValue(userAgent.generateCSeq(method)));
        
        return request;
    }
 
    public SipRequest createInitialRequest(String requestUri, String method,
            String profileUri) throws SipUriSyntaxException {
        return createInitialRequest(requestUri, method, profileUri, null);
    }
    
    public SipRequest createInitialRequest(String requestUri, String method,
            String profileUri, String callId) throws SipUriSyntaxException {
        
        return createInitialRequest(requestUri, method, profileUri, callId,
                null);
    }
    
    public SipRequest createInitialRequest(String requestUri, String method,
            String profileUri, String callId,
            MessageInterceptor messageInterceptor)
                throws SipUriSyntaxException {
        
        SipRequest sipRequest = createInitialRequestStart(requestUri, method,
                profileUri, callId);
        
        // TODO add route header for outbound proxy give it to xxxHandler to create
        // clientTransaction
        SipURI outboundProxy = userAgent.getOutboundProxy();
        if (outboundProxy != null) {
            NameAddress outboundProxyNameAddress =
                new NameAddress(outboundProxy.toString());
            sipRequest.getSipHeaders().add(new SipHeaderFieldName(RFC3261.HDR_ROUTE),
                    new SipHeaderFieldValue(outboundProxyNameAddress.toString()), 0);
        }
        ClientTransaction clientTransaction = null;
        if (RFC3261.METHOD_INVITE.equals(method)) {
            clientTransaction = inviteHandler.preProcessInvite(sipRequest);
        } else if (RFC3261.METHOD_REGISTER.equals(method)) {
            clientTransaction = registerHandler.preProcessRegister(sipRequest);
        }
        createInitialRequestEnd(sipRequest, clientTransaction, profileUri,
                messageInterceptor);
        return sipRequest;
    }
    
    private SipRequest createInitialRequestStart(String requestUri, String method,
            String profileUri, String callId) throws SipUriSyntaxException {
        SipRequest sipRequest = getGenericRequest(requestUri, method,
                profileUri);
        if (callId != null) {
            SipHeaderFieldValue callIdValue = sipRequest.getSipHeaders().get(
                    new SipHeaderFieldName(RFC3261.HDR_CALLID));
            callIdValue.setValue(callId);
        }
        return sipRequest;
    }
    
    private void createInitialRequestEnd(SipRequest sipRequest,
            ClientTransaction clientTransaction, String profileUri,
            MessageInterceptor messageInterceptor) {
        addContact(sipRequest, clientTransaction.getContact(), profileUri);
        if (messageInterceptor != null) {
            messageInterceptor.postProcess(sipRequest);
        }
        // TODO create message receiver on client transport port
        if (clientTransaction != null) {
            clientTransaction.start();
        } else {
            logger.error("method not supported");
        }
    }
    
    public void createCancel(SipRequest inviteRequest, MidDialogRequestManager midDialogRequestManager, String profileUri) {

        SipHeaderFieldValue callId = null;

        try
        {
            SipHeaders inviteHeaders = inviteRequest.getSipHeaders();
            callId = inviteHeaders.get(new SipHeaderFieldName(RFC3261.HDR_CALLID));
        }
        catch (Exception e)
        {
        }
        
        SipRequest sipRequest;

        try {
            sipRequest = createInitialRequestStart(
                    inviteRequest.getRequestUri().toString(), RFC3261.METHOD_CANCEL,
                    profileUri, callId.getValue());
        } catch (SipUriSyntaxException e) {
            logger.error("syntax error", e);
            return;
        }
        
        ClientTransaction clientTransaction = null;
            clientTransaction = cancelHandler.preProcessCancel(sipRequest,
                    inviteRequest, midDialogRequestManager);
        if (clientTransaction != null) {
            createInitialRequestEnd(sipRequest, clientTransaction, profileUri, null);
        }
        
        
    }

    public void manageInitialRequest(SipRequest sipRequest) {
        SipHeaders headers = sipRequest.getSipHeaders();
        
        // TODO authentication
        
        //method inspection
        
        if (!UAS.SUPPORTED_METHODS.contains(sipRequest.getMethod())) {
            //TODO generate 405 (using 8.2.6) with Allow header (20.5) and send it
        }

        
        SipHeaderFieldValue contentType =
            headers.get(new SipHeaderFieldName(RFC3261.HDR_CONTENT_TYPE));
        if (contentType != null) {
            if (!RFC3261.CONTENT_TYPE_SDP.equals(contentType.getValue())) {
                //TODO generate 415 with a Accept header listing supported content types
                //8.2.3
            }
        }

        
        //etc.
        
        
        //TODO create server transaction
        String method = sipRequest.getMethod();
        if (RFC3261.METHOD_INVITE.equals(method)) {
            inviteHandler.handleInitialInvite(sipRequest);
        } else if (RFC3261.METHOD_CANCEL.equals(method)) {
            cancelHandler.handleCancel(sipRequest);
        } else if (RFC3261.METHOD_OPTIONS.equals(method)) {
            optionsHandler.handleOptions(sipRequest);
        }
    }

    public void addContact(SipRequest sipRequest, String contactEnd,
            String profileUri) {
        SipHeaders sipHeaders = sipRequest.getSipHeaders();
        
        
        
        //Contact
        
        StringBuffer contactBuf = new StringBuffer();
        contactBuf.append(RFC3261.SIP_SCHEME);
        contactBuf.append(RFC3261.SCHEME_SEPARATOR);
        String userPart = Utils.getUserPart(profileUri);
        contactBuf.append(userPart);
        contactBuf.append(RFC3261.AT);
        contactBuf.append(contactEnd);

        NameAddress contactNA = new NameAddress(contactBuf.toString());
        SipHeaderFieldValue contact =
            new SipHeaderFieldValue(contactNA.toString());
        sipHeaders.add(new SipHeaderFieldName(RFC3261.HDR_CONTACT),
                new SipHeaderFieldValue(contact.toString()));
    }

}
