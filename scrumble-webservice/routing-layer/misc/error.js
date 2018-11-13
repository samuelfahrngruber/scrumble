module.exports = {
    respondWith(res, error) {
        let err;
        var code;

        if(error.message.includes("Keine Werte zu dieser Anfrage")){
            code = 404;
            err = new HttpError("Not Found", error.message);
        }
        else if(error.message.includes("NO CONTENT")){
            code = 204;
        }
        else if(error.message != undefined && error.message.includes("ORA-01400")){
            err = new HttpError("Bad Request", "Nicht alle notwendigen Parameter wurden übergeben");
            code = 400;
        }
        else{
            code = 500;
            err = new HttpError("Internal Server Error", error.message);
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