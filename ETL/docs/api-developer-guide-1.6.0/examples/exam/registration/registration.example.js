var https = require('https');
var fs = require('fs');
var express = require('express');

var args = process.argv.splice(2);
var keyfile = args[0];
var certfile = args[1];

var ResponseObject = function() {
    this.callback_result = 500;
    this.message = 'FAILURE';
    this.time = '0';
};

var responseObj = new ResponseObject();
var registrationResult = 500;


// Create a server that listens at 'https://localhost:7443/registration'
// for incoming requests.
var app = express.createServer({
    key: fs.readFileSync(keyfile || ''),
    cert: fs.readFileSync(certfile || '')
});

app.use(express.bodyParser());

app.post('/registration', function(req, res) {
    console.log("\nRegistration request from PowerSchool");

    // PowerSchool sent callback data.
    if (req.body.callback_data) {
        var callbackData = req.body.callback_data;
        console.log("\tcallback_data = " + callbackData);

        // Insert optional code to handle callbackData.
    }

    // PowerSchool sent authentication credentials.
    if (req.body.credentials) {
        var clientId = req.body.credentials.client_id;
        var clientSecret = req.body.credentials.client_secret;
        console.log("\tcredentials.client_id = " + clientId);
        console.log("\tcredentials.client_secret = " + clientSecret);

        // Insert optional code to persist your client credentials or
        // to request an authentication token from PowerSchool using
        // these client credentials.
    }

    // PowerSchool sent a test url.
    if (req.body.verify_url) {
        var verifyUrl = req.body.verify_url;
        console.log("\tverify_url = " + verifyUrl);

        // Verify the connection to PowerSchool.
        verifyPowerSchoolConnection(res, verifyUrl);
    }
    else {
        register(res);
    }
});

app.listen(7443, function() {
    console.log("Server started.");
    console.log("\n---------------------------------------------------------");
});

function register(resp) {
    // Insert optional code to perform registration.
    // If the registration was successful you can return
    // the status code: 200. If the registration failed
    // you can return the status code: 500.
    var registrationSuccess = true;
    if (registrationSuccess) {
        registrationResult = 200;
    }
    else {
        registrationResult = 500;
    }

    sendCallbackResponse(resp);
}

function sendCallbackResponse(resp) {
    console.log("\nSending registration response back to PowerSchool");

    if (responseObj.callback_result == 200) {
        responseObj.message = 'SUCCESS';
    }
    else {
        // You can add a more specific error message such as
        // INVALID_CERTIFICATE or HOST_UNKNOWN to help in
        // troubleshooting.
        responseObj.message = 'FAILURE';
    }

    // Send the current server time to PowerSchool.
    responseObj.time = (new Date()).toJSON();
    console.log("\tresponse = " + JSON.stringify(responseObj));
    resp.send(responseObj, registrationResult);

    console.log("\n---------------------------------------------------------");
}

function verifyPowerSchoolConnection(resp, verifyUrl) {
    console.log("\nVerifying connection to PowerSchool");

    var url = require('url');
    var reqOpts = url.parse(verifyUrl);
    reqOpts.headers = {
        'Accept': 'application/json'
    };

    var request = https.get(reqOpts, function(res) {
        responseObj.callback_result = res.statusCode;

        res.on('data', function(chunk) {
            console.log("\tServer time received from PowerSchool = " + chunk);
            register(resp);
        });
    });

    request.on('error', function(e) {
        console.log("ERROR: " + e.message);
        responseObj.callback_result = 500;
        register(resp);
    });

    request.setTimeout(1);
}
