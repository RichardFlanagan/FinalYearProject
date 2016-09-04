package api;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import server.ConnectionManager;
import server.DataCollectionRequest;


@Path("/twitter")
public class ApiPaths {
	private ConnectionManager connectionManager = ConnectionManager.getInstance();
	
	@POST
	@Path("/collect/")
	public Response collect(final String input) {
		JSONObject json = new JSONObject(input);
		DataCollectionRequest dataCollectionRequest = new DataCollectionRequest(
				json.getString("name"),
				json.getString("filterType"),
				json.getString("trackingTerms"),
				new Date().getTime(), 
				json.getLong("duration"), 
				json.getString("language"),
				json.getString("boundingType"),
				json.getJSONObject("boundingBox")
			);
		connectionManager.start(dataCollectionRequest);

		return Response.status(200).build();
	}
	
	@GET
	@Path("/status/")
	public Response getCollection(){
		String msg = connectionManager.getStatus().toString();
		return Response.status(200).entity(msg).build();
	}
	
	@GET
	@Path("/stop/")
	public Response stop() {
		connectionManager.stop();
		return Response.status(200).build();
	}

}