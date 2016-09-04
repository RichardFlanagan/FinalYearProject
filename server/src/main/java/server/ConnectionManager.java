package server;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {
	private static ConnectionManager instance = null;
	private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);;
	private DataCollectionRequest dataCollectionRequest = null;
	private TwitterStreamListener twitterStreamListener = null;
	private int interval = 10;

	private ConnectionManager() {
		this.twitterStreamListener = new TwitterStreamListener("a00193644-twitter-stream-listener",
				"aICdIR1ibVo7erGRbhtXFqSUL", "3R5NVcUBUYjQnzzaHgwX5j90sUBfnm9gTXit52FYgaDlO5lLkW",
				"239484629-Q8X2KpPnLwAyAGDWlzQLCJdmzGEvTP4XWu9HXCKU", "BBxJhPoObr3J3mBVQWgydHetzl2iPSr3DMKYU3q13cXIG",
				this.interval);
	}

	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
			logger.info("--> Created ConnectionManager Instance");
		}
		return instance;
	}

	public void stop() {
		logger.info("--> Stopping TwitterListener");
		twitterStreamListener.stopCollecting();
		logger.info("--> TwitterListener stopped");
	}

	public void start(DataCollectionRequest req) {
		logger.info("--> setDataRequest" + req.getName());
		dataCollectionRequest = req;

		logger.info("--> Starting TwitterListener");
		twitterStreamListener.startCollecting(dataCollectionRequest);
		logger.info("--> TwitterListener started");
	}

	public JSONObject getStatus() {
		logger.info("--> getStatus");
		JSONObject obj = new JSONObject();
		obj.put("status", twitterStreamListener.getStatus());
		if (dataCollectionRequest != null) {
			obj.put("dataRequest", new JSONObject(dataCollectionRequest));
		} else {
			obj.put("dataRequest", new JSONObject());
		}
		return obj;
	}

	public void destroy() {
		twitterStreamListener.destroy();
	}
}
