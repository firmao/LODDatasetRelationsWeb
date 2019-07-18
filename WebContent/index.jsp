<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<p>- Input types:
	<p>-- Dataset. Example: 360c91c1495095f39b7496893b50279e.hdt
	<p>-- Properties separated by coma ",". Example: http://purl.org/dc/terms/date,http://crime.rkbexplorer.com/id/location,http://purl.org/dc/terms/subject
	<p>-- SPARQL query. Example: <textarea>Select ?s ?p ?o where {?s < http://purl.org/dc/terms/date > ?o}</textarea>
	<br/>
	<form id="queryForm" action="matchDataset" method="get">
		<fieldset style="border:none">
		<textarea name="query" cols="100" rows="15"></textarea>
		<br/>
		<input type="submit" value="Execute" />
		</fieldset>
	</form>
</body>
</html>