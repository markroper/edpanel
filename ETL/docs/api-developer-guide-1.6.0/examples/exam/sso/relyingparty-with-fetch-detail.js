var args = process.argv.splice(2);
if (args.length != 6 ){
  console.log("Usage: node relyingparty-with-fetch-detail.js ip_hostname port key_file cert_file powerschool_url access_token");
  process.exit(1);
}
var ip_hostname = args[0];
var port = args[1]; 
var key_file = args[2];
var cert_file = args[3];
var powerschool_url = args[4];
var access_token = args[5];

var app = require('express').createServer({
  key: require('fs').readFileSync(key_file),
  cert: require('fs').readFileSync(cert_file)
});

app.register(".html", require("jqtpl").express);
app.set('views', __dirname + '/views');
app.use(require('express').static(__dirname + '/public'));

var openid = require('openid');
var attribute_exchange_ext = new openid.AttributeExchange();
attribute_exchange_ext.requestParams['openid.ax.type.dcid'] = "http://powerschool.com/entity/id";
attribute_exchange_ext.requestParams['openid.ax.required'] = "dcid";

var relyingParty = new openid.RelyingParty(
  'https://' + ip_hostname + ':'+ port + '/verify', 
  null, 
  false, 
  false, 
  [attribute_exchange_ext]); 

app.get('/authenticate', function(req, res){
  console.log("--------------------------------------------------------------------------------");  
  console.log('Accepting authentication request ...');
  var identifier = req.param('openid_identifier');
  relyingParty.authenticate(identifier, false, function(error, authUrl){
    if (error){
      res.render('failure.html', error.message);
    } else if (!authUrl){
      res.render('failure.html', {'message': "Authorization URL isn't available"});
    } else {
      res.writeHead(302, { Location: authUrl });
      res.end();
    }
  });
});

app.get('/verify', function(req,res){
  relyingParty.verifyAssertion(req, function(error, result){
    console.log("--------------------------------------------------------------------------------");  
    console.log('Verification Result: \n' + require('util').inspect(result, false));    
    if (error || result.authenticated != true) {
      res.render('failure.html', error.message);
      return;
    } 
    requestData(res, result.dcid);
  });
});

app.listen(port, function() {
    console.log("Server started.");
});

function requestData(response, id) {
  var options = require('url').parse(powerschool_url);
  options.path='/ws/v1/staff/' + id,
  options.method= 'GET',
  options.headers = {
    'Accept' : 'application/json',
    'Authorization' : 'Bearer ' + access_token
  };
  
  console.log("--------------------------------------------------------------------------------");  
  console.log('Requesting data from PowerSchool ...' );
  console.log(require('util').inspect(options, false));
  
  var req = require("https").request(options, function(res) {
    console.log("--------------------------------------------------------------------------------");  
    console.log('Response from PowerSchool ...' );
    console.log('Status Code: ', res.statusCode);
    console.log('Header: ', res.headers); 

    var data = '';  
    res.on('data', function(d) {
        data += d;
    });

    res.on('end',function(){
      if(data !== '') {
        try {
          result = JSON.parse(data);
          console.log("Body: " + require('util').inspect(result, false));                
        } catch ( exception ) {
          console.log("Body: " + data);
          console.log("Exception Caught: " + require('util').inspect(exception, false));
          response.render('failure-data-access.html');
          return;
        }                          
      } else {
        console.log("The response body is empty.");
        response.render('failure-data-access.html');
        return;          
      }           
      if (res.statusCode != 200) {
        response.render('failure-data-access.html');
      } else {
        response.render('success-data-access.html', result.staff.name);
      }          
      
    });     
  });

  req.end();

  req.on('error', function(e) {
    console.log("--------------------------------------------------------------------------------");  
    console.error("An error occurs while sending data access request to PowerSchool ... ");
    console.error(require('util').inspect(e, false));
    response.render('failure-data-access.html');
  });      
}

