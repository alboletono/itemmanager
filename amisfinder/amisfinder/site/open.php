<?php

require_once 'UserQueries.php';

	/**
	 * @param site should be provided in entry to redirect.
	 * @param query the query that has been entered by the user.
	 */
echo "avant";

if ($_GET['site'] != '' && $_GET['query'] != '') {
	echo "avant2";
	UserQueries::saveUserQuery($_GET['query'], $_GET['site']);
	header('Location: '.$_GET['site']);
}
else {
	die ("Are you trying to hack this site?");
}



?>