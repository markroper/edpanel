var gpaQuery = '    ID \n\
^(*gpa method="added value") \n\
^(*gpa method="simple") \n\
^(*gpa method="simple percent") \n\
^(*gpa method="added value" term="Q1") \n\
^(*gpa method="simple" term="Q1") \n\
^(*gpa method="simple percent" term="Q1")';

var exportSelector = "#lnk_QuickExport";
var formClass = '.noSubmitLoading';

var casper = require('casper').create({
    clientScripts: ["./jquery-1.11.3.js"],
    verbose: true,
    logLevel: "debug",
    waitTimeout: 10000
});

casper.userAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X)');
var utils = require('utils');
casper.options.viewportSize = {width: 900, height: 600};

var username = "jkim";
var password = "EA.password1";
var domain = "https://excelacademy.powerschool.com";


casper.start(domain + '/admin/pw.html', function() {
	this.waitForSelector("#LoginForm", function() {
			    this.fill('#LoginForm', {
			    	'username': username,
			    	'password': password
			    }, true);

	});

});

downloadStudents(casper, "#selectMaleStudents", "1");
downloadStudents(casper, "#selectFemaleStudents", "2");



//Downlaods the information for a selection of students
function downloadStudents(casper, selector, fileName) {


		casper.then(function() {

			casper.waitForSelector(selector,function(){
				casper.click(selector);
			});

			//This text is Current Student Selection(xStudents) It changing indicates that we have finished loading
			casper.waitForSelectorTextChange(".box-round h2", function() {
				casper.click("#selectFunctionDropdownButton");
				});
			
			//Appears from a dropdown, moves us the page we can select new prompt
			casper.waitUntilVisible(exportSelector, function() {
				casper.click(exportSelector);
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
					
					var res={};
					var f=$(formClass);
					res.action = f.attr('action');

					res.post = f.serialize();
					return res; //Return the form data to casper
				});

				if (res.post) {

					console.log("form.action = " + res.action);
					console.log("form.post = " + res.post);

					casper.download(res.action, "gpaExtract" + fileName + ".csv", "POST", res.post);
				}
				else {
					console.log("Unable to extract form properties for file download!");
				}

				casper.click("#navStudentSearch");

		    }
			});

	});

}

casper.run(function() {
    this.echo('Done.').exit();

});