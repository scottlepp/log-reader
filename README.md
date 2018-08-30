# log-reader

* reads text based log file into mysql
* allows fetching IPs by date range and threshold

__Running__

```console
java -jar log-reader-1.0-SNAPSHOT.jar --accesslog=./access.log --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100
```
OR
```console
java -cp log-reader-1.0-SNAPSHOT.jar org.springframework.boot.loader.JarLauncher --accesslog=./access.log --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100
```
Also supports --bypass=true  (to bypass inserting the log data into mysql again)

__SQL__

* Fetching IPs by date range and treshold (example)
~~~ mysql
select ip from access_log where date >= '2017-01-01.13:00:00' AND date <= '2017-01-01.14:00:00'
group by ip
having count(ip) > 100;
~~~
* Fetching "Blocked" IPs
~~~ mysql
select * from blocked;
~~~
