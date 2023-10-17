package main.java.server.service;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path(RestMedia.PATH)
public interface RestMedia {
    static final String PATH="/media";

    /**
     * Post a new user image.The id of the image is its hash prefixed with "user-".
     * @param contents of the media
     * @return 200 the id of the media
     *         400 otherwise
     */
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    String uploadUserMedia(byte[] contents);

    /**
     * Return the contents of a user media.
     * @param id of the media
     * @return 200 contents of the media
     *         404 if id does not exist.
     *         400 otherwise
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    byte[] downloadUserMedia(@PathParam("id") String id);

    /**
     * Post a new house image.The id of the image is its hash prefixed with "house-".
     * @param contents of the media
     * @return 200 the id of the media
     *         400 otherwise
     */
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    String uploadHouseMedia(byte[] contents);

    /**
     * Return the contents of a user media.
     * @param id of the media
     * @return 200 contents of the media
     *         404 if id does not exist.
     *         400 otherwise
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    byte[] downloadHouseMedia(@PathParam("id") String id);

}
