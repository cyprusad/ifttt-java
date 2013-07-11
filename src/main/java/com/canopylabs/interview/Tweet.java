package com.canopylabs.interview;

/**
 * Created with IntelliJ IDEA.
 * User: Sai Warang
 *
 * This class serves as a basic Tweet object that is used by
 * the program to store Tweets in a synchronized priority queue
 * and then dispatch off as text messages
 *
 */
public class Tweet {
    private String userName;
    private String tweetText;

    public Tweet(String newUserName, String newTweetText){
        userName = newUserName;
        tweetText = newTweetText;
    }

    public String getUserName() {
        return userName;
    }

    public String getTweetText() {
        return tweetText;
    }

    // As shown on the command line when the program is run
    public void prettyPrint() {
        System.out.println("Tweet by @" + userName);
        int divider = 10 + userName.length();
        for (int i = 0; i <= divider; i++){
            System.out.print("=");
        }
        System.out.print("\n");
        System.out.println(tweetText);
    }

}
