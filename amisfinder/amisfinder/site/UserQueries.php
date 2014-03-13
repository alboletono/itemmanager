<?php

class UserQueries {

	static $filename = "userqueries.txt";
	
	const MAX_QUERIES = 100;
	
	/**
	 * @return an array containing the query and the associated site url.
	 */
	static function getUserQueries() {
		$result = array();
		
		$fp = fopen(UserQueries::$filename, "r");
		$contents = '';
		if (flock($fp, LOCK_EX)) {  // acquire an exclusive lock
			$contents = fread($fp, filesize(UserQueries::$filename));
			flock($fp, LOCK_UN);    // release the lock
		} else {
			die ("Couldn't get the lock!");
		}
		fclose($fp);
		
		if ($contents != '') {
			
			$lines = explode("\n", $contents);
			// Shrinking last queries to the last 10 queries
			if (count($lines) > self::MAX_QUERIES) {
				die("prout");
				$contentsNew = '';
				for ($i = count($lines) - self::MAX_QUERIES ;  $i < count($lines) -1; $i++ ) {
					if ($lines[$i] != '\n') {
						$contentsNew .= $lines[$i] . "\n";
					}
				}
				
				if ($contentsNew != '') {
					$fp = fopen(UserQueries::$filename, "w");
					if (flock($fp, LOCK_EX)) {  // acquire an exclusive lock
						fwrite($fp, $contentsNew);
						fflush($fp);            // flush output before releasing the lock
						flock($fp, LOCK_UN);    // release the lock
					} else {
						die ("Couldn't get the lock!");
					}
					fclose($fp);
				}
				$lines = explode("\n", $contentsNew);
			}
			foreach($lines as $line) {
				$column = explode("\t", $line);
				if (count($column) == 2) {
					array_push($result, [$column[0], $column[1]]);
				}
			}
				
		}
		
		return $result;
	}
	
	/**
	 * Saves the user query in a simple txt file.
	 * Separator used is "tab"
	 * @param string $query the user query to save in file
	 * @param string $site the site found to save in file
	 */
	static function saveUserQuery( $query,  $site) {
	
		$fp = fopen(UserQueries::$filename, "a+");
	
		if (flock($fp, LOCK_EX)) {  // acquire an exclusive lock
			$line = sprintf("%s\t%s\n", $query, $site);
			fwrite($fp, $line);
			fflush($fp);            // flush output before releasing the lock
			flock($fp, LOCK_UN);    // release the lock
		} else {
			die ("Couldn't get the lock!");
		}
	
		fclose($fp);
	
	}
	
}

?>