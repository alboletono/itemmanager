<?php

define(QUERIES_FILE_NAME, "userqueries.txt");

define(MAX_QUERIES, 100);


class UserQueries {

	
	/**
	 * @return an array containing the query and the associated site url.
	 */
	function getUserQueries() {
		$result = array();
		
		$fp = fopen(QUERIES_FILE_NAME, "r");
		$contents = '';
		if (flock($fp, LOCK_EX)) {  // acquire an exclusive lock
			$contents = fread($fp, filesize(QUERIES_FILE_NAME));
			flock($fp, LOCK_UN);    // release the lock
		} else {
			die ("Couldn't get the lock!");
		}
		fclose($fp);
		
		if ($contents != '') {
			
			$lines = explode("\n", $contents);
			// Shrinking last queries to the last 10 queries
			if (count($lines) > MAX_QUERIES) {
				die("prout");
				$contentsNew = '';
				for ($i = count($lines) - MAX_QUERIES ;  $i < count($lines) -1; $i++ ) {
					if ($lines[$i] != '\n') {
						$contentsNew .= $lines[$i] . "\n";
					}
				}
				
				if ($contentsNew != '') {
					$fp = fopen(QUERIES_FILE_NAME, "w");
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
					$couple = array();
					array_push($couple, $column[0]);
					array_push($couple, $column[1]);
					array_push($result, $couple);
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
	function saveUserQuery( $query,  $site) {
	
		$fp = fopen(QUERIES_FILE_NAME, "a+");
	
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