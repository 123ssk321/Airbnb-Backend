package server.service;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path(RestMedia.PATH)
public interface RestMedia {
    static final String PATH="/media";

    /**
     * Post a new image.The id of the image is its hash.
     * @param contents
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    String upload(byte[] contents);

    /**
     * Return the contents of an image.
     * @param id
     * @return
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    byte[] download(@PathParam("id") String id);

}
