@echo off
start java Setup -i 11 Router0 Router1:192.168.0.26:5 Router2:192.168.56.1:8
start java Setup -i 34 Router1 Router0:10.127.127.1:5 Router2:192.168.56.1:4 Router3:192.168.61.1:3
start java Setup -i 41 Router2 Router0:10.127.127.1:8 Router1:192.168.0.26:4 Router3:192.168.61.1:2 Router4:192.168.71.1:11
start java Setup -i 64 Router3 Router1:192.168.0.26:3 Router2:192.168.56.1:2 Router4:192.168.71.1:6
start java Setup -i 95 Router4 Router2:192.168.56.1:11 Router3:192.168.61.1:6
rem 11  10.127.127.1 0
rem 34	192.168.0.26 1
rem 41	192.168.56.1 2
rem 64	192.168.61.1 3
rem 95	192.168.71.1 4
