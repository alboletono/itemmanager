<?php

require_once 'Logger.php';
/**
 * Fetcher used to fetch http data from a search engine (in our case yahoo.com).
 * http://fr.search.yahoo.com/search?p=site%3Acopainsdavant.linternaute.com+intitle%3Avigier
 * @author albo
 *
 */
class Fetcher {
	
	static $logger; 
	
	static $URL_PATTERN = "http://fr.search.yahoo.com/search?p=%s";

	var $useProxy = false;
	var $proxyUrl;
	var $proxyPort;
	var $ProxyAuth = CURLAUTH_ANY;
	var $proxyUser;
	var $proxyPassword;
	
	
	function __construct() {
		self::$logger = Logger::getInstance("Fetcher");
	}
	
	function fetch($request) {
		
		self::$logger->info("Begin of fetch.");
		
		// create a new cURL resource
		$ch = curl_init();
		
		// To allow multithreading we can use the following snippet:
		// http://fr2.php.net/manual/fr/function.curl-multi-exec.php
		
		// Set proxy if necessary
		if ($this->useProxy) {
			curl_setopt($ch, CURLOPT_PROXY, $this->proxyUrl);
			curl_setopt($ch, CURLOPT_PROXYUSERPWD, $this->proxyUser.':'.$this->proxyPassword);
			curl_setopt($ch, CURLOPT_PROXYPORT, $this->proxyPort);
			curl_setopt($ch, CURLOPT_PROXYAUTH, $this->ProxyAuth);
		}
		
		$url = sprintf(self::$URL_PATTERN, urlencode($request));
		self::$logger->debug("Url is $url");
		
		// set URL and other appropriate options
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_HEADER, 0);
		
		// grab URL and pass it to the browser
		$output = curl_exec($ch);
	    self::$logger->debug("Curl output is $output");
		
	    
	    $result = $this->extractSearchResults($output);
	    
		// close cURL resource, and free up system resources
		curl_close($ch);
		
		self::$logger->info("End of fetch.");
		return $result;
	}
	
	protected function extractSearchResults($output) {
		
		
		preg_match_all("/<li><div class=\"res\">.*?<\/div><\/li>/", $output, $matches, PREG_PATTERN_ORDER);
		
		$searchResults = array();
		
		foreach ($matches[0] as $match) {
			self::$logger->debug("group found is: $match");

			// Adding new void search result
			$searchResult = new SearchResult();
			array_push($searchResults, $searchResult);
			
			// Extracting url
			$searchResult->url = $this->extractMetadata($match, "/<span class=url>(.*?)<\/span>/");

			// Extracting title
			$searchResult->title = $this->extractMetadata($match, "/<h3><a id=\"link.*?>(.*?)<\/a><\/h3>/");
				
			// Extracting snippet
			$searchResult->snippet = $this->extractMetadata($match, "/<div class=\"abstr\">(.*?)<\/div>/");
				
			
			self::$logger->debug("search result is:\n" . $searchResult->toString());
			
		}
		
		
		return $searchResults;
	}
	
	private function extractMetadata($string, $pattern) {
		$result = null;
		preg_match($pattern, $string, $urlArray);
		if (count($urlArray) > 1) {
			$result = strip_tags($urlArray[1]);
		} else {
			self::$logger->error("no match found for $pattern");
		}
		return $result;
	} 
	
	
}


/**
 * Small class to embed search results.
 * @author albo
 *
 */
class SearchResult {

	public $url;

	public $title;

	public $snippet;
	
	function __construct() {
		
	}
	
	public function toString() {
		return sprintf("url: %s\n title: %s\n snippet: %s", 
				$this->url, $this->title, $this->snippet);
	}
}


?>