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
import org.idde.sip.peers.sip.core.useragent.handlers.ByeHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.CancelHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.InviteHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.OptionsHandler;
import org.idde.sip.peers.sip.core.useragent.handlers.RegisterHandler;
import org.idde.sip.peers.sip.syntaxencoding.NameAddress;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldName;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaderFieldValue;
import org.idde.sip.peers.sip.syntaxencoding.SipHeaders;
import org.idde.sip.peers.sip.syntaxencoding.SipURI;
import org.idde.sip.peers.sip.syntaxencoding.SipUriSyntaxException;
import org.idde.sip.peers.sip.transaction.TransactionManager;
import org.idde.sip.peers.sip.transactionuser.DialogManager;
import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.TransportManager;


public abstract class RequestManager {

    public static SipURI getDestinationUri(SipRequest sipRequest,
            Logger logger) {
        SipHeaders requestHeaders = sipRequest.getSipHeaders();
        SipURI destinationUri = null;
        SipHeaderFieldValue route = requestHeaders.get(
                new SipHeaderFieldName(RFC3261.HDR_ROUTE));
        if (route != null) {
            try {
                destinationUri = new SipURI(
                        NameAddress.nameAddressToUri(route.toString()));
            } catch (SipUriSyntaxException e) {
                logger.error("syntax error", e);
            }
        }
        if (destinationUri == null) {
            destinationUri = sipRequest.getRequestUri();
        }
        return destinationUri;
    }

    protected InviteHandler inviteHandler;
    protected CancelHandler cancelHandler;
    protected ByeHandler byeHandler;
    protected OptionsHandler optionsHandler;
    protected RegisterHandler registerHandler;
    
    protected UserAgent userAgent;
    protected TransactionManager transactionManager;
    protected TransportManager transportManager;
    protected Logger logger;
    
    public RequestManager(UserAgent userAgent,
            InviteHandler inviteHandler,
            CancelHandler cancelHandler,
            ByeHandler byeHandler,
            OptionsHandler optionsHandler,
            RegisterHandler registerHandler,
            DialogManager dialogManager,
            TransactionManager transactionManager,
            TransportManager transportManager,
            Logger logger) {
        this.userAgent = userAgent;
        this.inviteHandler = inviteHandler;
        this.cancelHandler = cancelHandler;
        this.byeHandler = byeHandler;
        this.optionsHandler = optionsHandler;
        this.registerHandler = registerHandler;
        this.transactionManager = transactionManager;
        this.transportManager = transportManager;
        this.logger = logger;
    }

    public InviteHandler getInviteHandler() {
        return inviteHandler;
    }

    public ByeHandler getByeHandler() {
        return byeHandler;
    }

    public RegisterHandler getRegisterHandler() {
        return registerHandler;
    }
    
}
