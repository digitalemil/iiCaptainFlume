FLUME_HOME=/opt/flume
$FLUME_HOME/bin/flume-ng agent -Xmx256m --conf conf --conf-file $FLUME_HOME/tomcatToFlume.conf --name a1 -Dflume.root.logger=INFO,console
