<%@include file="header.jsp" %>

	<div class=indexPage>

		<div class=indexLogoDiv>
			<img class=logoIndex alt="Yam++" src="images/yam.png">

			<h2>Online ontology and thesaurus matching, mapping validation
				and alignment evaluation</h2>

		</div>
		<div class=indexContentDiv>
			<div class=indexContent>
				<br>
				<h1 style='margin-top: 0;'>
					<a href=matcher.html>Matcher</a>
				</h1>
				<p>Takes as an input ontologies in different formats (owl, skos,
					and various serializations of rdf, such as ttl) and produces
					alignments in a format of the user's choice.</p>

				<hr>

				<h1>
					<a href=validator.html>Validator</a>
				</h1>

				<p>Takes as an input an alignment in one out of several possible
					formats (alignment, owl, skos), visualises it together with the
					degree of confidence of each mapping and lets the user confirm,
					reject or mark as uncertain each of the produced mappings. The
					modified alignment can be stored in one of the possible alignment
					formats.</p>

				<hr>

				<h1>
					<a href=evaluator.html>Evaluator</a>
				</h1>

				<p>Takes as an input an alignment in one out of several possible
					formats, compares it to a reference alignment and outputs the
					corresponding evaluation measures (precision, recall and f-measure)
					together with graphs.</p>
			</div>
		</div>
	</div>

<%@include file="footer.jsp" %>