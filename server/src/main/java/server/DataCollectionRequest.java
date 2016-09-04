package server;

import org.json.JSONObject;

public class DataCollectionRequest {
	private String name = "";
	private String filterType = "";
	private String trackingTerms = "";
	private long creationTime = 0L;
	private long duration = 0L;
	private String language = "en";
	private String boundingType = "";
	private JSONObject boundingBox = null;

	public DataCollectionRequest(String name, String filterType, String trackingTerms, long creationTime, long duration, String language,
			String boundingType, JSONObject boundingBox) {
		this.name = name;
		this.filterType = filterType;
		this.trackingTerms = trackingTerms;
		this.creationTime = creationTime;
		this.duration = duration;
		this.language = language;
		this.boundingType = boundingType;
		this.boundingBox = boundingBox;
	}
	
	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getTrackingTerms() {
		return trackingTerms;
	}

	public void setTrackingTerms(String trackingTerms) {
		this.trackingTerms = trackingTerms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public JSONObject getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(JSONObject boundingBox) {
		this.boundingBox = boundingBox;
	}

	public String getBoundingType() {
		return boundingType;
	}

	public void setBoundingType(String boundingType) {
		this.boundingType = boundingType;
	}

}
