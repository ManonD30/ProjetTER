<%@include file="header.jsp" %>

	<script>
		createCookie(0);
	</script>
	<div class="sideLeft"></div>

	<div class=sideMiddle>

		<h3 class=contentText>Select your ontologies and your alignment.</h3>
		<div class=form>

			<form action="rest/matcher/uploadValidationFiles" method="post"
				enctype="multipart/form-data" name=form
				onsubmit="display_div('btnValidate');">
				<label for=firstFile>Source ontology</label> <input type="file"
					id=firstFile name="firstFile" accept=".owl" required /> <br>
				<label for=secondFile>Target ontology&nbsp;</label> <input
					id=secondFile type="file" name="secondFile" accept=".owl" required />
				<br> <label for=rdfFile>Alignment
					&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
				<input id=rdfFile type="file" name="rdfFile" accept=".rdf" required />
				<br> <br> <input class=btn type="submit" value="Upload" />
			</form>

		</div>

		<div class=btnMatch id=btnValidate style="display: none;">
			<form action="validation"
				method="post" name=runMatch
				onsubmit="document.location.href = '#overlay';">

				I'm agreeing to let YAM++ save my files. <input type="radio"
					name="saveOption" value="yes" checked>Yes <input
					type="radio" name="saveOption" value="no">No <br> <br>
				<input class=btn type="submit" value="Validate!" />
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