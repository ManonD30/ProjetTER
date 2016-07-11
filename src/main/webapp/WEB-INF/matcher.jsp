<%@include file="header.jsp" %>
	
	<script>
		createCookie(0);
	</script>

	<div class="sideLeft"></div>

	<div class=sideMiddle>

		<h3 class=contentText>Select your ontologies.</h3>
		<div class=form>
			<form action="result" method="post"
				enctype="multipart/form-data" name=form
				onsubmit="document.location.href = '#overlay';">
                          
                                <div class="row">

                                  <div class="col-md-6">
                                    <label for=firstFile>Source ontology</label> 
                                    <br/>
                                    <label id="labelOnt1" class="btn btn-info btn-file">
                                      Choose file
                                      <input id=ont1 type="file" name="ont1" accept=".owl" style="display: none;" required />
                                    </label>
                                  </div>
                                  <div class="col-md-6">
                                    <label for=secondFile>Target ontology&nbsp;</label> 
                                    <br/>
                                    <input type="url" id="ont3" name="ont3"/>
                                    <br/>
                                    <span style="text-align: center">or</span>
                                    <br/>
                                    <label id="labelOnt2" class="btn btn-info btn-file">
                                      Choose file
                                      <input id=ont2 type="file" name="ont2" accept=".owl" style="display: none;" />
                                    </label>
                                  </div>
                                </div>
				<br/><br/>
                                <label style="font-weight: normal;"><input type="checkbox" value="save" checked>I'm agreeing to let YAM++ save my ontologies</label>
                                <br/><br/>
                                <input class=btn type="submit" value="Match!" />
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