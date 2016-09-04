from django.shortcuts import render
from pymongo import MongoClient
from django.utils.safestring import mark_safe
import datetime, json, random

def index(request):
	return render(request, 'website/index.html')

def harvest(request):
	return render(request, 'website/harvest.html')

def status(request):
	return render(request, 'website/status.html')

def collections(request):
	client = MongoClient()
	db = client.twitter
	collections = db.collection_names()
	return render(request, 'website/collections.html', {'collections': collections})

def collection(request, collection_name):
	if "system" in collection_name:
		return HttpResponseForbidden('Illegal collection name')

	else:
		client = MongoClient()
		collection = client.twitter.get_collection(collection_name).find()
		context = { 'collection_name':collection_name, 'collection':collection }
		return render(request, 'website/collection.html', context)

def about(request):
	return render(request, 'website/about.html')

def analysis(request, collection_name):
	if "system" in collection_name:
		return HttpResponseForbidden('Illegal collection name')

	else:
		context = { 'collection_name':collection_name }
		return render(request, 'website/analysis.html', context)

def tweetMap(request, collection_name):
	if "system" in collection_name:
		return HttpResponseForbidden('Illegal collection name')

	else:
		client = MongoClient()
		collection = client.twitter.get_collection(collection_name).find(filter={"coordinates":{"$ne":None}})
		context = { 'collection_name':collection_name, 'collection':collection }
		return render(request, 'website/analysis/tweetMap.html', context)

def visualize(request, collection_name):
	if "system" in collection_name:
		return HttpResponseForbidden('Illegal collection name')

	else:
		client = MongoClient()
		context = { 'collection_name':collection_name }


		tweetMap = request.GET.get('tweetMap', '0')
		if tweetMap == '1':
			data = client.twitter.get_collection(collection_name).find(
				filter={"coordinates":{"$ne":None}},
				projection={'coordinates':True, '_id':False}
			)
			context['tweetMapCollection'] = data


		heatMap = request.GET.get('heatMap', '0')
		if heatMap == '1':
			countries = []
			popularity = []

			data = client.twitter.get_collection(collection_name).find(
				filter={"place":{"$ne":None}},
				projection={'place':True, '_id':False}
			)

			for i in data:
				country = i["place"]["country"]
				if country:
					if country in countries:
						i = countries.index(country)
						popularity[i] += 1
					else:
						countries.append(country)
						popularity.append(1)

			res = [['Country', 'Popularity']]
			for i in range(len(countries)):
				res.append([countries[i], popularity[i]])

			context['heatMapCollection'] = mark_safe(json.dumps(res))


		timeGraph = request.GET.get('timeGraph', '0')
		if timeGraph == '1':
			data = client.twitter.get_collection(collection_name).find(
				filter={"timestamp_ms":{"$ne":None}},
				projection={'timestamp_ms':True, '_id':False}
			)

			res = [0 for x in range(24)]
			for i in data:
				date = datetime.datetime.fromtimestamp(int(i['timestamp_ms'])*0.001)
				res[date.hour] += 1
			context['timeGraphCollection'] = mark_safe(json.dumps(res))


		hashtagChart = request.GET.get('hashtagChart', '0')
		if hashtagChart == '1':
			data = client.twitter.get_collection(collection_name).find(
				filter={"entities.hashtags":{"$ne":[]}},
				projection={'entities.hashtags.text':True, '_id':False}
			)

			hashtags = []
			freq = []
			for i in data:

				entity = i["entities"]["hashtags"]
				for j in entity:
					hashtag = j["text"].lower()
					if hashtag in hashtags:
						i = hashtags.index(hashtag)
						freq[i] += 1
					else:
						hashtags.append(hashtag)
						freq.append(1)

			res = []
			other = {
				"label": "Other (<=1 mention)",
				"value": 0,
		    	"color":"#999999",
		    	"highlight": "#ffffff"

			}
			for i in range(len(hashtags)):
				if freq[i] <= 1 and len(hashtags)>200:
					other["value"] += freq[i]
				else:
					o = {
						"label": hashtags[i],
						"value": freq[i],
				    	"color":"#"+("".join(random.sample('0123456789abcdef', 6))),
				    	"highlight": "#ffffff"
					}
					res.append(o)

			res.sort(key=lambda x: x["value"], reverse=True)
			if other["value"] > 0:
				res.append(other)

			context['hashtagChartCollection'] = mark_safe(json.dumps(res))
		

		hashtagTimeTrend = request.GET.get('hashtagTimeTrend', '0')
		hashtagTerm = request.GET.get('hashtagTerm', '').lower()
		if hashtagTimeTrend == '1' and hashtagTerm != '':
			data = client.twitter.get_collection(collection_name).find(
				filter={"entities.hashtags":{"$ne":[]}},
				projection={'entities.hashtags.text':True, 'timestamp_ms':True, '_id':False}
			)

			timestamps = []
			freq = []
			for i in data:
				entity = i["entities"]["hashtags"]
				
				for j in entity:
					hashtag = j["text"].lower()
					
					if hashtag == hashtagTerm:
						timeUnit = datetime.datetime.fromtimestamp(float(i["timestamp_ms"])*0.001).minute
						if timeUnit in timestamps:
							i = timestamps.index(timeUnit)
							freq[i] += 1
						else:
							timestamps.append(timeUnit)
							freq.append(1)

			res = []
			for i in range(len(timestamps)):
				res.append([timestamps[i], freq[i]])
			
			res.sort(key=lambda x: x[0], reverse=False)
			
			context['hashtagTimeTrendCollection'] = mark_safe(json.dumps(res))


		return render(request, 'website/visualize.html', context)
