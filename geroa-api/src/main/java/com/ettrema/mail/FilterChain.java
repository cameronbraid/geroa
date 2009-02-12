package com.ettrema.mail;

import java.util.List;

/** Passes the request and response along a series of filters
 *
 *  By default the MailServer loads a single filter which executes the appropriate
 *  handler for the http method
 *
 *  Additional filters can be added using HttpManager.addFilter
 */
public class FilterChain {

    final List<Filter> filters;
    final Filter terminal;
    int pos = 0;

    public FilterChain(List<Filter> filters, Filter terminal) {
        this.filters =  filters;
        this.terminal = terminal;

    }

    public void doEvent(Event event) {
        if( pos < filters.size() ) {
            Filter filter = filters.get(pos++);
            filter.doEvent(this,event);
        } else {
            if( terminal != null ) terminal.doEvent(this, event);
        }
    }
}
