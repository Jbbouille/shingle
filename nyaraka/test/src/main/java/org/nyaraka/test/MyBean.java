package org.nyaraka.test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class MyBean {
    @QueryParam("myData")
    private String data;

    @HeaderParam("myHeader")
    private String header;

    @DefaultValue("def")
    @HeaderParam("myForm")
    private String form;

    private String id;

    @PathParam("id")
    public void getToto(String id) {
        this.id = id;
    }
}
