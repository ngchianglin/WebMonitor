#!/usr/bin/python3
#
# Simple python script
# to create a user entity, 
# the password, salt and otp field
# Refer to article 
# https://www.nighthour.sg/articles/2018/detect-web-defacement-javascript-google-appengine.html
# 
# Ng Chiang Lin
#

from google.cloud import datastore
print("--------------------------------------")
print("Simple utility to create a User entity")
print("--------------------------------------")
email = input("Enter User Email Address :")
password =  input("Enter User Hexadecimal Password :")
salt = input("Enter Hexadecimal Salt :")
otp = input("Enter Hexadecimal OTP Secret :")


datastore_client = datastore.Client()
kind = 'User'

ukey = datastore_client.key('User', email)
userentity = datastore.Entity(key=ukey, exclude_from_indexes=['Password','Salt','TOTP'])

userentity.update({
    'AccountLock': False,
    'Action':'Alert',
    'CaptureModeIPAddress' : '',
    'Domaincount' : 0,
    'FailLogin' : 0,
    'Mode' : 'Disable',
    'RedirectionURL' : '',
    'Password' : password,
    'Salt' : salt,
    'TOTP' : otp
})


datastore_client.put(userentity)

