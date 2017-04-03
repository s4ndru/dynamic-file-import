<%--
  Created by IntelliJ IDEA.
  User: s4ndru
  Date: 28/03/2016
  Time: 18:15
--%>

<%@ page contentType="text/html;charset=UTF-8"%>
%{--<link rel="stylesheet" href="web-app/css/bootstrap.css"/>--}%
<html>
    <head>

        <title>create routine</title>
        <g:javascript library="jquery" plugin="jquery"/>
        %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.css')}" type="text/css">--}%
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
              integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    </head>
    <body>
        <div class="col-lg-3 center-block">
            <dfi:createTransformationRoutine/>
        </div>
    </body>
</html>