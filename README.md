# DD Log Monitoring

A standalone HTTP log monitoring application written in Java.

## Architecture

![DD Log Monitoring Architecture](/images/dd-log-monitoring.png)

1. The [TailAccessLogTask](src/main/java/com/jorgeacetozi/dd/logmonitoring/tasks/TailAccessLogTask.java) thread simulates the functioning of the UNIX `tail -f` command by continuously reading (default: 1 second) the access.log file looking for new log entries. It keeps track of the current offset of the file so that it only scans new lines rather than going through the whole file over and over again.

2. Each new log entry is then parsed based on the common logfile format and stored into the Storage, which is an in-memory Circular Buffer with 120 positions (by default), each position representing a [DataPoint](src/main/java/com/jorgeacetozi/dd/logmonitoring/model/DataPoint.java), that is, the list of requests that happened on that specific second.

3. On every 10 seconds, the [GenerateStatsTask](src/main/java/com/jorgeacetozi/dd/logmonitoring/tasks/GenerateStatsTask.java) thread takes a snapshot of the storage for the past 10 seconds and calculates the statistics by iterating over a list of [Stats](src/main/java/com/jorgeacetozi/dd/logmonitoring/stats/). Then, it uses the [handlers](src/main/java/com/jorgeacetozi/dd/logmonitoring/handler/) to output the results.

4. On every second, the [ExecuteAlertsTask](src/main/java/com/jorgeacetozi/dd/logmonitoring/tasks/ExecuteAlertsTask.java) thread takes a snapshot of the storage for the past 120 seconds and execute the logic for each alert based on it. If the alert threshold is crossed, it uses the handlers to trigger a notification. If an alert was being triggered and its value now is below the threshold, it uses the handlers to output a recover message.
