

<?php

	include_once 'Fetcher.php';
	include_once 'UserQueries.php';

	$fetcher = new Fetcher();
	$expression = "site:http://copainsdavant.linternaute.com/p/ intitle:%s";
	$results = $fetcher->fetch(sprintf($expression, $_POST['request']));

	echo "<h2><span class='logo1'>".count($results)." résultats pour </span><span class='logo2'>".$_POST['request']."</span></h2>";
?>
	<div id="container">
	<div id="results">
<?php 
	foreach ($results as $result) {
		$resultTitle = sprintf("<a href='open.php?query=%s&site=http://%s' target='_new'>%s</a><br>", $_POST['request'], $result->url, $result->title); 
		echo "<div class='resultTitle'>$resultTitle</div>";
		echo "<div class='resultSnippet'>$result->snippet.</div>";
		echo "<div class='resultSpace'>&nbsp;</div>";
	}
?>
	</div>

</div>