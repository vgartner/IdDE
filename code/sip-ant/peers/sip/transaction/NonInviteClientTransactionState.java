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
    
    Copyright 2007, 2008, 2009 Yohann Martineau 
*/

package org.idde.sip.peers.sip.transaction;

import org.idde.sip.peers.sip.AbstractState;

public abstract class NonInviteClientTransactionState extends AbstractState {

    protected NonInviteClientTransaction nonInviteClientTransaction;
    
    public NonInviteClientTransactionState(String id,
            NonInviteClientTransaction nonInviteClientTransaction) {
        super(id);
        this.nonInviteClientTransaction = nonInviteClientTransaction;
    }
    
    public void start() {}
    public void timerEFires() {}
    public void timerFFires() {}
    public void transportError() {}
    public void received1xx() {}
    public void received200To699() {}
    public void timerKFires() {}
    
}
