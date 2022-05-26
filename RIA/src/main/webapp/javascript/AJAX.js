function get(url, callback) {
    callAJAX("GET", url, callback);
}

function post(url, callback) {
    callAJAX("POST", url, callback);
}

function callAJAX(method, url, callback) {
    let request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        callback(request)
    };
    request.open(method, url);
    request.send();
}