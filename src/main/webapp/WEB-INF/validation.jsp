<%@page import="java.util.ArrayList"%>
<%@ page pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>

	<div id=sideLeft class="sideLeft"></div>

	<div class=sideMiddle>

		<h3 class=contentText>Mappings</h3>

		<div id=toHideWhenDownload>

			<label for=seuilDynamic>Select your threshold</label> <br> <input
				id=seuilDynamic name=seuilDynamic type="number" min=0 max=1
				step=0.05 size=3 value=0 onchange="refreshTab();"> <br>
			<br>

			<table>
				<thead>
					<tr>
						<th class=thSmall>Line</th>
						<th>Source label</th>
						<th>Target label</th>
						<th>Relation</th>
						<th>Score</th>
						<th>Validity</th>
					</tr>
				</thead>

			</table>
			<%
				//get list from response
				ArrayList<matcher.rest.sw.Map> liste = (ArrayList) request
						.getAttribute("data");

				//get the execution time from response
				java.util.Map<String, String> onto1 = (java.util.Map) request
						.getAttribute("onto1");
				//get the execution time from response
				java.util.Map<String, String> onto2 = (java.util.Map) request
						.getAttribute("onto2");
			%>

			<form
				action='http://193.49.107.124/matcher.rest.sw/validationDownload'
				method='post'>
				<div class=tabDiv>
					<table id=table>
						<%
							//for each cell in the list
							for (int line = 0; line < liste.size(); line++) {
								int numLine = line + 1;
								out.println("<tr>"); //new table line
								out.println("<td class=tdSmall>" + numLine + "</td>"); //display line number
								out.println("<td>" + onto1.get(liste.get(line).getE1())
										+ "</td>"); //display first entity
								out.println("<td>" + onto2.get(liste.get(line).getE2())
										+ "</td>"); //display second entity
								out.println("<td>" + liste.get(line).getRelation() + "</td>"); //display relation
								out.println("<td>" + liste.get(line).getScore() + "</td>"); //display score
								out.println("<td class=tdSmall><INPUT type='checkbox' name='checkbox' class=css-checkbox value='"
										+ line + "' id='" + line + "' checked></td>"); //display checkbox
								out.println("</tr>"); //close table line
							}
						%>
					</table>
				</div>

				<div class=btnCenter>
					<input class=btn type=button value='Validate'
						onclick='seeDownloadButton();' />
				</div>
		</div>

		<div class=btnCenter id='seeMappings' style='display: none'>
			<input class=btn type="button" value="See mappings"
				onclick='seeMappings();' />
		</div>
		<div class=btnCenter id='download' style='display: none'>
			<input class=btn type="submit" value="Download mappings" />
		</div>
		</form>

		<div class=btnCenter>
			<input class=btn type="button" value="Return to validator"
				onclick="document.location.href = '#overlay';">
		</div>

		<div id="overlay">
			<div class="popup_block">
				<p class=popup_text>
					If you continue, all your results will be lost.<br> <br>
					<a href=#noWhere style="text-decoration: none"><input class=btn
						type="button" value="Stay on this page"></a> <a
						href=validator style="text-decoration: none"><input
						class=btn type="button" value="Return to validator"></a>
				</p>
			</div>
		</div>

	</div>
	<div class="sideRight"></div>
	
<%@include file="footer.jsp" %>