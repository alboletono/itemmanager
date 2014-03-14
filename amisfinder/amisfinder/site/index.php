
<html>

<title>Amisfinder - un moteur de recherche de personnes</title>

<head>
 <meta charset="UTF-8">
   <link href="assets/style.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="assets/jquery-ui.css">
    <script src="assets/jquery-1.9.1.js"></script>
	<script src="assets/jquery-ui-1.9.1.js"></script>
	<script src="assets/jquery-ui-core.js"></script>
	<script src="assets/jquery-ui-widget.js"></script>
	
	<script>
		function onSearchClick() {
			$(function() {
				$( "#progressbarLabel" ).text("Recherche en cours");
				$( "#progressbar" ).progressbar({
					value: false
				});
			});
		}
	</script>
 </head>

<body>


<h1><a href="index.php"><span class="logo1">Amis</span><span class="logo2">finder</span></a></h1>
<!-- Google ad -->
<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<!-- AF_Banner -->
<ins class="adsbygoogle"
     style="display:inline-block;width:468px;height:15px"
     data-ad-client="ca-pub-9211879234049376"
     data-ad-slot="9127294801"></ins>
<script>
(adsbygoogle = window.adsbygoogle || []).push({});
</script>
<!-- End of google ad -->

<div id="searchForm" >
<form method="post" action="index.php">
	<table>
		<tr><td>
			<input id="request" name="request" type="text" size="30" value="<?php echo $_POST['request']; ?>" onclick="inputRequestClick()"></input>
		</td></tr>
		<tr><td>
			<input name="submit" type="submit" value="rechercher" onclick="onSearchClick()"></input>
		</td></tr>
	</table>
</form>
</div>

<div id="progressbar"><div id="progressbarLabel" class="progress-label">&nbsp;</div></div>

<?php 
if ($_POST['request'] != "") include_once 'results.php';
?>

<?php 
if ($_POST['request'] == "") include_once 'lastresults.php';
?>


<!-- Scripting part to display message in the request input to invite people to write down a request -->
<script type="text/javascript">
	var defaultQuery = "Entrez le nom de la personne recherchée";
	var inputRequest = $("#request");
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

