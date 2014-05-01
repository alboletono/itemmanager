= Nutch-copainsdavant =

== Aim of this parser-filter ==

* Allows to crawl "avis de recherche" from copainsdavant
* Main entry point is http://copainsdavant.linternaute.com/recherche-amis/


== How it works ==

The main page is fetched: http://copainsdavant.linternaute.com/recherche-amis
This page contains: 
* links to "avis de recherche": "friends" that want to find other friends
  * http://copainsdavant.linternaute.com/recherche-amis/dominique-cadiou-richard-3698
* links to other pages of "avis de recherche": 
  * http://copainsdavant.linternaute.com/recherche-amis/?page=10&total=3511
* unwanted links
