<%@include file="header.jsp" %>
	
	<script>
		createCookie(0);
	</script>

	<div class="sideLeft"></div>

	<div class=sideMiddle>

		<h3 class=contentText>Select your ontologies.</h3>
		<div class=form>
			<form action="rest/matcher" method="post"
				enctype="multipart/form-data" name=form
				onsubmit="document.location.href = '#overlay';">

                          
				<label for=firstFile>Source ontology</label> 
                                <br/>
                                <label class="btn btn-info btn-file">
                                  Choose file
                                  <input type="file" id=ont1 name="ont1" accept=".owl" style="display: none;"required /> <br>
                                </label>
                                <br/>
				<label for=secondFile>Target ontology&nbsp;</label> 
                                <br/>
                                <label class="btn btn-info btn-file">
                                  Choose file
                                  <input id=ont2 type="file" name="ont2" accept=".owl" style="display: none;" required />
                                </label>
				<br> <br> <input class=btn type="submit" value="Upload" />
			</form>
		</div>

		<div class=btnMatch id=btnMatch style="display: none;">
			<form action="result"
				method="post" name=runMatch
				onsubmit="document.location.href = '#overlay';">

				I'm agreeing to let YAM++ save my ontologies.<input type="radio"
					name="saveOption" value="yes" checked>Yes <input
					type="radio" name="saveOption" value="no">No <br> <br>
				<input class=btn type="submit" value="Match!" />
			</form>
		</div>
		<div id="overlay">
			<div class="popup_block">
				<img width=300 alt="" src="images/loading-blue.gif">
				<p class=popup_text>
					Please wait while matching.<br>This can take a while...
				</p>
			</div>
		</div>

		<hr>

	</div>
	<div class="sideRight"></div>

<%@include file="footer.jsp" %>