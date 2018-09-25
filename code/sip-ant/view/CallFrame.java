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
    
    Copyright 2010 Yohann Martineau 
*/

package org.idde.sip.view;

import org.idde.sip.model.CallListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.idde.sip.peers.sip.transport.SipRequest;
import org.idde.sip.peers.sip.transport.SipResponse;

public class CallFrame implements ActionListener, WindowListener {

    public static final String HANGUP_ACTION_COMMAND = "hangup";
    public static final String PICKUP_ACTION_COMMAND = "pickup";
    public static final String BUSY_HERE_ACTION_COMMAND = "busyhere";
    public static final String CLOSE_ACTION_COMMAND = "close";

    private CallFrameState state;

    public final CallFrameState INIT;
    public final CallFrameState UAC;
    public final CallFrameState UAS;
    public final CallFrameState RINGING;
    public final CallFrameState SUCCESS;
    public final CallFrameState FAILED;
    public final CallFrameState REMOTE_HANGUP;
    public final CallFrameState TERMINATED;

    private JFrame frame;
    private JPanel callPanel;
    private CallListener callFrameListener;
    private SipRequest sipRequest;

    public CallFrame(String remoteParty, String id,
            CallListener callFrameListener) {
        INIT = new CallFrameStateInit(id, this);
        UAC = new CallFrameStateUac(id, this);
        UAS = new CallFrameStateUas(id, this);
        RINGING = new CallFrameStateRinging(id, this);
        SUCCESS = new CallFrameStateSuccess(id, this);
        FAILED = new CallFrameStateFailed(id, this);
        REMOTE_HANGUP = new CallFrameStateRemoteHangup(id, this);
        TERMINATED = new CallFrameStateTerminated(id, this);
        state = INIT;
        this.callFrameListener = callFrameListener;
        frame = new JFrame(remoteParty);
        JLabel remotePartyLabel = new JLabel(remoteParty);
        Border remotePartyBorder = BorderFactory.createEmptyBorder(5, 5, 0, 5);
        remotePartyLabel.setBorder(remotePartyBorder);
        Container contentPane = frame.getContentPane();
        contentPane.add(remotePartyLabel, BorderLayout.PAGE_START);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
    }

    public void callClicked() {
        state.callClicked();
    }

    public void incomingCall() {
        state.incomingCall();
    }

    public void remoteHangup() {
        state.remoteHangup();
    }

    public void error(SipResponse sipResponse) {
        state.error(sipResponse);
    }

    public void calleePickup() {
        state.calleePickup();
    }

    public void ringing() {
        state.ringing();
    }

    void hangup() {
        if (callFrameListener != null) {
            callFrameListener.hangupClicked(sipRequest);
        }
    }

    void pickup() {
        if (callFrameListener != null && sipRequest != null) {
            callFrameListener.pickupClicked(sipRequest);
        }
    }

    void busyHere() {
        if (callFrameListener != null && sipRequest != null) {
            frame.dispose();
            callFrameListener.busyHereClicked(sipRequest);
            sipRequest = null;
        }
    }

    void close() {
        frame.dispose();
    }
    
    public void setState(CallFrameState state) {
        this.state.log(state);
        this.state = state;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setCallPanel(JPanel callPanel) {
        Container contentPane = frame.getContentPane();
        if (this.callPanel != null) {
            contentPane.remove(this.callPanel);
            frame.remove(this.callPanel);
        }
        contentPane.add(callPanel, BorderLayout.CENTER);
        frame.pack();
        this.callPanel = callPanel;
    }

    public void addPageEndLabel(String text) {
        Container container = frame.getContentPane();
        JLabel label = new JLabel(text);
        Border labelBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
        label.setBorder(labelBorder);
        container.add(label, BorderLayout.PAGE_END);
        frame.pack();
    }

    public void setSipRequest(SipRequest sipRequest) {
        this.sipRequest = sipRequest;
    }

    // action listener methods

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (HANGUP_ACTION_COMMAND.equals(actionCommand)) {
            state.hangupClicked();
        } else if (CLOSE_ACTION_COMMAND.equals(actionCommand)) {
            state.closeClicked();
        } else if (PICKUP_ACTION_COMMAND.equals(actionCommand)) {
            state.pickupClicked();
        } else if (BUSY_HERE_ACTION_COMMAND.equals(actionCommand)) {
            state.busyHereClicked();
        }
    }

    // window listener methods

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowClosed(WindowEvent e) {
        state.hangupClicked();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

}
