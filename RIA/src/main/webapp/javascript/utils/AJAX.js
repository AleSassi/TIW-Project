function get(url, callback) {
    callAJAX("GET", url, null, function(request) {
        if (request.readyState === 4) {
            callback(request);
        }
    });
}

function post(url, form, callback) {
    callAJAX("POST", url, form, function(request) {
        if (request.readyState === 4) {
            callback(request);
        }
    });
}

function callAJAX(method, url, form, callback) {
    let request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        callback(request)
    };
    request.open(method, url);
    if (form) {
        request.send(new FormData(form));
    } else {
        request.send();
    }
}