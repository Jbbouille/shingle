package org.nyaraka.test;

import static com.libon.server.api.resource.SecuringWith.LibonScope.ADMIN_APN_PUSH;
import static com.libon.server.api.resource.SecuringWith.LibonScope.ARCHIVER_XMPP_SERVER;
import java.util.List;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.libon.server.api.annotations.common.OptionalAppId;
import com.libon.server.api.annotations.role.Admin;
import com.libon.server.api.resource.SecuringWith;
import org.nyaraka.annotations.ApiResponse;
import org.nyaraka.annotations.ApiResponses;
import fr.norad.jaxrs.oauth2.NotSecured;
import fr.norad.jaxrs.oauth2.ScopeStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/path")
@Produces(MediaType.APPLICATION_XML)
@Consumes
@Api("HAHAHA, OHOHOH")
@ApiResponse(statusCode = 606, message = "my awesome message")
@ApiResponse(statusCode = 707, message = "my awesome message", response = Bar.class, contentType = MediaType.APPLICATION_ATOM_XML)
public class Bar {
    @ApiOperation(value = "test description", notes = "notes")
    @Path("toto/xx/DD")
    @GET
    @SecuringWith(strategy = ScopeStrategy.ALL, value = {ADMIN_APN_PUSH, ARCHIVER_XMPP_SERVER})
    @OptionalAppId
    @NotSecured
    @io.swagger.annotations.ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 204, message = "Pack was successfully purchased and credited to the user", response = Child.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Pack was successfully purchased and credited to the user", response = Group.class)
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response ab(Group body) throws AnnotatedException {
        return null;
    }

    @ApiOperation(value = "test description", notes = "notes")
    @Path("toto/xx/UU")
    @GET
    @SecuringWith(strategy = ScopeStrategy.ALL, value = {ADMIN_APN_PUSH, ARCHIVER_XMPP_SERVER})
    @OptionalAppId
    @Admin
    @NotSecured
    @io.swagger.annotations.ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 204, message = "Pack was successfully purchased and credited to the user", response = Child.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Pack was successfully purchased and credited to the user", response = Group.class)
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response abc(Group body, @MatrixParam("test") String test) throws AnnotatedException {
        return null;
    }

    @Path("/matrix")
    @POST
    public List<Group> matrix(List<Child> children, @MatrixParam("boubli") MyBean matrix) {
        return null;
    }

    @Path("/bean-param")
    @POST
    public void beanParam(@BeanParam MyBean matrix) {
    }
}
