#!/bin/sh
export ASPECTJ_HOME=/home/jkirkley/git/smargs/java/lib

"$JAVA_HOME/bin/java" -classpath "/home/jkirkley/git/smargs/java/out/production/java:$ASPECTJ_HOME/aspectjrt.jar:$ASPECTJ_HOME/aspectjtools.jar:$JAVA_HOME/lib/tools.jar:$CLASSPATH" -Xmx64M org.smargs.Main



