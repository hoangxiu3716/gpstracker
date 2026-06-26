// Server configuration for gpstracker Hub
// Same webapp as REST API - use empty relative path (no CORS)

var socketPort = 8080;
var port = 8080;
var socketHost = "";
var host = "";
var server = '';

function checkMasterApp(){
	return true;
}

function getAppCodeByDomain(appCode){
	return appCode || null;
}

function changeOriginURL(url){
	return url;
}
