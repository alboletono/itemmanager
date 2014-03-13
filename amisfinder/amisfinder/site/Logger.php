<?php

class Logger{
	

	static $debugEnabled = true;
	static $ifStdoutEnabled = false;
	
	
	static $instance = null;
	
	protected $clazz;
	
	protected function __construct($clazz) {
		$this->clazz = $clazz;
	}
	
	static function getInstance($clazz) {
		if (Logger::$instance == null)
			Logger::$instance = new Logger($clazz);
		return Logger::$instance;
	}
	
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
	
	private function log($level, $message) {
		$this->printMessage($this->clazz . "\t" . $level . "\t" . $message . "\n");
	}
	
	function isDebugEnabled() {
		return Logger::$debugEnabled;
	}
	
	function printMessage($message) {
		if ($ifStdoutEnabled) {
			print $message;
		}
	}
}

?>