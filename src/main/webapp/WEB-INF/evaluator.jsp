<%@include file="header.jsp" %>

	<script>
		createCookie(0);
	</script>

	<div class="sideLeft"></div>

	<div class=sideMiddle>

		<h3 class=contentText>Select your alignment and your reference.</h3>
		<div class=form>
			<form action="rest/evaluator/uploadFiles" method="post"
				enctype="multipart/form-data" name=form
				onsubmit="display_div('btnMatch');">

				<label for=firstFile>Alignment &nbsp;</label> <input type="file"
					id=firstFile name="firstFile" accept=".rdf" required /> <br>
				<label for=secondFile>Reference&nbsp;</label> <input
					id=secondFile type="file" name="secondFile" accept=".rdf" required />
				<br> <br> <input class=btn type="submit" value="Upload" />
			</form>
		</div>

		<div class=btnCenter id=btnMatch style="display: none;">
			<form action="evaluation"
				method="post" name=runMatch
				onsubmit="document.location.href = '#overlay';">

				<input class=btn type="submit" value="Evaluate!" />
			</form>
		</div>
		<div id="overlay">
			<div class="popup_block">
				<img width=300 alt="" src="images/loading-blue.gif">
				<p class=popup_text>Please wait...</p>
			</div>
		</div>

		<hr>

	</div>
	<div class="sideRight"></div>

<%@include file="footer.jsp" %>