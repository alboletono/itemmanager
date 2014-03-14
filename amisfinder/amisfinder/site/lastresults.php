<br>
<br>
<h2><span class="logo1">Dernières</span><span class="logo2"> recherches</span></h2>
<?php
include_once 'UserQueries.php';
$userQueries = UserQueries::getUserQueries();
?>
<div id="container">
<div id="results" class="queryResults">
<?php 
foreach ($userQueries as $array) {
	echo "<span class='tagResult'>";
	echo sprintf("<a href='%s' target='_new'>%s</a>", $array[1], $array[0]);
	echo "</span>";
	echo "&nbsp;";
}
?>

<script language="javascript">
var htmlcontent = "";
var results = document.getElementById("results");

if (results != null) {
	for(var i=0; i<results.childNodes.length - 1;i++){
	   var rnd = 1 + Math.floor(Math.random() * 6);
	   var child = results.childNodes.item(i);
	   child.className = "tagResult" + rnd;
	}
}
</script>
</div>
</div>