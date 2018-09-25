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
import org.idde.sip.peers.media.CaptureRtpSender;
import org.idde.sip.peers.media.Echo;
import org.idde.sip.peers.media.IncomingRtpReader;
import org.idde.sip.peers.media.SoundManager;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.core.useragent.MidDialogRequestManager;
import org.idde.sip.peers.sip.core.useragent.SipListener;
import org.idde.sip.peers.sip.core.useragent.UserAgent;
import org.idde.sip.peers.sip.transaction.ServerTransaction;
import org.idde.sip.peers.sip.transaction.ServerTransactionUser;
import org.idde.sip.peers.sip.transaction.TransactionManager;
import org.idde.sip.peers.sip.transactionuser.Dialog;
import org.idde.sip.peers.sip.transactionuser.DialogManager;
import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.SipResponse;
import org.idde.sip.peers.sip.transport.TransportManager;

public class ByeHandler extends DialogMethodHandler
        implements ServerTransactionUser {

    public ByeHandler(UserAgent userAgent, DialogManager dialogManager,
            TransactionManager transactionManager,
            TransportManager transportManager) {
        super(userAgent, dialogManager, transactionManager, transportManager);
    }

    ////////////////////////////////////////////////
    // methods for UAC
    ////////////////////////////////////////////////
    
    public void preprocessBye(SipRequest sipRequest, Dialog dialog) {

        // 15.1.1
        
        String addrSpec = sipRequest.getRequestUri().toString();
        userAgent.getPeers().remove(addrSpec);
        
        dialog.receivedOrSentBye();
        
        dialogManager.removeDialog(dialog.getId());
        Logger.debug("removed dialog " + dialog.getId());
    }
    
    

    
    
    
    ////////////////////////////////////////////////
    // methods for UAS
    ////////////////////////////////////////////////
    
    public void handleBye(SipRequest sipRequest, Dialog dialog) {
        dialog.receivedOrSentBye();
        //String remoteUri = dialog.getRemoteUri();

        String addrSpec = sipRequest.getRequestUri().toString();
        userAgent.getPeers().remove(addrSpec);
        dialogManager.removeDialog(dialog.getId());
        Logger.debug("removed dialog " + dialog.getId());
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
        case none:
        default:
            break;
        }
        
        SipResponse sipResponse =
            MidDialogRequestManager.generateMidDialogResponse(
                    sipRequest,
                    dialog,
                    RFC3261.CODE_200_OK,
                    RFC3261.REASON_200_OK);
        
        // TODO determine port and transport for server transaction>transport
        // from initial invite
        // FIXME determine port and transport for server transaction>transport
        ServerTransaction serverTransaction = transactionManager
            .createServerTransaction(
                    sipResponse,
                    userAgent.getSipPort(),
                    RFC3261.TRANSPORT_UDP,
                    this,
                    sipRequest);
        
        serverTransaction.start();
        
        serverTransaction.receivedRequest(sipRequest);
        
        serverTransaction.sendReponse(sipResponse);
        
        dialogManager.removeDialog(dialog.getId());

        SipListener sipListener = userAgent.getSipListener();
        if (sipListener != null) {
            sipListener.remoteHangup(sipRequest);
        }

//        setChanged();
//        notifyObservers(sipRequest);
    }

    ///////////////////////////////////////
    //ServerTransactionUser methods
    ///////////////////////////////////////
    public void transactionFailure() {
        // TODO Auto-generated method stub
        
    }


    
    
}
