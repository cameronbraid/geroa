
package com.ettrema.mail;

import java.io.Serializable;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailboxAddress implements Serializable{
    public final String user;
    public final String domain;

    private static final long serialVersionUID = 1L;

    public static MailboxAddress parse(String address) throws IllegalArgumentException {
        if( address == null  ) throw new IllegalArgumentException("address argument is null");
        if( address.length() == 0 ) throw new IllegalArgumentException("address argument is empty");
                    
        String[] arr = address.split("[@]");
        if( arr.length != 2 ) throw new IllegalArgumentException("Not a valid email address: " + address);
        return new MailboxAddress(arr[0], arr[1]);                
    }
    
    public MailboxAddress(String user, String domain) {
        this.user = user;
        this.domain = domain;
    }

    @Override
    public String toString() {
        return user + "@" + domain;
    }

    public InternetAddress toInternetAddress() {
        try {
            InternetAddress ia = new InternetAddress(user + "@" + domain);
            return ia;
        } catch (AddressException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
}
