package org.altice.labseq.adapter;

import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.altice.labseq.domain.CalculateLabseqUseCase;
import org.altice.labseq.dto.LabseqResponse;
import org.altice.labseq.dto.ErrorResponse;

import java.math.BigInteger;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Path("/api/v1/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Labseq", description = "Operations for calculating Labseq sequence values")
public class LabseqAdapter {

    @Inject
    CalculateLabseqUseCase useCase;

    @GET
    @Path("/labseq/{n}")
    @Blocking
    @Operation(
            summary = "Calculate the Labseq value at a given position",
            description = "Dynamically selects the most efficient strategy to calculate Labseq values"
    )
    @APIResponse(
            responseCode = "200",
            description = "Labseq value calculated successfully",
            content = @Content(schema = @Schema(implementation = LabseqResponse.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid input parameter",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public LabseqResponse calculate(
            @Parameter(description = "Position in the Labseq sequence", required = true)
            @PathParam("n") long n) {

        BigInteger result = useCase.calculate(n);
        return new LabseqResponse(n, result.toString());
    }
}
