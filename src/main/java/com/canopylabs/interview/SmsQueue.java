package com.canopylabs.interview;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * Created with IntelliJ IDEA.
 * User: Sai Warang
 *
 * Singleton SMS queue instance. It maintains a finite synchronized queue, which buffers
 * the streaming tweets as they are received by the Twitter Streaming API. Queued messages
 * are then dispatched off as SMS messages in a separate thread.
 *
 */
public class SmsQueue {
    // Hard-coded Twilio credentials
    private static final String ACCOUNT_SID = "AC385e14442b0d2d194748031ee5d8da4f";
    private static final String AUTH_TOKEN = "dbd849f3ee2027a976ad09f9b7a6600d";

    // Twilio specific members
    private static TwilioRestClient twilioRestClient;
    private static SmsFactory messageFactory;

    /**
     * Hard coded parameters limiting the number of streaming tweets being sampled
     * Necessary to not get an SMS overload if searching for common tweets
     */
    private static int MAX_SMS_MESSAGES = 3;
    private static int MAX_QUEUE_SIZE = 20;
    private static int count = 0;

    private static final String fromNumber = "+16475576229";
    private static String toNumber;

    private static boolean hasStarted = false;

    // Check if the SMS loop has begun, defaults to 'false', until set in 'startSmsLoop'
    public boolean hasLoopStarted(){
        return hasStarted;
    }

    // Set the number to which SMSs will be sent to
    public void setToNumber(String to) {
        SmsQueue.toNumber = to;
    }

    private LinkedBlockingDeque<Tweet> tweetQueue;


    private static SmsQueue singletonQueue;
    private ExecutorService executorService;

    private SmsQueue() {
        // Create the client responsible for communicating with the Twilio's REST Api
        twilioRestClient = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
        messageFactory = twilioRestClient.getAccount().getSmsFactory();
        tweetQueue = new LinkedBlockingDeque<Tweet>(MAX_QUEUE_SIZE);
    }

    public static SmsQueue getInstance(){
        if(singletonQueue == null){
            singletonQueue = new SmsQueue();
        }
        return singletonQueue;
    }

    // Check if the queue is full
    public synchronized boolean isFull() throws InterruptedException {
        boolean result;
        int size = tweetQueue.size();
        if (size == MAX_QUEUE_SIZE) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    // Synchronized addition to the back of the queue
    public synchronized void addToQueue(Tweet newTweet) throws InterruptedException {
        tweetQueue.put(newTweet);
        notifyAll();
    }


    // Synchronized deletion from the front of the queue
    private synchronized Tweet removeFromQueue() throws InterruptedException {
        Tweet nextTweet = tweetQueue.take();
        notifyAll();
        return nextTweet;
    }

    private static String sendSms(Tweet tweet){
        String result = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Body", "@" + tweet.getUserName() + ": " + tweet.getTweetText());
            params.put("To",   toNumber);
            params.put("From", fromNumber);
            Sms message = messageFactory.create(params);
            result =  message.getSid();

        } catch (TwilioRestException e) {
            System.out.println(e.getErrorMessage());
        }

        // Message was successfully sent
        if (result != null) {
            // Increment the count of successful messages sent so far
            count++;
        }
        return result;
    }

    /**
     * Start up a background thread that removes tweets from the front of the
     * queue, and sends it as a text message to the 'toNumber'
     */
    public void startSmsLoop(){
        hasStarted = true;
        Runnable smsLoop = new Runnable(){
            public void run(){
                try {
                    Thread.sleep(2000);
                    while (true) {
                        Tweet tweetToSms = removeFromQueue();
                        if (count < MAX_SMS_MESSAGES) {
                            sendSms(tweetToSms);
                            Thread.sleep(1000);
                        } else {
                            stopSmsLoop();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        // Start the background thread
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(smsLoop);
    }

    // Stop the background thread, called when MAX number of SMSs have been sent
    public void stopSmsLoop(){
        executorService.shutdownNow();
        tweetQueue.clear();
    }

}
