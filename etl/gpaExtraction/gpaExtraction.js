var gpaQuery = '    ID \n\
^(*gpa method="added value") \n\
^(*gpa method="added value" term="Q1") \n\
^(*gpa method="added value" term="Q2")';

var exportSelector = "#lnk_QuickExport";
var formClass = '.noSubmitLoading';


var casper = require('casper').create({
    clientScripts: ["./jquery-1.11.3.js"],
    verbose: true,
    logLevel: "debug",
    waitTimeout: 10000
});
var fs = require('fs');
var propFile = fs.open("powerschool.properties", "r");
var lines = propFile.read().split("\n");
casper.userAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X)');
var utils = require('utils');
casper.options.viewportSize = {width: 900, height: 600};

var username = "";
var password = "";
var domain = "";
var enabled = "";
var screenScrapeGpa = "";

for (var i = 0; i < lines.length; i++) { 
    var usernameIndex = lines[i].search("powerschool.username=");
    if (usernameIndex > -1) { 
        username = lines[i].split("=")[1];
        continue;
    }
    var screenScrapeGpa = lines[i].search("powerschool.screenscrape.gpa=");
    if (screenScrapeGpa > -1) {
      screenScrapeGpa = lines[i].split("=")[1];
      continue;
    }
    var passwordIndex = lines[i].search("powerschool.password=");
    if (passwordIndex > -1) { 
        password = lines[i].split("=")[1];
        continue;
    }
    var domainIndex = lines[i].search("powerschool.client.url=");
    if (domainIndex > -1) { 
        domain = lines[i].split("=")[1];
        continue;
    }
    var enabledIndex = lines[i].search("powerschool.screenscrape.enabled=");
    if (enabledIndex > -1) { 
        enabled = lines[i].split("=")[1];
        continue;
    }
}

if (username.length == 0 || password.length == 0 || domain.length == 0 || enabled.length == 0 || screenScrapeGpa.length == 0) {
	casper.echo("Credentials, domain, or enabled properties are invalid, did you set the properties file?");
	casper.exit();
}

if (enabled.toLowerCase() !== "true") { 
    casper.echo("'enabled' property in file is not set to 'true', so skipping screen scraping.");
    casper.exit();
}
if (screenScrapeGpa.toLowerCase() !== "true") {
  casper.echo("'screenScrapeGpa' property in file is not set to 'true', so skipping screen scraping for GPA.");
  casper.exit();
}

if (domain.substring(domain.length-1,domain.length) === "/") { 
    domain = domain.substring(0, domain.length-1);
}

function login(casper) {
	casper.start(domain + '/admin/pw.html', function() {
	this.waitForSelector("#LoginForm", function() {
			    this.fill('#LoginForm', {
			    	'username': username,
			    	'password': password
			    }, true);

	});

});
}

login(casper);
downloadStudents(casper, "#selectMaleStudents", "1", 0);
downloadStudents(casper, "#selectFemaleStudents", "2", 0);



//Downlaods the information for a selection of students
function downloadStudents(casper, selector, fileName, attempt) {

		if (attempt >= 10) {
			casper.then(function() {
				this.echo("Maximum timeout reached... exiting");
				this.exit;
			});
		}

		casper.then(function() {

			casper.waitForSelector(selector,function(){
				casper.click(selector);
			}, function() {
				login(casper);
				downloadStudents(casper, selector, fileName, attempt+1);
			});

			//This text is Current Student Selection(xStudents) It changing indicates that we have finished loading
			casper.waitForSelectorTextChange(".box-round h2", function() {
				casper.click("#selectFunctionDropdownButton");
			}, function() {
				login(casper);
				downloadStudents(casper, selector, fileName, attempt+1);
			});
			
			//Appears from a dropdown, moves us the page we can select new prompt
			casper.waitUntilVisible(exportSelector, function() {
				casper.click(exportSelector);
			}, function() {
				login(casper);
				downloadStudents(casper, selector, fileName, attempt+1);
			});

			//Url for the GPA query page
			casper.waitForUrl("admin/importexport/exportstudents.html?dothisfor=selected", function() {
				
			if (casper.exists(formClass)) {
				casper.fill(formClass, {
					"fielddelim": "Comma"
				}, false);
				//Text area for the GPA query
				casper.sendKeys("#tt",gpaQuery, {
					reset: true
				});

				//Get the form data to casper
				var res = casper.page.evaluate(function() {
					var formClass = '.noSubmitLoading';
					var res={};
					var f=$(formClass);
					res.action = f.attr('action');

					res.post = f.serialize();
					return res; //Return the form data to casper
				});

				if (res.post) {

					console.log("form.action = " + res.action);
					console.log("form.post = " + res.post);

					casper.download(res.action, "extractedGpa" + fileName + ".csv", "POST", res.post);
				}
				else {
					console.log("Unable to extract form properties for file download!");
				}

				casper.click("#navStudentSearch");

		    }
			}, function() {
				login(casper);
				downloadStudents(casper, selector, fileName, attempt+1);
			});

	});

}

casper.run(function() {
    this.echo('Done.').exit();

});
