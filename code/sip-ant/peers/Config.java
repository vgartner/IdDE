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
package org.idde.sip.peers;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.idde.sip.peers.media.MediaMode;
import org.idde.sip.peers.sip.RFC3261;
import org.idde.sip.peers.sip.syntaxencoding.SipURI;
import org.idde.sip.peers.sip.syntaxencoding.SipUriSyntaxException;
import org.idde.util.Util;
import org.openide.util.Exceptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Config
{

    public final static String MEDIA_DIR = "media";
    public final static int RTP_DEFAULT_PORT = 8000;
    private InetAddress inetAddress;
    private String userPart;
    private String domain;
    private String password;
    private SipURI outboundProxy;
    private int sipPort;
    private MediaMode mediaMode;
    private boolean mediaDebug;
    private int rtpPort;

    public Config(String fileName)
    {
        Node node = null;

//        Node node = getFirstChild(documentElement, "network");
//        node = getFirstChild(node, "interfaces");
//        node = getFirstChild(node, "interface");
//        node = getFirstChild(node, "address");
        if (node != null)
        {
            String address = node.getTextContent();
            try
            {
                inetAddress = InetAddress.getByName(address);
            }
            catch (UnknownHostException e)
            {
                Logger.error("unknown host: " + address, e);
            }
        }
        else
        {
            try
            {
                boolean found = false;
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements() && !found)
                {
                    NetworkInterface networkInterface = e.nextElement();
                    Enumeration<InetAddress> f = networkInterface.getInetAddresses();
                    while (f.hasMoreElements() && !found)
                    {
                        InetAddress inetAddress = f.nextElement();
                        if (inetAddress.isSiteLocalAddress())
                        {
                            this.inetAddress = inetAddress;
                            found = true;
                        }
                    }
                }
            }
            catch (SocketException e)
            {
                Logger.error("socket exception", e);
            }
            if (inetAddress == null)
            {
                Logger.error("IP address not found, configure it manually");
            }
        }

        PropertiesConfiguration propertiesConfig=null;

        try
        {
            propertiesConfig = new PropertiesConfiguration(Util.configFile);
        }
        catch (ConfigurationException ex)
        {
            Exceptions.printStackTrace(ex);
        }


//        node = getFirstChild(documentElement, "sip");
//        Node parent = getFirstChild(node, "profile");
//        node = getFirstChild(parent, "userpart");
//        if (node != null)
//        {
            userPart = propertiesConfig.getString("login_sip");

//        }
//        else
//        {
//            Logger.error("userpart not found in configuration file");
//        }
//        node = getFirstChild(parent, "domain");
//        if (node != null)
//        {
            domain = propertiesConfig.getString("server_sip");
            password = propertiesConfig.getString("pass_sip");

        node = null; //getFirstChild(parent, "outboundProxy");
        if (node != null)
        {
            String uri = node.getTextContent();
            try
            {
                outboundProxy = new SipURI(uri);
            }
            catch (SipUriSyntaxException e)
            {
                Logger.error("sip uri syntax exception: " + uri, e);
            }
        }
//        node = getFirstChild(parent, "port");
        sipPort = Integer.parseInt(propertiesConfig.getString("port_sip"));

        if (node != null)
        {
            sipPort = Integer.parseInt(node.getTextContent());
        }
        else
        {
            sipPort = RFC3261.TRANSPORT_DEFAULT_PORT;
        }

//        parent = getFirstChild(documentElement, "codecs");
//        node = getFirstChild(parent, "mediaMode");

        if (node != null)
        {
            mediaMode = MediaMode.valueOf(node.getTextContent());
        }
        else
        {
            mediaMode = MediaMode.captureAndPlayback;
        }

//        node = getFirstChild(parent, "mediaDebug");

        if (node != null)
        {
            mediaDebug = Boolean.parseBoolean(node.getTextContent());
        }
        else
        {
            mediaDebug = false;
        }

//        node = getFirstChild(documentElement, "rtp");
//        node = getFirstChild(node, "port");

        if (node != null)
        {
            rtpPort = Integer.parseInt(node.getTextContent());
        }
        else
        {
            rtpPort = RTP_DEFAULT_PORT;
        }
    }

    private Node getFirstChild(Node parent, String childName)
    {
        if (parent == null || childName == null)
        {
            return null;
        }
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i)
        {
            Node node = nodeList.item(i);
            if (childName.equals(node.getNodeName()))
            {
                return node;
            }
        }
        return null;
    }

    public InetAddress getInetAddress()
    {
        return inetAddress;
    }

    public String getUserPart()
    {
        return userPart;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getPassword()
    {
        return password;
    }

    public SipURI getOutboundProxy()
    {
        return outboundProxy;
    }

    public int getSipPort()
    {
        return sipPort;
    }

    public MediaMode getMediaMode()
    {
        return mediaMode;
    }

    public boolean isMediaDebug()
    {
        return mediaDebug;
    }

    public int getRtpPort()
    {
        return rtpPort;
    }
}
