package com.canopylabs.interview;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;
import com.twilio.sdk.resource.list.SmsList;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.util.StringTokenizer;


/**
 * Created with IntelliJ IDEA.
 * User: Sai Warang
 *
 * Main class, takes input paramters and initializes the Twitter client and SmsQueue
 *
 */
public class App {

    public static void main( String[] args ) throws TwitterException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String toNumber;
        String continueStr;
        String[] keywords;

        while (true) {
            System.out.println("Enter the cell phone number to send text messages to..");
            System.out.println("Sample input: +16474715517 (don't forget the +1)");
            toNumber = br.readLine().trim();
            System.out.println("The number you entered: " + toNumber + ". Do you want to continue? (y/n):");
            continueStr = br.readLine().trim();
            if (continueStr.toLowerCase().equals("y") || continueStr.toLowerCase().equals("yes")) break;
        }

        // Set the number used to send tweets to
        SmsQueue.getInstance().setToNumber(toNumber);

        while (true) {
            System.out.println("Enter the space separated keywords to track tweets in real time..");
            System.out.println("Sample input: canopylabs analytics java dbx (Only one space between terms please!)");
            System.out.println("(If the keywords aren't trending right now, and you want to see something, send out a tweet right now!)");
            keywords = br.readLine().trim().split(" ");
            System.out.print("You entered keywords:");
            for (String str : keywords) {
                System.out.print(" " + str);
            }
            System.out.print(". Do you want to continue? (y/n):\n");
            continueStr = br.readLine().trim();
            if (continueStr.toLowerCase().equals("y") || continueStr.toLowerCase().equals("yes")) break;
        }
        System.out.println();
        System.out.println("Sending off your queries..");
        System.out.println("The following tweets match your search keywords, the first few in this list will be texted to you!");
        System.out.println();

        // Check if there were keywords in the input
        if (keywords != null) {
            FilterQuery streamQuery = new FilterQuery(0, null, keywords);
            TwitterStream stream = TwitterClient.getInstance().getStream();
            stream.filter(streamQuery);
        } else {
            System.out.println("You didn't enter any keywords to search for. Run with defaults (javascript, js, python)");
            String[] defaultKeywords = {"js", "javascript", "python"};
            FilterQuery streamQuery = new FilterQuery(0, null, defaultKeywords);
            TwitterStream stream = TwitterClient.getInstance().getStream();
            stream.filter(streamQuery);
        }
    }
}
