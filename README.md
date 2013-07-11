ifttt-java
==========

This app will allow you to track Tweets in real time by letting
you to pick keywords that will be used to filter for relevant tweets!
Every so oftern, these filtered tweets will be texted to a number that you
will also set here. This way you will be able to be notified when a someone
tweets about something relevant to you. If you look at the source, you will
that the max number of tweets and max number of SMSs sent are capped, but
but in a real world application, this daemon would run continuously without
being capped.

Inspiration for this comes from the amazing www.ifttt.com

###Prerequsites:
Need Maven installed on the machine. Uses super user privileges.

###Running instructions:
<code>chmod u+x install.sh</code>

<code>./install.sh</code>

###Note:
In it's default state, it only queues up 20 tweets, and sends SMS messages of the first 3 queued tweets
This can be changed by altering <code>MAX_QUEUE_SIZE</code> and <code>MAX_SMS_MESSAGES</code> respectively, in
SmsQueue.java

