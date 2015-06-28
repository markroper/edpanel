var https = require('https');
var fs = require('fs');
var express = require('express');
var url = require('url');
var util = require('util');

var args = process.argv.splice(2);
var ip_hostname = args[0];
var port = args[1];
var keyfile = args[2];
var certfile = args[3];
var powerschool_url = args[4];
var access_token = args[5];

var app = express.createServer({
    key: fs.readFileSync(keyfile || ''),
    cert: fs.readFileSync(certfile || '')
});

app.use(express.bodyParser());

app.get('/', function(request, response) {
    var options = url.parse(powerschool_url);
    options.path = '/ws/v1/event_subscription';
    options.method = 'PUT';
    options.headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + access_token
    };

    console.log('---------------------------------------------------------------------');
    console.log('Sending test event to PowerSchool:');
    console.log('url: ', options.protocol + '//' + options.hostname + ':' + options.port + options.path);
    console.log('method: ', options.method);
    console.log('headers: ', options.headers);

    var req = https.request(options, function(res) {
        console.log('---------------------------------------------------------------------');
        console.log('Response from PowerSchool:');
        console.log('status code: ', res.statusCode);
        console.log('headers: ', res.headers);

        var data = '';
        res.on('data', function(d) {
            data += d;
        });

        res.on('end', function() {
            var result;
            if (data !== '') {
                try {
                    result = JSON.parse(data);
                    console.log('body: ', util.inspect(result, false));
                }
                catch (ex) {
                    console.log('body: ', data);
                    console.log('exception caught: ', util.inspect(ex, false));
                }
            }
            else {
                console.log('body: The response body is empty.');
            }

            response.end();
        });
    });

    var content = {
        "event_subscriptions": {
            "key": "some key",
            "callback_url": "https://" + ip_hostname + ":" + port + "/events",
            "event_subscription": [
                {
                    "resource": "/ws/v1/test_subscription",
                    "event_type": "INSERT"
                },
                {
                    "resource": "/ws/v1/test_subscription",
                    "event_type": "UPDATE"
                },
                {
                    "resource": "/ws/v1/test_subscription",
                    "event_type": "DELETE"
                }
            ]
        }
    };

    req.write(JSON.stringify(content));

    req.end();

    req.on('error', function(e) {
        console.log('---------------------------------------------------------------------');
        console.error('An error occurred while sending the event subscription to PowerSchool:');
        console.error(util.inspect(e, false));
        response.end();
    });

    response.end("Sending a test event to PowerSchool.\nView the console output for details.");
});

app.post('/events', function(req, res) {
    console.log('---------------------------------------------------------------------');
    console.log("Event received from PowerSchool:");

    if (req.body) {
        console.log(JSON.stringify(req.body));
    }

    res.writeHead(200);
    res.end();
});

app.listen(port, function() {
    console.log("Server started.");
});
