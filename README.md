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


# 中文简介 - 使用客户端 JavaScript 监控网络变化

## 介绍

这是个网络监控，网络保安软件。 它使用客户端的 JavaScript 与在谷歌应用引擎 （Google App Engine）运行的 JSP/Servlet 应用软件，来监控网页。 一旦网页有未经授权的更改，软件就会发出提醒邮件。 客户端的 JavaScript 也能重定向到特定的错误页面。 这可以防止网页涂改， 加强网络安全。 


这软件类似于分析和性能监视 （analytics and performance monitoring) 软件。 
客户端 JavaScript 遍历 html DOM 文档，运用 SHA256 把内容散列。 在 html 文档的外部图像例如 html img tag 的图像，外部 javascript，
CSS文件等， 也包括在 SHA256 哈希内. JavaScript 利用 AJAX 将 SHA256 哈希发送到运行在谷歌应用引擎 （Google App Engine）的 JSP/Servlet 程序。
如果 传递的 SHA256 哈希与保存在数据库中的哈希不同，程序会发出电子邮件警报。程序也能使唤客户端 JavaScript 把客户端浏览器重定向到特定的错误页面。

## 软件解说与如何使用

可以参考下面的详细文章

[Monitoring Web Changes using Javascript and Google App Engine](https://www.nighthour.sg/articles/2018/monitor-webchange-javascript-google-appengine.html)


## 代码数字签名

> 这软件代码有数字签名。可以参考 
[https://www.nighthour.sg/git-gpg.html](https://www.nighthour.sg/git-gpg.html) 

> Github GPG签名验证 [https://github.com/blog/2144-gpg-signature-verification](https://github.com/blog/2144-gpg-signature-verification)


