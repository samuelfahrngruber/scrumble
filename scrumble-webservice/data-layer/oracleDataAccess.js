const oracledb = require('oracledb');

const DB_STRING = process.env.DB_STRING || '212.152.179.117/ora11g', //212.152.179.117 ------- 192.168.128.152
    DB_USER = process.env.DB_USER || 'd5a29',
    DB_PASS = process.env.DB_PASS || 'd5a',
    options = {
        autoCommit: true
    };

module.exports = {
    execute: (query, param = []) => {
        return new Promise((resolve, reject) => {
            oracledb.getConnection({
                user: DB_USER,
                password: DB_PASS,
                connectString: DB_STRING
            }, (err, connection) => {
                try {
                    param.forEach((item) => {
                        if(item === undefined)
                            closeAfter(connection, reject, [Error("ORA-01400")]); 
                    });
                    if (err)
                        closeAfter(connection, reject, [err]);
                    else
                        connection.execute(query, param, options, (err, result) => {
                            if (err)
                                closeAfter(connection, reject, [err]);
                            else
                                closeAfter(connection, resolve, [result]);
                        });
                } catch (ex) {
                    closeAfter(connection, reject, [ex]);
                }
            });
        });
    }
};

function closeAfter(connection, callback, params) {
    callback(...params);
    if (connection)
        connection.close((err) => {
            if (err)
                console.error(err.message);
        });
}