@echo off
start java Setup -i 41 Router0 Router1:192.168.61.1:5 Router2:192.168.71.1:8
start java Setup -i 64 Router1 Router0:192.168.51.1:5 Router2:192.168.71.1:4 Router3:192.168.81.1:3
start java Setup -i 95 Router2 Router0:192.168.51.1:8 Router1:192.168.61.1:4 Router3:192.168.81.1:2 Router4:192.168.91.1:11
start java Setup -i 105 Router3 Router1:192.168.61.1:3 Router2:192.168.71.1:2 Router4:192.168.91.1:6
start java Setup -i 110 Router4 Router2:192.168.71.1:11 Router3:192.168.81.1:6
rem 41	192.168.51.1 0
rem 64	192.168.61.1 1
rem 95	192.168.71.1 2
rem 105	192.168.81.1 3
rem 110	192.168.91.1 4

