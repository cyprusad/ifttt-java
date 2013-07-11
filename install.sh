#!/bin/bash

sudo mvn clean
sudo mvn install
clear
echo -e 'Welcome! This app will allow you to track Tweets in real time by letting
you to pick keywords that will be used to filter for relevant tweets!
Every so oftern, these filtered tweets will be texted to a number that you
will also set here. This way you will be able to be notified when a someone
tweets about something relevant to you. If you look at the source, you will
that the max number of tweets and max number of SMSs sent are capped, but
but in a real world application, this daemon would run continuously without
being capped. \n\n\tInspiration for this comes from the amazing www.ifttt.com\n'
sudo mvn -q exec:java -Dexec.mainClass="com.canopylabs.interview.App" -Dtwitter4j.loggerFactory=twitter4j.internal.logging.NullLoggerFactory
