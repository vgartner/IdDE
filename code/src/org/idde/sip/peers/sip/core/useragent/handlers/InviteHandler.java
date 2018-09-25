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
    
    Copyright 2007, 2008, 2009, 2010, 2011 Yohann Martineau 
*/

package org.idde.sip.peers.sip.core.useragent.handlers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.idde.sip.peers.Logger;
import org.idde.sip.peers.sdp.Codec;
import org.idde.sip.peers.sdp.MediaDestination;
import org.idde.sip.peers.sdp.NoCodecException;
import org.idde.sip.peers.sdp.SessionDescription;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.Utils;
import org.idde.sip.peers.sip.core.useragent.MidDialogRequestManager;
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
import org.idde.sip.peers.sip.transaction.InviteClientTransaction;
import org.idde.sip.peers.sip.transaction.InviteServerTransaction;
import org.idde.sip.peers.sip.transaction.ServerTransaction;
import org.idde.sip.peers.sip.transaction.ServerTransactionUser;
import org.idde.sip.peers.sip.transaction.Transaction;
import org.idde.sip.peers.sip.transaction.TransactionManager;
import org.idde.sip.peers.sip.transactionuser.Dialog;
import org.idde.sip.peers.sip.transactionuser.DialogManager;
import org.idde.sip.peers.sip.transport.MessageSender;
import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.SipResponse;
import org.idde.sip.peers.sip.transport.TransportManager;

public class InviteHandler extends DialogMethodHandler
        implements ServerTransactionUser, ClientTransactionUser {

    private MediaDestination mediaDestination;
    
    public InviteHandler(UserAgent userAgent,
            DialogManager dialogManager,
            TransactionManager transactionManager,
            TransportManager transportManager, Logger logger) {
        super(userAgent, dialogManager, transactionManager, transportManager,
                logger);
    }
    
    
    //////////////////////////////////////////////////////////
    // UAS methods
    //////////////////////////////////////////////////////////

    public void handleInitialInvite(SipRequest sipRequest) {
        
        //generate 180 Ringing
        SipResponse sipResponse = buildGenericResponse(sipRequest,
                RFC3261.CODE_180_RINGING, RFC3261.REASON_180_RINGING);
        Dialog dialog = buildDialogForUas(sipResponse, sipRequest);
        //here dialog is already stored in dialogs in DialogManager
        
        InviteServerTransaction inviteServerTransaction = (InviteServerTransaction)
            transactionManager.createServerTransaction(sipResponse,
                    userAgent.getSipPort(), RFC3261.TRANSPORT_UDP, this,
                    sipRequest);
        
        inviteServerTransaction.start();
        
        inviteServerTransaction.receivedRequest(sipRequest);
        
        //TODO send 180 more than once
        inviteServerTransaction.sendReponse(sipResponse);

        dialog.receivedOrSent1xx();

        SipListener sipListener = userAgent.getSipListener();
        if (sipListener != null) {
            sipListener.incomingCall(sipRequest, sipResponse);
        }

        List<String> peers = userAgent.getPeers();
        String responseTo = sipRequest.getSipHeaders().get(
                new SipHeaderFieldName(RFC3261.HDR_FROM)).getValue();
        if (!peers.contains(responseTo)) {
            peers.add(responseTo);
        }
        
    }
    
    public void handleReInvite(SipRequest sipRequest, Dialog dialog) {
        SipHeaders sipHeaders = sipRequest.getSipHeaders();

        // 12.2.2 update dialog
        SipHeaderFieldValue contact =
            sipHeaders.get(new SipHeaderFieldName(RFC3261.HDR_CONTACT));
        if (contact != null) {
            String contactStr = contact.getValue();
            if (contactStr.indexOf(RFC3261.LEFT_ANGLE_BRACKET) > -1) {
                contactStr = NameAddress.nameAddressToUri(contactStr);
            }
            dialog.setRemoteTarget(contactStr);
        }


        // update session
        sendSuccessfulResponse(sipRequest, dialog);
        
    }

    private void sendSuccessfulResponse(SipRequest sipRequest, Dialog dialog) {
        SipHeaders reqHeaders = sipRequest.getSipHeaders();
        SipHeaderFieldValue contentType =
            reqHeaders.get(new SipHeaderFieldName(RFC3261.HDR_CONTENT_TYPE));
        
        
        if (RFC3261.CONTENT_TYPE_SDP.equals(contentType)) {
            //TODO
//            String sdpResponse;
//            try {
//                sdpResponse = sdpManager.handleOffer(
//                        new String(sipRequest.getBody()));
//            } catch (NoCodecException e) {
//                sdpResponse = sdpManager.generateErrorResponse();
//            }
        } else {
            // TODO manage empty bodies and non-application/sdp content type
        }


        //TODO if mode autoanswer just send 200 without asking any question
        SipResponse sipResponse =
            MidDialogRequestManager.generateMidDialogResponse(
                    sipRequest,
                    dialog,
                    RFC3261.CODE_200_OK,
                    RFC3261.REASON_200_OK);

        // TODO 13.3 dialog invite-specific processing
        
        // TODO timer if there is an Expires header in INVITE
        
        // TODO 3xx
        
        // TODO 486 or 600
        
        byte[] offerBytes = sipRequest.getBody();
        SessionDescription answer;
        try {
            if (offerBytes != null && RFC3261.CONTENT_TYPE_SDP.equals(
                    contentType.getValue())) {
                // create response in 200
                try {
                    SessionDescription offer = sdpManager.parse(offerBytes);
                    answer = sdpManager.createSessionDescription(offer);
                    mediaDestination = sdpManager.getMediaDestination(offer);
                } catch (NoCodecException e) {
                    answer = sdpManager.createSessionDescription(null);
                }
            } else {
                // create offer in 200 (never tested...)
                answer = sdpManager.createSessionDescription(null);
            }
            sipResponse.setBody(answer.toString().getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        
        SipHeaders respHeaders = sipResponse.getSipHeaders();
        respHeaders.add(new SipHeaderFieldName(RFC3261.HDR_CONTENT_TYPE),
                new SipHeaderFieldValue(RFC3261.CONTENT_TYPE_SDP));
        
        ArrayList<String> routeSet = dialog.getRouteSet();
        if (routeSet != null) {
            SipHeaderFieldName recordRoute = new SipHeaderFieldName(RFC3261.HDR_RECORD_ROUTE);
            for (String route : routeSet) {
                respHeaders.add(recordRoute, new SipHeaderFieldValue(route));
            }
        }
        
        // TODO determine port and transport for server transaction>transport
        // from initial invite
        // FIXME determine port and transport for server transaction>transport
        ServerTransaction serverTransaction = transactionManager
                .getServerTransaction(sipRequest);
        if (serverTransaction == null) {
            // in re-INVITE case, no serverTransaction has been created
            serverTransaction = (InviteServerTransaction)
            transactionManager.createServerTransaction(sipResponse,
                    userAgent.getSipPort(), RFC3261.TRANSPORT_UDP, this,
                    sipRequest);
        }
        serverTransaction.start();
        
        serverTransaction.receivedRequest(sipRequest);
        
        serverTransaction.sendReponse(sipResponse);
        // TODO manage retransmission of the response (send to the transport)
        // until ACK arrives, if no ACK is received within 64*T1, confirm dialog
        // and terminate it with a BYE

//        logger.getInstance().debug("before dialog.receivedOrSent2xx();");
//        logger.getInstance().debug("dialog state: " + dialog.getState());
    }

    public void acceptCall(SipRequest sipRequest, Dialog dialog) {
        sendSuccessfulResponse(sipRequest, dialog);
        
        dialog.receivedOrSent2xx();
//        logger.getInstance().debug("dialog state: " + dialog.getState());
//        logger.getInstance().debug("after dialog.receivedOrSent2xx();");
        
//        setChanged();
//        notifyObservers(sipRequest);
    }
    
    public void rejectCall(SipRequest sipRequest) {
        //TODO generate 486, etc.
        SipHeaders reqHeaders = sipRequest.getSipHeaders();
        SipHeaderFieldValue callId = reqHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_CALLID));
        
        Dialog dialog = dialogManager.getDialog(callId.getValue());
        
        //TODO manage auto reject Do not disturb (DND)
        SipResponse sipResponse =
            MidDialogRequestManager.generateMidDialogResponse(
                    sipRequest,
                    dialog,
                    RFC3261.CODE_486_BUSYHERE,
                    RFC3261.REASON_486_BUSYHERE);
        
        // TODO determine port and transport for server transaction>transport
        // from initial invite
        // FIXME determine port and transport for server transaction>transport
        ServerTransaction serverTransaction = transactionManager
                .getServerTransaction(sipRequest);
        
        serverTransaction.start();
        
        serverTransaction.receivedRequest(sipRequest);
        
        serverTransaction.sendReponse(sipResponse);
        
        dialog.receivedOrSent300To699();
        
//        setChanged();
//        notifyObservers(sipRequest);
    }
    
    //////////////////////////////////////////////////////////
    // UAC methods
    //////////////////////////////////////////////////////////
    
    public ClientTransaction preProcessInvite(SipRequest sipRequest)
            throws SipUriSyntaxException {
        
        //8.1.2
        SipHeaders requestHeaders = sipRequest.getSipHeaders();
        SipURI destinationUri = RequestManager.getDestinationUri(sipRequest,
                logger);

        //TODO if header route is present, addrspec = toproute.nameaddress.addrspec

        String transport = RFC3261.TRANSPORT_UDP;
        Hashtable<String, String> params = destinationUri.getUriParameters();
        if (params != null) {
            String reqUriTransport = params.get(RFC3261.PARAM_TRANSPORT);
            if (reqUriTransport != null) {
                transport = reqUriTransport; 
            }
        }
        int port = destinationUri.getPort();
        if (port == SipURI.DEFAULT_PORT) {
            port = RFC3261.TRANSPORT_DEFAULT_PORT;
        }
        SipURI sipUri = userAgent.getConfig().getOutboundProxy();
        if (sipUri == null) {
            sipUri = destinationUri;
        }
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(sipUri.getHost());
        } catch (UnknownHostException e) {
            throw new SipUriSyntaxException("unknown host: "
                    + sipUri.getHost(), e);
        }
        ClientTransaction clientTransaction = transactionManager
                .createClientTransaction(sipRequest, inetAddress,
                    port, transport, null, this);
        try {
            SessionDescription sessionDescription =
                sdpManager.createSessionDescription(null);
            sipRequest.setBody(sessionDescription.toString().getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        requestHeaders.add(new SipHeaderFieldName(RFC3261.HDR_CONTENT_TYPE),
                new SipHeaderFieldValue(RFC3261.CONTENT_TYPE_SDP));
        return clientTransaction;
    }
    
    public void preProcessReInvite(SipRequest sipRequest) {
        //TODO
    }

    //////////////////////////////////////////////////////////
    // ClientTransactionUser methods
    //////////////////////////////////////////////////////////

    public void errResponseReceived(final SipResponse sipResponse) {
        Dialog dialog = dialogManager.getDialog(sipResponse);
        if (dialog != null) {
            dialog.receivedOrSent300To699();
        }
        int statusCode = sipResponse.getStatusCode();
        if (statusCode == RFC3261.CODE_401_UNAUTHORIZED
                || statusCode == RFC3261.CODE_407_PROXY_AUTHENTICATION_REQUIRED
                && !challenged) {
            InviteClientTransaction inviteClientTransaction =
                (InviteClientTransaction)
                transactionManager.getClientTransaction(sipResponse);
            SipRequest sipRequest = inviteClientTransaction.getRequest();
            String password = userAgent.getConfig().getPassword();
            if (password != null && !"".equals(password.trim())) {
                challengeManager.handleChallenge(sipRequest,
                        sipResponse);
            }
            challenged = true;
            return;
        } else {
            challenged = false;
        }
        SipListener sipListener = userAgent.getSipListener();
        if (sipListener != null) {
            sipListener.error(sipResponse);
        }
        List<String> guiClosedCallIds = userAgent.getUac().getGuiClosedCallIds();
        String callId = Utils.getMessageCallId(sipResponse);
        if (guiClosedCallIds.contains(callId)) {
            guiClosedCallIds.remove(callId);
        }
    }

    public void provResponseReceived(SipResponse sipResponse, Transaction transaction) {
        // dialog may have already been created if a previous 1xx has
        // already been received
        Dialog dialog = dialogManager.getDialog(sipResponse);
        boolean isFirstProvRespWithToTag = false;
        if (dialog == null) {
            SipHeaderFieldValue to = sipResponse.getSipHeaders().get(
                    new SipHeaderFieldName(RFC3261.HDR_TO));
            String toTag = to.getParam(new SipHeaderParamName(RFC3261.PARAM_TAG));
            if (toTag != null) {
                dialog = dialogManager.createDialog(sipResponse);
                isFirstProvRespWithToTag = true;
            } else {
                //TODO maybe stop retransmissions
            }
        }
        
        if (dialog != null) {
            buildOrUpdateDialogForUac(sipResponse, transaction);
        }
        
//        
//        if (dialog == null && sipResponse.getStatusCode() != RFC3261.CODE_100_TRYING) {
//            logger.debug("dialog not found for prov response");
//            isFirstProvRespWithToTag = true;
//            SipHeaderFieldValue to = sipResponse.getSipHeaders()
//                .get(new SipHeaderFieldName(RFC3261.HDR_TO));
//            String toTag = to.getParam(new SipHeaderParamName(RFC3261.PARAM_TAG));
//            if (toTag != null) {
//                dialog = buildOrUpdateDialogForUac(sipResponse, transaction);
//            }
//        }
        //TODO this notification is probably useless because dialog state modification
        //     thereafter always notify dialog observers
        if (isFirstProvRespWithToTag) {
            SipListener sipListener = userAgent.getSipListener();
            if (sipListener != null) {
                sipListener.ringing(sipResponse);
            }
            dialog.receivedOrSent1xx();
        }
        List<String> guiClosedCallIds = userAgent.getUac().getGuiClosedCallIds();
        String callId = Utils.getMessageCallId(sipResponse);
        if (guiClosedCallIds.contains(callId)) {
            SipRequest sipRequest = transaction.getRequest();
            logger.debug("cancel after prov response: sipRequest " + sipRequest
                    + ", sipResponse " + sipResponse);
            userAgent.getUac().terminate(sipRequest);
        }
    }

    public void successResponseReceived(SipResponse sipResponse, Transaction transaction) {
        SipHeaders responseHeaders = sipResponse.getSipHeaders();
        String cseq = responseHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_CSEQ)).getValue();
        String method = cseq.substring(cseq.trim().lastIndexOf(' ') + 1);
        if (!RFC3261.METHOD_INVITE.equals(method)) {
            return;
        }
        
        challenged = false;
        
        
        
        
        
        
        //13.2.2.4

        List<String> peers = userAgent.getPeers();
        String responseTo = responseHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_TO)).getValue();
        if (!peers.contains(responseTo)) {
            peers.add(responseTo);
            //timer used to purge dialogs which are not confirmed
            //after a given time
            ackTimer.schedule(new AckTimerTask(responseTo),
                    64 * RFC3261.TIMER_T1);
        }
        
        Dialog dialog = dialogManager.getDialog(sipResponse);
        
        if (dialog != null) {
            //dialog already created with a 180 for example
            dialog.setRouteSet(computeRouteSet(sipResponse.getSipHeaders()));
        }
        dialog = buildOrUpdateDialogForUac(sipResponse, transaction);
        
        SipListener sipListener = userAgent.getSipListener();
        if (sipListener != null) {
            sipListener.calleePickup(sipResponse);
        }

        //added for media
        SessionDescription sessionDescription =
            sdpManager.parse(sipResponse.getBody());
        try {
            mediaDestination = sdpManager.getMediaDestination(sessionDescription);
        } catch (NoCodecException e) {
            logger.error(e.getMessage(), e);
        }
        String remoteAddress = mediaDestination.getDestination();
        int remotePort = mediaDestination.getPort();
        Codec codec = mediaDestination.getCodec();
        String localAddress = userAgent.getConfig()
            .getLocalInetAddress().getHostAddress();

        userAgent.getMediaManager().successResponseReceived(localAddress,
                remoteAddress, remotePort, codec);
        
        //switch to confirmed state
        dialog.receivedOrSent2xx();
        
        //generate ack
        //p. 82 §3
        SipRequest ack = dialog.buildSubsequentRequest(RFC3261.METHOD_ACK);
        
        
        //update CSeq
        
        SipHeaders ackHeaders = ack.getSipHeaders();
        SipHeaderFieldName cseqName = new SipHeaderFieldName(RFC3261.HDR_CSEQ);
        SipHeaderFieldValue ackCseq = ackHeaders.get(cseqName);
        
        SipRequest request = transaction.getRequest();
        SipHeaders requestHeaders = request.getSipHeaders();
        SipHeaderFieldValue requestCseq = requestHeaders.get(cseqName);
        
        ackCseq.setValue(requestCseq.toString().replace(RFC3261.METHOD_INVITE, RFC3261.METHOD_ACK));
        
        //add Via with only the branchid parameter
        
        SipHeaderFieldValue via = new SipHeaderFieldValue("");
        SipHeaderFieldValue respTopVia = Utils.getTopVia(sipResponse);
        SipHeaderParamName branchIdName = new SipHeaderParamName(RFC3261.PARAM_BRANCH);
        via.addParam(branchIdName, respTopVia.getParam(branchIdName));
        
        ackHeaders.add(new SipHeaderFieldName(RFC3261.HDR_VIA), via, 0);
        
        //TODO authentication headers
        
        if (request.getBody() == null && sipResponse.getBody() != null) {
            //TODO add a real SDP answer
            ack.setBody(sipResponse.getBody());
        }

        //TODO check if sdp is acceptable

        SipURI destinationUri = RequestManager.getDestinationUri(ack, logger);

        //TODO if header route is present, addrspec = toproute.nameaddress.addrspec
        
        String transport = RFC3261.TRANSPORT_UDP;
        Hashtable<String, String> params = destinationUri.getUriParameters();
        if (params != null) {
            String reqUriTransport = params.get(RFC3261.PARAM_TRANSPORT);
            if (reqUriTransport != null) {
                transport = reqUriTransport; 
            }
        }
        int port = destinationUri.getPort();
        if (port == SipURI.DEFAULT_PORT) {
            port = RFC3261.TRANSPORT_DEFAULT_PORT;
        }

        SipURI sipUri = userAgent.getConfig().getOutboundProxy();
        if (sipUri == null) {
            sipUri = destinationUri;
        }
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(sipUri.getHost());
        } catch (UnknownHostException e) {
            logger.error("unknown host: " + sipUri.getHost(), e);
            return;
        }
        try {
            MessageSender sender = transportManager.createClientTransport(
                    ack, inetAddress, port, transport);
            sender.sendMessage(ack);
        } catch (IOException e) {
            logger.error("input/output error", e);
        }
        
        
        
        List<String> guiClosedCallIds = userAgent.getUac().getGuiClosedCallIds();
        String callId = Utils.getMessageCallId(sipResponse);
        if (guiClosedCallIds.contains(callId)) {
            userAgent.getUac().terminate(request);
        }
        
        
    }

    public void handleAck(SipRequest ack, Dialog dialog) {
        // TODO determine if ACK is ACK of an initial INVITE or a re-INVITE
        // in first case, captureRtpSender and incomingRtpReader must be
        // created, in the second case, they must be updated.
        logger.debug("handleAck");

        String destAddress = mediaDestination.getDestination();
        int destPort = mediaDestination.getPort();
        Codec codec = mediaDestination.getCodec();
        
        userAgent.getMediaManager().handleAck(destAddress, destPort, codec);
    }

    public void transactionTimeout(ClientTransaction clientTransaction) {
        // TODO Auto-generated method stub
        
    }

    public void transactionTransportError() {
        // TODO Auto-generated method stub
        
    }
    
    //////////////////////////////////////////////////////////
    // ServerTransactionUser methods
    //////////////////////////////////////////////////////////
    
    public void transactionFailure() {
        // TODO manage transaction failure (ACK was not received)
        
    }
    

}
