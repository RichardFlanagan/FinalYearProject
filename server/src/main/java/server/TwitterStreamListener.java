package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.Location.Coordinate;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterStreamListener {

	private Logger logger = LoggerFactory.getLogger(TwitterStreamListener.class);
	private BasicClient client = null;
	private BlockingQueue<String> queue = null;
	private boolean run = false;
	private MongoCollectionHandler twitterCollection = null;
	private int interval = 10;
	private Thread processThread = null;
	private String status = "";
	private Authentication auth = null;
	private String name = "";

	public TwitterStreamListener(String name, String consumerKey, String consumerSecret, String accessToken,
			String accessTokenSecret, int secondsInterval) {
		this.auth = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		this.interval = secondsInterval * 1000;
		this.status = "stopped";
	}

	private void createClient(DataCollectionRequest req) {
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		endpoint.stallWarnings(true);
		
		if(req.getLanguage().compareToIgnoreCase("all") != 0){
			endpoint.languages(Lists.newArrayList(req.getLanguage()));
		}
		

		if(req.getFilterType().compareToIgnoreCase("location") == 0){
			endpoint.locations(Lists.newArrayList(new Location(
				new Coordinate(
						req.getBoundingBox().getDouble("west"), 
						req.getBoundingBox().getDouble("south")), 
				new Coordinate(
						req.getBoundingBox().getDouble("east"), 
						req.getBoundingBox().getDouble("north"))
			)));
		} else if(req.getFilterType().compareToIgnoreCase("trackingTerms") == 0){
			endpoint.addPostParameter(Constants.TRACK_PARAM, req.getTrackingTerms());
		}


		logger.info(req.getTrackingTerms());
		logger.info(req.getFilterType());
		logger.info(endpoint.getPostParamString());
		logger.info(endpoint.getURI());		

		client = new ClientBuilder().name(name).hosts(Constants.STREAM_HOST).endpoint(endpoint).authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();
	}

	private void run() {
		processThread = new Thread(new Runnable() {
			public void run() {
				client.connect();
				status = "running";

				// Wait x seconds
				// If we aren't disconnected
				// Drain to a buffer list
				// Insert the new list into the db
				while (run) {
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (client.isDone()) {
						logger.error("Client connection closed unexpectedly: {}", client.getExitEvent().getMessage());
						break;
					} else {
						if (queue.peek() != null) {
							List<String> drain = new ArrayList<String>();
							queue.drainTo(drain, queue.size());
							logger.info("inserting {} into database", drain.size());
							twitterCollection.insertDocumentsFromStrings(drain);
						}
					}
				}

				client.stop();
				status = "stopped";
			}
		});
		processThread.start();
	}

	public void startCollecting(DataCollectionRequest req) {
		stopCollecting();
		this.twitterCollection = new MongoCollectionHandler("twitter", req.getName());
		this.queue = new LinkedBlockingQueue<String>(10000);
		createClient(req);
		
		run = true;
		run();
	}

	public void stopCollecting() {
		run = false;
		if (processThread != null) {
			try {
				processThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String getStatus() {
		return status;
	}

	public void destroy() {
		stopCollecting();
		twitterCollection.close();
	}

}
