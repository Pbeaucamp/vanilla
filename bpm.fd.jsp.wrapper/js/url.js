URL = {
    /**
     * function {public string} ? Returns a parameter from the given URL according its name.
     * If no path is provided, the current page URL is used and the arguments are all shifted one the left.
     */
    getParameter: function(path, name) {
        if(arguments.length < 2) {
            name = path;
            path = window.location.href;
        }
        var value = null;
        var capture = path.match(new RegExp("[\\?&]" + name + "=([^&#]*)"));
        if(capture) {
            value  = unescape(capture[1]);
        }
        return value;
    },
 
    /**
     * function {public string} ? Sets a parameter in the given URL and returns a new string.
     * If no path is provided, the current page URL is used and the arguments are all shifted one the left.
     */
    setParameter: function(path, name, value) {
        if(arguments.length < 3) {
            value = name;
            name = path;
            path = window.location.href;
        }
        var result = path;
 
        // Remove the parameter if it already exists.
        if(URL.getParameter(path, name)) {
            result = URL.removeParameter(path, name);
        }
 
        // If the URL contains parameters, add the parameter after '&'
        if(result.indexOf("?") > -1) {
            result += "&";
        }
        else {
            // If the URL does not contain any parameter, add the parameter after '?'
            result += "?";
        }
 
        result += name + "=" + value;
 
        return result;
    },
 
    /**
     * function {public string} ? Sets some parameters in the given URL and returns a new string.
     * If no path is provided, the current page URL is used and the arguments are all shifted one the left.
     */
    setParameters: function(path, _url_parameters) {
        if(arguments.length < 2) {
            _url_parameters = path;
            path = window.location.href;
        }
        var result = path;
        $H(_url_parameters).each(function(iterator) {
            result = URL.setParameter(result, iterator.key, iterator.value);
        });
 
        return result;
    },
 
    /**
     * function {public string} ? Returns a parameter from the given URL according its name.
     * If no path is provided, the current page URL is used and the arguments are all shifted one the left.
     */
    removeParameter: function(path, name) {
        if(arguments.length < 2) {
            name = path;
            path = window.location.href;
        }
        var result = path;
 
        if(URL.getParameter(path, name)) {
            var index = path.indexOf("?");
            var result = path.substring(0, index);
            if(index > -1) {
                _url_parameters = path.substring(index + 1).split("&");
                var parameter;
                var first = true;
                for(index = 0; index < _url_parameters.length; index++) {
                    parameter = _url_parameters[index];
                    if(parameter.split("=")[0] != name) {
                        if(first) {
                            result += "?";
                            first = false;
                        }
                        else {
                            result += "&";
                        }
                        result += parameter;
                    }
                }
            }
        }
 
        return result;
    },
 
    go: function(path, _url_parameters) {
        path = path || window.location.href;
        if(_url_parameters) {
            path = URL.setParameters(path, _url_parameters);
        }
        window.location.href = path;
    },
 
    /** function {public string} ? Returns the current page URL. */
    get: function() {
        return window.location.href;
    },
 
    pattern: /^(?:(ftp|https?)\:\/\/|~\/|\/)?(?:(\w+):(\w+)@)?((?:[-\w]+\.)+(?:[a-z]+))(?::([\d]{1,5}))?((?:(?:\/(?:[-\w~!$+|.,=]|%[a-f\d]{2})+)+|\/)+|\?|#)?((?:\?(?:[-\w~!$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!$+|.,*:=]|%[a-f\d]{2})*)(?:&(?:[-\w~!$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!$+|.,*:=]|%[a-f\d]{2})*)*)*(?:#((?:[-\w~!$+|.,*:=]|%[a-f\d]{2})*))?$/,
 
    getHost: function(path) {
        var host = null;
 
        var capture = path.match(URL.pattern);
        if(capture) {
            host = capture[4];
        }
 
        return host;
    }
};