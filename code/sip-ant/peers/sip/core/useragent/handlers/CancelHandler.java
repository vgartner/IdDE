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

import org.idde.sip.peers.Logger;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.Utils;
import org.idde.sip.peers.sip.core.useragent.MidDialogRequestManager;
import org.idde.sip.peers.sip.core.useragent.SipListener;
import org.idde.sip.peers.sip.core.useragent.UserAgent;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldValue;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderParamName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaders;
import org.idde.sip.peers.sip.transaction.ClientTransaction;
import org.idde.sip.peers.sip.transaction.InviteClientTransaction;
import org.idde.sip.peers.sip.transaction.InviteServerTransaction;
import org.idde.sip.peers.sip.transaction.ServerTransaction;
import org.idde.sip.peers.sip.transaction.ServerTransactionUser;
import org.idde.sip.peers.sip.transaction.TransactionManager;
import org.idde.sip.peers.sip.transactionuser.Dialog;
import org.idde.sip.peers.sip.transactionuser.DialogManager;
import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.SipResponse;
import org.idde.sip.peers.sip.transport.TransportManager;

public class CancelHandler extends DialogMethodHandler
        implements ServerTransactionUser {

    public CancelHandler(UserAgent userAgent, DialogManager dialogManager,
            TransactionManager transactionManager,
            TransportManager transportManager) {
        super(userAgent, dialogManager, transactionManager, transportManager);
    }

    //////////////////////////////////////////////////////////
    // UAS methods
    //////////////////////////////////////////////////////////

    public void handleCancel(SipRequest sipRequest) {
        SipHeaderFieldValue topVia = Utils.getTopVia(sipRequest);
        String branchId = topVia.getParam(new SipHeaderParamName(
                RFC3261.PARAM_BRANCH));
        InviteServerTransaction inviteServerTransaction =
            (InviteServerTransaction)transactionManager
                .getServerTransaction(branchId,RFC3261.METHOD_INVITE);
        SipResponse cancelResponse;
        if (inviteServerTransaction == null) {
            //TODO generate CANCEL 481 Call Leg/Transaction Does Not Exist
            cancelResponse = buildGenericResponse(sipRequest,
                    RFC3261.CODE_481_CALL_TRANSACTION_DOES_NOT_EXIST,
                    RFC3261.REASON_481_CALL_TRANSACTION_DOES_NOT_EXIST);
        } else {
            cancelResponse = buildGenericResponse(sipRequest,
                    RFC3261.CODE_200_OK, RFC3261.REASON_200_OK);
        }
        ServerTransaction cancelServerTransaction = transactionManager
                .createServerTransaction(cancelResponse,
                        userAgent.getSipPort(),
                        RFC3261.TRANSPORT_UDP, this, sipRequest);
        cancelServerTransaction.start();
        cancelServerTransaction.receivedRequest(sipRequest);
        cancelServerTransaction.sendReponse(cancelResponse);
        if (cancelResponse.getStatusCode() != RFC3261.CODE_200_OK) {
            return;
        }
        
        SipResponse lastResponse = inviteServerTransaction.getLastResponse();
        if (lastResponse != null &&
                lastResponse.getStatusCode() >= RFC3261.CODE_200_OK) {
            return;
        }
        
        SipResponse inviteResponse = buildGenericResponse(
                inviteServerTransaction.getRequest(),
                RFC3261.CODE_487_REQUEST_TERMINATED,
                RFC3261.REASON_487_REQUEST_TERMINATED);
        inviteServerTransaction.sendReponse(inviteResponse);
        
        Dialog dialog = dialogManager.getDialog(lastResponse);
        dialog.receivedOrSent300To699();

        SipListener sipListener = userAgent.getSipListener();
        if (sipListener != null) {
            sipListener.remoteHangup(sipRequest);
        }
    }
    
    //////////////////////////////////////////////////////////
    // UAC methods
    //////////////////////////////////////////////////////////
    
    public ClientTransaction preProcessCancel(SipRequest cancelGenericRequest,
            SipRequest inviteRequest,
            MidDialogRequestManager midDialogRequestManager) {
        //TODO
        //p. 54 §9.1
        
        SipHeaders cancelHeaders = cancelGenericRequest.getSipHeaders();
        SipHeaders inviteHeaders = inviteRequest.getSipHeaders();
        
        //cseq
        SipHeaderFieldName cseqName = new SipHeaderFieldName(RFC3261.HDR_CSEQ);
        SipHeaderFieldValue cancelCseq = cancelHeaders.get(cseqName);
        SipHeaderFieldValue inviteCseq = inviteHeaders.get(cseqName);
        cancelCseq.setValue(inviteCseq.getValue().replace(RFC3261.METHOD_INVITE,
                RFC3261.METHOD_CANCEL));

        
        //from
        SipHeaderFieldName fromName = new SipHeaderFieldName(RFC3261.HDR_FROM);
        SipHeaderFieldValue cancelFrom = cancelHeaders.get(fromName);
        SipHeaderFieldValue inviteFrom = inviteHeaders.get(fromName);
        cancelFrom.setValue(inviteFrom.getValue());
        SipHeaderParamName tagParam = new SipHeaderParamName(RFC3261.PARAM_TAG);
        cancelFrom.removeParam(tagParam);
        cancelFrom.addParam(tagParam, inviteFrom.getParam(tagParam));
        
        //top-via
//        cancelHeaders.add(new SipHeaderFieldName(RFC3261.HDR_VIA),
//                Utils.getInstance().getTopVia(inviteRequest));
        SipHeaderFieldValue topVia = Utils.getTopVia(inviteRequest);
        String branchId = topVia.getParam(new SipHeaderParamName(RFC3261.PARAM_BRANCH));
        
        //route
        SipHeaderFieldName routeName = new SipHeaderFieldName(RFC3261.HDR_ROUTE);
        SipHeaderFieldValue inviteRoute = inviteHeaders.get(routeName);
        if (inviteRoute != null) {
            cancelHeaders.add(routeName, inviteRoute);
        }
        
        
        InviteClientTransaction inviteClientTransaction =
            (InviteClientTransaction)transactionManager.getClientTransaction(
                    inviteRequest);
        if (inviteClientTransaction != null) {
            SipResponse lastResponse = inviteClientTransaction.getLastResponse();
            if (lastResponse != null &&
                    lastResponse.getStatusCode() >= RFC3261.CODE_200_OK) {
                return null;
            }
        } else {
            Logger.error("cannt retrieve invite client transaction for"
                    + " request " + inviteRequest);
        }

        return midDialogRequestManager.createNonInviteClientTransaction(
                cancelGenericRequest, branchId);
    }

    public void transactionFailure() {
        // TODO Auto-generated method stub
        
    }
}
