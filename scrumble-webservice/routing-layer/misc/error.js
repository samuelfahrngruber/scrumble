module.exports = {
    respondWith(res, error) {
        let err;
        var code;

        if(error.message.includes("No Content for this request")){
            err = new HttpError("Not Found", error.message);
            code = 404;
        }
        else if(error.message.includes("ORA-00001")){
            err = new HttpError("Conflict", "Username already exists");
            code = 409;
        }
        else if(error.message.includes("NO CONTENT")){
            code = 204;
        }
        else if(error.message.includes("ORA-01400") || error.message.includes("Bad Request")){
            err = new HttpError("Bad Request", "Not all required parameters have been passed");
            code = 400;
        }
        else if(error.message.includes("Unauthorized")){
            err = new HttpError("Unauthorized", "Username or password incorrect");
            code = 401;
        }
        else if(error.message.toLowerCase().includes("timeout")){
            err = new HttpError("Request Timeout", "timeout while processing the request");
            code = 408;
        }
        else{
            err = new HttpError("Internal Server Error", error.message);
            code = 500;
        }

        res.status(code).json(err);
    }
};

class HttpError {
    constructor(message, details) {
        this.message = message;
        this.details = details;
    }
}