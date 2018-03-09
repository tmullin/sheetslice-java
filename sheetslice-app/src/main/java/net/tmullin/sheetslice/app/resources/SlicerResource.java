package net.tmullin.sheetslice.app.resources;

import com.codahale.metrics.annotation.Timed;
import net.tmullin.sheetslice.app.services.SliceService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/slicer")
public class SlicerResource {
    private final SliceService sliceService;

    @Inject
    public SlicerResource(SliceService sliceService) {
        this.sliceService = sliceService;
    }

    @GET
    @Timed
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Hello world!";
    }

    @GET
    @Path("/test")
    @Timed
    @Produces("application/pdf")
    public byte[] test() throws IOException {
        return sliceService.test();
    }
}
