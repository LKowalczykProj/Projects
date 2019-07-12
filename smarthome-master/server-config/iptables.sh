#!/bin/bash

iptables -F
iptables -X

# create chains for opening ports
iptables -N TCP
iptables -N UDP

# set default policies
iptables -P FORWARD DROP
iptables -P OUTPUT ACCEPT
iptables -P INPUT DROP

# allow already established connections
iptables -A INPUT -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT

# accept all from loopback
iptables -A INPUT -i lo -j ACCEPT

# drop invalid packets
iptables -A INPUT -m conntrack --ctstate INVALID -j DROP

# accept icmp echo requests
iptables -A INPUT -p icmp --icmp-type 8 -m conntrack --ctstate NEW -j ACCEPT

# attach TCP and UDP chains to handle all new incoming connections
iptables -A INPUT -p udp -m conntrack --ctstate NEW -j UDP
iptables -A INPUT -p tcp --syn -m conntrack --ctstate NEW -j TCP

# reject connections to unopened ports
iptables -A INPUT -p udp -j REJECT --reject-with icmp-port-unreachable
iptables -A INPUT -p tcp -j REJECT --reject-with tcp-reset

# reject other protocols
iptables -A INPUT -j REJECT --reject-with icmp-proto-unreachable

# accept connections on port 58822 - SSH 
iptables -A TCP -p tcp --dport 58822 -j ACCEPT
# accept port 8000 - django test server
iptables -A TCP -p tcp --dport 8000 -j ACCEPT
# accept port 1883 - MQTT
iptables -A TCP -p tcp --dport 1883 -j ACCEPT
