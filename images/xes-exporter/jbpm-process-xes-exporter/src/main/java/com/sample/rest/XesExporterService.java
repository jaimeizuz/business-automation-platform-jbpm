package com.sample.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sample.service.XesExporter;


/**
 * XesExporterService
 */
@Path("/jbpm-xes-exporter/process-model/{processId}")
public class XesExporterService {
    
    private String xesFile = "";
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response getXes(@PathParam("processId") String processId, @QueryParam("processVersion") String processVersion) {

    	xesFile = XesExporter.getXes(processId, processVersion, null, null, null);
        return Response.ok(xesFile).build();
    }
    
}