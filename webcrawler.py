#!/usr/bin/python3
#
#  Simple python webcrawler script  
#  that takes a input file containing a list of urls
#  one per line
#
#  Example,
#
#  https://www.nighthour.sg/
#  https://www.nighthour.sg/about.html
#  https://www.nighthour.sg
#
#  The script uses selenium webdriver with the
#  mozilla geckodriver and visit each url
#  using firefox browser in headless mode
#  The geckodrive binary needs to be in the PATH. 
#  Javascript etc... will be executed in 
#  the headless firefox.
#
#  This script can be used to populate urls
#  for the web defacement monitoring application. The web
#  defacement monitoring needs to be in capture mode. 
#
#  See 
#  https://www.nighthour.sg/articles/2018/detect-web-defacement-javascript-google-appengine.html
#
#  Ng Chianglin 
#  Apr 2018 
# 
#

import sys
import time
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.common.by import By



def initWebDriver():
    opts=Options()
    opts.set_headless(headless=True)
    #opts.add_argument("--headless")
    driver = webdriver.Firefox(firefox_options=opts)
    return driver


def loadurl(driver, url):
    driver.get(url)
    # sleep 10 seconds for all the ajax calls to complete
    # this value can be increased if the url are not updated 
    # properly in the defacement monitoring app
    time.sleep(10)
    print("Processed " , driver.title)



if __name__ == "__main__": 

   if len(sys.argv) != 2:
      print("Usage: python3 " , sys.argv[0] , "<input file>")
      sys.exit(1)

fname = sys.argv[1]


try:
   ifp = open(fname,'r')
except OSError:
   print("Cannot open file : ", fname)
else:
   
   driver = initWebDriver()
  
   count = 1
   for line in ifp:
      line = line.lower()
      if line.startswith('https://') :
          print("Processing ", line,  end='')
          count += 1
          loadurl(driver, line)

      else:
          print("Invalid line " , count, " : " , line, end='')
          count += 1

finally:
    ifp.close()
    driver.quit()






 
