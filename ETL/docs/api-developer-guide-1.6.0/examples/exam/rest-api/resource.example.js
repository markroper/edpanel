var https = require('https');
var util = require('util');
var url = require('url');

var args = process.argv.splice(2);
var powerschoolServerUrl = args[0];
var path = args[1];
var accessToken = args[2];

var options = url.parse(powerschoolServerUrl);
options.path = path || '/';
options.method = 'GET';
options.headers = {
    'Accept' : 'application/json',
    'Authorization' : 'Bearer ' + accessToken
};

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
                var obj = JSON.parse(data);
                console.log(util.inspect(obj, false, null));
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

req.end();

req.on('error', function(e) {
    console.error('Powerschool test server not found, please check the PowerSchool URL and try again\n' + e);
});