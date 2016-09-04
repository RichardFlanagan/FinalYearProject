package server;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoCollectionHandler {

	private Logger logger = LoggerFactory.getLogger(MongoCollectionHandler.class);
	private static MongoClient mongoClient = new MongoClient();// "192.168.1.10",
																// 27017);
	private MongoDatabase database = null;
	private MongoCollection<Document> collection = null;

	public MongoCollectionHandler(String databaseName, String collectionname) {
		this.database = mongoClient.getDatabase(databaseName);
		this.collection = database.getCollection(collectionname);
	}

	public void insertDocumentsFromStrings(final List<String> strings) {
		new Thread(new Runnable() {
			public void run() {

				List<Document> documents = new ArrayList<Document>();
				for (String s : strings) {
					documents.add(Document.parse(s));
				}
				collection.insertMany(documents);
				logger.info("--> collection count {}", collection.count());

			}
		}).start();
	}

	public void close() {
		mongoClient.close();
	}
}
