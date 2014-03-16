<?php

class Logger{
	

	var $debugEnabled = true;
	var $ifStdoutEnabled = false;
	
	
	//var $instance = null;
	
	var $clazz;
	
	function __construct($clazz) {
		$this->clazz = $clazz;
	}
	
	/*
	function getInstance($clazz) {
		if ($instance == null)
			$instance = new Logger($clazz);
		return $instance;
	}
	*/
	
	function debug($message) {
		if ($this->isDebugEnabled())
			$this->log("DEBUG", $message);
	}

	function info($message) {
		$this->log("INFO", $message);
	}
	
	function error($message) {
		$this->log("ERROR", $message);
	}
	
	function log($level, $message) {
		$this->printMessage($this->clazz . "\t" . $level . "\t" . $message . "\n");
	}
	
	function isDebugEnabled() {
		return $this->debugEnabled;
	}
	
	function printMessage($message) {
		if ($this->ifStdoutEnabled) {
			print $message;
		}
	}
}

?>