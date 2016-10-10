#!/bin/sh
echo "<CPU_USER>"
rrdtool info /home.bak/MCA/Software/httpd/htdocs/ganglia/rrds/my\ ganglia/$1/cpu_user.rrd | grep last
echo "<MEM_TOTAL>"
rrdtool info /home.bak/MCA/Software/httpd/htdocs/ganglia/rrds/my\ ganglia/$1/mem_total.rrd | grep last
echo "<MEM_FREE>"
rrdtool info /home.bak/MCA/Software/httpd/htdocs/ganglia/rrds/my\ ganglia/$1/mem_free.rrd | grep last
echo "<DISK_TOTAL>"
rrdtool info /home.bak/MCA/Software/httpd/htdocs/ganglia/rrds/my\ ganglia/$1/disk_total.rrd | grep last
echo "<DISK_FREE>"
rrdtool info /home.bak/MCA/Software/httpd/htdocs/ganglia/rrds/my\ ganglia/$1/disk_free.rrd | grep last
echo "<LOAD>"
rrdtool info /home.bak/MCA/Software/httpd/htdocs/ganglia/rrds/my\ ganglia/$1/load_one.rrd | grep last
echo "<IO>"
rrdtool info /home.bak/MCA/Software/httpd/htdocs/ganglia/rrds/my\ ganglia/$1/cpu_wio.rrd | grep last

