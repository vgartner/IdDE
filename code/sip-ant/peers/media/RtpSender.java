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
    
    Copyright 2008, 2009, 2010 Yohann Martineau 
*/

package org.idde.sip.peers.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.idde.sip.peers.Config;
import org.idde.sip.peers.Logger;
import org.idde.sip.peers.rtp.RtpPacket;
import org.idde.sip.peers.rtp.RtpSession;
import org.idde.sip.peers.sip.Utils;

public class RtpSender implements Runnable {

    private PipedInputStream encodedData;
    private RtpSession rtpSession;
    private boolean isStopped;
    private boolean isTerminated;
    private FileOutputStream rtpSenderInput;
    private boolean mediaDebug;
    
    public RtpSender(PipedInputStream encodedData, RtpSession rtpSession,
            boolean mediaDebug) {
        this.encodedData = encodedData;
        this.rtpSession = rtpSession;
        this.mediaDebug = mediaDebug;
        isStopped = false;
        isTerminated = false;
    }
    
    public void run() {
        if (mediaDebug) {
            SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String date = simpleDateFormat.format(new Date());
            String fileName = Utils.getPeersHome() + File.separator
                + Config.MEDIA_DIR + File.separator + date + "_rtp_sender.input";
            try {
                rtpSenderInput = new FileOutputStream(fileName);
            } catch (FileNotFoundException e) {
                Logger.error("cannot create file", e);
                return;
            }
        }
        RtpPacket rtpPacket = new RtpPacket();
        rtpPacket.setVersion(2);
        rtpPacket.setPadding(false);
        rtpPacket.setExtension(false);
        rtpPacket.setCsrcCount(0);
        rtpPacket.setMarker(false);
        rtpPacket.setPayloadType(0);
        Random random = new Random();
        int sequenceNumber = random.nextInt();
        rtpPacket.setSequenceNumber(sequenceNumber);
        rtpPacket.setSsrc(random.nextInt());
        int buf_size = Capture.BUFFER_SIZE / 2;
        byte[] buffer = new byte[buf_size];
        long counter = 0;
        
        while (!isStopped) {
            int numBytesRead;
            try {
                numBytesRead = encodedData.read(buffer, 0, buf_size);
            } catch (IOException e) {
                Logger.error("input/output error", e);
                return;
            }
            byte[] trimmedBuffer;
            if (numBytesRead < buffer.length) {
                trimmedBuffer = new byte[numBytesRead];
                System.arraycopy(buffer, 0, trimmedBuffer, 0, numBytesRead);
            } else {
                trimmedBuffer = buffer;
            }
            if (mediaDebug) {
                try {
                    rtpSenderInput.write(trimmedBuffer);
                } catch (IOException e) {
                    Logger.error("cannot write to file", e);
                    break;
                }
            }
            rtpPacket.setSequenceNumber(sequenceNumber + (int)counter);
            rtpPacket.setTimestamp(buf_size * counter++);
            rtpPacket.setData(trimmedBuffer);
            
            rtpSession.send(rtpPacket);
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                Logger.error("Thread interrupted", e);
                return;
            }
        }
        isTerminated = true;
        if (mediaDebug) {
            try {
                rtpSenderInput.close();
            } catch (IOException e) {
                Logger.error("cannot close file", e);
                return;
            }
        }
    }

    public synchronized void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

}
