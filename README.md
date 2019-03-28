# DD Log Monitoring

A standalone HTTP log monitoring application written in Java.

## Architecture

![DD Log Monitoring Architecture](/images/dd-log-monitoring.png)

1. The [TailAccessLogTask](src/main/java/com/jorgeacetozi/dd/logmonitoring/tasks/TailAccessLogTask.java) thread simulates the functioning of the UNIX `tail -f` command by continuously reading (default: 1 second) the access.log file looking for new log entries. It keeps track of the current offset of the file so that it only scans new lines rather than going through the whole file over and over again.

2. Each new log entry is then parsed based on the common logfile format and stored into the Storage, which is an in-memory Circular Buffer with 120 positions (by default), each position representing a [DataPoint](src/main/java/com/jorgeacetozi/dd/logmonitoring/model/DataPoint.java), that is, the list of requests that happened on that specific second.

3. On every 10 seconds, the [GenerateStatsTask](src/main/java/com/jorgeacetozi/dd/logmonitoring/tasks/GenerateStatsTask.java) thread takes a snapshot of the storage for the past 10 seconds and calculates the statistics by iterating over a list of [Stats](src/main/java/com/jorgeacetozi/dd/logmonitoring/stats/). Then, it uses the [handlers](src/main/java/com/jorgeacetozi/dd/logmonitoring/handler/) to output the results.

4. On every second, the [ExecuteAlertsTask](src/main/java/com/jorgeacetozi/dd/logmonitoring/tasks/ExecuteAlertsTask.java) thread takes a snapshot of the storage for the past 120 seconds and execute the logic for each alert based on it. If the alert threshold is crossed, it uses the handlers to trigger a notification. If an alert was being triggered and its value now is below the threshold, it uses the handlers to output a recover message.

## Usage

**In order to run the application, make sure you have at least JDK 8 installed on your machine.**

In order to run the application, download the executable `jar` and the application configuration file:

```bash
$ wget https://github.com/jorgeacetozi/dd-log-monitoring/releases/download/1.0.0/dd-log-monitoring-1.0.0.jar && wget https://github.com/jorgeacetozi/dd-log-monitoring/releases/download/1.0.0/dd-log-monitoring.properties
```

Run the application:

```bash
$ java -jar dd-log-monitoring-1.0.0.jar
```

It's possible to run the application in `debug` mode, which allows to see each DataPoint in the storage. The right-most DataPoint contains the requests that happened just now, whereas the left-most one contains the requests issued 120 seconds ago.

## Questions and Answers

### Can this application monitor multiple access.log files?

Yes, with a very little code modification. Just instantiate a different storage for each file you want to monitor and submit new tasks to the executors. Make sure there are enough threads available in the thread pools.

### What happens if I restart the application?

It will smoothly work again as if nothing has happened, that is, it will scan the access.log from the beginning and populate the storage with the last 120 DataPoints. If an alert was being triggered, it will instantly start alerting again.

### What if I want to create a new alert, say "alert if the number of HTTP 500 response codes in the past 60 seconds is greater than 50"?

You can easily accomplish that by extending the [abstract class Alert](src/main/java/com/jorgeacetozi/dd/logmonitoring/alert/Alert.java) and implementing its abstract methods `boolean crossedThreshold()`, `String getTriggerMessage()`, `String getRecoverMessage()`. The Alert class implements the Template Method design pattern so you don't have to worry about the logic to trigger an alert or issue the recover message.

### What if I want to create a new statistic?

You can easily accomplish that by extending the [abstract class Stats](src/main/java/com/jorgeacetozi/dd/logmonitoring/stats/Stats.java) and implementing its abstract methods `T calculate()` and `Optional<String> calculateAndReturnAsString()` (Strategy design pattern). The Generics here represents the return type for your stats logic, which could be anything.

### What if I want to send my notifications via Slack or Email?

Easy again! Just implement the `Handler interface`, say SlackHandler or EmailHandler, and implement the method `sendNotification(String message)` (Strategy design pattern). Then, add your new handlers to your alerts and the notifications will automatically be sent without having to modify the existing code.
