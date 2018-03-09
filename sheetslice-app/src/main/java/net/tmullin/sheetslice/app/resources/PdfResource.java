package net.tmullin.sheetslice.app.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.ByteStreams;
import net.tmullin.sheetslice.app.services.PdfService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Path("/pdfs")
public class PdfResource {
    private final PdfService service;
    private final ObjectMapper objectMapper;

    @Inject
    public PdfResource(PdfService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Timed
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response index() {
        return index("");
    }

    @Timed
    @GET
    @Path("/{subPath:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response index(@PathParam("subPath") String subPath) {
        return Response.ok(service.list(subPath)).build();
    }

    @Timed
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(
            @Valid @Min(640) @Max(3840) @FormDataParam("width") @DefaultValue("800") int width,
            @Valid @NotNull @FormDataParam("file") InputStream file,
            @Valid @NotNull @FormDataParam("file") FormDataContentDisposition meta
    ) throws IOException {
        file = parseInput(file);
        String result = service.upload(width, file, meta.getFileName());
        return Response.ok(
                objectMapper.createObjectNode().put("result", result)
        ).build();
    }

    private InputStream parseInput(InputStream in) throws IOException {
        final long limit = 10 * 1024 * 1024;

        in = ByteStreams.limit(in, limit);

        byte[] bytes = ByteStreams.toByteArray(in);

        if (bytes.length >= limit) {
            throw new ClientErrorException(Response.Status.REQUEST_ENTITY_TOO_LARGE);
        }

        return new ByteArrayInputStream(bytes);
    }
}
