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
import java.io.PipedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.idde.sip.peers.Config;
import org.idde.sip.peers.Logger;
import org.idde.sip.peers.sip.Utils;


public class Encoder implements Runnable {

    private final static int cBias = 0x84;
    private final static short seg_end[] = new short[]{0xFF, 0x1FF, 0x3FF, 0x7FF,
        0xFFF, 0x1FFF, 0x3FFF, 0x7FFF
    };

    /**
     * Perform compression using U-law. Retrieved from Mobicents media server
     * code.
     * 
     * @param media the input uncompressed media
     * @return the output compressed media.
     */
    public static byte[] process(byte[] media) {
        byte[] compressed = new byte[media.length / 2];

        int j = 0;
        for (int i = 0; i < compressed.length; i++) {
            short sample = (short) ((media[j++] & 0xff) | ((media[j++]) << 8));
            compressed[i] = linear2ulaw(sample);
        }
        return compressed;
    }

    /*
     * linear2ulaw() - Convert a linear PCM value to u-law
     *
     * In order to simplify the encoding process, the original linear magnitude
     * is biased by adding 33 which shifts the encoding range from (0 - 8158) to
     * (33 - 8191). The result can be seen in the following encoding table:
     *
     *  Biased Linear Input Code    Compressed Code
     *  ------------------------    ---------------
     *  00000001wxyza           000wxyz
     *  0000001wxyzab           001wxyz
     *  000001wxyzabc           010wxyz
     *  00001wxyzabcd           011wxyz
     *  0001wxyzabcde           100wxyz
     *  001wxyzabcdef           101wxyz
     *  01wxyzabcdefg           110wxyz
     *  1wxyzabcdefgh           111wxyz
     *
     * Each biased linear code has a leading 1 which identifies the segment
     * number. The value of the segment number is equal to 7 minus the number
     * of leading 0's. The quantization interval is directly available as the
     * four bits wxyz.  * The trailing bits (a - h) are ignored.
     *
     * Ordinarily the complement of the resulting code word is used for
     * transmission, and so the code word is complemented before it is returned.
     *
     * For further information see John C. Bellamy's Digital Telephony, 1982,
     * John Wiley & Sons, pps 98-111 and 472-476.
     */
    private static byte linear2ulaw(short pcm_val) {
        int mask;
        int seg;
        byte uval;

        /* Get the sign and the magnitude of the value. */
        if (pcm_val < 0) {
            pcm_val = (short) (cBias - pcm_val);
            mask = 0x7F;
        } else {
            pcm_val += cBias;
            mask = 0xFF;
        }

        /* Convert the scaled magnitude to segment number. */
        seg = search(pcm_val, seg_end, 8);

        /*
         * Combine the sign, segment, quantization bits;
         * and complement the code word.
         */
        if (seg >= 8) /* out of range, return maximum value. */ {
            return (byte)(0x7F ^ mask);
        } else {
            uval = (byte)((seg << 4) | ((pcm_val >> (seg + 3)) & 0xF));
            return  (byte)(uval ^ mask);
        }

    }

    private static int search(int val, short[] table, int size) {
        int i;

        for (i = 0; i < size; i++) {
            if (val <= table[i]) {
                return (i);
            }
        }
        return (size);
    }
    
    private PipedInputStream rawData;
    private PipedOutputStream encodedData;
    private boolean isStopped;
    private FileOutputStream encoderOutput;
    private FileOutputStream encoderInput;
    private boolean mediaDebug;

    public Encoder(PipedInputStream rawData, PipedOutputStream encodedData,
            boolean mediaDebug) {
        this.rawData = rawData;
        this.encodedData = encodedData;
        this.mediaDebug = mediaDebug;
        isStopped = false;
    }
    
    public void run() {
        byte[] buffer = new byte[Capture.BUFFER_SIZE];
        if (mediaDebug) {
            SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String date = simpleDateFormat.format(new Date());
            String dir = Utils.getPeersHome() + File.separator
                + Config.MEDIA_DIR + File.separator;
            String fileName = dir + date + "_g711_encoder.output";
            try {
                encoderOutput = new FileOutputStream(fileName);
                fileName = dir + date + "_g711_encoder.input";
                encoderInput = new FileOutputStream(fileName);
            } catch (FileNotFoundException e) {
                Logger.error("cannot create file", e);
                return;
            }
        }
        while (!isStopped) {
            int numBytesRead;
            try {
                numBytesRead = rawData.read(buffer, 0, buffer.length);
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
                    encoderInput.write(trimmedBuffer);
                } catch (IOException e) {
                    Logger.error("cannot write to file", e);
                    break;
                }
            }
            byte[] ulawData = process(trimmedBuffer);
            if (mediaDebug) {
                try {
                    encoderOutput.write(ulawData);
                } catch (IOException e) {
                    Logger.error("cannot write to file", e);
                    break;
                }
            }
            try {
                encodedData.write(ulawData);
            } catch (IOException e) {
                Logger.error("input/output error", e);
                return;
            }
        }
        if (mediaDebug) {
            try {
                encoderOutput.close();
                encoderInput.close();
            } catch (IOException e) {
                Logger.error("cannot close file", e);
                return;
            }
        }
    }

    public synchronized void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

}
