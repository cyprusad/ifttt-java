package com.canopylabs.interview;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: Sai Warang
 *
 * This class is the base Twitter client implementing the singleton
 * pattern. It sets up a connection with the Twitter Streaming API
 * and then listens for new tweets streaming in, filtered on the
 * parameters initialized at the command line.
 *
 */
public class TwitterClient {
    private static ConfigurationBuilder configurationBuilder;
    private static TwitterStreamFactory streamFactory;
    private static TwitterStream stream;

    private static TwitterClient singletonClient;

    private TwitterClient(){
        // Hardcoded Twitter API credentials used for authentication
        configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(false)
                .setOAuthConsumerKey("eqPQSM9cbnNYrfudSnsA")
                .setOAuthConsumerSecret("YsN0BubXTYs2qKXxvyU1VF8tivrv9KWelv2VEg0a4")
                .setOAuthAccessToken("68595366-CAhKPiotuEX4tVvtVIPIIqFCHkSRzC9wMwVAjoCsM")
                .setOAuthAccessTokenSecret("GadElz5KFa6VDsSD0MkDatWY8vTDinIFdMzah5No");
        streamFactory = new TwitterStreamFactory(configurationBuilder.build());

        // The singleton stream object used to query the Twitter API
        stream = streamFactory.getInstance();

        /**
         * In order for the client to listen on incoming stream of tweets,
         * it's needed to implement a StatusListener interface
         */
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                Tweet newTweet = new Tweet(status.getUser().getScreenName(), status.getText());

                newTweet.prettyPrint();
                System.out.println();

                SmsQueue queue = SmsQueue.getInstance();

                // Start background thread sending queued filtered tweets as SMSs
                if (!queue.hasLoopStarted()) {
                    queue.startSmsLoop();
                }  else {
                    try {
                        // Queue has reached max size, stop streaming data
                        if (queue.isFull()) {
                            stream.cleanUp();
                        } else {
                            queue.addToQueue(newTweet);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                // NO-OP
            }

            @Override
            public void onTrackLimitationNotice(int i) {
                // NO-OP
            }

            @Override
            public void onScrubGeo(long l, long l2) {
                // NO-OP
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
                // NO-OP
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        };
        stream.addListener(listener);
    }

    public static TwitterClient getInstance(){
        if (singletonClient == null){
            singletonClient = new TwitterClient();
        }
        return singletonClient;
    }

    public TwitterStream getStream(){
        return stream;
    }

}
