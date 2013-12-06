FLUME_HOME=/usr/lib/flume
/usr/bin/flume-ng agent -Xmx256m --conf conf --conf-file /root/flumeToHBase.conf --name a1 -Dflume.root.logger=INFO,console

