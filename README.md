# Simple Monitoring for Web Changes Using Client-Side JavaScript

## Introduction
A simple application that monitors for unauthorized web changes (e.g. web defacements etc...) using client-side javascript 
and a Java application running on Google App Engine. 

The application works like performance and analytics software where a piece of javascript is inserted or embedded into the web pages/web
content being monitored. The javascript traverses the web document and calculate a sha256 hash of the document content.
External resources in the web document such as images, external javascript, css files are included in the hash calculation. 
If an image displayed in the document (e.g. using &lt;img&gt; tag) has been vandalized, the monitoring application will be able to detect this. 

The client-side javascript sends the sha256 hash to the backend application running on Google App Engine. 
If the hash is different from what is stored in the application datastore, an email alert will be sent to the website administrator. Optionally the application can be configured to instruct the javascript to redirect the visitor browser to a specific error page.

The redirection is a useful feature that can reduce the exposure time of a web defacement. While this is less effective than an in-line appliance which can block defaced content, it is an advantage over pure remote monitoring app which polls website for changes. 

This simple monitoring application is a "hybrid" between an in-line solution and a remote monitoring solution. It can be used as an additional security measure to protect static web content. For example, it can be used together with a remote monitoring app. 

## Building and Deployment

The application is a maven project that can be imported into Eclipse IDE. It can be built and deployed to Google App Engine using 
the Eclipse IDE (Google cloud tools for Eclipse installed). 

The monitoring javascript is present in the scripts directory and can be accessed via 

https://&lt;google-project-id&gt;.appspot.com/scripts/pmon.js 

This script can be included in web pages to be monitored. For greater security, it should be injected through a reverse proxy like Nginx. 
The NginxHtmlHeadFilter [https://github.com/ngchianglin/NginxHtmlHeadFilter](https://github.com/ngchianglin/NginxHtmlHeadFilter) is a 
filter module that can inject a monitoring script into web content. 

For the application to work properly, a Cloud datastore entity, User needs to be created. This creates a user account for logging into 
the App Engine application, registering domains (Fully qualified domain name) and URLs to be monitored etc...

The application uses 2 factor for its login mechanism and relies on Google Authenticator Mobile App for the 2nd Factor OTP. 
Refer to the following section for a detailed article that describes how to set up the application. The article also discuss the threat
model for the monitoring mechanism.

## Article with Detailed Instructions 

Refer to the link below for a detailed article on the setup.

[Monitoring Web Changes using Javascript and Google App Engine](https://www.nighthour.sg/articles/2018/monitor-webchange-javascript-google-appengine.html)

## Source signature
Gpg Signed commits are used for committing the source files. 

> Look at the repository commits tab for the verified label for each commit, or refer to [https://www.nighthour.sg/git-gpg.html](https://www.nighthour.sg/git-gpg.html) for instructions on verifying the git commit. 

> A userful link on how to verify gpg signature [https://github.com/blog/2144-gpg-signature-verification](https://github.com/blog/2144-gpg-signature-verification)



