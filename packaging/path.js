/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


module.exports = function () {

    var args = new Array();

    for (var i = 0; i != arguments.length; i++) {
        args.push(arguments[i]);
    }

    return args
            .map(function (f) {
                
                if (f.lastIndexOf("/") == (f.length - 1)) {
                    return f.substr(0, f.length - 1);
                } else {
                    return f;
                }

            })
            .map(function (f, i) {
                if(i ==0) return f;
                
                if(f.indexOf("/") == 0) {
                    return f.substr(1,f.length);
                }
                else {
                    return f;
                }
                
            })
            .join("/");
};