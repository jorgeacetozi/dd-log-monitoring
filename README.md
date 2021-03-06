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

Download the executable `jar` and the application configuration file:

```bash
$ wget https://github.com/jorgeacetozi/dd-log-monitoring/releases/download/1.0.0/dd-log-monitoring-1.0.0.jar

$ wget https://github.com/jorgeacetozi/dd-log-monitoring/releases/download/1.0.0/dd-log-monitoring.properties
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

## TODOs

- Accept others log formats by extracting the logic from Request constructor to a LogParser component, which implements a Strategy pattern, each format being a different strategy.
- Accept a list of access.log paths in the `dd-log-monitoring.properties` file so that it can monitor multiple files concurrently.
- Create tests for the tasks.

## Improvements

### Distributed Architecture for Scalability

In a scenario where this application is used to monitor a lot of log-intensive files on the filesystem, it could become CPU and memory intensive as it runs as a standalone application responsible for everything: collecting, parsing, storing, and notifying. In this case, a distributed architecture where each component is responsible for a single role would be more suitable (similar to what Elastic Stack does with Beats and Logstash or Fluentd with forwarders and aggregators).

### Crash Recovery

1. Create a new task named SnapshotTask responsible for periodically (configurable via properties file) taking snapshots. A snapshot consists of persisting to disk the currentOffset, the in-memory storage, and the current LocalDateTime.
2. Share a Lock between the SnapshotTask and the TailAccessLogTask to make sure a snapshot will not be issued when the TailAccessLogTask is active writing new DataPoints to the storage (which could lead to request duplication in case of a crash).
3. After recovering from the crash, load the storage and the currentOffset from disk, get the delta in seconds between the snapshot's LocalDateTime and LocalDateTime.now() and add delta empty datapoints to the storage. After that, just continue processing the missing log lines normally. This will make sure to forward the storage the number of seconds between the last snapshot and now() positions, otherwise we would end up with datapoints in the wrong positions.

### Non-Blocking TailAccessLogTask

Currently, the TailAccessLogTask reads each line, parses and stores them into the storage, making this thread CPU-intensive and hence taking more time on each execution than if it was just responsible for reading each line and submitting a new task to another ThreadPool for parsing the log line into a Request and store it into the storage.
