

<?php

	include_once 'Fetcher.php';
	include_once 'UserQueries.php';

	$fetcher = new Fetcher();
	$expression = "site:http://copainsdavant.linternaute.com/p/ intitle:%s";
	$results1 = $fetcher->fetch(sprintf($expression, $_POST['request'], $_POST['request']));
	
	$expression = "site:http://facebook.com/ intitle:%s";
	$results2 = $fetcher->fetch(sprintf($expression, $_POST['request'], $_POST['request']));
	
	$expression = "site:http://photo-de-classe.com/p/ intitle:%s";
	$results3 = $fetcher->fetch(sprintf($expression, $_POST['request'], $_POST['request']));

	$results = array_merge($results1, $results2, $results3);
	array_rand($results);
	
	echo "<h2><span class='logo1'>".count($results)." résultats pour </span><span class='logo2'>".$_POST['request']."</span></h2>";
?>
	<div id="container">
	<div id="results">
<?php 
	$i = 0;
	foreach ($results as $result) {
		$resultTitle = sprintf("<a href='open.php?query=%s&site=http://%s' target='_new'>%s</a><br>", $_POST['request'], $result->url, $result->title); 
		echo "<div class='resultTitle'>$resultTitle</div>";
		echo "<div class='resultSnippet'>$result->snippet.</div>";
		echo "<div class='resultSpace'>&nbsp;</div>";
		
		// Inserting google ad
		if ($i%5 == 0 && $i!=0) {
?>
			<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
			<!-- AF_Results -->
			<ins class="adsbygoogle"
			     style="display:inline-block;width:728px;height:90px"
			     data-ad-client="ca-pub-9211879234049376"
			     data-ad-slot="6034227605"></ins>
			<script>
			(adsbygoogle = window.adsbygoogle || []).push({});
			</script>

<?php 
		}
		
		$i++;
	}
?>
	</div>

</div>