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

package org.idde.sip.peers.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import org.idde.sip.peers.Config;
import org.idde.sip.peers.Logger;
import org.idde.sip.peers.sip.Utils;

public class SoundManager {

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private SourceDataLine sourceDataLine;
    private DataLine.Info targetInfo;
    private DataLine.Info sourceInfo;
    private FileOutputStream microphoneOutput;
    private FileOutputStream speakerInput;
    private boolean mediaDebug;
    
    public SoundManager(boolean mediaDebug) {
        // linear PCM 8kHz, 16 bits signed, mono-channel, little endian
        audioFormat = new AudioFormat(8000, 16, 1, true, false);
        targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        this.mediaDebug = mediaDebug;
    }

    public void openAndStartLines() {
        Logger.debug("openAndStartLines");
        if (mediaDebug) {
            SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String date = simpleDateFormat.format(new Date());
            StringBuffer buf = new StringBuffer();
            buf.append(Utils.getPeersHome()).append(File.separator);
            buf.append(Config.MEDIA_DIR).append(File.separator);
            buf.append(date).append("_");
            buf.append(audioFormat.getEncoding()).append("_");
            buf.append(audioFormat.getSampleRate()).append("_");
            buf.append(audioFormat.getSampleSizeInBits()).append("_");
            buf.append(audioFormat.getChannels()).append("_");
            buf.append(audioFormat.isBigEndian() ? "be" : "le");
            try {
                microphoneOutput = new FileOutputStream(buf.toString()
                        + "_microphone.output");
                speakerInput = new FileOutputStream(buf.toString()
                        + "_speaker.input");
            } catch (FileNotFoundException e) {
                Logger.error("cannot create file", e);
                return;
            }
        }
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            Logger.error("target line unavailable", e);
            return;
        }
        targetDataLine.start();
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            Logger.error("source line unavailable", e);
            return;
        }
        sourceDataLine.start();
    }

    public synchronized void closeLines() {
        Logger.debug("closeLines");
        if (microphoneOutput != null) {
            try {
                microphoneOutput.close();
            } catch (IOException e) {
                Logger.error("cannot close file", e);
            }
            microphoneOutput = null;
        }
        if (speakerInput != null) {
            try {
                speakerInput.close();
            } catch (IOException e) {
                Logger.error("cannot close file", e);
            }
            speakerInput = null;
        }
        if (targetDataLine != null) {
            targetDataLine.close();
            targetDataLine = null;
        }
        if (sourceDataLine != null) {
            sourceDataLine.drain();
            sourceDataLine.stop();
            sourceDataLine.close();
            sourceDataLine = null;
        }
    }

    public int readData(byte[] buffer, int offset, int length) {
        int numberOfBytesRead = targetDataLine.read(buffer, offset, length);
        if (mediaDebug) {
            try {
                microphoneOutput.write(buffer, offset, numberOfBytesRead);
            } catch (IOException e) {
                Logger.error("cannot write to file", e);
                return -1;
            }
        }
        return numberOfBytesRead;
    }

    public int writeData(byte[] buffer, int offset, int length) {
        int numberOfBytesWritten = sourceDataLine.write(buffer, offset, length);
        if (mediaDebug) {
            try {
                speakerInput.write(buffer, offset, numberOfBytesWritten);
            } catch (IOException e) {
                Logger.error("cannot write to file", e);
                return -1;
            }
        }
        return numberOfBytesWritten;
    }

}
