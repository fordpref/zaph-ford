#!/usr/bin/python

import socket, sys, threading, time
import Adafruit_BBIO.GPIO as GPIO

dirpins = '/sys/class/gpio/gpio'
exprep = '/sys/class/gpio/export'
pin1 = 30
pin2 = 31

def ready_pins():
    global exprep, dirpins
    GPIO.setup("P9_11", GPIO.OUT)
    GPIO.setup("P9_13", GPIO.OUT)
    print '[+] preset P9_11 and P9_13 as output'
    GPIO.output("P9_11", GPIO.LOW)
    GPIO.output("P9_13", GPIO.LOW)
    print '[+] Garagedoor in ready state'
    return()


def activate_relay():
    global sock, dirpins, exprep
    print "door activating"
    GPIO.output("P9_11", GPIO.HIGH)
    time.sleep(1)
    GPIO.output("P9_11", GPIO.LOW)
    return

def listener():
    global sock
    print "started listener"
    while True:
        data, address = sock.recvfrom(16384)
        data = data[:-1]
        print data
	if data == '31337 activate':
            send = sock.sendto('Activating Door\n\n', address)
            activate_relay()
        else:
            send = sock.sendto('Fuckoff Asshole\n\n', address)

            

# create socket object
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to the port
server_address = ('', 6736)
sock.bind(server_address)
ready_pins()
listener_thread = threading.Thread(target = listener)
listener_thread.setDaemon(True)
listener_thread.start()
while True:
    pass

