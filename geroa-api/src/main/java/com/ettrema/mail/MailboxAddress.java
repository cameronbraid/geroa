
package com.ettrema.mail;

public class MailboxAddress {
    public final String user;
    public final String domain;

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
    
    
}
