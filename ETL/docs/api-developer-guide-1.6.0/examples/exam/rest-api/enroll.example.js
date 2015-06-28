var https = require('https');
var url = require('url');
var fs = require('fs');

var args = process.argv.splice(2);
var powerschoolServerUrl = args[0];
var accessToken = args[1];

var options = url.parse(powerschoolServerUrl);
options.path = '/ws/v1/student';
options.method = 'POST';
options.headers = {
	'Accept' : 'application/xml',
	'Content-Type' : 'application/xml',
	'Authorization' : 'Bearer ' + accessToken
};

fs.readFile('enroll.payload.xml', 'utf8', function(err,payload) {

	if (err) {
		return console.log(err);
	}

	var req = https.request(options, function(res) {
		var data = '';    
		console.log('Status Code: ', res.statusCode);
		console.log('Header: ', res.headers); 
		res.on('data', function(d) {
			data += d;
		});
		res.on('end',function(){
			if(data !== '') {
				try {
					console.log('Body:');
					console.log(data);
				} catch ( exception ) {
					if (exception instanceof SyntaxError ) {
						console.log(data);
					} else {
						throw exception;
					} 
				}
			}        
		});
	});

	req.write(payload);

	req.end();

	req.on('error', function(e) {
		console.error('Powerschool test server not found, please check the PowerSchool URL and try again\n' + e);
	});

});