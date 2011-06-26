package com.googlecode.gtalksms.xmpp;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.content.SharedPreferences.Editor;

import com.googlecode.gtalksms.SettingsManager;

public class XmppAccountManager {
    private static final String[] USERNAME_IS_FULL_JID = new String[] {"gmail.com", "googlemail.com"};                                
    
    /**
     * Tries to create a new account with help of XMPP in-band registration
     * and if successful returns the XMPPConnection on success, 
     * otherwise throws an XMPPException
     * 
     * @param jid
     * @param host
     * @param password
     * @return
     * @throws XMPPException
     */
    public static XMPPConnection tryToCreateAccount(String username, String host, String password) throws XMPPException {
        username = needsDomainPart(username, host);
        
        ConnectionConfiguration conf = new ConnectionConfiguration(host);
        XMPPConnection connection = new XMPPConnection(conf);
        connection.connect();
        AccountManager accManager = new AccountManager(connection);
        if(!accManager.supportsAccountCreation()) {
            throw new XMPPException("Server does not support account creation");
        }
        accManager.createAccount(username, password);

        return connection;
    }
    
    /**
     * Tries to return the correct username for the given host.
     * 
     * Some XMPP service provider (like gTalk) require the username
     * to be concatenated with the host part
     * e.g. user -> user@server.tld
     * 
     * @param username
     * @param host
     * @return
     */
    private static String needsDomainPart(String username, String host) {
        for (String s : USERNAME_IS_FULL_JID) {
            if (host.equals(s)) {
                return username + "@" + host;
            }
        }
        return username;
    }

    /**
     * Writes the given minimal settings to the shared preferences.
     * The jid needs to be in the form of user@server.tld
     * because smack will do automatic DNS SRV lookups on server.tld
     * to find the right XMPP server
     * 
     * @param jid
     * @param password
     * @param settings
     */
    public static void savePreferences(String jid, String password, String notifiedAddress, SettingsManager settings) {
        Editor editor = settings.getEditor();
        
        editor.putString("notifiedAddress", notifiedAddress);
        editor.putString("xmppSecurityMode", "opt");
        editor.putBoolean("useCompression", false);
        editor.putBoolean("manuallySpecifyServerSettings", false);
        editor.putString("login", jid);
        editor.putString("password", password);
        
        editor.commit();
    }
    
    
}