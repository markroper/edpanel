var args = process.argv.splice(2);
if (args.length != 4 ){
  console.log("Usage: node relyingparty.js ip_hostname port key_file cert_file");
  process.exit(1);
}
var ip_hostname = args[0];
var port = args[1]; 
var key_file = args[2];
var cert_file = args[3];

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
attribute_exchange_ext.requestParams['openid.ax.type.email'] = "http://powerschool.com/entity/email";
attribute_exchange_ext.requestParams['openid.ax.type.firstName'] = "http://powerschool.com/entity/firstName";
attribute_exchange_ext.requestParams['openid.ax.type.lastName'] = "http://powerschool.com/entity/lastName";
attribute_exchange_ext.requestParams['openid.ax.required'] = "dcid,email,firstName,lastName";

var relyingParty = new openid.RelyingParty(
  'https://' + ip_hostname + ':'+ port + '/verify', 
  null, 
  false, 
  false, 
  [attribute_exchange_ext]); 

app.get('/authenticate', function(req, res){
  console.log("--------------------------------------------------------------------------------");  
  console.log('Accepting authentication request ...');
  console.log("Query params ...\n" + require('util').inspect(req.query, false));
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
    value = {};
    value.id = result.dcid;
	if (result.email) {
		value.email = result.email;
	}
	if (result.firstName) {
		value.firstName = result.firstName;
	}
	
	if (result.lastName) {
		value.lastName = result.lastName;
	}
    res.render('success.html', value);  
  });
});

app.listen(port, function() {
  console.log("Server started.");
});