
<html>

<title>Amisfinder - un moteur de recherche de personnes</title>

<head>
 <meta charset="UTF-8">
   <link href="assets/style.css" rel="stylesheet" type="text/css">
 </head>

<body>


<h1><a href="index.php"><span class="logo1">Amis</span><span class="logo2">finder</span></a></h1>
<div id="searchForm" >
<form method="post" action="index.php">
	<table>
		<tr><td>
			<input id="request" name="request" type="text" size="30" value="<?php echo $_POST['request']; ?>" onclick="inputRequestClick()"></input>
		</td></tr>
		<tr><td>
			<input name="submit" type="submit" value="rechercher"></input>
		</td></tr>
	</table>
</form>
</div>

<?php 
if ($_POST['request'] != "") include_once 'results.php';
?>

<?php 
if ($_POST['request'] == "") include_once 'lastresults.php';
?>


<!-- Scripting part to display message in the request input to invite people to write down a request -->
<script type="text/javascript">
	var defaultQuery = "Entrez le nom de la personne recherchée";
	var inputRequest = document.getElementById("request");
	if (inputRequest.value == "") {
		
		inputRequest.value = defaultQuery;
	}

	function inputRequestClick() {
		if (inputRequest.value == defaultQuery) 
			inputRequest.value = "";
	}
</script>


</body>

</html>

