
<%@include file="header.jsp" %>
<script src="https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js"></script>

    <div class="sideLeft"></div>

    <div class=sideMiddle>

	<div class=publicationPage>
			<h1>Using the REST API</h1>

			<h3>HTTP GET Request</h3>
                        
                        <h4>Using your browser</h4>
			<p>
                          <a href="http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl2=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl&sourceUrl1=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl">
                            http://localhost:8083/rest/matcher?sourceUrl2=https://Conference.owl&sourceUrl1=https://cmt.owl
                          </a>
                          <a href="http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl2=http://data.bioportal.lirmm.fr/ontologies/MEDLINEPLUS/download?apikey=7b82f0a5-a784-494c-9d2e-cae6698099db&sourceUrl1=http://data.bioportal.lirmm.fr/ontologies/CIF/download?apikey=7b82f0a5-a784-494c-9d2e-cae6698099db">
                            http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl2=http://data.bioportal.lirmm.fr/ontologies/MEDLINEPLUS/download&sourceUrl1=http://data.bioportal.lirmm.fr/ontologies/CIF/download
                          </a>
                        </p>
                        
                        <h4>Using cURL command</h4>
                        
                        <pre class="prettyprint">curl -X GET http://localhost:8083/rest/matcher?ont2=http://purl.obolibrary.org/obo/po.owl&ont1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
curl -X GET http://localhost:8083/rest/matcher?ont2=http://purl.obolibrary.org/obo/po.owl&ont1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
</pre>

			<hr>
			<h3>HTTP POST Request</h3>

                        <h4>Using cURL command</h4>
                        
                        <pre class="prettyprint">curl -X POST -H \"Content-Type: multipart/form-data\ -F ont1=@/path/to/ontology_file.owl http://localhost:8083/rest/matcher?sourceUrl2=http://purl.obolibrary.org/obo/po.owl
curl -X POST -H "Content-Type: multipart/form-data" -F ont2=@/srv/yam2013/cmt.owl -F ont1=@/srv/yam2013/Conference.owl http://localhost:8083/rest/matcher
curl -X POST http://localhost:8083/rest/matcher -d 'sourceUrl1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl' -d 'sourceUrl2=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl'
</pre>
  
            </div>
	</div>
    
        <div class="sideRight"></div>

<%@include file="footer.jsp" %>