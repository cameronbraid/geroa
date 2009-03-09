
package com.ettrema.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailboxAddress implements Serializable{
    public final String user;
    public final String domain;
    public final String personal;

    private static final long serialVersionUID = 1L;

    public static MailboxAddress parse(String address) throws IllegalArgumentException {
        if( address == null  ) throw new IllegalArgumentException("address argument is null");
        if( address.length() == 0 ) throw new IllegalArgumentException("address argument is empty");
                    
        String[] arr = address.split("[@]");
        if( arr.length != 2 ) throw new IllegalArgumentException("Not a valid email address: " + address);
        return new MailboxAddress(arr[0], arr[1]);                
    }

    public MailboxAddress(String user, String domain, String personal) {
        this.user = user;
        this.domain = domain;
        this.personal = personal;
    }


    public MailboxAddress(String user, String domain) {
        this.user = user;
        this.domain = domain;
        this.personal = null;
    }

    @Override
    public String toString() {
        if( personal == null ) {
            return user + "@" + domain;
        } else {
            return "\"" + personal + "\"" + " <" + user + "@" + domain + ">";
        }
    }

    public InternetAddress toInternetAddress() {
        try {
            if( personal == null ) {
                return  new InternetAddress(user + "@" + domain);
            } else {
                try {
                    return new InternetAddress(user + "@" + domain, personal);
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (AddressException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
}
