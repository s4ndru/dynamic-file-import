# Dynamic Import Module
Simple ETL module for Grails 2.2.*

## How to use
  - Download Source
  - Extract in folder of Grails application
  - Add inline plugin command in BuildConfig.groovy
  - Optionally, if inline plugin is not detected, do a grails clean

```groovy
// Example of inline plugin command
grails.plugin.location."dynamic-import-module" = "dynamic-import-module-master"
```

### Example html using one of the taglibs for configuring a XML Parser
```html
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title>create routine</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
			  integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	</head>
	<body style="background-color: #3c3c3c">
		<div class="col-lg-4 center-block">
			<dim:createSimpleXMLParser/>
		</div>
		<g:javascript src="setBootstrapClasses.js" plugin="dynamic-import-module"/>
	</body>
</html>
```


Detailed documentation with use cases can be found [here](https://github.com/s4ndru/dynamic-import-module/blob/master/misc/Dynamic_Import_Module.pdf)
