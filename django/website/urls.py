from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^harvest/$', views.harvest, name='harvest'),
    url(r'^status/$', views.status, name='status'),
    url(r'^collections/$', views.collections, name='collections'),
	url(r'^collection/(?P<collection_name>\w+)/$', views.collection, name='collection'),
	url(r'^about/$', views.about, name='about'),
	url(r'^analysis/(?P<collection_name>\w+)/$', views.analysis, name='analysis'),
	url(r'^visualize/(?P<collection_name>\w+)/$', views.visualize, name='visualize'),
	url(r'^analysis/tweetmap/(?P<collection_name>\w+)/$', views.tweetMap, name='analysis/tweetmap'),
]